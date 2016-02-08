package org.eclipse.viatra.cep.core.utils

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState

class AutomatonUtils {

	def public static getNormalStates(Automaton automaton) {
		automaton.states.filter[s|s.isNormal]
	}

	def public static isNormal(State state) {
		!((state instanceof InitState) || (state instanceof FinalState) || (state instanceof TrapState))
	}

	def public static enablesStrictInitTokenCreation(State state) {
		state == null || state instanceof FinalState || state instanceof TrapState
	}

	def public static notEmpty(State state) {
		!state.empty
	}

	def public static isEmpty(State state) {
		state.eventTokens.empty
	}

	def public static newEventToken(Automaton automaton, State state) {
		val token = AutomatonFactory.eINSTANCE.createEventToken
		val parameterTable = AutomatonFactory.eINSTANCE.createParameterTable
		token.parameterTable = parameterTable

		token.currentState = state

		automaton.eventTokens.add(token)
	}
}
