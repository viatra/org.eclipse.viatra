/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.internal.apiimpl;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryEngineManager;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseFactory;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.EngineTaintListener;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.internal.EMFPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.internal.PatternSanitizer;
import org.eclipse.incquery.runtime.internal.boundary.CallbackNode;
import org.eclipse.incquery.runtime.internal.engine.LifecycleProvider;
import org.eclipse.incquery.runtime.internal.engine.ModelUpdateProvider;
import org.eclipse.incquery.runtime.internal.matcherbuilder.EPMBuilder;
import org.eclipse.incquery.runtime.rete.construction.ReteContainerBuildable;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * An EMF-IncQuery engine back-end (implementation)
 * 
 * @author Bergmann Gábor
 * 
 */
public class IncQueryEngineImpl extends AdvancedIncQueryEngine {
	
    /**
     * The engine manager responsible for this engine. Null if this engine is unmanaged.
     */
    private final IncQueryEngineManager manager;
    /**
     * The model to which the engine is attached.
     */
    private final Notifier emfRoot;
    private final Map<IQuerySpecification<? extends IncQueryMatcher<?>>, IncQueryMatcher<?>> matchers;

    /**
     * The base index keeping track of basic EMF contents of the model.
     */
    private NavigationHelper baseIndex;
    /**
     * Whether to initialize the base index in wildcard mode.
     */
    private static final boolean WILDCARD_MODE_DEFAULT = false;
    /**
     * The RETE pattern matcher component of the EMF-IncQuery engine.
     */
    private ReteEngine<Pattern> reteEngine = null;
    /**
     * A sanitizer to catch faulty patterns.
     */
    private PatternSanitizer sanitizer = null;


    private final LifecycleProvider lifecycleProvider;
    private final ModelUpdateProvider modelUpdateProvider;
    private Logger logger;
    
    /**
     * EXPERIMENTAL
     */
    private final int reteThreads = 0;
    
    
    private boolean isAdvanced = false;
    
    /**
     * @param manager
     *            null if unmanaged
     * @param emfRoot
     * @throws IncQueryException
     *             if the emf root is invalid
     */
    public IncQueryEngineImpl(IncQueryEngineManager manager, Notifier emfRoot, boolean _isAdvanced) throws IncQueryException {
        super();
        this.isAdvanced = _isAdvanced;
        this.manager = manager;
        this.emfRoot = emfRoot;
        this.matchers = Maps.newHashMap();
        this.lifecycleProvider = new LifecycleProvider(this);
        this.modelUpdateProvider = new ModelUpdateProvider(this);
        if (!(emfRoot instanceof EObject || emfRoot instanceof Resource || emfRoot instanceof ResourceSet))
            throw new IncQueryException(IncQueryException.INVALID_EMFROOT
                    + (emfRoot == null ? "(null)" : emfRoot.getClass().getName()),
                    IncQueryException.INVALID_EMFROOT_SHORT);
    }

    @Override
	public Notifier getScope() {
        return emfRoot;
    }
    
    @Override
	public Set<? extends IncQueryMatcher<? extends IPatternMatch>> getCurrentMatchers(){
        return ImmutableSet.copyOf(matchers.values());
    }
    
    @Override
	public <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(IQuerySpecification<Matcher> querySpecification) throws IncQueryException {
        return querySpecification.getMatcher(this);
    }

