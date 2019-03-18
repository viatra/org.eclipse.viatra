/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.ddf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.ddf.DifferentialMailbox;
import org.eclipse.viatra.query.runtime.rete.util.Options;

/**
 * A differential dataflow-specific communication group. Mailboxes are ordered according to the timestamps of their
 * messages.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DifferentialCommunicationGroup extends CommunicationGroup {

    private final boolean isSingleton;
    private final TreeMap<MessageSelector, Set<Mailbox>> mailboxQueue;
    private boolean currentlyDelivering;
    private MessageSelector currentlyDeliveredTimestamp;
    
    public DifferentialCommunicationGroup(final CommunicationTracker tracker, final Node representative,
            final int identifier, final boolean isSingleton) {
        super(tracker, representative, identifier);
        this.isSingleton = isSingleton;
        this.mailboxQueue = CollectionsFactory.createTreeMap();
        this.currentlyDelivering = false;
    }

    @Override
    public void deliverMessages() {
        this.currentlyDelivering = true;
        while (!this.mailboxQueue.isEmpty()) {
            // care must be taken here how we iterate over the mailboxes
            // it is not okay to loop over the mailboxes at once because a mailbox may disappear from the collection as a result of 
            // delivering messages from another mailboxes under the same timestamp
            // because of this, it is crucial that we pick the mailboxes one by one
            final Entry<MessageSelector, Set<Mailbox>> entry = this.mailboxQueue.firstEntry();
            final MessageSelector timestamp = entry.getKey();
            final Set<Mailbox> mailboxes = entry.getValue();
            final Mailbox mailbox = mailboxes.iterator().next();
            mailboxes.remove(mailbox);
            
            if (mailboxes.isEmpty()) {
                this.mailboxQueue.pollFirstEntry();
            }
            
            assert mailbox instanceof DifferentialMailbox;
            
            if (Options.MONITOR_VIOLATION_OF_DIFFERENTIAL_DATAFLOW_TIMESTAMPS) {                
                this.currentlyDeliveredTimestamp = timestamp;
            }
            mailbox.deliverAll(timestamp);
            if (Options.MONITOR_VIOLATION_OF_DIFFERENTIAL_DATAFLOW_TIMESTAMPS) {                
                this.currentlyDeliveredTimestamp = null;
            }
        }
        this.currentlyDelivering = false;
    }

    @Override
    public boolean isEmpty() {
        return this.mailboxQueue.isEmpty();
    }

    // @SuppressWarnings is added because the IDE otherwise complains about the debug condition
    // as it is too clever to know that this is debug code controlled by the flag
    @SuppressWarnings("unused")
    @Override
    public void notifyHasMessage(final Mailbox mailbox, final MessageSelector kind) {
        if (kind instanceof DifferentialTimestamp) {
            final DifferentialTimestamp timestamp = (DifferentialTimestamp) kind;
            if (Options.MONITOR_VIOLATION_OF_DIFFERENTIAL_DATAFLOW_TIMESTAMPS && this.currentlyDeliveredTimestamp != null) {
                final DifferentialTimestamp first = (DifferentialTimestamp) this.currentlyDeliveredTimestamp;
                if (timestamp.compareTo(first) < 0) {
                    final Logger logger = this.representative.getContainer().getNetwork().getEngine().getLogger();
                    logger.error(
                            "[INTERNAL ERROR] Violation of differential dataflow communication schema! The communication component with representative "
                                    + this.representative + " observed decreasing timestamp during message delivery!");
                }
            }

            Set<Mailbox> mailboxes = this.mailboxQueue.get(timestamp);
            if (mailboxes == null) {
                mailboxes = new HashSet<Mailbox>();
                this.mailboxQueue.put(timestamp, mailboxes);
            }
            mailboxes.add(mailbox);
            if (!this.isEnqueued  && !this.currentlyDelivering) {
                this.tracker.activateUnenqueued(this);
            }
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_MESSAGE_KIND + kind);
        }
    }

    @Override
    public void notifyLostAllMessages(final Mailbox mailbox, final MessageSelector kind) {
        if (kind instanceof DifferentialTimestamp) {
            final Collection<Mailbox> mailboxes = this.mailboxQueue.get(kind);
            assert mailboxes.contains(mailbox);
            mailboxes.remove(mailbox);
            if (mailboxes.isEmpty()) {
                this.mailboxQueue.remove(kind);
            }
            if (this.mailboxQueue.isEmpty()) {
                this.tracker.deactivate(this);
            }
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_MESSAGE_KIND + kind);
        }
    }

    @Override
    public void addRederivable(final RederivableNode node) {
        throw new UnsupportedOperationException("Differential group does not support DRed mode!");
    }

    @Override
    public void removeRederivable(final RederivableNode node) {
        throw new UnsupportedOperationException("Differential group does not support DRed mode!");
    }

    @Override
    public Collection<RederivableNode> getRederivables() {
        return Collections.emptySet();
    }

    @Override
    public Map<MessageSelector, Collection<Mailbox>> getMailboxes() {
        return Collections.unmodifiableMap(this.mailboxQueue);
    }

    @Override
    public boolean isRecursive() {
        return !this.isSingleton;
    }

}
