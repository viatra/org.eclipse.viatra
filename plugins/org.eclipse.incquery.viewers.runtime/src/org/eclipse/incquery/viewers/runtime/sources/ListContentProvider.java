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
import org.eclipse.incquery.viewers.runtime.model.FilteredViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
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
    protected ViewerDataFilter vFilter;
    private AbstractListViewer viewer;

    @Override
    public Object[] getElements(Object inputElement) {
        return nodeList.toArray();
    }

    /**
     * @param diff
     */
    protected void handleListChanges(ListDiff diff) {
        try {
        for (ListDiffEntry entry : diff.getDifferences()) {
            if (entry.isAddition()) {
                viewer.insert(entry.getElement(), entry.getPosition());
            } else {
                viewer.remove(entry.getElement());
            }
        }
        } catch (Exception e) {
            vmodel.getLogger().error("Error refreshing viewer", e);
        }
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (viewer instanceof AbstractListViewer) {
            this.viewer = (AbstractListViewer) viewer;
        }
        removeListeners();

        if (newInput instanceof ViewerDataModel) {
            vmodel = (ViewerDataModel) newInput;
            vFilter = null;
        } else if (newInput instanceof FilteredViewerDataModel) {
            vmodel = ((FilteredViewerDataModel) newInput).getModel();
            vFilter = ((FilteredViewerDataModel) newInput).getFilter();
        }
        if (newInput == null) {
            return;
        }

        initializeContent(viewer, vmodel, vFilter);
    }

    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel, ViewerDataFilter filter) {
        if (filter == null) {
            nodeList = vmodel.initializeObservableItemList();
        } else {
            nodeList = vmodel.initializeObservableItemList(filter);
        }
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
