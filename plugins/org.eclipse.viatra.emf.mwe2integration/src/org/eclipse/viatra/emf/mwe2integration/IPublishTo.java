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
package org.eclipse.viatra.emf.mwe2integration;

/**
 * Provides an interface for handling message publishing to a topic. The objects implementing the IPublishTo 
 * interface define a topic name, and an IMessageFactory object that is repsonsible for creating 
 * the messages sent to the specified topic. 
 * 
 * @author Peter Lunk
 *
 */
public interface IPublishTo {
   
    public String getTopicName();
    public void setTopicName(String name);
    
    public IMessageFactory<? extends Object, ? extends IMessage<? extends Object>> getFactory();
    public void setFactory(IMessageFactory<? extends Object, ? extends IMessage<? extends Object>> factory);
    
    public void publishMessage(Object parameter);

}
