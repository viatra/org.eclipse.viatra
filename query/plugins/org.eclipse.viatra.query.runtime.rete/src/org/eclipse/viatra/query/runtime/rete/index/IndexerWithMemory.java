/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.index;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra.query.runtime.matchers.memories.MaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.NetworkStructureChangeSensitiveNode;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.timeless.BehaviorChangingMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.timely.TimelyMailbox;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class IndexerWithMemory extends StandardIndexer
        implements Receiver, NetworkStructureChangeSensitiveNode {

    protected MaskedTupleMemory<Timestamp> memory;

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic logic;

    /**
     * @since 1.6
     */
    protected final Mailbox mailbox;

    /**
     * @param reteContainer
     * @param mask
     */
    public IndexerWithMemory(ReteContainer reteContainer, TupleMask mask) {
        super(reteContainer, mask);
        memory = MaskedTupleMemory.create(mask, MemoryType.SETS, this, reteContainer.isDifferentialDataFlowEvaluation()
                && reteContainer.getCommunicationTracker().isInRecursiveGroup(this));
        reteContainer.registerClearable(memory);
        mailbox = instantiateMailbox();
        reteContainer.registerClearable(mailbox);
        this.logic = createLogic();
    }

    @Override
    public void networkStructureChanged() {
        super.networkStructureChanged();
        final boolean wasTimestampAware = this.memory.isTimely();
        final boolean isTimestampAware = this.reteContainer.isDifferentialDataFlowEvaluation()
                && this.reteContainer.getCommunicationTracker().isInRecursiveGroup(this);
        if (wasTimestampAware != isTimestampAware) {
            final MaskedTupleMemory<Timestamp> newMemory = MaskedTupleMemory.create(mask, MemoryType.SETS, this,
                    isTimestampAware);
            newMemory.initializeWith(this.memory, Timestamp.ZERO);
            memory.clear();
            memory = newMemory;
        }
        this.logic = createLogic();
    }

    /**
     * Instantiates the {@link Mailbox} of this receiver. Subclasses may override this method to provide their own
     * mailbox implementation.
     * 
     * @return the mailbox
     * @since 2.0
     */
    protected Mailbox instantiateMailbox() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()) {
            return new TimelyMailbox(this, this.reteContainer);
        } else {
            return new BehaviorChangingMailbox(this, this.reteContainer);
        }
    }

    @Override
    public Mailbox getMailbox() {
        return this.mailbox;
    }

    /**
     * @since 2.0
     */
    public MaskedTupleMemory<Timestamp> getMemory() {
        return memory;
    }

    @Override
    public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
        this.logic.update(direction, updateElement, timestamp);
    }

    /**
     * Refined version of update
     */
    protected abstract void update(Direction direction, Tuple updateElement, Tuple signature, boolean change,
            Timestamp timestamp);

    @Override
    public void appendParent(Supplier supplier) {
        if (parent == null) {
            parent = supplier;
        } else {
            throw new UnsupportedOperationException("Illegal RETE edge: " + this + " already has a parent (" + parent
                    + ") and cannot connect to additional parent (" + supplier + "). ");
        }
    }

    @Override
    public void removeParent(Supplier supplier) {
        if (parent == supplier) {
            parent = null;
        } else {
            throw new IllegalArgumentException(
                    "Illegal RETE edge removal: the parent of " + this + " is not " + supplier);
        }
    }

    @Override
    public Collection<Supplier> getParents() {
        return Collections.singleton(parent);
    }

    /**
     * @since 2.2
     */
    protected static abstract class NetworkStructureChangeSensitiveLogic {

        public abstract void update(final Direction direction, final Tuple updateElement, final Timestamp timestamp);

    }

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createLogic() {
        if (this.reteContainer.isDifferentialDataFlowEvaluation()
                && this.reteContainer.getCommunicationTracker().isInRecursiveGroup(this)) {
            return createTimelyLogic();
        } else {
            return createTimelessLogic();
        }
    }

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createTimelessLogic() {
        return this.TIMELESS;
    }

    /**
     * @since 2.2
     */
    protected NetworkStructureChangeSensitiveLogic createTimelyLogic() {
        return this.TIMELY;
    }

    private final NetworkStructureChangeSensitiveLogic TIMELY = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
            final Tuple signature = mask.transform(updateElement);
            if (direction == Direction.INSERT) {
                final TimestampReplacement<Timestamp> pair = IndexerWithMemory.this.memory.addWithTimestamp(updateElement,
                        signature, timestamp);
                if (pair.oldValue == null) {
                    // first time we see this tuple
                    IndexerWithMemory.this.update(Direction.INSERT, updateElement, signature, true, timestamp);
                } else if (pair.newValue.compareTo(pair.oldValue) < 0) {
                    // we have a new least timestamp
                    IndexerWithMemory.this.update(Direction.REVOKE, updateElement, signature, true, pair.oldValue);
                    IndexerWithMemory.this.update(Direction.INSERT, updateElement, signature, true, pair.newValue);
                }
            } else {
                final TimestampReplacement<Timestamp> pair = IndexerWithMemory.this.memory.removeWithTimestamp(updateElement,
                        signature, timestamp);
                if (pair.newValue == null) {
                    // we lost the tuple
                    IndexerWithMemory.this.update(Direction.REVOKE, updateElement, signature, true, pair.oldValue);
                } else if (pair.newValue.compareTo(pair.oldValue) > 0) {
                    // we have a new least timestamp
                    IndexerWithMemory.this.update(Direction.REVOKE, updateElement, signature, true, pair.oldValue);
                    IndexerWithMemory.this.update(Direction.INSERT, updateElement, signature, true, pair.newValue);
                }
            }
        }

    };

    private final NetworkStructureChangeSensitiveLogic TIMELESS = new NetworkStructureChangeSensitiveLogic() {

        @Override
        public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
            final Tuple signature = mask.transform(updateElement);
            final boolean change = (direction == Direction.INSERT) ? memory.add(updateElement, signature)
                    : memory.remove(updateElement, signature);
            IndexerWithMemory.this.update(direction, updateElement, signature, change, timestamp);
        }

    };

}