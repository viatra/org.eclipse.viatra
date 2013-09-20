/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.core.databinding.observable.set.IObservableSet;

import com.google.common.collect.Multimap;

/**
 * @author istvanrath
 *
 */
public abstract class ViewerDataModel {

	protected static final int NODE_PRIORITY = 1;
	protected static final int CONTAINMENT_PRIORITY = 2;
	protected static final int EDGE_PRIORITY = 3;


	/**
	 * Initializes and returns an observable Set of nodes. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link #initializeObservableItemSet(ViewerDataFilter)} with an empty filter.
	 * 
	 * @return an observable Set of {@link Item} elements representing the match results in the model.
	 */
	public IObservableSet initializeObservableItemSet(final Multimap<Object, Item> itemMap) {
	    return initializeObservableItemSet(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable list of nodes. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link #initializeObservableItemList(ViewerDataFilter)} with an empty filter.
	 * 
	 * @return an observable list of {@link Item} elements representing the match results in the model.
	 */
	public IObservableList initializeObservableItemList(final Multimap<Object, Item> itemMap) {
	    return initializeObservableItemList(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable Set of nodes. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable Set of {@link Item} elements representing the match results in the model.
	 */
	public abstract IObservableSet initializeObservableItemSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);
	/**
	 * Initializes and returns an observable list of nodes. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable list of {@link Item} elements representing the match results in the model.
	 */
	public abstract IObservableList initializeObservableItemList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);
	
	/**
	 * Initializes and returns an observable Set of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link IncQueryViewerDataModel#initializeObservableEdgeSet(ViewerDataFilter)} with an empty filter.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemSet()} method was called before.
	 * 
	 * @return an observable Set of {@link Edge} elements representing the match results in the model.
	 */
	public IObservableSet initializeObservableEdgeSet(final Multimap<Object, Item> itemMap) {
	    return initializeObservableEdgeSet(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable list of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link IncQueryViewerDataModel#initializeObservableEdgeList(ViewerDataFilter)} with an empty filter.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemList()} method was called before.
	 * 
	 * @return an observable list of {@link Edge} elements representing the match results in the model.
	 */
	public MultiList initializeObservableEdgeList(final Multimap<Object, Item> itemMap) {
	    return initializeObservableEdgeList(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable Set of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemSet()} method was called before.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable Set of {@link Edge} elements representing the match results in the model.
	 */
	public abstract IObservableSet initializeObservableEdgeSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);

	/**
	 * Initializes and returns an observable list of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemList()} method was called before.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable list of {@link Edge} elements representing the match results in the model.
	 */
	public abstract MultiList initializeObservableEdgeList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);

	/**
	 * Initializes and returns an observable Set of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link #initializeObservableContainmentSet(ViewerDataFilter)} with an empty filter.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemSet()} method was called before.
	 * 
	 * @return an observable Set of {@link Edge} elements representing the match results in the model.
	 */
	public IObservableSet initializeObservableContainmentSet(final Multimap<Object, Item> itemMap) {
	    return initializeObservableContainmentSet(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable list of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables. Equivalent of calling
	 * {@link #initializeObservableContainmentList(ViewerDataFilter)} with an empty filter.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemList()} method was called before.
	 * 
	 * @return an observable list of {@link Edge} elements representing the match results in the model.
	 */
	public MultiList initializeObservableContainmentList(final Multimap<Object, Item> itemMap) {
	    return initializeObservableContainmentList(ViewerDataFilter.UNFILTERED, itemMap);
	}

	/**
	 * Initializes and returns an observable Set of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemSet()} method was called before.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable Set of {@link Edge} elements representing the match results in the model.
	 */
	public abstract IObservableSet initializeObservableContainmentSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);
	/**
	 * Initializes and returns an observable list of edges. Each call initializes a new observable, it is the
	 * responsibility of the caller to dispose of the unnecessary observables.</p>
	 * 
	 * <p><strong>Precondition</strong>: The method expects that the {@link #initializeObservableItemList()} method was called before.
	 * 
	 * @param filter
	 *            filter specification
	 * 
	 * @return an observable list of {@link Edge} elements representing the match results in the model.
	 */
	public abstract MultiList initializeObservableContainmentList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap);
	
	/**
	 * Dispose of the data model once it's not needed anymore.
	 */
	public abstract void dispose();

}