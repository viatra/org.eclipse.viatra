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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.preference.PreferenceConstants;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.Maps;

/**
 * A top level element in the {@link QueryExplorer}'s tree viewer, which is actually displayed. Instances of this class
 * are always associated with an instance of {@link PatternMatcherRootContentKey}. The child elements of
 * this {@link CompositeContent} will consist of {@link PatternMatcherContent} instances.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class PatternMatcherRootContent extends CompositeContent<RootContent, PatternMatcherContent> {

    private final Map<String, PatternMatcherContent> mapping;
    private ContentChildren<PatternMatcherContent> children;
    private final PatternMatcherRootContentKey key;
    private IncQueryEngineLifecycleListener taintListener;
    private final ILog logger = IncQueryGUIPlugin.getDefault().getLog();
    private IStatus contentStatus;

    public PatternMatcherRootContent(RootContent parent, PatternMatcherRootContentKey key) {
        super(parent);
        this.children = new ContentChildren<PatternMatcherContent>();
        this.taintListener = new ContentEngineTaintListener();
        this.mapping = Maps.newHashMap();
        this.key = key;

        AdvancedIncQueryEngine engine = key.getEngine();
        if (engine == null) {
            engine = createEngine();
            key.setEngine(engine);
            RuleEngine ruleEngine = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                    Schedulers.getIQEngineSchedulerFactory(engine));
            key.setRuleEngine(ruleEngine);
            engine.addLifecycleListener(taintListener);
        }
    }

    private AdvancedIncQueryEngine createEngine() {
        boolean wildcardMode = IncQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.WILDCARD_MODE);
        boolean dynamicEMFMode = IncQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.DYNAMIC_EMF_MODE);

        try {
            AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(key.getNotifier(),
                    wildcardMode, dynamicEMFMode);
            return engine;
        } catch (IncQueryException e) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Could not retrieve IncQueryEngine for "
                    + key.getNotifier(), e));
            return null;
        }
    }

    public void registerPattern(final IQuerySpecification<?>... patterns) {
        IncQueryEngine engine = null;
        try {
            engine = key.getEngine();
    
            engine.getBaseIndex().coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() {
                    addMatchersForPatterns(patterns);
                    return null;
                }
            });
            contentStatus = Status.OK_STATUS;
        } catch (IncQueryException ex) {
            reportMatcherError("Cannot initialize pattern matcher engine.", ex);
        } catch (InvocationTargetException e) {
            reportMatcherError("Error during pattern matcher construction: " + e.getCause().getMessage(), e.getCause());
        }
    }

    private void addMatchersForPatterns(IQuerySpecification<?>... queries) {
        for (IQuerySpecification<?> query : queries) {
            boolean isGenerated = QueryExplorerPatternRegistry.getInstance().isGenerated(query);
            addMatcher(key.getEngine(), key.getRuleEngine(), query, isGenerated);
        }
    }

    public void addMatcher(IncQueryEngine engine, RuleEngine ruleEngine, IQuerySpecification<?> specification, boolean generated) {
        String fqn = specification.getFullyQualifiedName();

        PatternMatcherContent pm = new PatternMatcherContent(this, engine, ruleEngine, specification,
                generated);
        this.mapping.put(fqn, pm);

        if (generated) {
            // generated matchers are inserted in front of the list
            this.children.addChild(0, pm);
        } else {
            // generic matchers are inserted in the list according to the order in the eiq file
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
        AdvancedIncQueryEngine engine = key.getEngine();
        if (engine != null) {
            engine.removeLifecycleListener(taintListener);
            engine.dispose();
        }
    }

    public boolean isTainted() {
        AdvancedIncQueryEngine engine = key.getEngine();
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
            contentStatus = new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                message, t);
        } else {
            contentStatus = new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                    message);
        }
        logger.log(contentStatus);
        getParent().getViewer().refresh(this);
    }
    
    private class ContentEngineTaintListener implements IncQueryEngineLifecycleListener {

        @Override
        public void engineBecameTainted(String description, Throwable t) {
            reportMatcherError(description, t);
        }

        @Override
        public void matcherInstantiated(IncQueryMatcher<? extends IPatternMatch> matcher) {
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

    @Override
    public Iterator<PatternMatcherContent> getChildrenIterator() {
        return children.getElements().iterator();
    }

    public IStatus getStatus() {
        return contentStatus;
    }

    
}
