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
import com.google.common.collect.Maps
import java.util.List
import java.util.Map
import org.eclipse.viatra.integration.mwe2.IMessage
import org.eclipse.viatra.integration.mwe2.ITopic
import org.eclipse.viatra.integration.mwe2.ITransformationStep

/**
 *  Topics contain subscribing transformation steps and messages sent to these subscribers.
 *  Supports the addition of new subscribers and messages. To remove processed messages, 
 *  transformation steps have to call the removeMessage method, this way ensuring that no message 
 *  gets lost due a failure during processing
 * 
 *  This implementation can be used with most of the workflows that can be described using this library.
 * 
 * @author Peter Lunk
 */
class Topic implements ITopic{
    
    protected String name
    protected Map<ITransformationStep, List<IMessage<?>>> subscriberMap
    
    new(String name){
        this.name = name
        subscriberMap = Maps.newHashMap
    }
    
    override addMessage(IMessage<?> message){
        subscriberMap.keySet.forEach[k |
            subscriberMap.get(k).add(message)
        ]
    }
    
    override addSubscriber(ITransformationStep subscriber) {
        subscriberMap.put(subscriber, Lists.newArrayList)
    }
    
    override getMessages(ITransformationStep sub) {
        return subscriberMap.get(sub)
    }
    
    override getName() {
        return name
    }
    
    override getSubscribers() {
        return subscriberMap.keySet.toList
    }
    
    override setName(String name) {
        this.name = name
    }
    
    override removeMessage(IMessage<?> message){
        subscriberMap.keySet.forEach[k |
            subscriberMap.get(k).remove(message)
        ]
    }
    
    /**
     * Removes the message from the specified step. This is used to inform 
     * the topic that the message processing was successful and the message 
     * is no longer needed.
     */
    override removeMessage(IMessage<?> message, ITransformationStep sub) {
        subscriberMap.get(sub).remove(message)
    }	
}