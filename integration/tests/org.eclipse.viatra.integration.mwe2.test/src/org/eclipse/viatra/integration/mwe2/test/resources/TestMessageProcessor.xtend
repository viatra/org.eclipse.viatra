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
package org.eclipse.viatra.integration.mwe2.test.resources

import java.util.concurrent.BlockingQueue
import org.eclipse.viatra.integration.mwe2.IMessage
import org.eclipse.viatra.integration.mwe2.IMessageProcessor
import org.eclipse.viatra.integration.mwe2.ITransformationStep
import org.eclipse.viatra.integration.mwe2.mwe2impl.TransformationStep
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException
import org.eclipse.viatra.integration.mwe2.mwe2impl.messages.StringMessage

/**
 * 
 */
class TestMessageProcessor implements IMessageProcessor<String, StringMessage> {
	protected ITransformationStep parent;

	override getParent() {
		return parent
	}

	override setParent(ITransformationStep parent) {
		this.parent = parent
	}
	
	override processMessage(IMessage<?> message) throws InvalidParameterTypeException{
		if(message instanceof StringMessage){
			val castparent = parent as TransformationStep
			val list = castparent.context.get("TestOutput")as BlockingQueue<String>
			if(list!=null){
				list.put(message.parameter)
			}
		}else{
			throw new InvalidParameterTypeException
		}
	}

}