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
 * The IEventBroker interface defines methods for receiving and publishing messages from and to any given topic.
 * It also provides methods for managing topics and topic subscriptions.
 * 
 * @author Peter Lunk
 *
 */
public interface IMessageBroker {
	
    /**
     * Subscribes the given transformation step to the Topic whose name matches the @param topicName parameter.
     * @param step
     */
	public void subscribeTo(String topicName, ITransformationStep step);
	
	/**
     * Send the specified message to the topic whose name is matching the @param topicName parameter.
     * @param message
     */
	public void sendMessage(String topicName, IMessage<?> message);
	
	/**
	 * Returns messages sent to the transformation step subscription. 
	 * The @param topicName and  @param step parameters define a subscription.
	 * 
	 * @param topicName
	 * @param step
	 * @return
	 */
	public List<IMessage<?>> getMessages(String topicName, ITransformationStep step);
	
	/**
	 * Removes the message from the queue of the specified subscription.
	 * @param topicName
	 * @param step
	 * @param message
	 */
	public void removeMessage(String topicName, ITransformationStep step, IMessage<?> message);

	
}