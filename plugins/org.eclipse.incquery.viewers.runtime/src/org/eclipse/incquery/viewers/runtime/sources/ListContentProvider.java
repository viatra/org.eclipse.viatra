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

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ListContentProvider implements IStructuredContentProvider {

    private final class NodeListChangeListener implements IListChangeListener {

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            handleListChanges(diff);
        }
    }

    private IObservableList nodeList;
    private NodeListChangeListener nodeListener;
    protected ViewerDataModel vmodel;
    private AbstractListViewer viewer;

    @Override
    public Object[] getElements(Object inputElement) {
        return nodeList.toArray();
    }

    /**
     * @param diff
     */
    protected void handleListChanges(ListDiff diff) {
        for (ListDiffEntry entry : diff.getDifferences()) {
            if (entry.isAddition()) {
                viewer.insert(entry.getElement(), entry.getPosition());
            } else {
                viewer.remove(entry.getElement());
            }
        }
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (viewer instanceof AbstractListViewer) {
            this.viewer = (AbstractListViewer) viewer;
        }
        removeListeners();

        vmodel = (ViewerDataModel) newInput;

        if (newInput == null) {
            return;
        }

        initializeContent(viewer, vmodel);
    }

    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel) {
        nodeList = vmodel.initializeObservableItemList();
        nodeListener = new NodeListChangeListener();
        nodeList.addListChangeListener(nodeListener);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ArrayContentProvider#dispose()
     */
    @Override
    public void dispose() {
        removeListeners();
    }

    protected void removeListeners() {
        if (nodeList != null && !nodeList.isDisposed() && nodeListener != null) {
            nodeList.removeListChangeListener(nodeListener);
        }
    }

}
