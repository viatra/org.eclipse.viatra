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
package org.eclipse.viatra.integration.mwe2.mwe2impl.messages

import org.eclipse.viatra.integration.mwe2.IMessageFactory
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException

/**
 * Message Factory that is responsible for the creation of StringMessage objects.
 * @author Peter Lunk
 *
 */
class StringMessageFactory implements IMessageFactory<String,StringMessage>{
	
	/**
	 * Creates a StringMessage using the provided parameter. If the parameter is of a wrong type, it throws an InvalidParameterTypeException
	 */
	override createMessage(Object parameter) throws InvalidParameterTypeException{
		if(parameter.isValidParameter){
			return new StringMessage(parameter as String)
		}
		throw new InvalidParameterTypeException
	}
	
	/**
	 * Checks if the type of the given parameter matches the parameter type of StringMessage
	 */
	override isValidParameter(Object parameter) {
		if(parameter instanceof String){
			return true
		}
		return false;
	}
	
}