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

/**
 * Provides an interface for handling topic subscriptions. The objects implementing the ISubscribeTo 
 * interface define a topic name, and an IMessageProcessor object that is repsonsible for processing 
 * the messages contained in the specified topic.  
 * 
 * @author Peter Lunk
 *
 */
public interface ISubscribeTo {

    public String getTopicName();
    public void setTopicName(String name);
    
    public int getPriority() ;
    public void setPriority(String priority);

    public ITransformationStep getParent();
    public void setParent(ITransformationStep parent);

    public IMessageProcessor<? extends Object, ? extends IMessage<? extends Object>> getProcessor();
    public void setProcessor(IMessageProcessor<? extends Object, ? extends IMessage<? extends Object>> processor);

    public void processMessages();

}
