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

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.jface.databinding.viewers.IViewerUpdater;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class GraphNodeUpdater implements IViewerUpdater {

    GraphViewer viewer;

    /**
     * @param viewer
     */
    public GraphNodeUpdater(GraphViewer viewer) {
        this.viewer = viewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#insert(java.lang.Object, int)
     */
    @Override
    public void insert(Object element, int position) {
        viewer.addNode(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#remove(java.lang.Object, int)
     */
    @Override
    public void remove(Object element, int position) {
        viewer.removeNode(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#replace(java.lang.Object, java.lang.Object, int)
     */
    @Override
    public void replace(Object oldElement, Object newElement, int position) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#move(java.lang.Object, int, int)
     */
    @Override
    public void move(Object element, int oldPosition, int newPosition) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#add(java.lang.Object[])
     */
    @Override
    public void add(Object[] elements) {
        for (Object element : elements) {
            viewer.addNode(element);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.IViewerUpdater#remove(java.lang.Object[])
     */
    @Override
    public void remove(Object[] elements) {
        for (Object element : elements) {
            viewer.removeNode(element);
        }
    }

}
