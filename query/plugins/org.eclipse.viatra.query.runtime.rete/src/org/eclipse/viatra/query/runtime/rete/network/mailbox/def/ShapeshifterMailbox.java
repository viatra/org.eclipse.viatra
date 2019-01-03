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
package org.eclipse.viatra.query.runtime.rete.network.mailbox.def;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.AdaptableMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.FallThroughMailbox;

/**
 * An adaptive mailbox, which, based on the receiver of this mailbox, either behaves as a
 * {@link DefaultMailbox} or as an {@link UpdateSplittingMailbox}. The decision is made by the
 * {@link CommunicationTracker} based on the position of the mailbox's receiver in the communication network. See
 * {@link CommunicationTracker#refreshSplitFlag(Node)} for more details.
 * 
 * @author Tamas Szabo
 */
public class ShapeshifterMailbox implements FallThroughMailbox {

    protected boolean fallThrough;
    protected boolean split;
    protected AdaptableMailbox wrapped;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected CommunicationGroup group;

    public ShapeshifterMailbox(final Receiver receiver, final ReteContainer container) {
        this.fallThrough = false;
        this.split = false;
        this.receiver = receiver;
        this.container = container;
        this.wrapped = new DefaultMailbox(receiver, container);
        this.wrapped.setAdapter(this);
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update, final DifferentialTimestamp timestamp) {
        if (this.fallThrough && !this.container.isExecutingDelayedCommands()) {
            // disable fall through while we are in the middle of executing delayed construction commands
            this.receiver.update(direction, update, timestamp);
        } else {
            this.wrapped.postMessage(direction, update, timestamp);
        }
    }

    @Override
    public void deliverAll(final MessageSelector kind) {
        this.wrapped.deliverAll(kind);
    }

    @Override
    public String toString() {
        return "A_MBOX -> " + this.wrapped;
    }

    public void setSplitFlag(final boolean splitValue) {
        if (this.split != splitValue) {
            assert isEmpty();
            if (splitValue) {
                this.wrapped = new UpdateSplittingMailbox(this.receiver, this.container);
            } else {
                this.wrapped = new DefaultMailbox(this.receiver, this.container);
            }
            this.wrapped.setAdapter(this);
            this.split = splitValue;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override
    public void clear() {
        this.wrapped.clear();
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

    @Override
    public boolean isFallThrough() {
        return this.fallThrough;
    }
    
    @Override
    public void setFallThrough(final boolean fallThrough) {
        this.fallThrough = fallThrough;
    }
    
}