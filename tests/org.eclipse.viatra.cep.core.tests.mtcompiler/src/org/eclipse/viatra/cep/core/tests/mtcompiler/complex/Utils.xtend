package org.eclipse.viatra.cep.core.tests.mtcompiler.complex

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState

class Utils {

	def public static boolean noEpsilonTransitions(Automaton automaton) {
		automaton.states.forall [ s |
			s.outTransitions.forall [ t |
				!(t instanceof EpsilonTransition)
			]
		] && automaton.states.forall [ s |
			s.inTransitions.forall [ t |
				!(t instanceof EpsilonTransition)
			]
		]
	}

	def public static boolean noOrphanTransitions(Automaton automaton) {
		automaton.states.forall [ s |
			s.outTransitions.forall [ t |
				t.postState != null
			]
		] && automaton.states.forall [ s |
			s.inTransitions.forall [ t |
				t.preState != null
			]
		]
	}

	def public static boolean noOrphanStates(Automaton automaton) {
		automaton.states.filter [ s |
			!(s instanceof InitState)
		].filter[s|!(s instanceof TrapState)].forall [ s |
			!s.inTransitions.empty
		] && automaton.states.filter [ s |
			!(s instanceof FinalState)
		].filter[s|!(s instanceof TrapState)].forall [ s |
			!s.outTransitions.empty
		]
	}
}