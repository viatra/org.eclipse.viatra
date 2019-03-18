/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.index.GenericProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * Base implementation for a supplier node.
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class StandardNode extends BaseNode implements Supplier, NetworkStructureChangeSensitiveNode {
    protected final List<Receiver> children = CollectionsFactory.createObserverList();
    /**
     * @since 2.2
     */
    protected final Map<Receiver, Mailbox> childMailboxes = CollectionsFactory.createMap();

    public StandardNode(ReteContainer reteContainer) {
        super(reteContainer);
    }
    
    /**
     * @since 2.2
     */
    protected void propagateUpdate(Direction direction, Tuple updateElement, DifferentialTimestamp timestamp) {
        for (Mailbox childMailbox : childMailboxes.values())
            childMailbox.postMessage(direction, updateElement, timestamp);            
    }

    @Override
    public void appendChild(Receiver receiver) {
        children.add(receiver);
        childMailboxes.put(receiver, this.getCommunicationTracker().proxifyMailbox(this, receiver.getMailbox()));
    }

    @Override
    public void removeChild(Receiver receiver) {
        children.remove(receiver);
        childMailboxes.remove(receiver);
    }
    
    @Override
    public void networkStructureChanged() {
        childMailboxes.clear();
        for (Receiver receiver : children) {
            childMailboxes.put(receiver, this.getCommunicationTracker().proxifyMailbox(this, receiver.getMailbox()));
        }
    }

    @Override
    public Collection<Receiver> getReceivers() {
        return children;
    }
    
    /**
     * @since 2.2
     */
    public Collection<Mailbox> getChildMailboxes() {
        return this.childMailboxes.values();
    }
    
    @Override
    public Set<Tuple> getPulledContents(boolean flush) {
        HashSet<Tuple> results = new HashSet<Tuple>();
        pullInto(results, flush);
        return results;
    }

    @Override
    public ProjectionIndexer constructIndex(TupleMask mask, TraceInfo... traces) {
        final GenericProjectionIndexer indexer = new GenericProjectionIndexer(reteContainer, mask);
        for (TraceInfo traceInfo : traces) indexer.assignTraceInfo(traceInfo);
        reteContainer.connectAndSynchronize(this, indexer);
        return indexer;
    }
    
    /**
     * @since 1.6
     */
    protected void issueError(String message, Exception ex) {
        if (ex == null) {
            this.reteContainer.getNetwork().getEngine().getLogger().error(message);
        } else {
            this.reteContainer.getNetwork().getEngine().getLogger().error(message, ex);
        }
    }

}
