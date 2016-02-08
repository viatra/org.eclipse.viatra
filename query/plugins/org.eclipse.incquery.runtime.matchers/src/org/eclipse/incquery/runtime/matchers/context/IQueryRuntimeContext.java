/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.context;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * Provides instance model information (relations corresponding to input keys) to query evaluator backends at runtime.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IQueryRuntimeContext {
	/** 
	 * Provides metamodel-specific info independent of the runtime instance model.
	 */
	public IQueryMetaContext getMetaContext();
	
	
    /**
     * The given callable will be executed, and all model traversals will be delayed until the execution is done. If
     * there are any outstanding information to be read from the model, a single coalesced model traversal will
     * initialize the caches and deliver the notifications.
     * 
     * <p> Calls may be nested. A single coalesced traversal will happen at the end of the outermost call.
     * 
     * <p> <b>Caution: </b> results returned by the runtime context may be incomplete during the coalescing period, to be corrected by notifications sent during the final coalesced traversal. 
     * For example, if a certain input key is not cached yet, an empty relation may be reported during <code>callable.call()</code>; the cache will be constructed after the call terminates and notifications will deliver the entire content of the relation.  
     * Non-incremental query backends should therefore never enumerate input keys while coalesced (verify using {@link #isCoalescing()}).
     * 
     * @param callable
     */
    public abstract <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException;	
	/**
	 * @return true iff currently within a coalescing section (i.e. within the callable of a call to {@link #coalesceTraversals(Callable)}).
	 */
	public boolean isCoalescing();
	
	/**
	 * @return true iff the given input key is already indexed, and contents are available without costly model traversal.
	 */
	public boolean isIndexed(IInputKey key);
	/**
	 * If the given (enumerable) input key is not yet indexed, the model will be traversed 
	 * (after the end of the outermost coalescing block, see {@link IQueryRuntimeContext#coalesceTraversals(Callable)}) 
	 * so that the index can be built.
	 * 
	 * <p><b>Postcondition:</b> After invoking this method, {@link #isIndexed(IInputKey)} for the same key 
	 * will be guaranteed to return true as soon as {@link #isCoalescing()} first returns false.
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public void ensureIndexed(IInputKey key);
	

	/**
	 * Returns the number of tuples in the extensional relation identified by the input key, optionally seeded with the given tuple.
	 * 
	 * @param key an input key
	 * @param seed can be null or a tuple with matching arity; 
	 * 	if non-null, only those tuples in the model are counted 
	 * 	that match the seed at positions where the seed is non-null. 
	 * @return the number of tuples in the model for the given key and seed
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public int countTuples(IInputKey key, Tuple seed);
	
	/**
	 * Returns the tuples in the extensional relation identified by the input key, optionally seeded with the given tuple.
	 * 
	 * @param key an input key
	 * @param seed can be null or a tuple with matching arity; 
	 * 	if non-null, only those tuples in the model are enumerated 
	 * 	that match the seed at positions where the seed is non-null. 
	 * @return the tuples in the model for the given key and seed
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public Iterable<Tuple> enumerateTuples(IInputKey key, Tuple seed);
	
	/**
	 * Simpler form of {@link #enumerateTuples(IInputKey, Tuple)} in the case where all values of the tuples are bound by the seed except for one. 
	 * 
	 * <p> Selects the tuples in the extensional relation identified by the input key, optionally seeded with the given tuple, 
	 * 	and then returns the single value from each tuple which corresponds to the only null value in the seed.
	 * 
	 * @param key an input key
	 * @param seed can be null (if key is unary) or a tuple with matching arity; 
	 * 	if non-null, only those tuples in the model are enumerated 
	 * 	that match the seed at positions where the seed is non-null;
	 *  the seed is null at exactly one position. 
	 * @return the objects in the model for the given key and seed
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public Iterable<? extends Object> enumerateValues(IInputKey key, Tuple seed);
	
	/**
	 * Simpler form of {@link #enumerateTuples(IInputKey, Tuple)} in the case where all values of the tuples are bound by the seed. 
	 * 
	 * <p> Returns whether the given tuple is in the extensional relation identified by the input key.
	 * 
	 * <p> Note: this call works for non-enumerable input keys as well.
	 * 
	 * @param key an input key
	 * @param seed a tuple with matching arity, consisting of non-null elements (null can be used in the 0-ary case). 
	 * @return true iff the seed tuple is contained in the relation
	 */
	public boolean containsTuple(IInputKey key, Tuple seed);

	
	/**
	 * Subscribes for updates in the extensional relation identified by the input key, optionally seeded with the given tuple.
	 * <p> This should be called after invoking 
	 * 
	 * @param key an input key
	 * @param seed can be null or a tuple with matching arity; 
	 * 	if non-null, only those updates in the model are notified about 
	 * 	that match the seed at positions where the seed is non-null. 
	 * @param listener will be notified of future changes
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public void addUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener);
	/**
	 * Unsubscribes from updates in the extensional relation identified by the input key, optionally seeded with the given tuple.
	 * 
	 * @param key an input key
	 * @param seed can be null or a tuple with matching arity; 
	 * 	if non-null, only those updates in the model are notified about 
	 * 	that match the seed at positions where the seed is non-null. 
	 * @param listener will no longer be notified of future changes
	 * 
	 * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
	 */
	public void removeUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener);
	/*
	 TODO: uniqueness
	 */
	
    /**
     * Wraps the external element into the internal representation that is to be used by the query backend 
     * <p> model element -> internal object.
     * <p> null must be mapped to null.
     */
    public Object wrapElement(Object externalElement);

    /**
     * Unwraps the internal representation of the element into its original form  
     * <p> internal object -> model element
     * <p> null must be mapped to null.
     */
    public Object unwrapElement(Object internalElement);

    /**
     * Unwraps the tuple of elements into the internal representation that is to be used by the query backend
     * <p> model elements -> internal objects
     * <p> null must be mapped to null.
    */
    public Tuple wrapTuple(Tuple externalElements);

    /**
     * Unwraps the tuple of internal representations of elements into their original forms
     * <p> internal objects -> model elements
     * <p> null must be mapped to null.
    */
    public Tuple unwrapTuple(Tuple internalElements);

	
}
