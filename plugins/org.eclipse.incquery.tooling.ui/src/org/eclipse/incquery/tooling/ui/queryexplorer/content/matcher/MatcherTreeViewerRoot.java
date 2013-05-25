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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.ui.IEditorPart;

public class MatcherTreeViewerRoot {
    private Map<ModelConnectorTreeViewerKey, ObservablePatternMatcherRoot> roots;

    public MatcherTreeViewerRoot() {
        roots = new HashMap<ModelConnectorTreeViewerKey, ObservablePatternMatcherRoot>();
    }

    public void addPatternMatcherRoot(IEditorPart editorPart, Notifier notifier) {
        ModelConnectorTreeViewerKey key = new ModelConnectorTreeViewerKey(editorPart, notifier);
        addPatternMatcherRoot(key);
    }

    public void addPatternMatcherRoot(ModelConnectorTreeViewerKey key) {
        if (!roots.containsKey(key)) {
            ObservablePatternMatcherRoot root = ObservablePatternMatcherRoot.createPatternMatcherRoot(key);
            this.roots.put(key, root);
            if (QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
            }
        }
    }

    public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
        ModelConnectorTreeViewerKey key = new ModelConnectorTreeViewerKey(editorPart, res);
        removePatternMatcherRoot(key);
    }

    public void removePatternMatcherRoot(ModelConnectorTreeViewerKey key) {
        if (roots.containsKey(key)) {
            // Notifier notifier = key.getNotifier();
            // disposing IncQueryEngine instance associated to the given Notifier
            // EngineManager.getInstance().disposeEngine(notifier);
            ObservablePatternMatcherRoot root = this.roots.get(key);
            root.dispose();
            AdvancedIncQueryEngine engine = root.getKey().getEngine();
            if (engine != null) {
                engine.dispose();
            }
            this.roots.remove(key);
            if (QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
            }
        }
    }

    public Map<ModelConnectorTreeViewerKey, ObservablePatternMatcherRoot> getRootsMap() {
        return roots;
    }

    public List<ObservablePatternMatcherRoot> getRoots() {
        return new ArrayList<ObservablePatternMatcherRoot>(roots.values());
    }
}
