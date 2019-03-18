/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.sources;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ListContentProvider extends AbstractViewerStateListener implements IStructuredContentProvider, IViewerLabelListener {

    AbstractListViewer viewer;
    ViewerState state;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof AbstractListViewer);
        this.viewer = (AbstractListViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
            ((ViewerState) oldInput).removeLabelListener(this);
        }
        if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            if (this.state.isDisposed()) {
                this.state = null;
            } else {
                state.addStateListener(this);
                state.addLabelListener(this);
            }
        } else if (newInput != null) {
            throw new IllegalArgumentException(String.format("Invalid input type %s for List Viewer.", newInput
                    .getClass().getName()));
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (state == null) {
            return new Object[0];
        }
        return state.getItems().toArray(new Item[state.getItems().size()]);
    }

    @Override
    public void itemAppeared(final Item item) {
        viewer.getControl().getDisplay().syncExec(() -> viewer.add(item));
    }

    @Override
    public void itemDisappeared(final Item item) {
        viewer.getControl().getDisplay().syncExec(() -> viewer.remove(item));
    }

    @Override
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
