/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.index;

import java.util.List;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;

/**
 * A specialized projection indexer that can be memory-less (relying on an external source of information).
 * 
 * <p> All specialized projection indexers of a single node will share the same listener list, so
 * that notification order is maintained (see Bug 518434).
 * 
 * @author Gabor Bergmann
 * @noimplement Rely on the provided implementations
 * @noreference Use only via standard Node and Indexer interfaces
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public abstract class SpecializedProjectionIndexer extends StandardIndexer implements ProjectionIndexer {

    protected Node activeNode;
    protected List<ListenerSubscription> sharedSubscriptionList;

    /**
     * @since 1.7
     */
    public SpecializedProjectionIndexer(ReteContainer reteContainer, TupleMask mask, Supplier parent, 
            Node activeNode, List<ListenerSubscription> sharedSubscriptionList) {
        super(reteContainer, mask);
        this.parent = parent;
        this.activeNode = activeNode;
        this.sharedSubscriptionList = sharedSubscriptionList;
    }

    @Override
    public Node getActiveNode() {
        return activeNode;
    }
    
    @Override
    protected void propagate(Direction direction, Tuple updateElement, Tuple signature, boolean change, Timestamp timestamp) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void attachListener(IndexerListener listener) {
        super.attachListener(listener);
        ListenerSubscription subscription = new ListenerSubscription(this, listener);
        // See Bug 518434
        // Must add to the first position, so that the later listeners are notified earlier.
        // Thus if the beta node added as listener is also an indirect descendant of the same indexer on its opposite slot, 
        // then the beta node is connected later than its ancestor's listener, therefore it will be notified earlier,
        // eliminating duplicate insertions and lost deletions that would result from fall-through update propagation
        sharedSubscriptionList.add(0, subscription);
    }
    @Override
    public void detachListener(IndexerListener listener) {
        super.detachListener(listener);
        ListenerSubscription subscription = new ListenerSubscription(this, listener);
        sharedSubscriptionList.remove(subscription);
    }
    
    /**
     * @since 1.7
     */
    public abstract void propagateToListener(IndexerListener listener, Direction direction, Tuple updateElement, Timestamp timestamp);
        
    /** 
     * Infrastructure to share subscriptions between specialized indexers of the same parent node.
     * 
     * @author Gabor Bergmann
     * @since 1.7
     */
    public static class ListenerSubscription {
        protected SpecializedProjectionIndexer indexer;
        protected IndexerListener listener;

        public ListenerSubscription(SpecializedProjectionIndexer indexer, IndexerListener listener) {
            super();
            this.indexer = indexer;
            this.listener = listener;
        }
        
        /**
         * Call this from parent node.
         */
        public void propagate(Direction direction, Tuple updateElement, Timestamp timestamp) {
            indexer.propagateToListener(listener, direction, updateElement, timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(indexer, listener);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ListenerSubscription other = (ListenerSubscription) obj;
            return Objects.equals(listener, other.listener) &&
                    Objects.equals(indexer, other.indexer);
        }
        
        
    }


}