	@Override
	@SuppressWarnings("unchecked")
	public <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification) {
		return (Matcher) matchers.get(querySpecification);
	}
    
    @Override
	public IncQueryMatcher<? extends IPatternMatch> getMatcher(Pattern pattern) throws IncQueryException {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification = 
        		QuerySpecificationRegistry.getOrCreateQuerySpecification(pattern);
        return getMatcher(querySpecification);
    }
        
    

    /**
     * Internal accessor for the base index.
     * 
     * @return the baseIndex the NavigationHelper maintaining the base index
     * @throws IncQueryException
     *             if the base index could not be constructed
     */
    protected NavigationHelper getBaseIndexInternal() throws IncQueryException {
        return getBaseIndexInternal(WILDCARD_MODE_DEFAULT, true);
    }

    /**
     * Internal accessor for the base index.
     * 
     * @return the baseIndex the NavigationHelper maintaining the base index
     * @throws IncQueryException
     *             if the base index could not be initialized
     * @throws IncQueryBaseException
     *             if the base index could not be constructed
     */
    protected NavigationHelper getBaseIndexInternal(boolean wildcardMode, boolean initNow) throws IncQueryException {
        if (baseIndex == null) {
            try {
                // sync to avoid crazy compiler reordering which would matter if derived features use eIQ and call this
                // reentrantly
                synchronized (this) {
                    baseIndex = IncQueryBaseFactory.getInstance().createNavigationHelper(null, wildcardMode,
                            getLogger());
                }
            } catch (IncQueryBaseException e) {
                throw new IncQueryException("Could not create EMF-IncQuery base index", "Could not create base index",
                        e);
            }

            if (initNow) {
                initBaseIndex();
            }

        }
        return baseIndex;
    }

    /**
     * @throws IncQueryException
     */
    private synchronized void initBaseIndex() throws IncQueryException {
        try {
            baseIndex.addRoot(getScope());
        } catch (IncQueryBaseException e) {
            throw new IncQueryException("Could not initialize EMF-IncQuery base index",
                    "Could not initialize base index", e);
        }
    }

    @Override
	public NavigationHelper getBaseIndex() throws IncQueryException {
        return getBaseIndexInternal();
    }

   

    @Override
	public Logger getLogger() {
        if (logger == null) {
            final int hash = System.identityHashCode(this);
            logger = Logger.getLogger(IncQueryLoggingUtil.getDefaultLogger().getName() + "." + hash);
            if (logger == null)
                throw new AssertionError(
                        "Configuration error: unable to create EMF-IncQuery runtime logger for engine " + hash);

            // if an error is logged, the engine becomes tainted
            taintListener = new SelfTaintListener(this);
            logger.addAppender(taintListener);
        }
        return logger;
    }

    
    @Override
    public void setWildcardMode(boolean wildcardMode) throws IncQueryException {
        if (baseIndex != null && baseIndex.isInWildcardMode() != wildcardMode)
            throw new IllegalStateException("Base index already built, cannot change wildcard mode anymore");

        if (wildcardMode != WILDCARD_MODE_DEFAULT)
            getBaseIndexInternal(wildcardMode, true);
    }

    
    ///////////////// internal stuff //////////////

    /**
     * Report when a pattern matcher has been completely initialized, so that it can be registered into the engine.
     * @param querySpecification the {@link IQuerySpecification} that corresponds to the matcher
     * @param matcher the {@link IncQueryMatcher} that has finished its initialization process
     * 
     * TODO make it package-only visible when implementation class is moved to impl package
     */
    public void reportMatcherInitialized(IQuerySpecification<?> querySpecification, IncQueryMatcher<?> matcher) {
        if(matchers.containsKey(querySpecification)) {
            // TODO simply dropping the matcher can cause problems
            logger.debug("Pattern " + 
                    CorePatternLanguageHelper.getFullyQualifiedName(querySpecification.getPattern()) + 
                    " already initialized in IncQueryEngine!");
        } else {
            matchers.put(querySpecification, matcher);
            lifecycleProvider.matcherInstantiated(matcher);
        }
    }

    /**
     * Provides access to the internal RETE pattern matcher component of the EMF-IncQuery engine.
     * 
     * @noreference A typical user would not need to call this method.
     * TODO make it package visible only
     */
    public ReteEngine<Pattern> getReteEngine() throws IncQueryException {
        if (reteEngine == null) {
            // if uninitialized, don't initialize yet
            getBaseIndexInternal(WILDCARD_MODE_DEFAULT, false);

            EMFPatternMatcherRuntimeContext context = new EMFPatternMatcherRuntimeContext(this, baseIndex);
            // if (emfRoot instanceof EObject)
            // context = new EMFPatternMatcherRuntimeContext.ForEObject<Pattern>((EObject)emfRoot, this);
            // else if (emfRoot instanceof Resource)
            // context = new EMFPatternMatcherRuntimeContext.ForResource<Pattern>((Resource)emfRoot, this);
            // else if (emfRoot instanceof ResourceSet)
            // context = new EMFPatternMatcherRuntimeContext.ForResourceSet<Pattern>((ResourceSet)emfRoot, this);
            // else throw new IncQueryRuntimeException(IncQueryRuntimeException.INVALID_EMFROOT);

            synchronized (this) {
                reteEngine = buildReteEngineInternal(context);
            }

            // lazy initialization now,
            initBaseIndex();

            // if (reteEngine != null) engines.put(emfRoot, new WeakReference<ReteEngine<String>>(engine));
        }
        return reteEngine;

    }

    

    private ReteEngine<Pattern> buildReteEngineInternal(IPatternMatcherRuntimeContext<Pattern> context) {
        ReteEngine<Pattern> engine;
        engine = new ReteEngine<Pattern>(context, reteThreads);
        ReteContainerBuildable<Pattern> buildable = new ReteContainerBuildable<Pattern>(engine);
        EPMBuilder<Address<? extends Supplier>, Address<? extends Receiver>> builder = new EPMBuilder<Address<? extends Supplier>, Address<? extends Receiver>>(
                buildable, context);
        engine.setBuilder(builder);
        return engine;
    }

    
    
    /**
     * @return the sanitizer
     * TODO make this package visible only
     */
    public PatternSanitizer getSanitizer() {
        if (sanitizer == null) {
            sanitizer = new PatternSanitizer(getLogger());
        }
        return sanitizer;
    }
    
    /**
     * To be called after already removed from engineManager.
     */
    void killInternal() {
        wipe();
        if (baseIndex != null) {
            baseIndex.dispose();
        }
        getLogger().removeAppender(taintListener);
        
    }
    
    
    ///////////////// advanced stuff /////////////
    
    @Override
    public void dispose() {
        if (!isAdvanced) {
            return;
        }
        if (manager != null) {
            managerKillInternal(manager, emfRoot);
            logger.warn(String.format("Managed engine disposed for notifier %s !", emfRoot));
        }
        killInternal();
        lifecycleProvider.engineDisposed();
    }

    @Override
    public void wipe() {
        if (!isAdvanced) {
            return;
        }
        if (manager != null) {
            logger.warn(String.format("Managed engine wiped for notifier %s !", emfRoot));
        }
        if (reteEngine != null) {
            reteEngine.killEngine();
            reteEngine = null;
        }
        sanitizer = null;
        lifecycleProvider.engineWiped();
    }

    
    
    /**
     * Indicates whether the engine is in a tainted, inconsistent state.
     */
    private boolean tainted = false;
    private EngineTaintListener taintListener;

    private static class SelfTaintListener extends EngineTaintListener {
        WeakReference<IncQueryEngineImpl> iqEngRef;

        public SelfTaintListener(IncQueryEngineImpl iqEngine) {
            this.iqEngRef = new WeakReference<IncQueryEngineImpl>(iqEngine);
        }

        @Override
        public void engineBecameTainted() {
            final IncQueryEngineImpl iqEngine = iqEngRef.get();
            iqEngine.tainted = true;
            iqEngine.lifecycleProvider.engineBecameTainted();
        }
    }
    
    @Override
	public boolean isTainted() {
        return tainted;
    }

    @Override
	public boolean isManaged() {
        return manager != null;
        // return isAdvanced; ???
    }


    @Override
	public <Match extends IPatternMatch> void addMatchUpdateListener(IncQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener, boolean fireNow) {
        checkArgument(listener != null, "Cannot add null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot register listener for matcher of different engine!");
       
        //((BaseMatcher<Match>)matcher).addCallbackOnMatchUpdate(listener, fireNow);
        
        final BaseMatcher<Match> bm = (BaseMatcher<Match>)matcher;
        
        final CallbackNode<Match> callbackNode = new CallbackNode<Match>(reteEngine.getReteNet().getHeadContainer(),
                this, listener) {
            @Override
            public Match statelessConvert(Tuple t) {
                //return bm.tupleToMatch(t);
                return bm.newMatch(t.getElements());
            }
        };
        try {
            reteEngine.accessMatcher(matcher.getPattern()).connect(callbackNode, listener, fireNow);
        } catch (RetePatternBuildException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Override
	public <Match extends IPatternMatch> void removeMatchUpdateListener(IncQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener) {
        checkArgument(listener != null, "Cannot remove null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot register listener for matcher of different engine!");
        //((BaseMatcher<Match>)matcher).removeCallbackOnMatchUpdate(listener);
        try {
            reteEngine.accessMatcher(matcher.getPattern()).disconnectByTag(listener);
        } catch (RetePatternBuildException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
	public void addModelUpdateListener(IncQueryModelUpdateListener listener) {
        modelUpdateProvider.addListener(listener);
    }
    
    @Override
	public void removeModelUpdateListener(IncQueryModelUpdateListener listener) {
        modelUpdateProvider.removeListener(listener);
    }
    
    @Override
	public void addLifecycleListener(IncQueryEngineLifecycleListener listener) {
        lifecycleProvider.addListener(listener);
    }
    
    @Override
	public void removeLifecycleListener(IncQueryEngineLifecycleListener listener) {
        lifecycleProvider.removeListener(listener);
    }
    
    // /**
    // * EXPERIMENTAL: Creates an EMF-IncQuery engine that executes post-commit, or retrieves an already existing one.
    // * @param emfRoot the EMF root where this engine should operate
    // * @param reteThreads experimental feature; 0 is recommended
    // * @return a new or previously existing engine
    // * @throws IncQueryRuntimeException
    // */
    // public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws
    // IncQueryRuntimeException {
    // final ResourceSet resourceSet = editingDomain.getResourceSet();
    // WeakReference<ReteEngine<String>> weakReference = engines.get(resourceSet);
    // ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
    // if (engine == null) {
    // IPatternMatcherRuntimeContext<String> context = new
    // EMFPatternMatcherRuntimeContext.ForTransactionalEditingDomain<String>(editingDomain);
    // engine = buildReteEngine(context, reteThreads);
    // if (engine != null) engines.put(resourceSet, new WeakReference<ReteEngine<String>>(engine));
    // }
    // return engine;
    // }

    
}
