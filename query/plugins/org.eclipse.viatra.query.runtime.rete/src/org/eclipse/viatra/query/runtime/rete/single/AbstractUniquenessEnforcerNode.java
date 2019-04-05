/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, itemis AG, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.SpecializedProjectionIndexer.ListenerSubscription;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.Options;

/**
 * Ensures that no identical copies get to the output. Only one replica of each pattern substitution may traverse this
 * node.
 * 
 * @author Gabor Bergmann
 * @author Tamas Szabo
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @since 2.2
 */
public abstract class AbstractUniquenessEnforcerNode extends StandardNode implements Tunnel {

    protected final Collection<Supplier> parents;
    protected ProjectionIndexer memoryNullIndexer;
    protected ProjectionIndexer memoryIdentityIndexer;
    protected final int tupleWidth;
    // MUST BE INSTANTIATED IN THE CONCRETE SUBCLASSES AFTER ALL FIELDS ARE SET 
    protected Mailbox mailbox;
    protected final TupleMask nullMask;
    protected final TupleMask identityMask;
    protected final List<ListenerSubscription> specializedListeners;

    public AbstractUniquenessEnforcerNode(final ReteContainer reteContainer, final int tupleWidth) {
        super(reteContainer);
        this.parents = new ArrayList<Supplier>();
        this.specializedListeners = new ArrayList<ListenerSubscription>();
        this.tupleWidth = tupleWidth;
        this.nullMask = TupleMask.linear(0, tupleWidth);
        this.identityMask = TupleMask.identity(tupleWidth);
    }

    protected abstract Mailbox instantiateMailbox();

    @Override
    public Mailbox getMailbox() {
        return this.mailbox;
    }

    protected void propagate(final Direction direction, final Tuple update, final Timestamp timestamp) {
        // See Bug 518434
        // trivial (non-active) indexers must be updated before other listeners
        // so that if they are joined against each other, trivial indexers lookups
        // will be consistent with their notifications;
        // also, their subscriptions must share a single order
        for (final ListenerSubscription subscription : specializedListeners) {
            subscription.propagate(direction, update, timestamp);
        }
        propagateUpdate(direction, update, timestamp);
    }

    @Override
    public ProjectionIndexer constructIndex(final TupleMask mask, final TraceInfo... traces) {
        if (Options.employTrivialIndexers) {
            if (nullMask.equals(mask)) {
                final ProjectionIndexer indexer = getNullIndexer();
                for (final TraceInfo traceInfo : traces) {
                    indexer.assignTraceInfo(traceInfo);
                }
                return indexer;
            }
            if (identityMask.equals(mask)) {
                final ProjectionIndexer indexer = getIdentityIndexer();
                for (final TraceInfo traceInfo : traces) {
                    indexer.assignTraceInfo(traceInfo);
                }
                return indexer;
            }
        }
        return super.constructIndex(mask, traces);
    }
    
    /**
     * @since 2.2
     */
    public abstract Collection<Tuple> getMemory();

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        for (final Tuple tuple : this.getMemory()) {
            collector.add(tuple);
        }
    }

    public abstract ProjectionIndexer getNullIndexer();

    public abstract ProjectionIndexer getIdentityIndexer();

    @Override
    public void appendParent(final Supplier supplier) {
        parents.add(supplier);
    }

    @Override
    public void removeParent(final Supplier supplier) {
        parents.remove(supplier);
    }

    @Override
    public Collection<Supplier> getParents() {
        return parents;
    }

    @Override
    public void assignTraceInfo(final TraceInfo traceInfo) {
        super.assignTraceInfo(traceInfo);
        if (traceInfo.propagateFromStandardNodeToSupplierParent()) {
            for (final Supplier parent : parents) {
                parent.acceptPropagatedTraceInfo(traceInfo);
            }
        }
    }

}