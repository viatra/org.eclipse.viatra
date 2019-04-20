/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.network;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.Direction;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * ALL METHODS: FOR INTERNAL USE ONLY; ONLY INVOKE FROM {@link ReteContainer}
 * 
 * @author Gabor Bergmann
 * @noimplement This interface is not intended to be implemented by external clients.
 */
public interface Receiver extends Node {

    /**
     * updates the receiver with a newly found or lost partial matching
     * @since 2.4
     */
    public void update(final Direction direction, final Tuple updateElement, final Timestamp timestamp);

    /**
     * Returns the {@link Mailbox} of this receiver.
     * 
     * @return the mailbox
     * @since 2.0
     */
    public Mailbox getMailbox();
        
    /**
     * appends a parent that will continuously send insert and revoke updates to this supplier
     */
    void appendParent(final Supplier supplier);

    /**
     * removes a parent
     */
    void removeParent(final Supplier supplier);

    /**
     * access active parent
     */
    Collection<Supplier> getParents();
    
}
