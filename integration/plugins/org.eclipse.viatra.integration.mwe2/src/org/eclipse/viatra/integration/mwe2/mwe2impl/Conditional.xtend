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
import org.eclipse.viatra.integration.mwe2.ITransformationStep
import org.eclipse.viatra.integration.mwe2.providers.IConditionProvider

/**
 * Composite transformation step that implements an IF style conditional construction. The condition is
 * specified by an IConditionProvider which enables the specification of dynamically evaluated conditions.
 * 
 * If the condition evaluation returns true, the ifTrue step is executed, if otherwise, the ifFalse step is executed.
 * 
 * Note: As the condition is evaluated runtime, both of the steps is initialized.
 * 
 * @author Peter Lunk
 */
class Conditional implements ITransformationStep {
	protected var IWorkflowContext ctx;
	protected IConditionProvider condition;
	protected ITransformationStep ifTrue;
	protected ITransformationStep ifFalse;
	def setIfTrue(ITransformationStep ifTrue){
		this.ifTrue = ifTrue
	}
	
	def setIfFalse(ITransformationStep ifFalse){
		this.ifFalse = ifFalse
	}
	
		
	def void setCondition(IConditionProvider condition){
		this.condition = condition
	}

	override void initialize(IWorkflowContext ctx) {
		this.ctx = ctx
		if(condition!=null){
			condition.context = ctx
		}
		ifTrue.initialize(ctx)
		ifFalse.initialize(ctx)
	}

	override void execute() {
		if(condition.apply){
			ifTrue.execute
		}else{
			ifFalse.execute
		}
	}

	override void dispose() {
		ifTrue.dispose
		ifFalse.dispose
	}
}
