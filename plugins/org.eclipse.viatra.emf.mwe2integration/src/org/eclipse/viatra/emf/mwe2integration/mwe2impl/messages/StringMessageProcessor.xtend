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
package org.eclipse.viatra.emf.mwe2integration.mwe2impl.messages

import java.security.InvalidParameterException
import org.eclipse.viatra.emf.mwe2integration.IMessage
import org.eclipse.viatra.emf.mwe2integration.IMessageProcessor
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.exceptions.InvalidParameterTypeException

/**
 * Message Processor that is responsible for processing StringMessage objects.
 * It serves as an example for user defined message processors.
 * @author Peter Lunk
 *
 */
class StringMessageProcessor implements IMessageProcessor<String, StringMessage> {
	/**
	 * Similar to every typical IMessageProcessor, this class also has a reference to its parent transformation step
	 * This way it can hand its result to the transformation step.
	 */
	protected ITransformationStep parent;

	override getParent() {
		return parent
	}

	override setParent(ITransformationStep parent) {
		this.parent = parent
	}
	
	override processMessage(IMessage<?> message) throws InvalidParameterTypeException{
		if(message instanceof StringMessage){
			/**
			 * Message processing is done here
			 */
		}else{
			throw new InvalidParameterException
		}
	}

}