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

import org.eclipse.viatra.emf.mwe2integration.ICompositeStep
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep
import java.util.List
import java.util.ArrayList

/**
 * MWE2 workflow component that represents a Transformation chain. Each one of these transformation chains 
 * can contain more transformation steps, as the class implements the ICompositeStep interface.
 * 
 * 
 * @author Peter Lunk
 */
class TransformationChain implements IWorkflowComponent, ICompositeStep{
	private List<ITransformationStep> steps = new ArrayList<ITransformationStep>();
	
	/**
	 * Upon being invoked by the MWE runner, the transformation chain will initialize its 
	 * subcomponents and execute them as well.
	 */
	override invoke(IWorkflowContext ctx) {
        steps.forEach[initialize(ctx)]
		steps.forEach[execute]
	}
	
	/**
	 * After it is invoked, it disposes all of the subcomponents.
	 */
	override postInvoke() {
		steps.forEach[dispose]
	}
	
	override preInvoke() {
		//do nothing
	}
	
	override addStep(ITransformationStep step) {
		steps.add(step)
	}
	
	override getStep() {
		return step
	}
	
}