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
 * Interface that defines the base functions of the parametric messages being sent between individual
 * <link>ITransformationStep</link> objects. These messages have a parameter defined by the <ParameterType> template
 * parameter. These messages are not used for transferring control from one step to another, only for data transfer.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType> Template parameter which defines the type of the given event's parameter.
 */
public interface IMessage<ParameterType extends Object> {
    
    /**
     * Returns the parameter of the <link>IMessage</link> object.
     * @return The parameter
     */
    public ParameterType getParameter();

    /**
     * Sets the parameter of the <link>IMessage</link> object.
     *  
     * @param parameter New value of the parameter.
     */
    public void setParameter(ParameterType parameter);
}
