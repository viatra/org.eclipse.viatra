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

import java.util.List
import org.eclipse.viatra.cep.core.engine.compiler.PermutationsHelper
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory

class ComplexMappingUtils {
	protected val extension AutomatonFactory automatonFactory = AutomatonFactory.eINSTANCE
	protected val extension TraceFactory traceFactory = TraceFactory.eINSTANCE

	public def buildFollowsPath(Automaton automaton, List<EventPatternReference> eventPatternReferences, State preState,
		State postState) {
		var State lastState = null

		for (eventPatternReference : eventPatternReferences) {
			var newTransition = createTypedTransition
			var guard = createGuard
			guard.eventType = eventPatternReference.eventPattern
			newTransition.guards.add(guard)

			if (lastState == null) {
				newTransition.preState = preState
			} else {
				newTransition.preState = lastState
			}

			lastState = createState
			automaton.states += lastState
			newTransition.postState = lastState
		}
	}

	public def buildOrPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
		var State lastState = createState
		automaton.states += lastState

		for (eventPatternReference : eventPattern.containedEventPatterns) {
			var transition = createTypedTransition
			var guard = createGuard
			guard.eventType = eventPatternReference.eventPattern
			transition.guards.add(guard)

			transition.preState = automaton.initialState
			transition.postState = lastState
		}
	}

	public def buildAndPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
		for (permutation : new PermutationsHelper<EventPatternReference>().getAll(
			eventPattern.containedEventPatterns)) {
			automaton.buildFollowsPath(permutation, preState, postState)
		}
	}

	public def buildNotPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
		var transition = createNegativeTransition
		var guard = createGuard
		guard.eventType = eventPattern
		transition.guards += guard

		transition.preState = preState
		transition.postState = postState
	}
}