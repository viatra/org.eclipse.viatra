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
import org.eclipse.viatra.cep.core.experimental.mtcompiler.EquivalentStatesMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.EquivalentTransitionsMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.builders.BuilderPrimitives
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class OptimizationRules extends MappingRules {

	private extension BuilderPrimitives builderPrimitives

	new(InternalModel internalModel, TraceModel traceModel) {
		super(internalModel, traceModel)
		builderPrimitives = new BuilderPrimitives(traceModel)
	}

	override getAllRules() {
		return #[mergeUponEpsilonTransitionRule, mergeEquivalentTransitionsRule, mergeEquivalentStatesRule]
	}

	/**
	 * Transformation rule to merge {@link State}s connected by an {@link EpsilonTransition}.
	 */
	val mergeUponEpsilonTransitionRule = createRule(EpsilonTransitionMatcher::querySpecification)[
		if(preState instanceof InitState){
			mergeStates((preState as InitState), postState, transition)
		} else if(postState instanceof FinalState){
			mergeStates((postState as FinalState), preState, transition)	
		} else{
			mergeStates(preState, postState, transition)
		}
	]

	/**
	 * Transformation rule to merge equivalent {@link Transition}s.
	 */
	val mergeEquivalentTransitionsRule = createRule(EquivalentTransitionsMatcher::querySpecification) [
		transition2.parameters += transition1.parameters
		removeTransition(transition1)
	]

	/**
	 * Transformation rule to merge equivalent {@link State}s.
	 */
	val mergeEquivalentStatesRule = createRule(EquivalentStatesMatcher::querySpecification) [
		Preconditions::checkArgument(!((postState1 instanceof InitState) && (postState2 instanceof FinalState)))
		Preconditions::checkArgument(!((postState1 instanceof FinalState) && (postState2 instanceof InitState)))

		switch (postState1) {
			InitState:
				mergeStates(postState1, postState2, transition1, transition2)
			FinalState:
				mergeStates(postState1, postState2, transition1, transition2)
			default:
				switch (postState2) {
					InitState: mergeStates(postState2, postState1, transition2, transition1)
					FinalState: mergeStates(postState2, postState1, transition2, transition1)
					default: mergeStates(postState1, postState2, transition1, transition2)
				}
		}
	]

	/**
	 * Merge a {@link State} into an {@link InitState} through a {@link Transition}.
	 */
	private def mergeStates(InitState stateToKeep, State stateToDelete, Transition transition) {
		mergeStates(stateToDelete, stateToKeep, transition)
	}

	/**
	 * Merge a {@link State} into an {@link FinalState} through a {@link Transition}.
	 */
	private def mergeStates(FinalState stateToKeep, State stateToDelete, Transition transition) {
		mergeStates(stateToDelete, stateToKeep, transition)
	}

	/**
	 * Merge two {@link State}s with the respective associated {@link Transition}s.
	 */
	private def mergeStates(State stateToDelete, State stateToKeep, TypedTransition transitionToRemove,
		TypedTransition transitionToKeep) {
		transitionToKeep.parameters += transitionToRemove.parameters
		mergeStates(stateToDelete, stateToKeep, transitionToRemove)
	}

	/**
	 * Merge two {@link State}s through a {@link Transition}.
	 */
	private def mergeStates(State stateToDelete, State stateToKeep, Transition transition) {
		removeTransition(transition)
		mergeStates(stateToDelete, stateToKeep)
	}

	/**
	 * Merge two {@link State}s.
	 */
	private def mergeStates(State stateToDelete, State stateToKeep) {
		stateToKeep.inTransitions += stateToDelete.inTransitions
		stateToKeep.outTransitions += stateToDelete.outTransitions

		stateToKeep.inStateOf += stateToDelete.inStateOf
		stateToKeep.outStateOf += stateToDelete.outStateOf

		Preconditions::checkArgument(stateToDelete.outTransitions.forall[t|t instanceof EpsilonTransition])
		removeState(stateToDelete)
	}
}