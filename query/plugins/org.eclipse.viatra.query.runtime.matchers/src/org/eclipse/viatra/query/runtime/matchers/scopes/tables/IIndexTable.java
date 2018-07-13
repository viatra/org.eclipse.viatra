/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.scopes.tables;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * Read-only interface that provides the {@link IInputKey}-specific slice of an instance store to realize a
 * {@link IQueryRuntimeContext}. Implemented by a customizable data store that is responsible for:
 * <ul>
 * <li>storing the instance tuples of the {@link IInputKey},</li>
 * <li>providing efficient lookup via storage-specific indexing,</li>
 * <li>delivering notifications. (TODO not designed yet)</li>
 * </ul>
 * 
 * <p>
 * Can be specialized for unary / binary / etc., opposite edges or node subtypes, specific types, distributed storage,
 * etc.
 * <p>
 * Writeable API is specific to the customized implementations (e.g. unary).
 * 
 * <p>
 * <b>Precondition:</b> the associated input key is enumerable, see {@link IQueryMetaContext#isEnumerable(IInputKey)}.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same.
 *
 * @since 2.0
 * @author Gabor Bergmann
 * @noimplement This interface is not intended to be implemented directly. Extend {@link AbstractIndexTable} instead.
 */
public interface IIndexTable {

    // TODO add superinterface that represents a statistics-only counter?

    /**
     * @return the input key indexed by this table
     */
    public IInputKey getInputKey();

    /**
     * Returns the tuples, optionally seeded with the given tuple.
     * 
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value; must not be null. <strong>Note</strong>: any given index must occur at most
     *            once in seedMask.
     * @param seed
     *            the tuple of fixed values restricting the row set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered row tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(row)) should hold. Must not be null.
     * @return the tuples in the table for the given key and seed
     */
    public Iterable<Tuple> enumerateTuples(TupleMask seedMask, ITuple seed);

    /**
     * Simpler form of {@link #enumerateTuples(TupleMask, ITuple)} in the case where all values of the tuples are bound
     * by the seed except for one.
     * 
     * <p>
     * Selects the tuples in the table, optionally seeded with the given tuple, and then returns the single value from
     * each tuple which is not bound by the seed mask.
     * 
     * @param seedMask
     *            a mask that extracts those parameters of the input key (from the entire parameter list) that should be
     *            bound to a fixed value; must not be null. <strong>Note</strong>: any given index must occur at most
     *            once in seedMask, and seedMask must include all parameters in any arbitrary order except one.
     * @param seed
     *            the tuple of fixed values restricting the row set to be considered, in the same order as given in
     *            parameterSeedMask, so that for each considered row tuple,
     *            projectedParameterSeed.equals(parameterSeedMask.transform(row)) should hold. Must not be null.
     * @return the objects in the table for the given key and seed
     * 
     */
    public Iterable<? extends Object> enumerateValues(TupleMask seedMask, ITuple seed);

    /**
     * Simpler form of {@link #enumerateTuples(TupleMask, ITuple)} in the case where all values of the tuples are bound
     * by the seed.
     * 
     * <p>
     * Returns whether the given tuple is in the table identified by the input key.
     * 
     * @param seed
     *            a row tuple of fixed values whose presence in the table is queried
     * @return true iff there is a row tuple contained in the table that corresponds to the given seed
     */
    public boolean containsTuple(ITuple seed);

    public int countTuples(TupleMask seedMask, ITuple seed);

     /**
     * Subscribes for updates in the table, optionally seeded with the given tuple.
     * <p> This should be called after initializing a result cache by an enumeration method.
     *
     * @param seed can be null or a tuple with matching arity;
     *   if non-null, notifications will delivered only about those updates of the table
     *   that match the seed at positions where the seed is non-null.
     * @param listener will be notified of future changes
     * 
     * @since 2.1
     */
     public void addUpdateListener(Tuple seed, IQueryRuntimeContextListener listener);
    
     /**
     * Unsubscribes from updates in the table, optionally seeded with the given tuple.
     *
     * @param seed can be null or a tuple with matching arity;
     *   see {@link #addUpdateListener(Tuple, IQueryRuntimeContextListener)} for definition.
     * @param listener will no longer be notified of future changes
     * 
     * @since 2.1
     */
     public void removeUpdateListener(Tuple seed, IQueryRuntimeContextListener listener);

}
