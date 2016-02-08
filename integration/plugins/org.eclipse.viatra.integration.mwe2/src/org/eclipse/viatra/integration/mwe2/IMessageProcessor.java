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

import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException;

/**
 * Interface that defines the base functions of a message processor. The message processors are responsible for processing
 * certain typed messages. The type of these events is specified in the <Message> template parameter, while the parameter
 * type of these messages is specified in the <ParameterType> template parameter.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType>
 * @param <Message>
 */
public interface IMessageProcessor<ParameterType extends Object, Message extends IMessage<ParameterType>> {

    /**
     * Returns the parent <link>ITransformationStep</link> object of this particular message processor.
     * 
     * @return
     */
    public ITransformationStep getParent();

    /**
     * Sets the parent reference of the message processor to the given <link>ITransformationStep</link>
     * 
     * @param parent
     */
    public void setParent(ITransformationStep parent);

    /**
     * The <link>IMessageProcessor</link> processes the given <link>IMEssage</link>. The additional functionality of the
     * processor is added here.
     * 
     * @param message
     */
    public void processMessage(IMessage<? extends Object> message) throws InvalidParameterTypeException;
}
