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
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.querybasedui.runtime.model.Edge;
import org.eclipse.incquery.querybasedui.runtime.model.ViewerDataModel;
import org.eclipse.incquery.querybasedui.runtime.sources.ListContentProvider;
import org.eclipse.jface.databinding.viewers.IViewerUpdater;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends ListContentProvider implements IGraphEntityRelationshipContentProvider {


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

    private MultiList edgeList;

    private EdgeListChangeListener edgeListener;


    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel) {
        super.initializeContent(viewer, vmodel);

        edgeList = vmodel.initializeObservableEdgeList();
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
     * org.eclipse.incquery.querybasedui.runtime.sources.ListContentProvider#getUpdater(org.eclipse.jface.viewers.Viewer
     * )
     */
    @Override
    protected IViewerUpdater getUpdater(Viewer viewer) {
        return new GraphNodeUpdater((GraphViewer) viewer);
    }

}
