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

package org.eclipse.viatra.cep.core.experimental.mtcompiler.builders

import com.google.common.base.Preconditions
import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
import org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
import org.eclipse.viatra.cep.core.metamodels.events.Infinite
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class BuilderPrimitives {
	protected val extension AutomatonFactory automatonFactory = AutomatonFactory.eINSTANCE
	protected val extension TraceFactory traceFactory = TraceFactory.eINSTANCE

	private TraceModel traceModel

	new(TraceModel traceModel) {
		this.traceModel = traceModel
	}

	/**
	 * Creates a new state and connects it with the preState using the eventPatternReference as a guard type
	 */
	def transitionToNewState(Automaton automaton, EventPatternReference eventPatternReference, State preState) {
		var nextState = createState
		automaton.states += nextState

		transitionBetween(eventPatternReference, preState, nextState)

		nextState
	}

	/**
	 * Connects the preState and the postState using the eventPatternReference as a guard type
	 */
	def transitionBetween(EventPatternReference eventPatternReference, State preState, State postState) {
		var transition = createTypedTransition
		var guard = createGuard
		guard.eventType = eventPatternReference.eventPattern
		transition.guards.add(guard)

		transition.preState = preState
		transition.postState = postState

		eventPatternReference.handleTransitionParameters(transition)
	}

	/**
	 * Maps the eventPatternReference wrt its multiplicity between the preState and a fixed postState.
	 * Creates an Epsilon transition between the last created intermediate state and the postState.
	 */
	def mapWithMultiplicity(EventPatternReference eventPatternReference, Automaton automaton, State preState,
		State postState) {
		val lastState = mapWithMultiplicity(eventPatternReference, automaton, preState)

		if (lastState == null) {
			createEpsilon(preState, postState)
		} else {
			createEpsilon(lastState, postState)
		}
	}

	/**
	 *  Maps the eventPatternReference wrt its multiplicity without a fixed postState.
	 */
	def mapWithMultiplicity(EventPatternReference eventPatternReference, Automaton automaton, State preState) {
		var State nextState = null
		if (eventPatternReference.multiplicity == null) {
			if (nextState == null) {
				nextState = transitionToNewState(automaton, eventPatternReference, preState)
			} else {
				nextState = transitionToNewState(automaton, eventPatternReference, nextState)
			}
		} else if (eventPatternReference.multiplicity instanceof Multiplicity) {
			for (var i = 0; i < (eventPatternReference.multiplicity as Multiplicity).value; i++) {
				if (nextState == null) {
					nextState = transitionToNewState(automaton, eventPatternReference, preState)
				} else {
					nextState = transitionToNewState(automaton, eventPatternReference, nextState)
				}
			}
		} else if (eventPatternReference.multiplicity instanceof AtLeastOne ||
			eventPatternReference.multiplicity instanceof Infinite) {

			if (eventPatternReference.multiplicity instanceof AtLeastOne) {
				if (nextState == null) {
					nextState = transitionToNewState(automaton, eventPatternReference, preState)
				} else {
					nextState = transitionToNewState(automaton, eventPatternReference, nextState)
				}
			}
			if (nextState == null) {
				nextState = selfTransition(eventPatternReference, preState)
			} else {
				nextState = selfTransition(eventPatternReference, nextState)
			}

		} else {
			throw new IllegalArgumentException
		}
		nextState
	}

	/**
	 * Creates a self-transition on the state.
	 */
	def selfTransition(EventPatternReference eventPatternReference, State state) {
		var selfTransition = createTypedTransition
		var selfGuard = createGuard
		selfGuard.eventType = eventPatternReference.eventPattern
		selfTransition.guards.add(selfGuard)

		selfTransition.preState = state
		selfTransition.postState = state

		state
	}

	/**
	 * Creates an Epsilon-transition between two states.
	 */
	def createEpsilon(State preState, State postState) {
		Preconditions::checkArgument(preState != null && postState != null)
		var epsilon = createEpsilonTransition
		epsilon.preState = preState
		epsilon.postState = postState
	}

	/**
	 * Removes a transition from the model.
	 */
	def removeTransition(Transition transition) {
		transition.postState = null
		transition.preState.outTransitions.remove(transition)
	}

	/**
	 * Maps parameters onto a transition.
	 */
	def handleTransitionParameters(EventPatternReference eventPatternReference, TypedTransition transition) {
		val parameterSymbolicNames = eventPatternReference.parameterSymbolicNames

		for (var i = 0; i < parameterSymbolicNames.size; i++) {
			val symbolicName = parameterSymbolicNames.get(i)

			if (!(symbolicName.equalsIgnoreCase(TransformationBasedCompiler.OMITTED_PARAMETER_SYMBOLIC_NAME))) {
				var transitionParameter = createParameter
				transitionParameter.symbolicName = symbolicName
				transitionParameter.position = i
				transition.parameters += transitionParameter
			}
		}
	}

//	/**
//	 * Maps timewindows onto timed zones.
//	 */
//	def handleTimewindow(Automaton automaton, ComplexEventPattern eventPattern, List<Transition> transitions) {
//		if (eventPattern.timewindow == null) {
//			return
//		}
//
//		transitions.forEach [ transition |
//			handleTimewindow(automaton, eventPattern, transition)
//		]
//
//	}
//
//	def handleTimewindow(Automaton automaton, ComplexEventPattern eventPattern, State inState, State outState) {
//		if (eventPattern.timewindow == null) {
//			return
//		}
//		var timedZone = createWithin
//		timedZone.time = eventPattern.timewindow.time
//		timedZone.inState = inState
//		timedZone.outState = outState
//		automaton.timedZones += timedZone
//
//		if (eventPattern.operator instanceof OR) {
//			val tzTrace = createTimedZoneTrace
//			tzTrace.timedZone = timedZone
//			tzTrace.transition = transition
//			traceModel.timedZoneTraces += tzTrace
//		}
//	}
}