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
package org.eclipse.viatra.query.runtime.rete.network.communication.def;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.RederivableNode;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * A communication group containing only a single node with a single default
 * mailbox.
 * 
 * @author Tamas Szabo
 * @since 1.6
 */
public class SingletonCommunicationGroup extends CommunicationGroup {
    
    private Mailbox mailbox;

    /**
     * @since 1.7
     */
    public SingletonCommunicationGroup(final CommunicationTracker tracker, final Node representative, final int identifier) {
        super(tracker, representative, identifier);
    }

    @Override
    public void deliverMessages() {
        this.mailbox.deliverAll(DefaultSelector.DEFAULT);
    }

    @Override
    public boolean isEmpty() {
        return this.mailbox == null;
    }

    @Override
    public void notifyHasMessage(final Mailbox mailbox, final MessageSelector kind) {
        if (kind == DefaultSelector.DEFAULT) {
            this.mailbox = mailbox;
            if (!this.isEnqueued) {
                this.tracker.activateUnenqueued(this);
            }
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_MESSAGE_KIND + kind);
        }
    }

    @Override
    public void notifyLostAllMessages(final Mailbox mailbox, final MessageSelector kind) {
        if (kind == DefaultSelector.DEFAULT) {
            this.mailbox = null;
            this.tracker.deactivate(this);
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_MESSAGE_KIND + kind);
        }
    }

    @Override
    public void addRederivable(final RederivableNode node) {
        throw new UnsupportedOperationException("Singleton group does not support DRED mode!");
    }

    @Override
    public void removeRederivable(final RederivableNode node) {
        throw new UnsupportedOperationException("Singleton group does not support DRED mode!");
    }

    @Override
    public Collection<RederivableNode> getRederivables() {
        return Collections.emptySet();
    }

    @Override
    public Map<MessageSelector, Collection<Mailbox>> getMailboxes() {
        if (mailbox != null) {
            return Collections.singletonMap(DefaultSelector.DEFAULT, Collections.singleton(mailbox));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean isRecursive() {
        return false;
    }
    
}
