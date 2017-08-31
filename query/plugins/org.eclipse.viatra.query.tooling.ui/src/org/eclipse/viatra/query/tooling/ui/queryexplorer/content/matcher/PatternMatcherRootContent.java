/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.preference.PreferenceConstants;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherCollection;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;

/**
 * A top level element in the {@link QueryExplorer}'s tree viewer, which is actually displayed. Instances of this class
 * are always associated with an instance of {@link PatternMatcherRootContentKey}. The child elements of
 * this {@link CompositeContent} will consist of {@link PatternMatcherContent} instances.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class PatternMatcherRootContent extends CompositeContent<RootContent, PatternMatcherContent> implements IFilteredMatcherCollection {

    private final Map<String, PatternMatcherContent> mapping;
    private ContentChildren<PatternMatcherContent> children;
    private final PatternMatcherRootContentKey key;
    private ViatraQueryEngineLifecycleListener taintListener;
    private final ILog logger = ViatraQueryGUIPlugin.getDefault().getLog();
    private IStatus contentStatus;

    public PatternMatcherRootContent(RootContent parent, PatternMatcherRootContentKey key) {
        super(parent);
        this.children = new ContentChildren<>();
        this.taintListener = new ContentEngineTaintListener();
        this.mapping = Maps.newHashMap();
        this.key = key;

        AdvancedViatraQueryEngine engine = key.getEngine();
        if (engine == null) {
            engine = createEngine();
            key.setEngine(engine);
            RuleEngine ruleEngine = ExecutionSchemas.createViatraQueryExecutionSchema(engine,
                    Schedulers.getQueryEngineSchedulerFactory(engine));
            key.setRuleEngine(ruleEngine);
            engine.addLifecycleListener(taintListener);
        }
    }

    private AdvancedViatraQueryEngine createEngine() {
        boolean wildcardMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.WILDCARD_MODE);
        boolean dynamicEMFMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.DYNAMIC_EMF_MODE);

        try {
            BaseIndexOptions options = new BaseIndexOptions(dynamicEMFMode, wildcardMode);
            QueryScope scope = new EMFScope(key.getNotifier(), options);
            AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
            return engine;
        } catch (ViatraQueryException e) {
            logger.log(new Status(IStatus.ERROR, ViatraQueryGUIPlugin.PLUGIN_ID, "Could not retrieve ViatraQueryEngine for "
                    + key.getNotifier(), e));
            return null;
        }
    }

    public void registerPattern(final QueryEvaluationHint hint, final IQuerySpecification<?>... patterns) {
        ViatraQueryEngine engine = null;
        try {
            engine = key.getEngine();
    
            engine.getBaseIndex().coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() {
                    addMatchersForPatterns(hint, patterns);
                    return null;
                }
            });
            contentStatus = Status.OK_STATUS;
        } catch (ViatraQueryException ex) {
            reportMatcherError("Cannot initialize pattern matcher engine.", ex);
        } catch (InvocationTargetException e) {
            reportMatcherError("Error during pattern matcher construction: " + e.getCause().getMessage(), e.getCause());
        }
    }

    private void addMatchersForPatterns(QueryEvaluationHint hint, IQuerySpecification<?>... queries) {
        for (IQuerySpecification<?> query : queries) {
            boolean isGenerated = QueryExplorerPatternRegistry.getInstance().isGenerated(query);
            addMatcher(key.getEngine(), key.getRuleEngine(), query, isGenerated, hint);
        }
    }

    public void addMatcher(AdvancedViatraQueryEngine engine, RuleEngine ruleEngine, IQuerySpecification<?> specification, boolean generated,  QueryEvaluationHint hint) {
        String fqn = specification.getFullyQualifiedName();

        PatternMatcherContent pm = new PatternMatcherContent(this, engine, ruleEngine, specification,
                generated, hint);
        this.mapping.put(fqn, pm);

        if (generated) {
            // generated matchers are inserted in front of the list
            this.children.addChild(0, pm);
        } else {
            // generic matchers are inserted in the list according to the order in the vql file
            this.children.addChild(pm);
        }
    }

    public void unregisterPattern(IQuerySpecification<?> specification) {
        removeMatcher(specification.getFullyQualifiedName());
    }

    public void removeMatcher(String patternFqn) {
        PatternMatcherContent matcher = this.mapping.get(patternFqn);
        if (matcher != null) {
            matcher.dispose();
            this.children.removeChild(matcher);
            // this call makes sure that the children observable list is disposed even if lazy propagation is used
            IObservableList observableList = matcher.getChildren();
            if (observableList != null && !observableList.isDisposed()) observableList.dispose();
            this.mapping.remove(patternFqn);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        RuleEngine ruleEngine = key.getRuleEngine();
        if(ruleEngine != null) {
            ruleEngine.dispose();
        }
        AdvancedViatraQueryEngine engine = key.getEngine();
        if (engine != null) {
            engine.removeLifecycleListener(taintListener);
            engine.dispose();
        }
    }

    public boolean isTainted() {
        AdvancedViatraQueryEngine engine = key.getEngine();
        return (engine == null) ? true : engine.isTainted();
    }

    public PatternMatcherRootContentKey getKey() {
        return key;
    }

    public IEditorPart getEditorPart() {
        return this.key.getEditorPart();
    }

    public Notifier getNotifier() {
        return this.key.getNotifier();
    }

    private void reportMatcherError(String message, Throwable t) {
        if (t != null) {
            contentStatus = new Status(IStatus.ERROR, ViatraQueryGUIPlugin.PLUGIN_ID,
                message, t);
        } else {
            contentStatus = new Status(IStatus.ERROR, ViatraQueryGUIPlugin.PLUGIN_ID,
                    message);
        }
        logger.log(contentStatus);
        getParent().getViewer().getControl().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                getParent().getViewer().refresh(this);
            }
        });
    }
    
    private class ContentEngineTaintListener implements ViatraQueryEngineLifecycleListener {

        @Override
        public void engineBecameTainted(String description, Throwable t) {
            reportMatcherError(description, t);
        }

        @Override
        public void matcherInstantiated(ViatraQueryMatcher<? extends IPatternMatch> matcher) {
        }

        @Override
        public void engineWiped() {
            // TODO handle wipe
        }

        @Override
        public void engineDisposed() {
            // TODO handle dipsose
        }
        
    }

    @Override
    public String getText() {
        return this.key.toString();
    }
    
    @Override
    public IObservableList getChildren() {
        return children;
    }

    /**
     * @since 1.4
     */
    @Override
    public Iterable<IFilteredMatcherContent> getFilteredMatchers() {
        Builder<IFilteredMatcherContent> builder = ImmutableSet.<IFilteredMatcherContent>builder();
        for (PatternMatcherContent matcher : children.getElements()) {
            builder.add(matcher);
        }
        return builder.build();
    }

    @Override
    public Iterator<PatternMatcherContent> getChildrenIterator() {
        return children.getElements().iterator();
    }

    public IStatus getStatus() {
        return contentStatus;
    }

    
}
