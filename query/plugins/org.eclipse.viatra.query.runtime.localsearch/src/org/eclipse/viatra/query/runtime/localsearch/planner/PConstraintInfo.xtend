/** 
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Danil Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Marton Bur - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.localsearch.planner

import com.google.common.base.Function
import com.google.common.collect.Sets
import java.util.Set
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext

/** 
 * Wraps a PConstraint together with information required for the planner. Currently contains information about the expected binding state of
 * the affected variables also called application condition, and the cost of the enforcement, based on the meta and/or the runtime context.
 *  
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 */
class PConstraintInfo implements IConstraintEvaluationContext {

	private PConstraint constraint
	private Set<PVariable> boundMaskVariables
	private Set<PVariable> freeMaskVariables
	private Set<PConstraintInfo> sameWithDifferentBindings
	private IQueryRuntimeContext runtimeContext

	private double cost

	/** 
	 * Instantiates the wrapper
	 * @param constraintfor which the information is added and stored
	 * @param boundMaskVariablesthe bound variables in the operation mask
	 * @param freeMaskVariablesthe free variables in the operation mask
	 * @param sameWithDifferentBindingsduring the planning process, multiple operation adornments are considered for a constraint, so that it
	 * is represented by multiple plan infos. This parameter contains all plan infos that are for the same
	 * constraint, but with different adornment
	 * @param runtimeContextthe runtime query context
	 */
	new(PConstraint constraint, Set<PVariable> boundMaskVariables, Set<PVariable> freeMaskVariables,
		Set<PConstraintInfo> sameWithDifferentBindings, IQueryRuntimeContext runtimeContext, Function<IConstraintEvaluationContext, Double> costFunction) {
		this.constraint = constraint
		this.boundMaskVariables = boundMaskVariables
		this.freeMaskVariables = freeMaskVariables
		this.sameWithDifferentBindings = sameWithDifferentBindings
		this.runtimeContext = runtimeContext

		// Calculate cost of the constraint based on its type
		this.cost = costFunction.apply(this);
	}
	
    override getRuntimeContext() {
        runtimeContext
    }

	override PConstraint getConstraint() {
		return constraint
	}

	override Set<PVariable> getFreeVariables() {
		return freeMaskVariables
	}

	override Set<PVariable> getBoundVariables() {
		return boundMaskVariables
	}

	def Set<PConstraintInfo> getSameWithDifferentBindings() {
		return sameWithDifferentBindings
	}

	def double getCost() {
		return cost
	}

    def PConstraintCategory getCategory(PBody pBody, Set<PVariable> boundVariables) {
        if (!Sets.intersection(this.freeMaskVariables, boundVariables).isEmpty) {
            return PConstraintCategory.PAST
        } else if (!Sets.intersection(this.boundMaskVariables, Sets.difference(pBody.getAllVariables(), boundVariables)).
            isEmpty) {
            return PConstraintCategory.FUTURE
        } else {
            return PConstraintCategory.PRESENT
        }
    }

	override String toString()
		'''«String.format("\n")»«constraint.toString», bound variables: «boundMaskVariables», cost: «String.format("%.2f",cost)»'''	
	

}
