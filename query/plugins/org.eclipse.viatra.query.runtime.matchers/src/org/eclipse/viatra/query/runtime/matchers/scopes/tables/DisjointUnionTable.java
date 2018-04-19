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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * Disjoint union of the provided child tables.
 * 
 * Used e.g. to present a transitive instance table as a view composed from direct instance tables.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same.
 *
 * @since 2.0
 * @author Gabor Bergmann
 */
public class DisjointUnionTable extends AbstractIndexTable {

    // TODO aggregate notifications from child tables

    protected List<IIndexTable> childTables = CollectionsFactory.createObserverList();

    public DisjointUnionTable(IInputKey inputKey, ITableContext tableContext) {
        super(inputKey, tableContext);
    }

    public List<IIndexTable> getChildTables() {
        return Collections.unmodifiableList(childTables);
    }

    /**
     * Precondition: the new child currently is, and will forever stay, disjoint from any other child tables.
     */
    public void addChildTable(IIndexTable child) {
        if (getInputKey().getArity() != child.getInputKey().getArity())
            throw new IllegalArgumentException(child.toString());

        childTables.add(child);
    }

    @Override
    public int countTuples(TupleMask seedMask, ITuple seed) {
        int count = 0;
        for (IIndexTable child : childTables) {
            count += child.countTuples(seedMask, seed);
        }
        return count;
    }

    @Override
    public Iterable<Tuple> enumerateTuples(TupleMask seedMask, ITuple seed) {
        return () -> {
            Stream<Tuple> stream = Stream.empty();
            for (IIndexTable child : childTables) {
                Iterable<Tuple> childResult = child.enumerateTuples(seedMask, seed);
                stream = Stream.concat(stream, StreamSupport.stream(childResult.spliterator(), false));
            }
            return stream.iterator();
        };
    }

    @Override
    public Iterable<? extends Object> enumerateValues(TupleMask seedMask, ITuple seed) {
        return () -> {
            Stream<Object> stream = Stream.empty();
            for (IIndexTable child : childTables) {
                Iterable<? extends Object> childResult = child.enumerateValues(seedMask, seed);
                stream = Stream.concat(stream, StreamSupport.stream(childResult.spliterator(), false));
            }
            return stream.iterator();
        };
    }

    @Override
    public boolean containsTuple(ITuple seed) {
        for (IIndexTable child : childTables) {
            if (child.containsTuple(seed))
                return true;
        }
        return false;
    }

}
