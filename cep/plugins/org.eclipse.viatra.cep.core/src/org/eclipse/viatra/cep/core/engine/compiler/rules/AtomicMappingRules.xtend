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

package org.eclipse.viatra.cep.core.engine.compiler.rules

import org.eclipse.viatra.cep.core.engine.compiler.AtomicEventPatternMatcher
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class AtomicMappingRules extends MappingRules {

	new(InternalModel internalModel, TraceModel traceModel) {
		super(internalModel, traceModel)
	}

	override getAllRules() {
		return #[atomicPattern2AutomatonRule]
	}

	/**
	 * Transformation rule to compile {@link AtomicEventPattern}s to {@link Automaton}.
	 */
	val atomicPattern2AutomatonRule = createRule.precondition(AtomicEventPatternMatcher::querySpecification).action [
		val mappedAutomaton = checkForMappedAutomaton(eventPattern)
		if (mappedAutomaton != null) {
			createTrace(eventPattern, mappedAutomaton)
			return
		}

		var automaton = eventPattern.initializeAutomaton
		var transition = createTypedTransition
		var guard = createGuard
		guard.eventType = eventPattern
		transition.guards += guard
		
		var k = 0;
		for (String parameterName : eventPattern.parameterNames) {
		    val param = createParameter
		    param.position = k++;
            param.symbolicName = parameterName
		    transition.parameters += param
		}

		transition.preState = automaton.initialState

		val finalState = createFinalState
		automaton.states += finalState
		transition.postState = finalState

		createTrace(eventPattern, automaton)
	].build

}