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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

/**
 * An instance of this class represents the root element in the {@link QueryExplorer}'s tree viewer.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class RootContent extends CompositeContent<Object, PatternMatcherRootContent> {

    private Map<PatternMatcherRootContentKey, PatternMatcherRootContent> mapping;
    private ContentChildren<PatternMatcherRootContent> children;
    private TreeViewer viewer;
    
    public RootContent() {
        super(null);
        this.children = new ContentChildren<PatternMatcherRootContent>();
        this.mapping = new HashMap<PatternMatcherRootContentKey, PatternMatcherRootContent>();
    }

    public void addPatternMatcherRoot(IEditorPart editorPart, Notifier notifier, QueryEvaluationHint hint) {
        PatternMatcherRootContentKey key = new PatternMatcherRootContentKey(editorPart,
                notifier);
        addPatternMatcherRoot(key, hint);
    }

    public void addPatternMatcherRoot(PatternMatcherRootContentKey key, QueryEvaluationHint hint) {
        if (!mapping.containsKey(key)) {
            PatternMatcherRootContent root = new PatternMatcherRootContent(this, key);
            List<IQuerySpecification<?>> activePatterns = QueryExplorerPatternRegistry.getInstance()
                    .getActivePatterns();
            // runtime & generated matchers
            root.registerPattern(hint, activePatterns.toArray(new IQuerySpecification<?>[activePatterns.size()]));

            this.mapping.put(key, root);
            this.children.addChild(root);
            viewer.setExpandedState(root, true);
        }
    }

    public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
        PatternMatcherRootContentKey key = new PatternMatcherRootContentKey(editorPart, res);
        removePatternMatcherRoot(key);
    }

    public void removePatternMatcherRoot(PatternMatcherRootContentKey key) {
        if (mapping.containsKey(key)) {
            PatternMatcherRootContent root = this.mapping.get(key);
            root.dispose();
            this.children.removeChild(root);
            // this call makes sure that the children observable list is disposed even if lazy propagation is used
            if (!root.getChildren().isDisposed()) root.getChildren().dispose();
            this.mapping.remove(key);
        }
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public IObservableList getChildren() {
        return children;
    }

    @Override
    public Iterator<PatternMatcherRootContent> getChildrenIterator() {
        return children.getElements().iterator();
    }

    public void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }

    public TreeViewer getViewer() {
        return viewer;
    }

}
