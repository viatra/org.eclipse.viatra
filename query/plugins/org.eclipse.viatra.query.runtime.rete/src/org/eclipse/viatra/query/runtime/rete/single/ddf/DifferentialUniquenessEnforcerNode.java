/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single.ddf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Pair;
import org.eclipse.viatra.query.runtime.matchers.util.TimestampAwareMemory;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.SpecializedProjectionIndexer.ListenerSubscription;
import org.eclipse.viatra.query.runtime.rete.index.ddf.DifferentialMemoryIdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ddf.DifferentialMemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.Tunnel;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.ddf.DifferentialMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.def.ShapeshifterMailbox;
import org.eclipse.viatra.query.runtime.rete.single.def.DefaultUniquenessEnforcerNode;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.Options;

/**
 * Ensures that no identical copies get to the output. Only one replica of each pattern substitution may traverse this
 * node. Compared to {@link DefaultUniquenessEnforcerNode}, this node is used in differential dataflow evaluation.
 * 
 * @author Tamas Szabo
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @since 2.2
 */
public class DifferentialUniquenessEnforcerNode extends StandardNode implements Tunnel {

    protected final Collection<Supplier> parents;
    protected final TimestampAwareMemory<DifferentialTimestamp> memory;
    protected ProjectionIndexer memoryNullIndexer;
    protected ProjectionIndexer memoryIdentityIndexer;
    protected final int tupleWidth;
    protected final Mailbox mailbox;
    private final TupleMask nullMask;
    private final TupleMask identityMask;
    protected final List<ListenerSubscription> specializedListeners;

    public DifferentialUniquenessEnforcerNode(final ReteContainer reteContainer, final int tupleWidth) {
        super(reteContainer);
        this.parents = new ArrayList<Supplier>();
        this.specializedListeners = new ArrayList<ListenerSubscription>();
        this.tupleWidth = tupleWidth;
        this.memory = new TimestampAwareMemory<DifferentialTimestamp>();
        reteContainer.registerClearable(this.memory);
        this.nullMask = TupleMask.linear(0, tupleWidth);
        this.identityMask = TupleMask.identity(tupleWidth);
        this.mailbox = instantiateMailbox();
        reteContainer.registerClearable(this.mailbox);
    }

    protected Mailbox instantiateMailbox() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()) {
            return new DifferentialMailbox(this, this.reteContainer);
        } else {
            return new ShapeshifterMailbox(this, this.reteContainer);
        }
    }

    @Override
    public Mailbox getMailbox() {
        return this.mailbox;
    }

    /**
     * The output of the production node should be the smallest timestamp for a given tuple.
     */
    @Override
    public void update(final Direction direction, final Tuple update, final DifferentialTimestamp timestamp) {
        if (direction == Direction.INSERT) {
            final Pair<DifferentialTimestamp, DifferentialTimestamp> pair = this.memory.put(update, timestamp);
            final DifferentialTimestamp oldLeast = pair.first;
            final DifferentialTimestamp newLeast = pair.second;

            if (oldLeast != null) {
                final int comparisonResult = newLeast.compareTo(oldLeast);
                if (comparisonResult > 0) {
                    // it can never happen that we end up with a new least timestamp that 
                    // is greater than the old least as a result of an insertion
                    issueError("[INTERNAL ERROR] Illegal least timestamp detected for " + update + " in "
                            + this.getClass().getName() + " " + this + " for pattern(s) "
                            + getTraceInfoPatternsEnumerated(), null);
                } else if (comparisonResult == 0) {
                    // we do not care about this case - oldLeast is still the least timestamp
                } else {
                    // we have a new least timestamp - delete the old and insert the new
                    propagate(Direction.REVOKE, update, oldLeast);
                    propagate(Direction.INSERT, update, newLeast);
                }
            } else {
                // first time we insert the update
                propagate(Direction.INSERT, update, newLeast);
            }
        } else {
            Pair<DifferentialTimestamp, DifferentialTimestamp> pair = null;
            try {
                pair = this.memory.remove(update, timestamp);
            } catch (final IllegalStateException e) {
                issueError("[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in "
                        + this.getClass().getName() + " " + this + " for pattern(s) "
                        + getTraceInfoPatternsEnumerated(), e);
                // pair will remain unset in case of the exception, it is time to return
                return;
            }
            final DifferentialTimestamp oldLeast = pair.first;
            final DifferentialTimestamp newLeast = pair.second;

            if (newLeast != null) {
                final int comparisonResult = newLeast.compareTo(oldLeast);
                if (comparisonResult < 0) {
                    // it can never happen that we end up with a smaller timestamp as a result of a deletion
                    issueError("[INTERNAL ERROR] Illegal least timestamp detected for " + update + " in "
                            + this.getClass().getName() + " " + this + " for pattern(s) "
                            + getTraceInfoPatternsEnumerated(), null);
                } else if (comparisonResult == 0) {
                    // we do not care about this case - oldLeast is still the least timestamp
                } else {
                    // we lost the least timestamp, but we have a new alternative
                    propagate(Direction.REVOKE, update, oldLeast);
                    propagate(Direction.INSERT, update, newLeast);
                }
            } else {
                // we lost the least timestamp without a new alternative
                propagate(Direction.REVOKE, update, oldLeast);
            }
        }
    }

    protected void propagate(final Direction direction, final Tuple update, final DifferentialTimestamp timestamp) {
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

    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, DifferentialTimestamp> collector, final boolean flush) {
        collector.putAll(this.memory.asMap());
    }
    
    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        for (final Tuple tuple : this.memory.keySet()) {
            collector.add(tuple);
        }
    }

    public ProjectionIndexer getNullIndexer() {
        if (memoryNullIndexer == null) {
            memoryNullIndexer = new DifferentialMemoryNullIndexer(reteContainer, tupleWidth, memory.asMap(), this, this,
                    specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryNullIndexer);
        }
        return memoryNullIndexer;
    }

    public ProjectionIndexer getIdentityIndexer() {
        if (memoryIdentityIndexer == null) {
            memoryIdentityIndexer = new DifferentialMemoryIdentityIndexer(reteContainer, tupleWidth, memory.asMap(),
                    this, this, specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryIdentityIndexer);
        }
        return memoryIdentityIndexer;
    }

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