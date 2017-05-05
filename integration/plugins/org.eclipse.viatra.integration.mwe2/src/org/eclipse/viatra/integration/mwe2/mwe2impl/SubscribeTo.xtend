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

import com.google.common.collect.Lists
import org.eclipse.viatra.integration.mwe2.IMessage
import org.eclipse.viatra.integration.mwe2.IMessageProcessor
import org.eclipse.viatra.integration.mwe2.ISubscribeTo
import org.eclipse.viatra.integration.mwe2.ITransformationStep
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException

/**
 * SubscribeTo objects provide an interface for receiving messages from a given topic and processing them in a single call,
 * via binding a Topic and a MessageProcessor together
 * 
 * @author Peter Lunk
 */
class SubscribeTo implements ISubscribeTo{
    //Broker used to fetch messages
    extension MessageBroker broker = MessageBroker.instance
    
    //Priority number
    private int priority = 0;
    private ITransformationStep parent
    private String topicName
    private IMessageProcessor<? extends Object, ? extends IMessage<? extends Object>> processor
    
    
    override getParent() {
        return parent
    }
    
    override getProcessor() {
        return processor
    }
    
    override getTopicName() {
        return topicName
    }
    
    /**
     * Process all unprocessed messages contained by the topic specified in the topicName property
     */
    override processMessages() {
        val messages = broker.getMessages(topicName, parent)
        val msgToRemove = Lists.newArrayList
        messages.forEach[ m|
            try{
                processor.processMessage(m)
                msgToRemove.add(m)
            }catch (InvalidParameterTypeException e){
                e.printStackTrace
            }
        ]
        //only remove the messages if the processing was successful
        msgToRemove.forEach[m|
            broker.removeMessage(topicName, parent, m)
        ]
    }
    
    override setParent(ITransformationStep parent) {
        this.parent = parent
    }
    
    override setProcessor(IMessageProcessor<?, ? extends IMessage<?>> processor) {
        this.processor = processor
    }
    
    override setTopicName(String name) {
        this.topicName = name
    }
    
    override getPriority() {
        return priority
    }
    
    override setPriority(String priority) {
        this.priority = priority
    }
    
}