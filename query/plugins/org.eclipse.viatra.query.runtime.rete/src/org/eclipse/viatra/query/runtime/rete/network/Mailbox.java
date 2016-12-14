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
     * The returned value indicates the effect of the message posting on the mailbox:
     * <ul>
     * <li>The mailbox can become active in response to posting if it had no messages before.</li>
     * <li>The mailbox can become inactive if the newly posted message cancelled out the last message in the mailbox.</li>
     * <li>The mailbox can remain unchanged if there are (still) messages to deliver after the posting of the new message.</li>
     * </ul> 
     * 
     * @param direction the direction of the update
     * @param update the update element
     * @return the effect of the message posting
     */
    public MessagePostEffect postMessage(Direction direction, Tuple update);
    
    /**
     * Delivers all messages to the {@link Receiver} of this mailbox.
     */
    public void deliverMessages();
    
    /**
     * Returns the {@link Receiver} of this mailbox.
     * 
     * @return the receiver
     */
    public Receiver getReceiver();
    
    public enum MessagePostEffect {
        UNCHANGED,
        BECAME_INACTIVE,
        BECAME_ACTIVE
    }
    
}
