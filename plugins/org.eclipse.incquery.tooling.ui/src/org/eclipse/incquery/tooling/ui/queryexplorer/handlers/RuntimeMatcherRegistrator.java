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

package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.DisplayUtil;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

import com.google.inject.Inject;

/**
 * Runnable unit of registering patterns in given file.
 *
 * Note that if the work is implemented as a job, NullPointerException will occur when creating observables as the
 * default realm will be null (because of non-ui thread).
 *
 * @author Tamas Szabo
 *
 */
public class RuntimeMatcherRegistrator implements Runnable {

    private final IFile file;

    @Inject
    DisplayUtil dbUtil;

    public RuntimeMatcherRegistrator(final IFile file) {
        this.file = file;
    }

    @Override
    public void run() {
        final QueryExplorer queryExplorerInstance = QueryExplorer.getInstance();
        if (queryExplorerInstance != null) {
            final MatcherTreeViewerRoot vr = queryExplorerInstance.getMatcherTreeViewerRoot();
            final PatternComposite viewerInput = queryExplorerInstance.getPatternsViewerInput().getGenericPatternsRoot();
            openPatternsViewerIfNoPreviousPatterns(queryExplorerInstance);
            // UNREGISTERING PATTERNS
            unregisterPatternsFromMatcherTreeViewer(vr);
            // remove labels from pattern registry for the corresponding pattern model
            removeLabelsFromPatternRegistry(queryExplorerInstance, viewerInput);
            // REGISTERING PATTERNS
            final List<Pattern> newPatterns = registerPatternsFromPatternModel(vr);
            setCheckedStatesOnNewPatterns(queryExplorerInstance, viewerInput, newPatterns);
        }
    }

    private void setCheckedStatesOnNewPatterns(final QueryExplorer queryExplorerInstance, final PatternComposite viewerInput,
            final List<Pattern> newPatterns) {
        final List<PatternComponent> components = new ArrayList<PatternComponent>();
        for (final Pattern pattern : newPatterns) {
            final PatternComponent component = viewerInput.addComponent(CorePatternLanguageHelper
                    .getFullyQualifiedName(pattern));
            components.add(component);
        }
        // note that after insertion a refresh is necessary otherwise setting check state will not work
        queryExplorerInstance.getPatternsViewer().refresh();

        for (final PatternComponent component : components) {
            queryExplorerInstance.getPatternsViewer().setChecked(component, true);
        }

        // it is enough to just call selection propagation for one pattern
        if (components.size() > 0) {
            components.get(0).getParent().propagateSelectionToTop(components.get(0));
        }
    }

    private void removeLabelsFromPatternRegistry(final QueryExplorer queryExplorerInstance, final PatternComposite viewerInput) {
        final List<Pattern> oldParsedModel = QueryExplorerPatternRegistry.getInstance().getRegisteredPatternsForFile(file);
        if (oldParsedModel != null) {
            for (final Pattern pattern : oldParsedModel) {
                viewerInput.removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
            }
        }

        queryExplorerInstance.getPatternsViewerInput().getGenericPatternsRoot().purge();
        queryExplorerInstance.getPatternsViewer().refresh();
    }

    private List<Pattern> registerPatternsFromPatternModel(final MatcherTreeViewerRoot vr) {
        final PatternModel newParsedModel = dbUtil.parseEPM(file);
        final List<Pattern> newPatterns = QueryExplorerPatternRegistry.getInstance().registerPatternModel(file, newParsedModel);
        final List<Pattern> allActivePatterns = QueryExplorerPatternRegistry.getInstance().getActivePatterns();
        // now the active patterns also contain of the new patterns
        for (final ObservablePatternMatcherRoot root : vr.getRoots()) {
            root.registerPattern(allActivePatterns.toArray(new Pattern[allActivePatterns.size()]));
        }
        return newPatterns;
    }

    private void unregisterPatternsFromMatcherTreeViewer(final MatcherTreeViewerRoot vr) {
        final List<Pattern> allActivePatterns = QueryExplorerPatternRegistry.getInstance().getActivePatterns();
        // deactivate patterns within the given file
        QueryExplorerPatternRegistry.getInstance().unregisterPatternModel(file);

        // unregister all active patterns from the roots and wipe the appropriate iq engine
        for (final ObservablePatternMatcherRoot root : vr.getRoots()) {
            for (final Pattern pattern : allActivePatterns) {
                root.unregisterPattern(pattern);
            }
            // final IncQueryEngine engine =
            // EngineManager.getInstance().getIncQueryEngineIfExists(root.getNotifier());
            final AdvancedIncQueryEngine engine = root.getKey().getEngine();
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
