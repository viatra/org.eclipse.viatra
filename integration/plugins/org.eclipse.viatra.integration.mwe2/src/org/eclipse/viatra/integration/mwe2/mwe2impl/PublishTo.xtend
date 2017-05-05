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
package org.eclipse.viatra.integration.mwe2.mwe2impl

import org.eclipse.viatra.integration.mwe2.IPublishTo
import org.eclipse.viatra.integration.mwe2.IMessageFactory
import org.eclipse.viatra.integration.mwe2.IMessage

/**
 * SubscribeTo objects provide an interface for creating messages and sending them to a given topic in a single call,
 * via binding a Topic and a MessageFactory together
 * 
 * @author Peter Lunk
 */
class PublishTo implements IPublishTo{
    //Broker to which the message will be sent
    extension MessageBroker broker = MessageBroker.instance
    
    private String topicName;
    private IMessageFactory<? extends Object, ? extends IMessage<? extends Object>> factory;
    
    override getFactory() {
        return factory
    }
    
    override getTopicName() {
        return topicName
    }
    
    /**
     * Uses the factory to create a new message instance and sends it to the Topic specified by the topicName attribute.
     */
    override publishMessage(Object parameter) {
        if (factory.isValidParameter(parameter)) {
            try {
                val message = factory.createMessage(parameter);
                sendMessage(topicName, message)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    override setFactory(IMessageFactory<?, ? extends IMessage<?>> factory) {
        this.factory = factory
    }
    
    override setTopicName(String name) {
        this.topicName = name
    }
    
}