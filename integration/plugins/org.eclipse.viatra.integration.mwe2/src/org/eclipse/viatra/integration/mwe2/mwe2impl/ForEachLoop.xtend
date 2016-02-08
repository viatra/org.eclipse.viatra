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
package org.eclipse.viatra.integration.mwe2.mwe2impl

import org.eclipse.viatra.integration.mwe2.providers.IIterableProvider

/**
 * Composite transformation step that implements a 'foreach' style loop. 
 * This kind of loop requires an IIterable object to iterate through. It is provided runtime 
 * by an IIterableProvider.
 * 
 * @author Peter Lunk
 */
class ForEachLoop extends Sequence {
	var IIterableProvider provider;

	def void setIterable(IIterableProvider iterable){
		this.provider = iterable
	}

	override void execute() {
		provider.iterable.forEach[
			step.forEach [
				execute
			]
		]	
	}
}