/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.def;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * A communication group representing either a single node where the
 * node is a monotonicity aware one or a set of nodes that form an SCC. 
 * 
 * @author Tamas Szabo
 * @since 1.6
 */
public class RecursiveCommunicationGroup extends CommunicationGroup {

    private final Set<Mailbox> antiMonotoneMailboxes;
    private final Set<Mailbox> monotoneMailboxes;
    private final Set<Mailbox> defaultMailboxes;
    private final Set<RederivableNode> rederivables;
    private boolean currentlyDelivering;

    /**
     * @since 1.7
     */
    public RecursiveCommunicationGroup(final CommunicationTracker tracker, final Node representative, final int identifier) {
        super(tracker, representative, identifier);
        this.antiMonotoneMailboxes = CollectionsFactory.createSet();
        this.monotoneMailboxes = CollectionsFactory.createSet();
        this.defaultMailboxes = CollectionsFactory.createSet();
        this.rederivables = new LinkedHashSet<RederivableNode>();
        this.currentlyDelivering = false;
    }

    @Override
    public void deliverMessages() {
        this.currentlyDelivering = true;
        
        // ANTI-MONOTONE PHASE
        while (!this.antiMonotoneMailboxes.isEmpty() || !this.defaultMailboxes.isEmpty()) {
            while (!this.antiMonotoneMailboxes.isEmpty()) {
                final Mailbox mailbox = this.antiMonotoneMailboxes.iterator().next();
                this.antiMonotoneMailboxes.remove(mailbox);
                mailbox.deliverAll(DefaultSelector.ANTI_MONOTONE);
            }
            while (!this.defaultMailboxes.isEmpty()) {
                final Mailbox mailbox = this.defaultMailboxes.iterator().next();
                this.defaultMailboxes.remove(mailbox);
                mailbox.deliverAll(DefaultSelector.DEFAULT);
            }
        }

        // REDERIVE PHASE
        while (!this.rederivables.isEmpty()) {
            // re-derivable nodes take care of their unregistration!!
            final RederivableNode node = this.rederivables.iterator().next();
            node.rederiveOne();
        }

        // MONOTONE PHASE
        while (!this.monotoneMailboxes.isEmpty() || !this.defaultMailboxes.isEmpty()) {
            while (!this.monotoneMailboxes.isEmpty()) {
                final Mailbox mailbox = this.monotoneMailboxes.iterator().next();
                this.monotoneMailboxes.remove(mailbox);
                mailbox.deliverAll(DefaultSelector.MONOTONE);
            }
            while (!this.defaultMailboxes.isEmpty()) {
                final Mailbox mailbox = this.defaultMailboxes.iterator().next();
                this.defaultMailboxes.remove(mailbox);
                mailbox.deliverAll(DefaultSelector.DEFAULT);
            }
        }
        
        this.currentlyDelivering = false;
    }

    @Override
    public boolean isEmpty() {
        return this.rederivables.isEmpty() && this.antiMonotoneMailboxes.isEmpty()
                && this.monotoneMailboxes.isEmpty() && this.defaultMailboxes.isEmpty();
    }

    @Override
    public void notifyHasMessage(final Mailbox mailbox, final MessageSelector kind) {
        final Collection<Mailbox> mailboxes = getMailboxContainer(kind);
        mailboxes.add(mailbox);
        if (!this.isEnqueued && !this.currentlyDelivering) {
            this.tracker.activateUnenqueued(this);
        }
    }

    @Override
    public void notifyLostAllMessages(final Mailbox mailbox, final MessageSelector kind) {
        final Collection<Mailbox> mailboxes = getMailboxContainer(kind);
        mailboxes.remove(mailbox);
        if (isEmpty()) {
            this.tracker.deactivate(this);
        }
    }

    private Collection<Mailbox> getMailboxContainer(final MessageSelector kind) {
        if (kind == DefaultSelector.ANTI_MONOTONE) {
            return this.antiMonotoneMailboxes;
        } else if (kind == DefaultSelector.MONOTONE) {
            return this.monotoneMailboxes;
        } else if (kind == DefaultSelector.DEFAULT) {
            return this.defaultMailboxes;
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_MESSAGE_KIND + kind);
        }
    }

    @Override
    public void addRederivable(final RederivableNode node) {
        this.rederivables.add(node);
        if (!this.isEnqueued) {
            this.tracker.activateUnenqueued(this);
        }
    }

    @Override
    public void removeRederivable(final RederivableNode node) {
        this.rederivables.remove(node);
        if (isEmpty()) {
            this.tracker.deactivate(this);
        }
    }

    @Override
    public Collection<RederivableNode> getRederivables() {
        return this.rederivables;
    }

    @Override
    public Map<MessageSelector, Collection<Mailbox>> getMailboxes() {
        Map<DefaultSelector, Collection<Mailbox>> map = new EnumMap<>(DefaultSelector.class);
        map.put(DefaultSelector.ANTI_MONOTONE, antiMonotoneMailboxes);
        map.put(DefaultSelector.MONOTONE, monotoneMailboxes);
        map.put(DefaultSelector.DEFAULT, defaultMailboxes);
        return Collections.unmodifiableMap(map);
    }
    
    @Override
    public boolean isRecursive() {
        return true;
    }
    
}
