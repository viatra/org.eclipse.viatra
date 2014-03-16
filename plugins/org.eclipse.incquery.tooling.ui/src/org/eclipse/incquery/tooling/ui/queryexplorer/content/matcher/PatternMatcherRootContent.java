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
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.EngineTaintListener;
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
    private ContentEngineTaintListener taintListener;
    private final ILog logger = IncQueryGUIPlugin.getDefault().getLog();

    public PatternMatcherRootContent(RootContent parent, PatternMatcherRootContentKey key) {
        super(parent);
        this.children = new ContentChildren<PatternMatcherContent>();
        this.taintListener = new ContentEngineTaintListener();
        this.mapping = Maps.newHashMap();
        this.key = key;

        AdvancedIncQueryEngine engine = key.getEngine();
        if (engine == null) {
            key.setEngine(createEngine());
        }
        if (engine != null) {
            engine.getLogger().addAppender(taintListener);
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

    public void addMatcher(IncQueryEngine engine, IQuerySpecification<?> specification, boolean generated) {
        String fqn = specification.getFullyQualifiedName();

        PatternMatcherContent pm = new PatternMatcherContent(this, engine, specification,
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

    public void removeMatcher(String patternFqn) {
        // if the pattern is first deactivated then removed, than the matcher corresponding matcher is disposed
        PatternMatcherContent matcher = this.mapping.get(patternFqn);
        if (matcher != null) {
            this.children.removeChild(matcher);
            matcher.dispose();
            this.mapping.remove(patternFqn);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        
        AdvancedIncQueryEngine engine = key.getEngine();
        if (engine != null) {
            engine.getLogger().removeAppender(taintListener);
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

    public void registerPattern(final IQuerySpecification<?>... patterns) {
        IncQueryEngine engine = null;
        try {
            engine = key.getEngine();

            if (engine.getBaseIndex().isInWildcardMode()) {
                addMatchersForPatterns(patterns);
            } else {
                engine.getBaseIndex().coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() {
                        addMatchersForPatterns(patterns);
                        return null;
                    }
                });
            }
        } catch (IncQueryException ex) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                    "Cannot initialize pattern matcher engine.", ex));
        } catch (InvocationTargetException e) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                    "Error during pattern matcher construction: " + e.getCause().getMessage(), e.getCause()));
        }
    }

    private void addMatchersForPatterns(IQuerySpecification<?>... queries) {
        for (IQuerySpecification<?> query : queries) {
            boolean isGenerated = QueryExplorerPatternRegistry.getInstance().isGenerated(query);
            addMatcher(key.getEngine(), query, isGenerated);
        }
    }

    public void unregisterPattern(IQuerySpecification<?> specification) {
        removeMatcher(specification.getFullyQualifiedName());
    }

    private class ContentEngineTaintListener extends EngineTaintListener {

        @Override
        public void engineBecameTainted() {
//            for (PatternMatcherContent matcher : mapping.values()) {
//                matcher.stopMonitoring();
//            }
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

}
