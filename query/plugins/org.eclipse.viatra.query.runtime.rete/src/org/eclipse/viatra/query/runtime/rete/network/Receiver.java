/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.network;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
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
     */
    public void update(final Direction direction, final Tuple updateElement, final DifferentialTimestamp timestamp);

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
