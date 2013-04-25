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
package org.eclipse.incquery.viewers.runtime.zest.sources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.IEdgeReadyListener;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.incquery.viewers.runtime.sources.ListContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends ListContentProvider implements IGraphEntityRelationshipContentProvider {


    private final class EdgeListChangeListener implements IListChangeListener, IEdgeReadyListener {

        private GraphViewer viewer;

        public EdgeListChangeListener(GraphViewer viewer) {
            super();
            this.viewer = viewer;
        }

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            try {
                for (ListDiffEntry entry : diff.getDifferences()) {
                    Edge edge = (Edge) entry.getElement();
                    if (entry.isAddition()) {
                        addRelationship(edge);
                    } else {
                        viewer.removeRelationship(edge);
                    }
                }
            } catch (Exception e) {
                vmodel.getLogger().error("Error refreshing the graph structure", e);
            }
        }

        /**
         * @param edge
         */
        protected void addRelationship(Edge edge) {
            if (edge.isReady()) {
                viewer.addRelationship(edge, edge.getSource(), edge.getTarget());
            } else {
                edge.setListener(this);
            }
        }

        @Override
        public void edgeReady(Edge edge) {
            addRelationship(edge);
        }
    }

    private MultiList edgeList;

    private EdgeListChangeListener edgeListener;

    private GraphViewer viewer;

    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel, ViewerDataFilter filter) {
        this.viewer = (GraphViewer) viewer;
        super.initializeContent(viewer, vmodel, filter);

        if (vmodel == null) {
            edgeList = new MultiList(new IObservableList[]{ new ObservableList(new ArrayList(), new Object()){} });
        } 
        else {
            if (filter == null) {
                edgeList = vmodel.initializeObservableEdgeList();
            } else {
                edgeList = vmodel.initializeObservableEdgeList(filter);
            }
        }
        edgeListener = new EdgeListChangeListener((GraphViewer) viewer);
        edgeList.addListChangeListener(edgeListener);
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

    @Override
    protected void removeListeners() {
        if (edgeList != null && !edgeList.isDisposed() && edgeListener != null) {
            edgeList.removeListChangeListener(edgeListener);
        }
        super.removeListeners();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.incquery.querybasedui.runtime.sources.ListContentProvider#handleListChanges(org.eclipse.core.databinding
     * .observable.list.ListDiff)
     */
    @Override
    protected void handleListChanges(ListDiff diff) {
        for (ListDiffEntry entry : diff.getDifferences()) {
            if (entry.isAddition()) {
                viewer.addNode(entry.getElement());
            } else {
                viewer.removeNode(entry.getElement());
            }
        }
    }


}
