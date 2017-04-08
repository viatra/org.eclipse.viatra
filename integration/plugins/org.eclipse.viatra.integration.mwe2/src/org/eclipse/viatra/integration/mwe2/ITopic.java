/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.mwe2;

import java.util.List;

/**
 * Interface that defines the base functions of a Topic object, which is used to implement message based communication between individual
 * <link>ITransformationStep</link> objects. Transformation steps can add messages to or subscribe to these topics. The creation and
 * procession of these events is done by <link>IMessageFactory</link> and <link>IMessageProcessor</link> objects.
 * 
 * @author Peter Lunk
 *
 */
public interface ITopic {
    
    public String getName();
    public void setName(String name);

    /**
     * Adds the provided message to this topic
     * @param message Message added to this topic
     */
    public void addMessage(IMessage<? extends Object> message);
    
    /**
     * Removes messages assigned to the given transformation step
     * @param message Message to be removed
     * @param sub Specified subscriber
     */
    public void removeMessage(IMessage<?> message, ITransformationStep sub);
    
    /**
     * Removes the given message from every subscriber
     */
    public void removeMessage(IMessage<?> message);
    
    /**
     * Returns every message sent to the given subscriber
     */
    public List<IMessage<? extends Object>> getMessages(ITransformationStep sub);      
    
    /**
     * Adds a new subscriber to this topic
     */
    public void addSubscriber(ITransformationStep subscriber);
    
    /**
     * Returns the subscribers of this topic
     */
    public List<ITransformationStep> getSubscribers();
}
