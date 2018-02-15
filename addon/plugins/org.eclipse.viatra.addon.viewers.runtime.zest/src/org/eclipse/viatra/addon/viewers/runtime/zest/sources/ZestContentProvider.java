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
package org.eclipse.viatra.addon.viewers.runtime.zest.sources;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.integration.zest.viewer.IGraphEdgeContentProvider;
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * Content provider for Zest graphs. The implementation is more performant than
 * {@link ZestContentWithIsolatedNodesProvider}, but does not support displaying
 * isolated nodes.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends AbstractViewerStateListener implements IGraphEdgeContentProvider {

    protected ModifiableZestContentViewer viewer;
    protected ViewerState state;
    protected boolean displayContainment;
    
    public ZestContentProvider() {
        this(false);
    }
    
    public ZestContentProvider(boolean displayContainment) {
        this.displayContainment = displayContainment;
    }

    @Override
    public Object[] getNodes() {
        if (state!=null) {
            Collection<Item> items = state.getItems();
            return items.toArray(new Item[items.size()]);
        }
        else return new Object[]{};
    }
    
    @Override
    public Object[] getNestedGraphNodes(Object node) {
        return new Object[0];
    }

    @Override
    public boolean hasNestedGraph(Object node) {
        return false;
    }

    @Override
    public Object[] getEdges() {
        if (state!=null) {
            Collection<Edge> items = state.getEdges();
            return items.toArray(new Edge[items.size()]);
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
    public Object getTarget(Object edge) {
        if (edge instanceof Edge) {
            return ((Edge) edge).getTarget();
        }
        return null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof ModifiableZestContentViewer);
        this.viewer = (ModifiableZestContentViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
        }
        if (newInput == null) {
            this.state = null;
        } else if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            if (this.state.isDisposed()) {
                this.state = null;
            } else {
                state.addStateListener(this);
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
        viewer.addEdge(edge);
    }

    @Override
    public void edgeDisappeared(final Edge edge) {
        viewer.removeEdge(edge);
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

}
