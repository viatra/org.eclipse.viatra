package org.eclipse.viatra.cep.core.utils

import com.google.common.base.Preconditions
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState

class AutomatonUtils {

	def public static getInitState(Automaton automaton) {
		var initStates = automaton.states.filter[s|s instanceof InitState]
		Preconditions.checkArgument(initStates.size == 1)
		initStates.head
	}

	def public static getFinalState(Automaton automaton) {
		var finalStates = automaton.states.filter[s|s instanceof FinalState]
		Preconditions.checkArgument(finalStates.size == 1)
		finalStates.head
	}

	def public static getTrapState(Automaton automaton) {
		var trapStates = automaton.states.filter[s|s instanceof TrapState]
		Preconditions.checkArgument(trapStates.size == 1)
		trapStates.head
	}

	def public static getNormalStates(Automaton automaton) {
		automaton.states.filter[s|s.isNormal]
	}

	def public static isNormal(State state) {
		!((state instanceof InitState) || (state instanceof FinalState) || (state instanceof TrapState))
	}

	def public static notEmpty(State state) {
		!state.empty
	}

	def public static isEmpty(State state) {
		state.eventTokens.empty
	}
}
