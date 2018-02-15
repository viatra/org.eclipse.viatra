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
package org.eclipse.viatra.addon.viewers.runtime.sources;

import java.util.Collection;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ItemExtender.RootItem;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

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

    @Override
    public Object[] getElements(Object inputElement) {
        if (state == null) {
            return new Object[0];
        }
        
        return state.getItems().stream().filter(filter).toArray(Item[]::new);
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
    public void itemAppeared(final Item item) {
        if (filter.test(item)) {
            viewer.getControl().getDisplay().syncExec(() -> viewer.add(viewer.getInput(), item));
        }
    }

    @Override
    public void itemDisappeared(final Item item) {
        viewer.getControl().getDisplay().syncExec(() -> viewer.remove(item));
    }

    @Override
    public void containmentAppeared(final Containment edge) {
        viewer.getControl().getDisplay().syncExec(() -> {
            viewer.add(edge.getSource(), edge.getTarget());
            viewer.setExpandedState(edge.getSource(), true);
            viewer.refresh(edge.getTarget());
            
        });
    }

    @Override
    public void containmentDisappeared(final Containment edge) {
        viewer.getControl().getDisplay().syncExec(() -> {
            viewer.remove(edge.getSource(), new Object[] { edge.getTarget() });
            viewer.refresh(edge.getSource());
        });
    }

    @Override
    public void dispose() {
        if (state != null) {
            state.removeStateListener(this);
        }
    }

}
