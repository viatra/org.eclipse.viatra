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

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import java.util.ArrayList
import java.util.List
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.viatra.integration.mwe2.IPublishTo
import org.eclipse.viatra.integration.mwe2.ISubscribeTo
import org.eclipse.viatra.integration.mwe2.ITransformationStep
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.NoSuchTopicNameException

/**
 * The TransformationStep abstract class implements the ITransformationStep and adds further 
 * basic functions that are typically present in case of (VIATRA based) model transformation steps.
 * 
 * These include the following:
 * 		1. Support for containing ISubscribeTo and IPublishTo objects
 * 		2. Process all incoming messages
 * 		3. Support the addition of user defined execute functionality and message publication
 * 
 * @author Peter Lunk
 */
abstract class TransformationStep implements ITransformationStep {
	/**
	 * Broker used to manage Topic subscriptions
	 */
	extension MessageBroker broker = MessageBroker.instance
	
	protected ListMultimap<Integer, ISubscribeTo> subscribeTo = ArrayListMultimap.create();
	protected List<IPublishTo> publishTo = new ArrayList<IPublishTo>();
	protected IWorkflowContext context;

	def IWorkflowContext getContext() {
		return context;
	}
	
	def void addSubscription(ISubscribeTo sub) {
		//Add subscription and set parent properties
		subscribeTo.put(sub.getPriority(), sub);
		sub.setParent(this);
		sub.processor.parent = this
		broker.subscribeTo(sub.topicName,this)
	}

	def List<ISubscribeTo> getSubscriptions(Integer priority) {
		return subscribeTo.get(priority);
	}

	def List<ISubscribeTo> getSubscriptions() {
		val ret = new ArrayList<ISubscribeTo>();
		ret.addAll(subscribeTo.values());
		return ret;
	}

	def void addPublishing(IPublishTo channel) {
		publishTo.add(channel);
	}

	def List<IPublishTo> getPublishings() {
		return publishTo;
	}

	def ISubscribeTo getSubscription(String topicName) throws NoSuchTopicNameException{
		val subs = subscriptions.filter[it.topicName == topicName]
		if (!subs.isEmpty) {
			return subs.head
		}
		throw new NoSuchTopicNameException
	}

	def IPublishTo getPublishing(String topicName) throws NoSuchTopicNameException{
		val pubs = publishTo.filter[it.topicName == topicName]
		if (!pubs.isEmpty) {
			return pubs.head
		}
		throw new NoSuchTopicNameException
	}

	override execute() {
		processMessages
		doExecute
		publishMessages
	}
	
	def void processMessages() {
        subscriptions.forEach[ 
            processMessages
        ]
    }

    def void publishMessages(){
    	//By default do nothing
    	//Override this method to send parametric messages
    }
	
	def void doExecute()
	
	override initialize(IWorkflowContext ctx) {
		this.context = ctx
		doInitialize(ctx)
	}
	
	def void doInitialize(IWorkflowContext ctx)
	

}