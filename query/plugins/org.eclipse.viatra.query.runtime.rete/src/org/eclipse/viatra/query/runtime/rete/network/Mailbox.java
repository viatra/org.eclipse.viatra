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
package org.eclipse.viatra.query.runtime.rete.network;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.tuple.Clearable;

/**
 * A mailbox is associated with every {@link Receiver}. Messages can be sent to a {@link Receiver} by posting 
 * them into the mailbox. Different mailbox implementations may differ in the way how they deliver the posted messages.
 * 
 * @author Tamas Szabo
 *
 */
public interface Mailbox extends Clearable {

    /**
     * Posts a new message to this mailbox.
     * 
     * @param direction the direction of the update
     * @param update the update element
     * @return the effect of the message posting
     */
    public void postMessage(Direction direction, Tuple update);
        
    /**
     * Delivers all messages of the given kind from this mailbox.
     * The kind can also be null. In this case, there no special separation is expected between the messages. 
     *  
     * @param kind the message kind
     */
    public void deliverAll(MessageKind kind);
    
    /**
     * Returns the {@link Receiver} of this mailbox.
     * 
     * @return the receiver
     */
    public Receiver getReceiver();
    
}
