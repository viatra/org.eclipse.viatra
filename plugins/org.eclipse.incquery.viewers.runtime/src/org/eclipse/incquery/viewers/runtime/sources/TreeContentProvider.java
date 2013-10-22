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
package org.eclipse.incquery.viewers.runtime.sources;

import java.util.Collection;

import org.eclipse.incquery.viewers.runtime.model.Containment;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.Item.RootItem;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class TreeContentProvider extends AbstractViewerStateListener implements ITreeContentProvider {

    AbstractTreeViewer viewer;
    ViewerState state;
    RootItem filter = new RootItem();

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof AbstractTreeViewer);
        this.viewer = (AbstractTreeViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
        }
        if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            if (this.state.isDisposed()) {
				this.state = null;
			} else {
				state.addStateListener(this);
			}
        } else if (newInput != null) {
            throw new IllegalArgumentException(String.format("Invalid input type %s for Tree Viewer.", newInput
                    .getClass().getName()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
    	if (state == null) {
    		return new Object[0];
    	}
        return Iterables.toArray(Iterables.filter(state.getItems(), filter), Item.class);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        final Collection<Item> children = state.getChildren((Item) parentElement);
        return children.toArray(new Object[children.size()]);
    }

    @Override
    public Object getParent(Object element) {
        return state.getParent((Item) element);
    }

    @Override
    public boolean hasChildren(Object element) {
        return !(state.getChildren((Item) element).isEmpty());
    }

    @Override
    public void itemAppeared(Item item) {
        if (filter.apply(item)) {
            viewer.add(viewer.getInput(), item);
        }
    }

    @Override
    public void itemDisappeared(Item item) {
        viewer.remove(item);
    }

    @Override
    public void containmentAppeared(Containment edge) {
        viewer.add(edge.getSource(), edge.getTarget());
        viewer.setExpandedState(edge.getSource(), true);
        viewer.refresh(edge.getTarget());
    }

    @Override
    public void containmentDisappeared(Containment edge) {
        viewer.remove(edge.getSource(), new Object[] { edge.getTarget() });
        viewer.refresh(edge.getSource());
    }

    @Override
    public void dispose() {
        if (state != null) {
            state.removeStateListener(this);
        }
    }

}
