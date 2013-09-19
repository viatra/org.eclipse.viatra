/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.databinding.runtime.observables.ObservableLabelFeature;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerStateListener;

import com.google.common.collect.Multimap;

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
 * <p>
 * A ViewerState needs to be cleaned up using the {@link #dispose()} method to unregister all listeners. 
 * </p>
 * @author Zoltan Ujhelyi, Istvan Rath
 *
 */
public abstract class ViewerState {

	
	/* factory method */
	
	// here you can easily switch between list and set-based implementations
	private static boolean setMode = true;
	// at the moment, the set-based one is very buggy
	
	public static ViewerState newInstance(ResourceSet set, IncQueryEngine engine,
			Collection<Pattern> patterns, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features) {
		if (setMode)
			return new ViewerStateSet(set, engine, patterns, filter, features);
		else
			return new ViewerStateList(set, engine, patterns, filter, features);		
	}
	
	/**
	 * If true, then the viewerstate has an "external" model that should not be disposed internally.
	 */
	protected boolean hasExternalViewerDataModel = false;
	
	public static ViewerState newInstance(ViewerDataModel model, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features)
	{
		ViewerState s = null;
		if (setMode)
			s = new ViewerStateSet(model, filter, features);
		else
			s=  new ViewerStateList(model, filter, features);
		
		s.hasExternalViewerDataModel=true;
		return s;
	}
	
	
	
	/**
	 * Maps low-xlevel model objects to their corresponding items.
	 */
	protected Multimap<Object, Item> itemMap;
	
	/**
	 * Maps parent-child relationships in the viewer model.
	 */
	protected Multimap<Item, Item> childrenMap;
	
	/**
     * Maps child-parent relationships in the viewer model.
     */
	protected Map<Item, Item> parentMap;
	
	public Collection<Item> getChildren(Item parent) {
		return childrenMap.get(parent);
	}

	public Item getParent(Item child) {
		return parentMap.get(child);
	}
	
	protected ViewerDataModel model;

//	public ViewerDataModel getModel() {
//		return model;
//	}

	public enum ViewerStateFeature {
		EDGE, CONTAINMENT
	}
	
	
	/*
	 * Listener management
	 */


	protected ListenerList stateListeners = new ListenerList();
	
	protected ListenerList labelListeners = new ListenerList();
	protected IChangeListener labelChangeListener = new IChangeListener() {
		@Override
		public void handleChange(ChangeEvent event) {
            Object element = ((ObservableLabelFeature) event.getSource()).getContainer();
            for (Object _listener : labelListeners.getListeners()) {
            	IViewerLabelListener listener = (IViewerLabelListener) _listener;
            	if (element instanceof Item) {
            		Item item = (Item) element;
					listener.labelUpdated(item, ((Item) element).getLabel().getValue().toString());
            	} else if (element instanceof Edge) {
					Edge edge = (Edge) element;
            		listener.labelUpdated(edge, ((Edge) element).getLabel().getValue().toString());
            	}
            }
		}
	};
	
	/**
	 * Adds a new state Listener to the Viewer State
	 */
	public void addStateListener(IViewerStateListener listener) {
		stateListeners.add(listener);
	}

	/**
	 * Removes a state Listener from the Viewer State
	 */
	public void removeStateListener(IViewerStateListener listener) {
		stateListeners.remove(listener);
	}
	
	public void addLabelListener(IViewerLabelListener listener) {
		labelListeners.add(listener);
	}
	
	public void removeLabelListener(IViewerLabelListener listener) {
		labelListeners.remove(listener);
	}
	
	/**
	 * Exposes EObject -> Item* traceability information.
	 * 
	 * Access the Set of Items mapped to an EObject.
	 */
	public Collection<Item> getItemsFor(Object target) {
		return itemMap.get(target);
	}
	
	/* Hooks into concrete implementations */
	
	public abstract IObservableCollection getItems();
	
	public abstract IObservableCollection getEdges();
	
	public abstract IObservableCollection getContainments();
	
	/**
	 * Removes all listeners and disposes all observable collections managed by the class.
	 */
	public void dispose() {
		if (!getItems().isDisposed()) {
			for (Object _item : getItems()) {
				Item item = (Item) _item;
				item.getLabel().removeChangeListener(labelChangeListener);
				item.dispose();
			}
			getItems().dispose();
			
		}
		if (!getEdges().isDisposed()) {
			for (Object _edge : getEdges()) {
				Edge edge = (Edge) _edge;
				edge.getLabel().removeChangeListener(labelChangeListener);
				edge.dispose();
			}
			getEdges().dispose();
		}
		if (!getContainments().isDisposed()) {
			getContainments().dispose();
		}
		
		stateListeners.clear();
		labelListeners.clear();
		
		if (!hasExternalViewerDataModel) {
			// we have an "internal" data model -> dispose it too
			this.model.dispose();
		}
		
	}
	
	public boolean isDisposed(){
		return this.getItems().isDisposed() || this.getEdges().isDisposed() || this.getContainments().isDisposed();
	}
	
	
}
