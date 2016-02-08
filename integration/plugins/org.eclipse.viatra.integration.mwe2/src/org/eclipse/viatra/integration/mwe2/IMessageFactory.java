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
 * Interface that defines the base functions of a message factory. These factories are responsible for the creation of
 * certain typed messages. The type of these messages is specified in the <Message> template parameter, while the parameter
 * type of these messages is specified in the <ParameterType> template parameter.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType> Type of the parameter of the messages to be created.
 * @param <Message> Type of the messages to be created.
 */
public interface IMessageFactory<ParameterType extends Object, Message extends IMessage<ParameterType>> {

    /**
     * Creates a message of the type specified by the <Message> parameter.
     * 
     * @param parameter Parameter of the message to be created.
     * @return The created message.
     */
    public Message createMessage(Object parameter) throws InvalidParameterTypeException;

    /**
     * Checks if the given <link>Object</link> is an eligible parameter.
     * 
     * @param parameter <link>Object</link> to be checked.
     * @return Validity of the parameter
     */
    public boolean isValidParameter(Object parameter);
}
