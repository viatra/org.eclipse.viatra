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

import java.util.List
import org.eclipse.viatra.cep.core.engine.compiler.PermutationsHelper
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class ComplexMappingUtils {
	protected val extension AutomatonFactory automatonFactory = AutomatonFactory.eINSTANCE
	protected val extension TraceFactory traceFactory = TraceFactory.eINSTANCE
	private extension BuilderPrimitives builderPrimitives
	private TraceModel traceModel

	new(TraceModel traceModel) {
		this.traceModel = traceModel
		this.builderPrimitives = new BuilderPrimitives(traceModel)
	}

	public def buildFollowsPath(Automaton automaton, ComplexEventPattern eventPattern, State preState,
		State postState) {
		buildFollowsPath(automaton, eventPattern.containedEventPatterns, preState, postState)
	}

	private def buildFollowsPath(Automaton automaton, List<EventPatternReference> eventPatternReferences,
		State preState, State postState) {
		var State nextState = null

		for (eventPatternReference : eventPatternReferences) {
			if (nextState == null) {
				nextState = mapWithMultiplicity(eventPatternReference, automaton, preState)
			} else {
				nextState = mapWithMultiplicity(eventPatternReference, automaton, nextState)
			}
		}

		createEpsilon(nextState, postState)
	}

	public def buildOrPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
		val State lastState = createState
		automaton.states += lastState

		for (eventPatternReference : eventPattern.containedEventPatterns) {
			mapWithMultiplicity(eventPatternReference, automaton, preState, lastState)
		}

		createEpsilon(lastState, postState)
	}

	public def buildAndPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
		for (permutation : new PermutationsHelper<EventPatternReference>().getAll(
			eventPattern.containedEventPatterns)) {
			automaton.buildFollowsPath(permutation, preState, postState)
		}
	}

//	public def buildNotPath(Automaton automaton, ComplexEventPattern eventPattern, State preState, State postState) {
//		var transition = createNegativeTransition
//		var guard = createGuard
//		guard.eventType = eventPattern
//		transition.guards += guard
//
//		transition.preState = preState
//		transition.postState = postState
//	}
}