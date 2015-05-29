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
package org.eclipse.viatra.emf.mwe2integration.mwe2impl

/**
 * Composite transformation step that implements a 'do..while' style loop. similar to the
 * while loop, the dynamically evaluated loop condition is provided by an IConditionProvider.
 * 
 * @author Peter Lunk
 */
class DoWhileLoop extends WhileLoop{
		
	override void execute() {
		do{
			step.forEach[
				execute
			]
		}while(condition.apply());
	}
}