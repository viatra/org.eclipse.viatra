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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerStateListener;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * <p>
 * A Viewer state represents a stateful data model for an IncQuery Viewer. The
 * state is capable of either returning observable lists of its content, and is
 * also capable of sending of sending state change notifications based to
 * {@link IViewerStateListener} implementations.
 * </p>
 * 
 * <p>
 * A Viewer can be initialized directly with a set of patterns and model, or a
 * {@link ViewerDataModel} can be used to prepare and share such data between
 * instances.
 * </p>
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public final class ViewerStateList extends ViewerState {

	ViewerStateList(ResourceSet set, IncQueryEngine engine,
			Collection<Pattern> patterns, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features) {
		this.model = new ViewerDataModel(set, patterns, engine);
		initializeViewerState(model, filter, features);
	}

	ViewerStateList(ViewerDataModel model, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features) {
		this.model = model;
		initializeViewerState(model, filter, features);
	}
	
	private IObservableList itemList;
	private IObservableList edgeList;

	private IObservableList containmentList;

	
	private Multimap<Object, Item> initializeItemMap() {
		Map<Object, Collection<Item>> map = Maps.newHashMap();
		return Multimaps.newListMultimap(map, new Supplier<List<Item>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Item> get() {
				ArrayList<Item> list = Lists.newArrayList();
				return new WritableList(list, Item.class);
			}

		});
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



	private void initializeViewerState(ViewerDataModel model,
			ViewerDataFilter filter, Collection<ViewerStateFeature> features) {
		itemMap = initializeItemMap();
		initializeItemList(model.initializeObservableItemList(filter, itemMap));
		for (ViewerStateFeature feature : features) {
			switch (feature) {
			case EDGE:
				initializeEdgeList(model.initializeObservableEdgeList(filter,
						itemMap));
				break;
			case CONTAINMENT:
				initializeContainmentList(model
						.initializeObservableContainmentList(filter, itemMap));
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
	private IObservableList getItemList() {
		return itemList;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.model.ViewerState#getItems()
	 */
	@Override
	public IObservableCollection getItems() {
		return getItemList();
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
	private IObservableList getEdgeList() {
		return edgeList;
	}
	
	@Override
	public IObservableCollection getEdges() {
		return getEdgeList();
	};

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
	private IObservableList getContainmentList() {
		return containmentList;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.model.ViewerState#getContainments()
	 */
	@Override
	public IObservableCollection getContainments() {
		return getContainmentList();
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
			((IViewerStateListener) listener)
					.containmentDisappeared(containment);
		}
	}

	private void addContainmentListener(IObservableList oldContainmentList) {
		oldContainmentList.addListChangeListener(containmentListener);
	}

	private void removeContainmentListener(IObservableList oldContainmentList) {
		oldContainmentList.removeListChangeListener(containmentListener);
	}
	
	
}

