/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest.sources;

import java.util.Collection;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.integration.zest.viewer.IGraphEdgeContentProvider;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.zest.core.viewers.GraphViewer;

/**
 * Content provider for Zest graphs. The implementation is more performant than
 * {@link ZestContentWithIsolatedNodesProvider}, but does not support displaying
 * isolated nodes.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends AbstractViewerStateListener implements IGraphEdgeContentProvider, IViewerLabelListener {

    protected GraphViewer viewer;
    protected ViewerState state;
    protected boolean displayContainment;
    
    public ZestContentProvider() {
        this(false);
    }
    
    public ZestContentProvider(boolean displayContainment) {
        this.displayContainment = displayContainment;
    }

    @Override
    public Object[] getElements(Object input) {
        if (state!=null) {
            Collection<Item> items = state.getItems();
            return items.toArray(new Item[items.size()]);
        }
        else return new Object[]{};
    }

    @Override
    public Object[] getRelationships(Object input) {
        if (state!=null) {
            Stream<Edge> stream = (displayContainment) 
                    ? Stream.concat(state.getEdges().stream(), state.getContainments().stream())
                    : state.getEdges().stream();                
            return stream.toArray(Edge[]::new);
        }
        else return new Object[]{};
    }

    @Override
    public Object getSource(Object edge) {
        if (edge instanceof Edge) {
            return ((Edge) edge).getSource();
        }
        return null;
    }

    @Override
    public Object getDestination(Object edge) {
        if (edge instanceof Edge) {
            return ((Edge) edge).getTarget();
        }
        return null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof GraphViewer);
        this.viewer = (GraphViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
            ((ViewerState) oldInput).removeLabelListener(this);
        }
        if (newInput == null) {
            this.state = null;
        } else if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            if (this.state.isDisposed()) {
                this.state = null;
            } else {
                state.addStateListener(this);
                state.addLabelListener(this);
            }
        } else {
            throw new IllegalArgumentException(String.format("Invalid input type %s for Zest Viewer.", newInput
                    .getClass().getName()));
        }
    }

    @Override
    public void itemAppeared(final Item item) {
        viewer.addNode(item);
    }

    @Override
    public void itemDisappeared(final Item item) {
        viewer.removeNode(item);
    }

    @Override
    public void edgeAppeared(final Edge edge) {
        viewer.addRelationship(edge);
    }

    @Override
    public void edgeDisappeared(final Edge edge) {
        viewer.removeRelationship(edge);
    }
    
    @Override
    public void containmentAppeared(Containment containment) {
        if (displayContainment) {
            edgeAppeared(containment);
        }
    }

    @Override
    public void containmentDisappeared(Containment containment) {
        if (displayContainment) {
            edgeDisappeared(containment);
        }
    }

    public void dispose() {
        if (state != null) {
            state.removeStateListener(this);
        }
        
    }

    @Override
    public void labelUpdated(Item item, String newLabel) {
        viewer.getControl().getDisplay().syncExec(() -> viewer.refresh(item));
        
    }

    @Override
    public void labelUpdated(Edge edge, String newLabel) {
        viewer.getControl().getDisplay().syncExec(() -> viewer.refresh(edge));
    }
}
