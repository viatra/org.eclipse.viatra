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
package org.eclipse.viatra.query.runtime.matchers.context;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * Provides instance model information (relations corresponding to input keys) to query evaluator backends at runtime.
 * Implementors shall extend {@link AbstractQueryRuntimeContext} instead directly this interface.
 * 
 * @author Bergmann Gabor
 * @noimplement This interface is not intended to be implemented by clients. Extend {@link AbstractQueryRuntimeContext} instead.
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
     * Returns true if index is available for the given key providing the given service.
     * @throws IllegalArgumentException if key is not enumerable or an unknown type, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @since 1.4
     */
    public boolean isIndexed(IInputKey key, IndexingService service);
    
    /**
     * @return true iff the given input key is already indexed, and contents are available without costly model traversal.
     * @deprecated use {@link #isIndexed(IInputKey, IndexingService)} instead
     * @throws IllegalArgumentException if key is not enumerable or an unknown type, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     */
    @Deprecated
    public boolean isIndexed(IInputKey key);
    
    /**
     * If the given (enumerable) input key is not yet indexed, the model will be traversed 
     * (after the end of the outermost coalescing block, see {@link IQueryRuntimeContext#coalesceTraversals(Callable)}) 
     * so that the index can be built. It is possible that the base indexer will select a higher indexing level merging
     * multiple indexing requests to an appropriate level.
     * 
     * <p><b>Postcondition:</b> After invoking this method, {@link #getIndexed(IInputKey, IndexingService)} for the same key
     * and service will be guaranteed to return the requested or a highing indexing level as soon as {@link #isCoalescing()} first returns false.
     * 
     * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException if key is not enumerable or an unknown type, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @since 1.4
     */
    public void ensureIndexed(IInputKey key, IndexingService service);
    
    /**
     * If the given (enumerable) input key is not yet indexed, the model will be traversed 
     * (after the end of the outermost coalescing block, see {@link IQueryRuntimeContext#coalesceTraversals(Callable)}) 
     * so that the index can be built.
     * 
     * <p><b>Postcondition:</b> After invoking this method, {@link #isIndexed(IInputKey)} for the same key 
     * will be guaranteed to return true as soon as {@link #isCoalescing()} first returns false.
     * 
     * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException if key is not enumerable or an unknown type, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * 
     * @deprecated use ensureIndexed(IInputKey, IndexingServices) instead
     */
    @Deprecated
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
     * @deprecated use {@link #countTuples(IInputKey, ITuple)} instead
     */
    @Deprecated
    public int countTuples(IInputKey key, Tuple seed);
    
    /**
     * Returns the number of tuples in the extensional relation identified by the input key seeded with the given mask and tuple.
     * 
     * @param key an input key 
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value;  must not be null. <strong>Note</strong>: any given index must occur at most once in seedMask.
     * @param seed
     *            the tuple of fixed values restricting the match set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered match tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(match)) should hold. Must not be null.
     *  
     * @return the number of tuples in the model for the given key and seed
     * 
     * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @since 1.7
     */
    public int countTuples(IInputKey key, TupleMask seedMask, ITuple seed);
    
    /**
     * Returns the tuples in the extensional relation identified by the input key seeded with the given mask and tuple.
     * 
     * @param key an input key
     * @param seed can be null or a tuple with matching arity; 
     * 	if non-null, only those tuples in the model are enumerated 
     * 	that match the seed at positions where the seed is non-null. 
     * @return the tuples in the model for the given key and seed
     * 
     * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @deprecated use {@link #enumerateTuples(IInputKey, ITuple)} instead
     */
    @Deprecated
    public Iterable<Tuple> enumerateTuples(IInputKey key, Tuple seed);
    
    /**
     * Returns the tuples in the extensional relation identified by the input key, optionally seeded with the given tuple.
     * 
     * @param key an input key
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value;  must not be null. <strong>Note</strong>: any given index must occur at most once in seedMask.
     * @param seed
     *            the tuple of fixed values restricting the match set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered match tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(match)) should hold. Must not be null. 
     * @return the tuples in the model for the given key and seed
     * 
     * <p><b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @since 1.7
     */
    public Iterable<Tuple> enumerateTuples(IInputKey key, TupleMask seedMask, ITuple seed);
    
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
     * @deprecated use {@link #enumerateValues(IInputKey, ITuple)} instead
     */
    @Deprecated
    public Iterable<? extends Object> enumerateValues(IInputKey key, Tuple seed);
    
    /**
     * Simpler form of {@link #enumerateTuples(IInputKey, TupleMask, Tuple)} in the case where all values of the tuples
     * are bound by the seed except for one.
     * 
     * <p>
     * Selects the tuples in the extensional relation identified by the input key, optionally seeded with the given
     * tuple, and then returns the single value from each tuple which is not bound by the ssed mask.
     * 
     * @param key
     *            an input key
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value; must not be null. <strong>Note</strong>: any given index must occur at most
     *            once in seedMask, and seedMask must include all parameters in any arbitrary order except one.
     * @param seed
     *            the tuple of fixed values restricting the match set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered match tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(match)) should hold. Must not be null.
     * @return the objects in the model for the given key and seed
     * 
     *         <p>
     *         <b>Precondition:</b> the given key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @throws IllegalArgumentException
     *             if key is not enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
     * @since 1.7
     */
    public Iterable<? extends Object> enumerateValues(IInputKey key, TupleMask seedMask, ITuple seed);
    
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
     * @deprecated use {@link #containsTuple(IInputKey, ITuple)} instead
     */
    @Deprecated
    public boolean containsTuple(IInputKey key, Tuple seed);
    
    /**
     * Simpler form of {@link #enumerateTuples(IInputKey, TupleMask, Tuple)} in the case where all values of the tuples
     * are bound by the seed.
     * 
     * <p>
     * Returns whether the given tuple is in the extensional relation identified by the input key.
     * 
     * <p>
     * Note: this call works for non-enumerable input keys as well.
     * 
     * @param key
     *            an input key
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value; must not be null. <strong>Note</strong>: this mask is expected to be
     *            TupleMask.identity(type.getArity()); the parameter is entirely ignored. The parameter is only
     *            introduced to avoid potential confusion between the deprecated
     *            {@link #containsTuple(IInputKey, Tuple)} method and this. For version 2.0 this conflict will be
     *            resolved by having only a single containsTuple(IInputKey, ITuple) method.
     * @param seed
     *            the tuple of fixed values restricting the match set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered match tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(match)) should hold. Must not be null.
     * @return true iff there is at least a single tuple contained in the relation that corresponds to the seed tuple
     * @since 1.7
     */
    public boolean containsTuple(IInputKey key, TupleMask seedMask, ITuple seed);

    
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

    /**
     * Starts wildcard indexing for the given service. After this call, no registration is required for this {@link IndexingService}.
     * a previously set wildcard level cannot be lowered, only extended.
     * @since 1.4
     */
    public void ensureWildcardIndexing(IndexingService service);
    
    /**
     * Execute the given runnable after traversal. It is guaranteed that the runnable is executed as soon as
     * the indexing is finished. The callback is executed only once, then is removed from the callback queue.
     * @param traversalCallback
     * @throws InvocationTargetException 
     * @since 1.4
     */
    public void executeAfterTraversal(Runnable runnable) throws InvocationTargetException;
}
