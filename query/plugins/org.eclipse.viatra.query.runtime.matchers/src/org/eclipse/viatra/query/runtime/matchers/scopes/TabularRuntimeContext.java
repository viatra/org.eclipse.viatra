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

package org.eclipse.viatra.query.runtime.matchers.scopes;

import java.util.Map;
import java.util.Optional;

import org.eclipse.viatra.query.runtime.matchers.context.AbstractQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.IIndexTable;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Accuracy;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * An abstract runtime context that serves enumerable input key instances from tables.
 * 
 * <p>
 * Usage: first, instantiate {@link IIndexTable} tables with this as the 'tableContext' argument. Call
 * {@link #registerIndexTable(IIndexTable)} to register them; this may happen either during a coalesced indexing, or on
 * external initiation. Afterwards, they will be visible to the query backends.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public abstract class TabularRuntimeContext extends AbstractQueryRuntimeContext implements ITableContext {

    private Map<IInputKey, IIndexTable> instanceTables = CollectionsFactory.createMap();

    public void registerIndexTable(IIndexTable table) {
        IInputKey inputKey = table.getInputKey();
        instanceTables.put(inputKey, table);
    }

    /**
     * @return null if the table is not registered
     */
    public IIndexTable peekIndexTable(IInputKey key) {
        return instanceTables.get(key);
    }

    /**
     * If the table is not registered, {@link #handleUnregisteredTableRequest(IInputKey)} is invoked; it may handle it
     * by raising an error or e.g. on-demand index construction
     */
    public IIndexTable getIndexTable(IInputKey key) {
        IIndexTable table = instanceTables.get(key);
        if (table != null)
            return table;
        else
            return handleUnregisteredTableRequest(key);
    }

    /**
     * Override this to provide on-demand table registration
     */
    protected IIndexTable handleUnregisteredTableRequest(IInputKey key) {
        throw new IllegalArgumentException(key.getPrettyPrintableName());
    }

    @Override
    public int countTuples(IInputKey key, TupleMask seedMask, ITuple seed) {
        return getIndexTable(key).countTuples(seedMask, seed);
    }
    
    @Override
    public Optional<Long> estimateCardinality(IInputKey key, TupleMask groupMask, Accuracy requiredAccuracy) {
        return getIndexTable(key).estimateProjectionSize(groupMask, requiredAccuracy);
    }

    @Override
    public Iterable<Tuple> enumerateTuples(IInputKey key, TupleMask seedMask, ITuple seed) {
        return getIndexTable(key).enumerateTuples(seedMask, seed);
    }

    @Override
    public Iterable<? extends Object> enumerateValues(IInputKey key, TupleMask seedMask, ITuple seed) {
        return getIndexTable(key).enumerateValues(seedMask, seed);
    }

    @Override
    public boolean containsTuple(IInputKey key, ITuple seed) {
        if (key.isEnumerable()) {
            return getIndexTable(key).containsTuple(seed);
        } else {
            return isContainedInStatelessKey(key, seed);
        }
    }
    
    @Override
    public void addUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
        getIndexTable(key).addUpdateListener(seed, listener);
    }
    @Override
    public void removeUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
        getIndexTable(key).removeUpdateListener(seed, listener);
    }
    
    /**
     * Handles non-enumerable input keys that are not backed by a table
     */
    protected abstract boolean isContainedInStatelessKey(IInputKey key, ITuple seed);

}
