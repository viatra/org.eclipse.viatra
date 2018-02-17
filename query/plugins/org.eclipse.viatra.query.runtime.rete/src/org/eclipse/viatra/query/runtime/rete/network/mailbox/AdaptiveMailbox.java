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

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.MessageKind;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * An adaptive mailbox implementation. Based on the receiver of this mailbox, it either behaves as a
 * {@link DefaultMailbox} or as an {@link UpdateSplittingMailbox}. The decision is made by the
 * {@link CommunicationTracker} based on the position of the mailbox's receiver in the communication network. See
 * {@link CommunicationTracker#refreshSplitFlag(Node)} for more details.
 * 
 * @author Tamas Szabo
 */
public class AdaptiveMailbox implements Mailbox {

    protected boolean fallThrough;
    protected boolean split;
    protected AdaptableMailbox wrappedMailbox;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected CommunicationGroup group;

    public AdaptiveMailbox(final Receiver receiver, final ReteContainer container) {
        this.fallThrough = false;
        this.split = false;
        this.receiver = receiver;
        this.container = container;
        this.wrappedMailbox = new DefaultMailbox(receiver, container);
        this.wrappedMailbox.setAdapter(this);
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update) {
        if (this.fallThrough) {
            this.receiver.update(direction, update);
        } else {
            this.wrappedMailbox.postMessage(direction, update);
        }
    }

    @Override
    public void deliverAll(final MessageKind kind) {
        this.wrappedMailbox.deliverAll(kind);
    }

    @Override
    public String toString() {
        return "A_MBOX -> " + this.wrappedMailbox;
    }

    public boolean isFallThrough() {
        return this.fallThrough;
    }

    /**
     * Controlled by the {@link CommunicationTracker} which can determine based on node type and network topology
     * whether fall-through is allowed.
     */
    public void setFallThrough(final boolean fallThrough) {
        this.fallThrough = fallThrough;
    }

    public void setSplitFlag(final boolean splitValue) {
        if (this.split != splitValue) {
            assert isEmpty();
            if (splitValue) {
                this.wrappedMailbox = new UpdateSplittingMailbox(this.receiver, this.container);
            } else {
                this.wrappedMailbox = new DefaultMailbox(this.receiver, this.container);
            }
            this.wrappedMailbox.setAdapter(this);
            this.split = splitValue;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.wrappedMailbox.isEmpty();
    }

    @Override
    public void clear() {
        this.wrappedMailbox.clear();
    }

    @Override
    public Receiver getReceiver() {
        return this.receiver;
    }

    @Override
    public CommunicationGroup getCurrentGroup() {
        return this.group;
    }

    @Override
    public void setCurrentGroup(final CommunicationGroup group) {
        this.group = group;
    }

}
