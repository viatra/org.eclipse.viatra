/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.TimelyMemory;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.index.timely.TimelyMemoryIdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.timely.TimelyMemoryNullIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.timely.TimelyMailbox;

/**
 * Timely uniqueness enforcer node implementation.
 * 
 * @author Tamas Szabo
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @since 2.2
 */
public class TimelyUniquenessEnforcerNode extends AbstractUniquenessEnforcerNode {

    protected final TimelyMemory<Timestamp> memory;
    
    public TimelyUniquenessEnforcerNode(final ReteContainer reteContainer, final int tupleWidth) {
        super(reteContainer, tupleWidth);
        this.memory = new TimelyMemory<Timestamp>();
        reteContainer.registerClearable(this.memory);
        this.mailbox = instantiateMailbox();
        reteContainer.registerClearable(this.mailbox);
    }

    protected Mailbox instantiateMailbox() {
        return new TimelyMailbox(this, this.reteContainer);
    }
    
    @Override
    public Collection<Tuple> getMemory() {
        return this.memory.keySet();
    }

    /**
     * The output of the production node should be the smallest timestamp for a given tuple.
     */
    @Override
    public void update(final Direction direction, final Tuple update, final Timestamp timestamp) {
        if (direction == Direction.INSERT) {
            final TimestampReplacement<Timestamp> pair = this.memory.put(update, timestamp);
            final Timestamp oldLeast = pair.oldValue;
            final Timestamp newLeast = pair.newValue;

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
            TimestampReplacement<Timestamp> pair = null;
            try {
                pair = this.memory.remove(update, timestamp);
            } catch (final IllegalStateException e) {
                issueError("[INTERNAL ERROR] Duplicate deletion of " + update + " was detected in "
                        + this.getClass().getName() + " " + this + " for pattern(s) "
                        + getTraceInfoPatternsEnumerated(), e);
                // pair will remain unset in case of the exception, it is time to return
                return;
            }
            final Timestamp oldLeast = pair.oldValue;
            final Timestamp newLeast = pair.newValue;

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

    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
        collector.putAll(this.memory.asMap());
    }

    @Override
    public ProjectionIndexer getNullIndexer() {
        if (memoryNullIndexer == null) {
            memoryNullIndexer = new TimelyMemoryNullIndexer(reteContainer, tupleWidth, memory, this, this,
                    specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryNullIndexer);
        }
        return memoryNullIndexer;
    }

    @Override
    public ProjectionIndexer getIdentityIndexer() {
        if (memoryIdentityIndexer == null) {
            memoryIdentityIndexer = new TimelyMemoryIdentityIndexer(reteContainer, tupleWidth, memory,
                    this, this, specializedListeners);
            this.getCommunicationTracker().registerDependency(this, memoryIdentityIndexer);
        }
        return memoryIdentityIndexer;
    }

}