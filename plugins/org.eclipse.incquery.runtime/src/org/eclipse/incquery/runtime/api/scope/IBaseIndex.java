/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api.scope;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.incquery.runtime.base.api.IIndexingErrorListener;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseIndexChangeListener;

/**
 * Represents the index maintained on the model.
 * @author Bergmann Gabor
 * 
 */
public interface IBaseIndex {
	// TODO lightweightObserver?
	// TODO IncQueryBaseIndexChangeListener?
	
    /**
     * The given callback will be executed, and all model traversals and index registrations will be delayed until the
     * execution is done. If there are any outstanding feature, class or datatype registrations, a single coalesced model
     * traversal will initialize the caches and deliver the notifications.
     * 
     * @param runnable
     */
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException;
	
    /**
     * Adds a coarse-grained listener that will be invoked after the NavigationHelper index or the underlying model is changed. Can be used
     * e.g. to check model contents. Not intended for general use.
     * 
     * <p/> See {@link #removeBaseIndexChangeListener(IncQueryBaseIndexChangeListener)}
     * @param listener
     */
    public void addBaseIndexChangeListener(IncQueryBaseIndexChangeListener listener);
    
    /**
     * Removes a registered listener.
     * 
     * <p/> See {@link #addBaseIndexChangeListener(IncQueryBaseIndexChangeListener)}
     * 
     * @param listener
     */
    public void removeBaseIndexChangeListener(IncQueryBaseIndexChangeListener listener);
    
    /**
     * Updates the value of indexed derived features that are not well-behaving.
     */
    void resampleDerivedFeatures();

    /**
     * Adds a listener for internal errors in the index. A listener can only be added once.
     * @param listener
     * @returns true if the listener was not already added
     * @since 0.8.0
     */
    boolean addIndexingErrorListener(IIndexingErrorListener listener);
    /**
     * Removes a listener for internal errors in the index
     * @param listener
     * @returns true if the listener was successfully removed (e.g. it did exist)
     * @since 0.8.0
     */
    boolean removeIndexingErrorListener(IIndexingErrorListener listener);

}
