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
package org.eclipse.viatra.emf.mwe2integration.test.resources

import org.eclipse.viatra.emf.mwe2integration.IMessageFactory
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.exceptions.InvalidParameterTypeException
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.messages.StringMessage

/**
 * 
 */
class TestMessageFactory implements IMessageFactory<String,StringMessage>{
	
	override createMessage(Object parameter) throws InvalidParameterTypeException{
		if(parameter.isValidParameter){
			return new StringMessage(parameter as String)
		}
		throw new InvalidParameterTypeException
	}
	
	override isValidParameter(Object parameter) {
		if(parameter instanceof String){
			return true
		}
		return false;
	}
	
}