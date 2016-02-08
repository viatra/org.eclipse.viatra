/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.index;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * A generic Indexer capable of indexing along any valid TupleMask. Does not keep track of parents, because will not
 * ever pull parents.
 * 
 * @author Gabor Bergmann
 * 
 */
public class GenericProjectionIndexer extends IndexerWithMemory implements ProjectionIndexer {

    public GenericProjectionIndexer(ReteContainer reteContainer, TupleMask mask) {
        super(reteContainer, mask);
    }

    @Override
    protected void update(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
        propagate(direction, updateElement, signature, change);
    }

    @Override
    public Collection<Tuple> get(Tuple signature) {
        return memory.get(signature);
    }

    @Override
    public Iterator<Tuple> iterator() {
        return memory.iterator();
    }

    @Override
    public Collection<Tuple> getSignatures() {
        return memory.getSignatures();
    }

    @Override
    public Receiver getActiveNode() {
        return this;
    }

}
