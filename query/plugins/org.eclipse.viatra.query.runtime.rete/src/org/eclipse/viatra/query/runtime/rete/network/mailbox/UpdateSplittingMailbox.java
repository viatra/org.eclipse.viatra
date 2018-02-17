/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.mailbox;

import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.MessageKind;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.indexer.DefaultMessageIndexer;

/**
 * A mailbox implementation that splits updates messages according to the standard subset ordering into anti-monotonic
 * (deletions) and monotonic (insertions) updates.
 * 
 * @author Tamas Szabo
 */
public class UpdateSplittingMailbox extends AbstractUpdateSplittingMailbox<DefaultMessageIndexer, Receiver>
        implements AdaptableMailbox {

    protected Mailbox adapter;

    public UpdateSplittingMailbox(final Receiver receiver, final ReteContainer container) {
        super(receiver, container, new MessageIndexerFactory<DefaultMessageIndexer>() {
            @Override
            public DefaultMessageIndexer create() {
                return new DefaultMessageIndexer();
            }
        });
    }

    @Override
    public Mailbox getAdapter() {
        return this.adapter;
    }

    @Override
    public void setAdapter(final Mailbox adapter) {
        this.adapter = adapter;
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update) {
        final DefaultMessageIndexer monotoneQueue = getActiveMonotoneQueue();
        final DefaultMessageIndexer antiMonotoneQueue = getActiveAntiMonotoneQueue();
        final boolean wasPresentAsMonotone = monotoneQueue.getCount(update) != 0;
        final boolean wasPresentAsAntiMonotone = antiMonotoneQueue.getCount(update) != 0;

        // it cannot happen that it was present in both
        assert !(wasPresentAsMonotone && wasPresentAsAntiMonotone);

        if (direction == Direction.INSERT) {
            if (wasPresentAsAntiMonotone) {
                // it was an anti-monotone one before
                antiMonotoneQueue.insert(update);
            } else {
                // it was a monotone one before or did not exist at all
                monotoneQueue.insert(update);
            }
        } else {
            if (wasPresentAsMonotone) {
                // it was a monotone one before
                monotoneQueue.delete(update);
            } else {
                // it was an anti-monotone one before or did not exist at all
                antiMonotoneQueue.delete(update);
            }
        }

        final CommunicationGroup targetGroup = this.getAdapter() != null ? this.getAdapter().getCurrentGroup()
                : this.getCurrentGroup();
        final Mailbox targetMailbox = this.getAdapter() != null ? this.getAdapter() : this;

        if (antiMonotoneQueue.isEmpty()) {
            targetGroup.notifyLostAllMessages(targetMailbox, MessageKind.ANTI_MONOTONE);
        } else {
            targetGroup.notifyHasMessage(targetMailbox, MessageKind.ANTI_MONOTONE);
        }

        if (monotoneQueue.isEmpty()) {
            targetGroup.notifyLostAllMessages(targetMailbox, MessageKind.MONOTONE);
        } else {
            targetGroup.notifyHasMessage(targetMailbox, MessageKind.MONOTONE);
        }
    }

    @Override
    public void deliverAll(final MessageKind kind) {
        if (kind == MessageKind.ANTI_MONOTONE) {
            // deliver anti-monotone
            this.deliveringAntiMonotone = true;
            for (final Entry<Tuple, Integer> entry : this.antiMonotoneQueue.getTuples().entrySet()) {
                final Tuple update = entry.getKey();
                final int count = entry.getValue();
                assert count < 0;
                for (int i = 0; i < Math.abs(count); i++) {
                    this.receiver.update(Direction.REVOKE, update);
                }
            }
            this.deliveringAntiMonotone = false;
            swapAndClearAntiMonotone();
        } else if (kind == MessageKind.MONOTONE) {
            // deliver monotone
            this.deliveringMonotone = true;
            for (final Entry<Tuple, Integer> entry : this.monotoneQueue.getTuples().entrySet()) {
                final Tuple update = entry.getKey();
                final int count = entry.getValue();
                assert count > 0;
                for (int i = 0; i < count; i++) {
                    this.receiver.update(Direction.INSERT, update);
                }
            }
            this.deliveringMonotone = false;
            swapAndClearMonotone();
        } else {
            throw new IllegalArgumentException("Unsupported message kind " + kind);
        }
    }

    @Override
    public String toString() {
        return "US_MBOX (" + this.receiver + ") " + this.getActiveMonotoneQueue() + " "
                + this.getActiveAntiMonotoneQueue();
    }

}
