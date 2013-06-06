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
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerStateListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Zoltan Ujhelyi
 *
 */
public final class ViewerState {

    private IObservableList itemList;
    private IObservableList edgeList;

    private IObservableList containmentList;
    private Multimap<Item, Item> childrenMap;
    private Map<Item, Item> parentMap;

    private ListenerList stateListeners = new ListenerList();


    public enum ViewerStateFeature {
        EDGE, CONTAINMENT
    }

    private IListChangeListener itemListener = new IListChangeListener() {

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                Item item = (Item) entry.getElement();
                if (entry.isAddition()) {
                    for (Object listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) listener).itemAppeared(item);
                    }
                } else {
                    for (Object listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) listener).itemDisappeared(item);
                    }
                }
            }
        }
    };

    private IListChangeListener edgeListener = new IListChangeListener() {

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                Edge edge = (Edge) entry.getElement();
                if (entry.isAddition()) {
                    for (Object listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) listener).edgeAppeared(edge);
                    }
                } else {
                    for (Object listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) listener).edgeDisappeared(edge);
                    }
                }
            }
        }
    };

    private IListChangeListener containmentListener = new IListChangeListener() {

        @Override
        public void handleListChange(ListChangeEvent event) {
            ListDiff diff = event.diff;
            for (ListDiffEntry entry : diff.getDifferences()) {
                Containment edge = (Containment) entry.getElement();
                if (entry.isAddition()) {
                    containmentAppeared(edge);
                } else {
                    containmentDisappeared(edge);
                }
            }

        }
    };

    public ViewerState(ViewerDataModel model, ViewerDataFilter filter, Collection<ViewerStateFeature> features) {
        initializeItemList(model.initializeObservableItemList(filter));
        for (ViewerStateFeature feature : features) {
            switch (feature) {
            case EDGE:
                initializeEdgeList(model.initializeObservableEdgeList(filter));
                break;
            case CONTAINMENT:
                initializeContainmentList(model.initializeObservableContainmentList(filter));
            }
        }
    }

    /*
     * Item management
     */
    /**
     * Returns the item stored in this Viewer State
     * 
     * @return
     */
    public IObservableList getItemList() {
        return itemList;
    }

    private void initializeItemList(IObservableList itemList) {
        if (this.itemList != null) {
            removeItemListener(itemList);
        }
        this.itemList = itemList;
        addItemListener(itemList);
    }

    private void addItemListener(IObservableList containmentList) {
        containmentList.addListChangeListener(itemListener);
    }

    private void removeItemListener(IObservableList oldContainmentList) {
        oldContainmentList.removeListChangeListener(itemListener);
    }

    /*
     * Edge management
     */

    /**
     * Returns the edges stored in this Viewer State
     * 
     * @return
     */
    public IObservableList getEdgeList() {
        return edgeList;
    }

    private void initializeEdgeList(IObservableList edgeList) {
        if (this.edgeList != null) {
            removeEdgeListener(this.edgeList);
        }
        this.edgeList = edgeList;
        addEdgeListener(edgeList);
    }

    private void addEdgeListener(IObservableList edgeList) {
        edgeList.addListChangeListener(edgeListener);
    }

    private void removeEdgeListener(IObservableList oldEdgeList) {
        oldEdgeList.removeListChangeListener(edgeListener);
    }

    /*
     * Containment management
     */

    /**
     * Returns the containments stored in this Viewer State
     * 
     * @return
     */
    public IObservableList getContainmentList() {
        return containmentList;
    }

    public Collection<Item> getChildren(Item parent) {
        return childrenMap.get(parent);
    }

    public Item getParent(Item child) {
        return parentMap.get(child);
    }

    private void initializeContainmentList(IObservableList containmentList) {
        if (this.containmentList != null) {
            removeContainmentListener(this.containmentList);
        }
        this.containmentList = containmentList;
        childrenMap = HashMultimap.create();
        parentMap = Maps.newHashMap();
        for (Object obj : containmentList) {
            Containment containment = (Containment) obj;
            containmentAppeared(containment);
        }
        addContainmentListener(containmentList);
    }

    private void containmentAppeared(Containment containment) {
        childrenMap.put(containment.getSource(), containment.getTarget());
        parentMap.put(containment.getTarget(), containment.getSource());
        for (Object listener : stateListeners.getListeners()) {
            ((IViewerStateListener) listener).containmentAppeared(containment);
        }
    }

    private void containmentDisappeared(Containment containment) {
        childrenMap.remove(containment.getSource(), containment.getTarget());
        parentMap.remove(containment.getTarget());
        for (Object listener : stateListeners.getListeners()) {
            ((IViewerStateListener) listener).containmentDisappeared(containment);
        }
    }

    private void addContainmentListener(IObservableList oldContainmentList) {
        oldContainmentList.addListChangeListener(containmentListener);
    }

    private void removeContainmentListener(IObservableList oldContainmentList) {
        oldContainmentList.removeListChangeListener(containmentListener);
    }

    /*
     * Listener management
     */

    /**
     * Adds a new state listener to the Viewer State
     */
    public void addStateListener(IViewerStateListener listener) {
        stateListeners.add(listener);
    }

    /**
     * Removes a state listener from the Viewer State
     */
    public void removeStateListener(IViewerStateListener listener) {
        stateListeners.remove(listener);
    }
}
