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

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Content provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends AbstractViewerStateListener implements IGraphEntityRelationshipContentProvider {

    GraphViewer viewer;
    ViewerState state;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof GraphViewer);
        this.viewer = (GraphViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
        }
        if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            state.addStateListener(this);
        } else if (newInput != null) {
            throw new IllegalArgumentException(String.format("Invalid input type %s for Zest Viewer.", newInput
                    .getClass().getName()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        return Iterables.toArray(state.getItemList(), Item.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getRelationships(final Object source, final Object dest) {
        return Iterables.toArray(Iterables.filter(state.getEdgeList(), new Predicate<Edge>() {
            @Override
            public boolean apply(Edge edge) {
                return edge.getSource().equals(source) && edge.getTarget().equals(dest);
    }
        }), Edge.class);
    }

    @Override
    public void itemAppeared(Item item) {
        viewer.addNode(item);
    }

    @Override
    public void itemDisappeared(Item item) {
        viewer.removeNode(item);
    }

    @Override
    public void edgeAppeared(Edge edge) {
        viewer.addRelationship(edge, edge.getSource(), edge.getTarget());
    }

    @Override
    public void edgeDisappeared(Edge edge) {
        viewer.removeRelationship(edge);
    }

    @Override
    public void dispose() {
        if (state != null) {
            state.removeStateListener(this);
        }

    }

}
