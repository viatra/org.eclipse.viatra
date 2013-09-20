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
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerStateListener;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * An {@link IObservableSet}-based implementation of {@link ViewerState}.
 * 
 * 
 * @author Istvan Rath
 * 
 */
public class ViewerStateSet extends ViewerState {

	
	public ViewerStateSet(ViewerDataModel model, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features) {
		this.model = model;
		initializeViewerState(model, filter, features);
	}

	
	
    /**
     * Set of known items.
     */
	private IObservableSet itemSet;
	/**
	 * Set of known edges.
	 */
	private IObservableSet edgeSet;

	/**
	 * Set of known containment relationships.
	 */
	private IObservableSet containmentSet;



	private Multimap<Object, Item> initializeItemMap() {
		Map<Object, Collection<Item>> map = Maps.newHashMap();
		/*
		return Multimaps.newSetMultimap(map, new Supplier<Set<Item>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Set<Item> get() {
				HashSet<Item> Set = Sets.newHashSet();
				return new WritableSet(Set, Item.class);
			}

		});
		*/
		return Multimaps.newListMultimap(map, new Supplier<List<Item>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Item> get() {
				ArrayList<Item> list = Lists.newArrayList();
				return new WritableList(list, Item.class);
			}

		});
	}

	private ISetChangeListener itemListener = new ISetChangeListener() {

		@Override
		public void handleSetChange(SetChangeEvent event) {
			SetDiff diff = event.diff;
			for (Object entry : diff.getAdditions()) {
			    if (entry instanceof Item) {
    				Item item = (Item) entry;
    				for (Object Listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) Listener).itemAppeared(item);
                        item.getLabel().addChangeListener(labelChangeListener);
                    }
			    }
			}
			for (Object entry : diff.getRemovals()) {
			    if (entry instanceof Item) {
                    Item item = (Item) entry;
    			    for (Object Listener : stateListeners.getListeners()) {
    			    	item.getLabel().removeChangeListener(labelChangeListener);
                        ((IViewerStateListener) Listener).itemDisappeared(item);
                    }
			    }
			}
		}
	};

	private ISetChangeListener edgeListener = new ISetChangeListener() {

		@Override
		public void handleSetChange(SetChangeEvent event) {
			SetDiff diff = event.diff;
			
			for (Object entry : diff.getAdditions()) {
                if (entry instanceof Edge) {
                    Edge edge = (Edge) entry;
                    for (Object Listener : stateListeners.getListeners()) {
                        ((IViewerStateListener) Listener).edgeAppeared(edge);
                        edge.getLabel().addChangeListener(labelChangeListener);
                    }
                }
            }
            for (Object entry : diff.getRemovals()) {
                if (entry instanceof Edge) {
                    Edge edge = (Edge) entry;
                    for (Object Listener : stateListeners.getListeners()) {
                    	edge.getLabel().removeChangeListener(labelChangeListener);
                        ((IViewerStateListener) Listener).edgeDisappeared(edge);
                    }
                }
            }
		}
	};

	private ISetChangeListener containmentListener = new ISetChangeListener() {

		@Override
		public void handleSetChange(SetChangeEvent event) {
			SetDiff diff = event.diff;
			for (Object entry : diff.getAdditions()) {
                if (entry instanceof Containment) {
                    Containment edge = (Containment) entry;
                    containmentAppeared(edge);
                }
            }
            for (Object entry : diff.getRemovals()) {
                if (entry instanceof Containment) {
                    Containment edge = (Containment) entry;
                    containmentDisappeared(edge);
                }
            }
		}
	};


	private void initializeViewerState(ViewerDataModel model,
			ViewerDataFilter filter, Collection<ViewerStateFeature> features) {
		itemMap = initializeItemMap();
		initializeItemSet(model.initializeObservableItemSet(filter, itemMap));
		for (ViewerStateFeature feature : features) {
			switch (feature) {
			case EDGE:
				initializeEdgeSet(model.initializeObservableEdgeSet(filter,
						itemMap));
				break;
			case CONTAINMENT:
				initializeContainmentSet(model
						.initializeObservableContainmentSet(filter, itemMap));
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
	private IObservableSet getItemSet() {
		return itemSet;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.model.ViewerState#getItems()
	 */
	@Override
	public IObservableCollection getItems() {
		return getItemSet();
	}

	private void initializeItemSet(IObservableSet itemSet) {
		if (this.itemSet != null) {
			for (Object _item : itemSet) {
				Item item = (Item) _item;
				item.getLabel().removeChangeListener(labelChangeListener);
			}
			removeItemListener(itemSet);
		}
		this.itemSet = itemSet;
		addItemListener(itemSet);
		for (Object _item : itemSet) {
			Item item = (Item) _item;
			item.getLabel().addChangeListener(labelChangeListener);
		}
	}

	private void addItemListener(IObservableSet containmentSet) {
		containmentSet.addSetChangeListener(itemListener);
	}

	private void removeItemListener(IObservableSet oldContainmentSet) {
		oldContainmentSet.removeSetChangeListener(itemListener);
	}

	/*
	 * Edge management
	 */

	/**
	 * Returns the edges stored in this Viewer State
	 * 
	 * @return
	 */
	private IObservableSet getEdgeSet() {
		return edgeSet;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.model.ViewerState#getEdges()
	 */
	@Override
	public IObservableCollection getEdges() {
		return getEdgeSet();
	}

	private void initializeEdgeSet(IObservableSet edgeSet) {
		if (this.edgeSet != null) {
			removeEdgeListener(this.edgeSet);
			for (Object _edge : this.edgeSet) {
				Edge edge = (Edge) _edge;
				edge.getLabel().addChangeListener(labelChangeListener);
			}
		}
		this.edgeSet = edgeSet;
		addEdgeListener(edgeSet);
		for (Object _edge : edgeSet) {
			Edge edge = (Edge) _edge;
			edge.getLabel().addChangeListener(labelChangeListener);
		}
	}

	private void addEdgeListener(IObservableSet edgeSet) {
		edgeSet.addSetChangeListener(edgeListener);
	}

	private void removeEdgeListener(IObservableSet oldEdgeSet) {
		oldEdgeSet.removeSetChangeListener(edgeListener);
	}

	/*
	 * Containment management
	 */

	/**
	 * Returns the containments stored in this Viewer State
	 * 
	 * @return
	 */
	private IObservableSet getContainmentSet() {
		return containmentSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.model.ViewerState#getContainments()
	 */
	@Override
	public IObservableCollection getContainments() {
		return getContainmentSet();
	}
	

	private void initializeContainmentSet(IObservableSet containmentSet) {
		if (this.containmentSet != null) {
			removeContainmentListener(this.containmentSet);
		}
		this.containmentSet = containmentSet;
		childrenMap = HashMultimap.create();
		parentMap = Maps.newHashMap();
		for (Object obj : containmentSet) {
			Containment containment = (Containment) obj;
			containmentAppeared(containment);
		}
		addContainmentListener(containmentSet);
	}

	private void containmentAppeared(Containment containment) {
		childrenMap.put(containment.getSource(), containment.getTarget());
		parentMap.put(containment.getTarget(), containment.getSource());
		for (Object Listener : stateListeners.getListeners()) {
			((IViewerStateListener) Listener).containmentAppeared(containment);
		}
	}

	private void containmentDisappeared(Containment containment) {
		childrenMap.remove(containment.getSource(), containment.getTarget());
		parentMap.remove(containment.getTarget());
		for (Object Listener : stateListeners.getListeners()) {
			((IViewerStateListener) Listener)
					.containmentDisappeared(containment);
		}
	}

	private void addContainmentListener(IObservableSet oldContainmentSet) {
		oldContainmentSet.addSetChangeListener(containmentListener);
	}

	private void removeContainmentListener(IObservableSet oldContainmentSet) {
		oldContainmentSet.removeSetChangeListener(containmentListener);
	}

	
	
	
	/*
	 * Runtime consistency checks
	 */
	
	private void checkDanglingEdge(Edge e) {
	    //this.item
	    // TODO runtime checking - depends on set-based implementation
	}
	
	
	
	
}
