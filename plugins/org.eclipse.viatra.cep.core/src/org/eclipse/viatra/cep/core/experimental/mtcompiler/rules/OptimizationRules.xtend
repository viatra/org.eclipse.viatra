/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.experimental.mtcompiler.rules

import com.google.common.base.Preconditions
import org.eclipse.viatra.cep.core.experimental.mtcompiler.EpsilonTransitionMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.EquivalentTransitionsMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.builders.BuilderPrimitives
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class OptimizationRules extends MappingRules {
	
	private extension BuilderPrimitives builderPrimitives

	new(InternalModel internalModel, TraceModel traceModel) {
		super(internalModel, traceModel)
		builderPrimitives= new BuilderPrimitives(traceModel)
	}

	override getAllRules() {
		return #[mergeUponEpsilonTransitionRule, mergeEquivalentTransitionsRule]
	}

	val mergeUponEpsilonTransitionRule = createRule(EpsilonTransitionMatcher::querySpecification) [
		if(preState instanceof InitState){
			mergeStates((preState as InitState), postState, transition)
		}else if(postState instanceof FinalState){
			mergeStates((postState as FinalState), preState, transition)	
		}else{
			mergeStates(preState, postState, transition)
		}
	]

	val mergeEquivalentTransitionsRule = createRule(EquivalentTransitionsMatcher::querySpecification) [
		transition2.parameters += transition1.parameters
		
		transition1.postState = null
		preState.outTransitions.remove(transition1)
	]

	/**
	 * merge state1 into state2
	 */
	private def mergeStates(State stateToDelete, State stateToKeep, EpsilonTransition transition) {
		removeTransition(transition)
		
		stateToKeep.inTransitions += stateToDelete.inTransitions
		stateToKeep.outTransitions += stateToDelete.outTransitions

		stateToKeep.inStateOf += stateToDelete.inStateOf
		stateToKeep.outStateOf += stateToDelete.outStateOf
		
		Preconditions::checkArgument(stateToDelete.outTransitions.forall[t | t instanceof EpsilonTransition])
		
		
		(stateToDelete.eContainer as Automaton).states.remove(stateToDelete)
	}

	private def mergeStates(InitState stateToKeep, State stateToDelete, EpsilonTransition transition) {
		mergeStates(stateToDelete, stateToKeep, transition)
	}

	private def mergeStates(FinalState stateToKeep, State stateToDelete, EpsilonTransition transition) {
		mergeStates(stateToDelete, stateToKeep, transition)
	}

}