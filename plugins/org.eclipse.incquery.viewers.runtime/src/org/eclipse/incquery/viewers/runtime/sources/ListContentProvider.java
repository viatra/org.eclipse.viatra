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

import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ListContentProvider extends AbstractViewerStateListener implements IStructuredContentProvider {

    AbstractListViewer viewer;
    ViewerState state;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof AbstractListViewer);
        this.viewer = (AbstractListViewer) viewer;
        if (oldInput instanceof ViewerState) {
            ((ViewerState) oldInput).removeStateListener(this);
        }
        if (newInput instanceof ViewerState) {
            this.state = (ViewerState) newInput;
            state.addStateListener(this);
        } else if (newInput != null) {
            throw new IllegalArgumentException(String.format("Invalid input type %s for List Viewer.", newInput
                    .getClass().getName()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        return Iterables.toArray(state.getItems(), Item.class);
    }

    @Override
    public void itemAppeared(Item item) {
        viewer.add(item);
    }

    @Override
    public void itemDisappeared(Item item) {
        viewer.remove(item);
    }

    @Override
    public void dispose() {
        if (state != null) {
            state.removeStateListener(this);
        }
    }
}
