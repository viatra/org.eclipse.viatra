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
package org.eclipse.viatra.query.runtime.rete.single;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.Tuple;
import org.eclipse.viatra.query.runtime.base.itc.graphimpl.Graph;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcObserver;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.tuple.Clearable;

// TODO egyelore (i,j) elek, majd helyette mask megoldas
// TODO bemeneti index
/**
 * This class represents a transitive closure node in the rete net.
 * 
 * @author Gabor Bergmann
 * 
 */
public class TransitiveClosureNode extends SingleInputNode implements Clearable, ITcObserver<Object> {

    private Graph<Object> graphDataSource;
    private ITcDataSource<Object> transitiveClosureAlgorithm;

    /**
     * Create a new transitive closure rete node. 
     * 
     * Client may optionally call {@link #reinitializeWith(Collection)} before using the node, 
     * instead of inserting the initial set of tuples one by one.
     * 
     * @param reteContainer
     *            the rete container of the node
     */
    public TransitiveClosureNode(ReteContainer reteContainer) {
        super(reteContainer);
        graphDataSource = new Graph<Object>();
        transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
        transitiveClosureAlgorithm.attachObserver(this);
        reteContainer.registerClearable(this);
    }
    
    /**
     * Initializes the graph data source with the given collection of tuples.
     * @param tuples
     *            the initial collection of tuples
     */
    public void reinitializeWith(Collection<org.eclipse.viatra.query.runtime.matchers.tuple.Tuple> tuples) {
        clear();
        
        for (org.eclipse.viatra.query.runtime.matchers.tuple.Tuple t : tuples) {
            graphDataSource.insertNode(t.get(0));
            graphDataSource.insertNode(t.get(1));
            graphDataSource.insertEdge(t.get(0), t.get(1));
        }
        transitiveClosureAlgorithm.attachObserver(this);    	
    }

    @Override
    public void pullInto(Collection<org.eclipse.viatra.query.runtime.matchers.tuple.Tuple> collector) {
        for (Tuple<Object> tuple : ((IncSCCAlg<Object>) transitiveClosureAlgorithm).getTcRelation()) {
            collector.add(new FlatTuple(tuple.getSource(), tuple.getTarget()));
        }
    }

    @Override
    public void update(Direction direction, org.eclipse.viatra.query.runtime.matchers.tuple.Tuple updateElement) {
        if (updateElement.getSize() == 2) {
            Object source = updateElement.get(0);
            Object target = updateElement.get(1);

            if (direction == Direction.INSERT) {
                graphDataSource.insertNode(source);
                graphDataSource.insertNode(target);
                graphDataSource.insertEdge(source, target);
            }
            if (direction == Direction.REVOKE) {
                graphDataSource.deleteEdge(source, target);

                if (((IncSCCAlg<Object>) transitiveClosureAlgorithm).isIsolated(source)) {
                    graphDataSource.deleteNode(source);
                }
                if (!source.equals(target) && ((IncSCCAlg<Object>) transitiveClosureAlgorithm).isIsolated(target)) {
                    graphDataSource.deleteNode(target);
                }
            }
        }
    }

    @Override
    public void clear() {
        transitiveClosureAlgorithm.dispose();
        graphDataSource = new Graph<Object>();
        transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
    }

    @Override
    public void tupleInserted(Object source, Object target) {
        org.eclipse.viatra.query.runtime.matchers.tuple.Tuple tuple = new FlatTuple(source, target);
        propagateUpdate(Direction.INSERT, tuple);
    }

    @Override
    public void tupleDeleted(Object source, Object target) {
        org.eclipse.viatra.query.runtime.matchers.tuple.Tuple tuple = new FlatTuple(source, target);
        propagateUpdate(Direction.REVOKE, tuple);
    }

    @Override
    protected void propagateUpdate(Direction direction, org.eclipse.viatra.query.runtime.matchers.tuple.Tuple updateElement) {
        super.propagateUpdate(direction, updateElement);
    }
}
