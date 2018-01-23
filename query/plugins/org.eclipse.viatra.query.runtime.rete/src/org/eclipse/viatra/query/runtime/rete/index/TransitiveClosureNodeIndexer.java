/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.single.TransitiveClosureNode;
import org.eclipse.viatra.query.runtime.rete.tuple.MaskedTuple;

// UNFINISHED, not used yet
public class TransitiveClosureNodeIndexer extends StandardIndexer implements IterableIndexer {
    private TransitiveClosureNode tcNode;
    private IncSCCAlg<Object> tcAlg;
    private Collection<Tuple> emptySet;

    public TransitiveClosureNodeIndexer(TupleMask mask, IncSCCAlg<Object> tcAlg, TransitiveClosureNode tcNode) {
        super(tcNode.getContainer(), mask);
        this.tcAlg = tcAlg;
        this.tcNode = tcNode;
        this.emptySet = Collections.emptySet();
        this.parent = tcNode;
    }

    @Override
    public Collection<Tuple> get(Tuple signature) {
        if (signature.getSize() == mask.sourceWidth) {
            if (mask.indices.length == 0) {
                // mask ()/2
                return getSignatures();
            } else if (mask.indices.length == 1) {
                Set<Tuple> retSet = CollectionsFactory.createSet();

                // mask (0)/2
                if (mask.indices[0] == 0) {
                    Object source = signature.get(0);
                    for (Object target : tcAlg.getAllReachableTargets(source)) {
                        retSet.add(Tuples.staticArityFlatTupleOf(source, target));
                    }
                    return retSet;
                }
                // mask (1)/2
                if (mask.indices[0] == 1) {
                    Object target = signature.get(1);
                    for (Object source : tcAlg.getAllReachableSources(target)) {
                        retSet.add(Tuples.staticArityFlatTupleOf(source, target));
                    }
                    return retSet;
                }
            } else {
                // mask (0,1)/2
                if (mask.indices[0] == 0 && mask.indices[1] == 1) {
                    Object source = signature.get(0);
                    Object target = signature.get(1);
                    Tuple singleton = Tuples.staticArityFlatTupleOf(source, target);
                    return (tcAlg.isReachable(source, target) ? Collections.singleton(singleton) : emptySet);
                }
                // mask (1,0)/2
                if (mask.indices[0] == 1 && mask.indices[1] == 0) {
                    Object source = signature.get(1);
                    Object target = signature.get(0);
                    Tuple singleton = Tuples.staticArityFlatTupleOf(source, target);
                    return (tcAlg.isReachable(source, target) ? Collections.singleton(singleton) : emptySet);
                }
            }
        }
        return null;
    }

    @Override
    public int getBucketCount() {
        throw new UnsupportedOperationException();
    }
    
    public Collection<Tuple> getSignatures() {
        return asTupleCollection(tcAlg.getTcRelation());
    }

    public Iterator<Tuple> iterator() {
        return asTupleCollection(tcAlg.getTcRelation()).iterator();
    }

    private Collection<Tuple> asTupleCollection(
            Collection<org.eclipse.viatra.query.runtime.base.itc.alg.misc.Tuple<Object>> tuples) {
        Set<Tuple> retSet = CollectionsFactory.createSet();
        for (org.eclipse.viatra.query.runtime.base.itc.alg.misc.Tuple<Object> tuple : tuples) {
            retSet.add(Tuples.staticArityFlatTupleOf(tuple.getSource(), tuple.getTarget()));
        }
        return retSet;
    }

    public void propagate(Direction direction, Tuple updateElement, boolean change) {
        propagate(direction, updateElement, new MaskedTuple(updateElement, mask), change);
    }

    @Override
    public Receiver getActiveNode() {
        return tcNode;
    }
    
}
