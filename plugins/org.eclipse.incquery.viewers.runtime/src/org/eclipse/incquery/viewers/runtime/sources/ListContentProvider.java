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

import java.util.Collections;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.incquery.viewers.runtime.model.FilteredViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.Item.RootItem;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

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
    private Predicate<Item> rootFilter = new RootItem();

    public ListContentProvider(boolean onlyRoots) {
        if (onlyRoots) {
            rootFilter = new RootItem();
        } else {
            rootFilter = Predicates.alwaysTrue();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {        
        return Iterables.toArray(Iterables.filter(nodeList, rootFilter), Item.class);
    }

    /**
     * @param diff
     */
    protected void handleListChanges(ListDiff diff) {
        try {
        for (ListDiffEntry entry : diff.getDifferences()) {
            Item item = (Item) entry.getElement();
            if (rootFilter.apply(item)) {
                    if (entry.isAddition()) {
                        viewer.insert(item, entry.getPosition());
                    } else {
                        viewer.remove(item);
                    }
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
            //return;
            initializeContent(viewer, null, vFilter);
        } else {
            initializeContent(viewer, vmodel, vFilter);
        }
    }

    protected void initializeContent(Viewer viewer, ViewerDataModel vmodel, ViewerDataFilter filter) {
        if (vmodel == null) {
            nodeList = new WritableList(Collections.emptyList(), new Object());
        }
        else {
            if (filter == null) {
                nodeList = vmodel.initializeObservableItemList();
            } else {
                nodeList = vmodel.initializeObservableItemList(filter);
            }
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
