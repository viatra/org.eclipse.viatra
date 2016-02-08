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

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.viatra.emf.mwe2integration.ICompositeStep
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep

/**
 * Composite transformation step that implements a basic sequence control flow construction. 
 * It initializes and executes its contained steps in the same sequence they have been defined.
 * 
 * @author Peter Lunk
 */
class Sequence implements ITransformationStep, ICompositeStep {
	protected val List<ITransformationStep> step
	protected var IWorkflowContext ctx;

	new() {
		super()
		this.step = Lists.newArrayList()
	}

	override void addStep(ITransformationStep step) {
		this.step.add(step)
	}

	override List<ITransformationStep> getStep() {
		return step
	}

	override void initialize(IWorkflowContext ctx) {
		this.ctx = ctx
		step.forEach[
			initialize(ctx)
		]
	}

	override void execute() {
		step.forEach[
			execute
		]
	}

	override void dispose() {
		step.forEach[
			dispose
		]
	}

}
