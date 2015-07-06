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

package org.eclipse.viatra.cep.core.experimental.mtcompiler

import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton

class OptimizationRules extends MappingRules {

	new(InternalModel internalModel, TraceModel traceModel) {
		super(internalModel, traceModel)
	}

	override getAllRules() {
		return #[mergeUponEpsilonTransitionRule, mergeEquivalentTransitionsRule]
	}

	val mergeUponEpsilonTransitionRule = createRule(EpsilonTransitionMatcher::querySpecification) [
		mergeStates(preState, postState)
		automaton.states.remove(preState)
	]

	val mergeEquivalentTransitionsRule = createRule(EquivalentTransitionsMatcher::querySpecification) [
		transition2.parameters += transition1.parameters
		preState.outTransitions.remove(transition1)
		postState.inTransitions
	]

	/**
	 * merge state1 into state2
	 */
	private def mergeStates(State state1, State state2) {
		state2.inTransitions += state1.inTransitions
		state2.outTransitions += state1.outTransitions.filter[t|!t.sharedTranstion(state1, state2)]

		state2.inStateOf += state1.inStateOf
		state2.outStateOf += state1.outStateOf

		state1.outTransitions.removeAll()

		(state1.eContainer as Automaton).states.remove(state1)
	}

	private def sharedTranstion(Transition transition, State preState, State postState) {
		return transition.preState.equals(preState) && transition.postState.equals(postState)
	}
}