/** 
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Danil Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Marton Bur - initial API and implementation
 */
package org.eclipse.incquery.runtime.localsearch.planner

import com.google.common.collect.Sets
import java.util.Collection
import java.util.List
import java.util.Map
import java.util.Set
import org.eclipse.incquery.runtime.matchers.context.IInputKey
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext
import org.eclipse.incquery.runtime.matchers.context.InputKeyImplication
import org.eclipse.incquery.runtime.matchers.psystem.PBody
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint
import org.eclipse.incquery.runtime.matchers.psystem.PVariable
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint

/** 
 * Wraps a PConstraint together with information required for the planner. Currently contains information about the expected binding state of
 * the affected variables also called application condition, and the cost of the enforcement, based on the meta and/or the runtime context.
 *  
 * @author Marton Bur
 */
class PConstraintInfo {

	

	private PConstraint constraint
	private Set<PVariable> boundMaskVariables
	private Set<PVariable> freeMaskVariables
	private Set<PConstraintInfo> sameWithDifferentBindings
	private IQueryRuntimeContext runtimeContext

	private static float MAX_COST = 250.0f
	private static float DEFAULT_COST = MAX_COST-100.0f
	private float cost

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
		Set<PConstraintInfo> sameWithDifferentBindings, IQueryRuntimeContext runtimeContext) {
		this.constraint = constraint
		this.boundMaskVariables = boundMaskVariables
		this.freeMaskVariables = freeMaskVariables
		this.sameWithDifferentBindings = sameWithDifferentBindings
		this.runtimeContext = runtimeContext

		// Calculate cost of the constraint based on its type
		calculateCost(constraint);
	}

	protected def dispatch void calculateCost(ConstantValue constant) {
		cost = 1.0f;
		return;
	}

	protected def dispatch void calculateCost(TypeConstraint typeConstraint) {

		var IInputKey supplierKey = (constraint as TypeConstraint).getSupplierKey()
		var long arity = supplierKey.getArity()
		if (arity == 1) {
			// unary constraint
			calculateUnaryConstraintCost(supplierKey)
		} else if (arity == 2) {
			// binary constraint
			var long edgeCount = runtimeContext.countTuples(supplierKey, null)
			var srcVariable = (constraint as TypeConstraint).getVariablesTuple().get(0) as PVariable
			var dstVariable = (constraint as TypeConstraint).getVariablesTuple().get(1) as PVariable
			var isInverse = false
			// Check if inverse navigation is needed along the edge
			if (freeMaskVariables.contains(srcVariable) && boundMaskVariables.contains(dstVariable)) {
				isInverse = true
			}
			if (freeMaskVariables.contains(srcVariable) || freeMaskVariables.contains(dstVariable)) {
				// This case it is not a check
				// at least one of the variables are free, so calculate cost based on the meta or/and the runtime context
				calculateBinaryExtendCost(supplierKey, srcVariable, dstVariable, isInverse, edgeCount)
			} else {
				// It is a check operation, both variables are bound
				cost = 1.0f
			}
		} else {
			// n-ary constraint
			throw new RuntimeException('''Cost calculation for arity �arity� is not implemented yet''')
		}
	}
	
	protected def calculateBinaryExtendCost(IInputKey supplierKey, PVariable srcVariable, PVariable dstVariable, boolean isInverse, long edgeCount) {
		var metaContext = runtimeContext.getMetaContext()
		var Collection<InputKeyImplication> implications = metaContext.getImplications(supplierKey)
		// TODO prepare for cases when this info is not available - use only metamodel related cost calculation (see TODO at the beginning of the function)
		var long srcCount = -1
		var long dstCount = -1
		// Obtain runtime information
		for (InputKeyImplication implication : implications) {
			var List<Integer> impliedIndices = implication.getImpliedIndices()
			if (impliedIndices.size() == 1 && impliedIndices.contains(0)) {
				// Source key implication
				srcCount = runtimeContext.countTuples(implication.getImpliedKey(), null)
			} else if (impliedIndices.size() == 1 && impliedIndices.contains(1)) {
				// Target key implication
				dstCount = runtimeContext.countTuples(implication.getImpliedKey(), null)
			}
		
		}
		if (freeMaskVariables.contains(srcVariable) && freeMaskVariables.contains(dstVariable)) {
			cost = dstCount * srcCount
		} else {
			var long srcNodeCount = -1
			var long dstNodeCount = -1
			if (isInverse) {
				srcNodeCount = dstCount
				dstNodeCount = srcCount
			} else {
				srcNodeCount = srcCount
				dstNodeCount = dstCount
			}
			
			if (srcNodeCount > -1 && edgeCount > -1) {
				// The end nodes had implied (type) constraint and both nodes and adjacent edges are indexed
				if (srcNodeCount == 0) {
					cost = 0
				} else {
					cost = ((edgeCount) as float) / srcNodeCount
				}
			} else if (srcCount > -1 && dstCount > -1) {
				// Both of the end nodes had implied (type) constraint
				if(srcCount != 0) {
					cost = dstNodeCount / srcNodeCount
				} else {
					// No such element exists in the model, so the traversal will backtrack at this point
					cost = 1.0f;
				}
			} else {
				// At least one of the end variables had no restricting type information
				// Strategy: try to navigate along many-to-one relations
				var Map<Set<PVariable>, Set<PVariable>> functionalDependencies = constraint.getFunctionalDependencies(metaContext);
				var impliedVariables = functionalDependencies.get(boundMaskVariables)
				if(impliedVariables.containsAll(freeMaskVariables)){
					cost = 1.0f;
				} else {
					cost = DEFAULT_COST
				}
			}
		}
		return
	}
	
	protected def calculateUnaryConstraintCost(IInputKey supplierKey) {
		var variable = (constraint as TypeConstraint).getVariablesTuple().get(0) as PVariable
		if (boundMaskVariables.contains(variable)) {
			cost = 1
		} else {
			cost = runtimeContext.countTuples(supplierKey, null)
		}
	}

	protected def dispatch void calculateCost(ExportedParameter exportedParam) {
		cost = MAX_COST;
	}

	/**
	 * Default cost calculation strategy
	 */
	protected def dispatch void calculateCost(PConstraint constraint) {
		if (freeMaskVariables.isEmpty) {
			cost = 1.0f;
		} else {
			cost = DEFAULT_COST
		}
	}

	def PConstraint getConstraint() {
		return constraint
	}

	def Set<PVariable> getFreeVariables() {
		return freeMaskVariables
	}

	def Set<PVariable> getBoundVariables() {
		return boundMaskVariables
	}

	def Set<PConstraintInfo> getSameWithDifferentBindings() {
		return sameWithDifferentBindings
	}

	def float getCost() {
		return cost
	}

	def PConstraintCategory getCategory(PBody pBody, Set<PVariable> boundVariables) {
		if (Sets.intersection(this.freeMaskVariables, boundVariables).size() > 0) {
			return PConstraintCategory.PAST
		} else if (Sets.intersection(this.boundMaskVariables, Sets.difference(pBody.getAllVariables(), boundVariables)).
			size() > 0) {
			return PConstraintCategory.FUTURE
		} else {
			return PConstraintCategory.PRESENT
		}
	}

	override String toString()
		'''«String.format(System.lineSeparator)»«constraint.toString», bound variables: «boundMaskVariables», cost: «String.format("%.2f",cost)»'''	
	

}
