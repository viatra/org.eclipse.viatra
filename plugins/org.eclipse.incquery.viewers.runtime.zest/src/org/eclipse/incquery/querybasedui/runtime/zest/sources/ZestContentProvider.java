/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedui.runtime.zest.sources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.querybasedui.runtime.model.Edge;
import org.eclipse.incquery.querybasedui.runtime.model.ViewerDataModel;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends ArrayContentProvider implements IGraphEntityRelationshipContentProvider {
    private final class NodeListChangeListener implements IListChangeListener {

        private GraphViewer viewer;

        public NodeListChangeListener(GraphViewer viewer) {
            super();
            this.viewer = viewer;
        }

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                if (entry.isAddition()) {
                    viewer.addNode(entry.getElement());
                } else {
                    viewer.removeNode(entry.getElement());
                }
            }
        }
    }

    private final class EdgeListChangeListener implements IListChangeListener {

        private GraphViewer viewer;

        public EdgeListChangeListener(GraphViewer viewer) {
            super();
            this.viewer = viewer;
        }

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                Edge edge = (Edge) entry.getElement();
                if (entry.isAddition()) {
                    viewer.addRelationship(edge, edge.getSource(), edge.getTarget());
                } else {
                    viewer.removeRelationship(edge);
                }
            }
        }
    }

    private ViewerDataModel vmodel;
    private IObservableList nodeList;
    private MultiList edgeList;

    private NodeListChangeListener nodeListener;
    private EdgeListChangeListener edgeListener;

	@Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        removeListeners();

        vmodel = (ViewerDataModel) newInput;

        if (newInput == null) {
            return;
        }

        nodeList = vmodel.initializeObservableItemList();
        nodeListener = new NodeListChangeListener((GraphViewer) viewer);
        nodeList.addListChangeListener(nodeListener);

        edgeList = vmodel.initializeObservableEdgeList();
        edgeListener = new EdgeListChangeListener((GraphViewer) viewer);
        edgeList.addListChangeListener(edgeListener);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return nodeList.toArray();
    }

	@Override
    public Object[] getRelationships(Object source, Object target) {
        List<Edge> edgeto = new ArrayList<Edge>();
        for (Object edgeObj : edgeList) {
            Edge edge = (Edge) edgeObj;
            if (source.equals(edge.getSource()) && target.equals(edge.getTarget())) {
                edgeto.add(edge);
            }
        }
		return edgeto.toArray();
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ArrayContentProvider#dispose()
     */
    @Override
    public void dispose() {
        removeListeners();
        super.dispose();
    }

    /**
     * 
     */
    private void removeListeners() {
        if (nodeList != null && !nodeList.isDisposed() && nodeListener != null) {
            nodeList.removeListChangeListener(nodeListener);
        }
        if (edgeList != null && !edgeList.isDisposed() && edgeListener != null) {
            nodeList.removeListChangeListener(edgeListener);
        }
    }

}
