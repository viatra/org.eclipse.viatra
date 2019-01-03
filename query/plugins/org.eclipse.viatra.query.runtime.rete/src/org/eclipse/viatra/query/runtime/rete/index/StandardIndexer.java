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
import java.util.List;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.BaseNode;
import org.eclipse.viatra.query.runtime.rete.network.NetworkStructureChangeSensitiveNode;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * An abstract standard implementation of the Indexer interface, providing common bookkeeping functionality.
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class StandardIndexer extends BaseNode implements Indexer, NetworkStructureChangeSensitiveNode {

    protected Supplier parent;
    protected final List<IndexerListener> originalListeners;
    protected final List<IndexerListener> proxyListeners;
    protected TupleMask mask;

    public StandardIndexer(ReteContainer reteContainer, TupleMask mask) {
        super(reteContainer);
        this.parent = null;
        this.mask = mask;
        this.originalListeners = CollectionsFactory.createObserverList();
        this.proxyListeners = CollectionsFactory.createObserverList();
    }

    protected void propagate(Direction direction, Tuple updateElement, Tuple signature, boolean change, DifferentialTimestamp timestamp) {
        for (IndexerListener listener : proxyListeners) {
            listener.notifyIndexerUpdate(direction, updateElement, signature, change, timestamp);
        }
    }

    @Override
    public TupleMask getMask() {
        return mask;
    }

    @Override
    public Supplier getParent() {
        return parent;
    }

    @Override
    public void attachListener(IndexerListener listener) {
        this.getCommunicationTracker().registerDependency(this, listener.getOwner());
        // obtain the proxy after registering the dependency because then the proxy reflects the new SCC structure
        final IndexerListener proxy = this.getCommunicationTracker().proxifyIndexerListener(this, listener);
        // See Bug 518434
        // Must add to the first position, so that the later listeners are notified earlier.
        // Thus if the beta node added as listener is also an indirect descendant of the same indexer on its opposite slot, 
        // then the beta node is connected later than its ancestor's listener, therefore it will be notified earlier,
        // eliminating duplicate insertions and lost deletions that would result from fall-through update propagation
        this.originalListeners.add(0, listener);
        this.proxyListeners.add(0, proxy);
    }

    @Override
    public void detachListener(IndexerListener listener) {
        // obtain the proxy before unregistering the dependency because that may change SCCs
        final IndexerListener proxy = this.getCommunicationTracker().proxifyIndexerListener(this, listener);
        assert this.originalListeners.remove(listener);
        assert this.proxyListeners.remove(proxy);
        this.getCommunicationTracker().unregisterDependency(this, listener.getOwner());
    }
    
    @Override
    public void networkStructureChanged() {
        this.proxyListeners.clear();
        for (final IndexerListener original : this.originalListeners) {
            this.proxyListeners.add(this.getCommunicationTracker().proxifyIndexerListener(this, original));
        }
    }

    @Override
    public Collection<IndexerListener> getListeners() {
        return proxyListeners;
    }

    @Override
    public ReteContainer getContainer() {
        return reteContainer;
    }

    @Override
    protected String toStringCore() {
        return super.toStringCore() + "(" + parent + "/" + mask + ")";
    }
    
    @Override
    public void assignTraceInfo(TraceInfo traceInfo) {
        super.assignTraceInfo(traceInfo);
        if (traceInfo.propagateFromIndexerToSupplierParent())
            if (parent != null)
                parent.acceptPropagatedTraceInfo(traceInfo);
    }

    
}
