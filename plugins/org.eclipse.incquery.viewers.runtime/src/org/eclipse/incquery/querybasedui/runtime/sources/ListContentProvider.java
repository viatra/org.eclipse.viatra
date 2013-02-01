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
package org.eclipse.incquery.querybasedui.runtime.sources;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.incquery.querybasedui.runtime.model.ViewerDataModel;
import org.eclipse.jface.databinding.viewers.IViewerUpdater;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ListContentProvider implements IStructuredContentProvider {

    private final class NodeListChangeListener implements IListChangeListener {


        private IViewerUpdater updater;

        public NodeListChangeListener(IViewerUpdater updater) {
            super();
            this.updater = updater;
        }

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                if (entry.isAddition()) {
                    updater.add(new Object[] { entry });
                    updater.insert(entry.getElement(), entry.getPosition());
                } else {
                    updater.remove(entry.getElement(), entry.getPosition());
                }
            }
        }
    }

    private IObservableList nodeList;
    private NodeListChangeListener nodeListener;
    protected ViewerDataModel vmodel;

    @Override
    public Object[] getElements(Object inputElement) {
        return nodeList.toArray();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        removeListeners();

        vmodel = (ViewerDataModel) newInput;

        if (newInput == null) {
            return;
        }

        initializeContent(viewer, vmodel);
    }

    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel) {
        nodeList = vmodel.initializeObservableItemList();
        nodeListener = new NodeListChangeListener(getUpdater(viewer));
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

    protected IViewerUpdater getUpdater(Viewer viewer) {
        return new ListViewerUpdater((AbstractListViewer) viewer);
    }

}
