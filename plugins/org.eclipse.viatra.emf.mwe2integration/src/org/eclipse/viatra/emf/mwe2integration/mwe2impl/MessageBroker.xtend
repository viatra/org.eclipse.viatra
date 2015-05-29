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
package org.eclipse.viatra.emf.mwe2integration.mwe2impl

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.viatra.emf.mwe2integration.IMessage
import org.eclipse.viatra.emf.mwe2integration.ITopic
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep
import org.eclipse.viatra.emf.mwe2integration.IMessageBroker

/**
 * The MessageBroker is a singleton class that manages topics and subscriptions.
 * 
 * @author Peter Lunk
 */
class MessageBroker implements IMessageBroker{
	
	protected static  MessageBroker broker
	protected List<ITopic> topics
	
	protected new(){
		topics = Lists.newArrayList
	}
	
	public static def getInstance(){
		if(broker == null){
			broker = new MessageBroker
		}
		return broker
	}
		
	override subscribeTo(String topicName, ITransformationStep step) {
		getTopic(topicName).addSubscriber(step)
	}

	
	override sendMessage(String topicName, IMessage<?> message) {
		val topic = getTopic(topicName)
		topic.addMessage(message)
	}
	
	/**
	 * Returns all messages sent to a topic that have not been processed by the specified transformation step.
	 */
	override List<IMessage<?>> getMessages(String topicName, ITransformationStep step) {
		val topic = getTopic(topicName)
		topic.getMessages(step)
	}
	
	/**
	 * Removes the specified message from the specified topic and the specified subscriber queue.
	 * It can be used to inform the broker and the topic that the given transformation step has 
	 * finished processing the specified message, and therefore it is no longer needed.
	 */
	override void removeMessage(String topicName, ITransformationStep step, IMessage<?> message) {
		val topic = getTopic(topicName)
		topic.removeMessage(message, step)

	}
	
	protected def getTopic(String topicName){
		var ITopic retVal;
		val reducedList = topics.filter[name == topicName]
		if(reducedList.isEmpty){
			retVal = new Topic(topicName)
			topics.add(retVal)
		}else{
			retVal = reducedList.head
		}
		return retVal
	}
	
	protected def void removeTopic(String topicName){
		val reducedList = topics.filter[name == topicName]
		if(!reducedList.isEmpty){
			topics.removeAll(reducedList)
		}
	}	
}