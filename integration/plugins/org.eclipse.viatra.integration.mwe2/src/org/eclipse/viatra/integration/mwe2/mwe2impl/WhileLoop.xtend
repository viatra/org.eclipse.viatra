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

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.viatra.integration.mwe2.providers.IConditionProvider

/**
 * Composite transformation step that implements a 'while' style loop. similar to the
 * conditional step, the dynamically evaluated loop condition is provided by an IConditionProvider.
 * 
 * @author Peter Lunk
 */
class WhileLoop extends Sequence{
	protected IConditionProvider condition;
	
	override void execute() {
		while(condition.apply()){
			step.forEach[
				execute
			]
		}
	}
	
	override initialize(IWorkflowContext ctx) {
		super.initialize(ctx)
		if(condition!=null){
			condition.context = ctx
		}
	}
	
	def void setCondition(IConditionProvider condition){
		this.condition = condition
	}
		
}