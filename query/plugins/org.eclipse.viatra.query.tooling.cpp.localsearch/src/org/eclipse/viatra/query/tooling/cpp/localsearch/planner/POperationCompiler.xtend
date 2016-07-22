/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.planner

import org.eclipse.viatra.query.tooling.cpp.localsearch.model.TypeInfo
import org.eclipse.viatra.query.tooling.cpp.localsearch.planner.util.CompilerHelper
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PApply
import org.eclipse.viatra.query.runtime.matchers.planning.operations.POperation
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PStart
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall

/**
 * @author Robert Doczi
 */
class POperationCompiler {

	var Map<PVariable, Integer> variableMapping
	var Map<PConstraint, Set<Integer>> variableBindings
	var Map<PVariable, TypeInfo> typeMapping

	def void compile(SubPlan plan, PBody pBody, Set<PVariable> boundVariables, ISearchOperationAcceptor acceptor) {
		variableMapping = CompilerHelper::createVariableMapping(plan)
		typeMapping = CompilerHelper::createTypeMapping(plan)
		variableBindings = CompilerHelper::cacheVariableBindings(plan, variableMapping, boundVariables.map[variableMapping.get(it)].toSet)

		acceptor.initialize(plan, variableMapping, variableBindings)

		CompilerHelper::createOperationsList(plan).forEach[compile(acceptor)]
	}
	
	def compile(POperation pOperation, ISearchOperationAcceptor acceptor) {
		switch (pOperation) {
			PApply: {
				val pConstraint = pOperation.getPConstraint

				if(pConstraint.allBound)
					return createCheck(pConstraint, acceptor)
				else
					return createExtend(pConstraint, acceptor)

			}
			PStart: {
			}
			PProject: {
			}
			default: { // TODO: throw an error
			}
		}
		return #[]
	}

	def dispatch createCheck(TypeConstraint constraint, ISearchOperationAcceptor acceptor) {
		val inputKey = constraint.supplierKey

		switch (inputKey) {
			EClassTransitiveInstancesKey: {
				val variable = constraint.getVariableInTuple(0)
				acceptor.acceptInstanceOfClassCheck(variable, inputKey)
			}
			EStructuralFeatureInstancesKey: {
				val src = constraint.getVariableInTuple(0)
				val trg = constraint.getVariableInTuple(1)

				acceptor.acceptContainmentCheck(src, trg, inputKey)
			}
		}
	}

	def dispatch createCheck(NegativePatternCall negativePatternCall, ISearchOperationAcceptor acceptor) {
		val bindings = variableBindings.get(negativePatternCall)
		val adornment = negativePatternCall.actualParametersTuple.elements.filter(PVariable).filter[
			bindings.contains(variableMapping.get(it))
		].toSet
		
		val keySize = negativePatternCall.actualParametersTuple.size
		
		val params = negativePatternCall.referredQuery.parameters
		val boundParams = newHashSet
		
		for(i : 0..<keySize) {
			val pVariable = negativePatternCall.actualParametersTuple.get(i) as PVariable
			if(bindings.contains(variableMapping.get(pVariable))) {
				boundParams += params.get(i)
			}	
		}
		
		acceptor.acceptNACOperation(negativePatternCall.referredQuery, adornment, boundParams)
	}

	def dispatch createCheck(ExportedParameter constraint, ISearchOperationAcceptor acceptor) {
		// nop
	}

	def dispatch createCheck(PConstraint constraint, ISearchOperationAcceptor acceptor) {
	}

	def dispatch createExtend(TypeConstraint constraint, ISearchOperationAcceptor acceptor) {
		val inputKey = constraint.supplierKey

		// TODO : this is wasteful
		val paramPositionMap = newHashMap
		variableMapping.forEach [ variable, position |
			paramPositionMap.put(variable.name, position)
		]

		switch (inputKey) {
			EClassTransitiveInstancesKey: {
				val variable = constraint.getVariableInTuple(0)
				acceptor.acceptIterateOverClassInstances(variable, inputKey)
			}
			EStructuralFeatureInstancesKey: {
				var src = constraint.getVariableInTuple(0)
				var trg = constraint.getVariableInTuple(1)
				
				val fromBound = variableBindings.get(constraint).contains(variableMapping.get(src))
				val toBound = variableBindings.get(constraint).contains(variableMapping.get(trg))
				
				if (!fromBound && !toBound) {
					acceptor.acceptIterateOverClassInstances(src, new EClassTransitiveInstancesKey(inputKey.wrappedKey.EContainingClass))
				}
				
				if (toBound) {
					acceptor.acceptExtendToAssociationTarget(src, trg, inputKey)
				} else {
					acceptor.acceptExtendToAssociationSource(src, trg, inputKey)
				}
			}
		}
	}

	def dispatch createExtend(NegativePatternCall negativePatternCall, ISearchOperationAcceptor acceptor) {
		throw new UnsupportedOperationException("Cannot extend through a negative pattern call");
	}

	def dispatch createExtend(ExportedParameter constraint, ISearchOperationAcceptor acceptor) {
		// nop
	}

	def dispatch createExtend(PConstraint constraint, ISearchOperationAcceptor acceptor) {
		println("Constraint type not yet implemented: " + constraint)
	}

	private def allBound(PConstraint pConstraint) {
		switch (pConstraint) {
			NegativePatternCall: return true
			default: return variableBindings.get(pConstraint).containsAll(
					pConstraint.affectedVariables.map [
						variableMapping.get(it)
					].toSet
				) 
		}
	}
}