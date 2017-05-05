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

package org.eclipse.viatra.query.tooling.ui.queryexplorer.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.RootContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DisplayUtil;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

import com.google.inject.Inject;

/**
 * Runnable unit of registering patterns in given resource.
 * 
 * Note that if the work is implemented as a job, NullPointerException will occur when creating observables as the
 * default realm will be null (because of non-ui thread).
 * 
 * @author Tamas Szabo
 * 
 */
public class RuntimeMatcherRegistrator implements Runnable {

    private final IFile file;
    private final Resource resource;
    
    @Inject
    DisplayUtil dbUtil;

    public RuntimeMatcherRegistrator(final IFile file, final Resource res) {
        this.file = file;
        this.resource = res;
    }

    
    @Override
    public void run() {
        final QueryExplorer queryExplorer = QueryExplorer.getInstance();
        if (queryExplorer != null) {
            try {
                final RootContent vr = queryExplorer.getRootContent();
                final PatternComposite viewerInput = queryExplorer.getPatternsViewerRoot()
                        .getGenericPatternsRoot();
                openPatternsViewerIfNoPreviousPatterns(queryExplorer);
                // UNREGISTERING PATTERNS
                unregisterPatternsFromMatcherTreeViewer(vr);
                // remove labels from pattern registry for the corresponding pattern model
                removeLabelsFromPatternRegistry(queryExplorer, viewerInput);
                // REGISTERING PATTERNS
                Set<IQuerySpecification<?>> newPatterns = registerPatternsFromPatternModel(vr, queryExplorer.getHints());
                setCheckedStatesOnNewPatterns(queryExplorer, viewerInput, newPatterns);
                
                queryExplorer.refreshPatternsViewer();
            } catch (ViatraQueryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setCheckedStatesOnNewPatterns(final QueryExplorer queryExplorerInstance,
            final PatternComposite viewerInput, final Collection<IQuerySpecification<?>> newSpecifications) {
        final List<PatternComponent> components = new ArrayList<PatternComponent>();
        for (final IQuerySpecification<?> specification : newSpecifications) {
            final PatternComponent component = viewerInput.addComponent(specification.getFullyQualifiedName());
            components.add(component);
        }
        // note that after insertion a refresh is necessary otherwise setting check state will not work
        queryExplorerInstance.refreshPatternsViewer();

        for (final PatternComponent component : components) {
            component.setCheckedState(true);
        }
    }

    private void removeLabelsFromPatternRegistry(final QueryExplorer queryExplorerInstance, final PatternComposite viewerInput) {
        final List<IQuerySpecification<?>> oldParsedModel = QueryExplorerPatternRegistry.getInstance().getRegisteredPatternsForFile(file);
        if (oldParsedModel != null) {
            for (final IQuerySpecification<?> pattern : oldParsedModel) {
                viewerInput.removeComponent(pattern.getFullyQualifiedName());
            }
        }

        queryExplorerInstance.getPatternsViewerRoot().getGenericPatternsRoot().purge();
        queryExplorerInstance.refreshPatternsViewer();
    }

    private Set<IQuerySpecification<?>> registerPatternsFromPatternModel(final RootContent vr, QueryEvaluationHint hint) throws ViatraQueryException {
        PatternModel newParsedModel = null;
        if (this.resource!=null) {
            newParsedModel = dbUtil.extractPatternModelFromResource(resource);
        } else {
            // TODO dangerously slow due to xbase jvm model inference process being invoked
            newParsedModel = dbUtil.parseEPM(file);
        }
        // end TODO
        final Set<IQuerySpecification<?>> newPatterns = QueryExplorerPatternRegistry.getInstance().registerPatternModel(file, newParsedModel);
        final List<IQuerySpecification<?>> allActivePatterns = QueryExplorerPatternRegistry.getInstance().getActivePatterns();
        // now the active patterns also contain of the new patterns
        Iterator<PatternMatcherRootContent> iterator = vr.getChildrenIterator();
        while (iterator.hasNext()) {
            PatternMatcherRootContent root = iterator.next();
            root.registerPattern(hint, allActivePatterns.toArray(new IQuerySpecification<?>[allActivePatterns.size()]));
            root.updateHasChildren();
        }
        return newPatterns;
    }

    private void unregisterPatternsFromMatcherTreeViewer(final RootContent vr) {
        final List<IQuerySpecification<?>> allActivePatterns = QueryExplorerPatternRegistry.getInstance().getActivePatterns();
        // deactivate patterns within the given file
        QueryExplorerPatternRegistry.getInstance().unregisterPatternModel(file);

        // unregister all active patterns from the roots and wipe the appropriate iq engine
        Iterator<PatternMatcherRootContent> iterator = vr.getChildrenIterator();
        while (iterator.hasNext()) {
            PatternMatcherRootContent root = iterator.next();
            for (final IQuerySpecification<?> pattern : allActivePatterns) {
                root.unregisterPattern(pattern);
                root.updateHasChildren();
            }
            // wipe the engine after unregistration
            final AdvancedViatraQueryEngine engine = root.getKey().getEngine();
            if (engine != null) {
                engine.wipe();
            }
        }
    }

    private void openPatternsViewerIfNoPreviousPatterns(final QueryExplorer queryExplorerInstance) {
        if (QueryExplorerPatternRegistry.getInstance().isEmpty()) {
            final FlyoutControlComposite flyout = queryExplorerInstance.getPatternsViewerFlyout();
            flyout.getPreferences().setState(IFlyoutPreferences.STATE_OPEN);
            // redraw();
            flyout.layout();
        }
    }
}
