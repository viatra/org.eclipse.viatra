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

import java.util.List
import org.apache.log4j.Logger
import org.eclipse.incquery.runtime.api.impl.BaseMatcher
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler
import org.eclipse.viatra.cep.core.logging.LoggerUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRule
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRuleFactory

/**
 * Superclass for specific mapping rules of the {@link TransformationBasedCompiler}.
 */
abstract class MappingRules {
	protected val extension Logger LOGGER = LoggerUtils.getInstance().getLogger();
	protected val extension BatchTransformationRuleFactory ruleFactory = new BatchTransformationRuleFactory

	protected val extension AutomatonFactory automatonFactory = AutomatonFactory.eINSTANCE
	protected val extension TraceFactory traceFactory = TraceFactory.eINSTANCE

	private InternalModel internalModel
	private TraceModel traceModel

	new(InternalModel internalModel, TraceModel traceModel) {
		this.internalModel = internalModel
		this.traceModel = traceModel
	}

	def abstract List<? extends BatchTransformationRule<? extends BasePatternMatch, ? extends BaseMatcher<? extends BasePatternMatch>>> getAllRules()

	def checkForMappedAutomaton(EventPattern eventPattern) {
		if (traceModel.traces.empty) {
			return null
		} else {
			val existingMapping = traceModel.traces.findFirst[trace|trace.eventPattern.id.equals(eventPattern.id)]
			if (existingMapping == null) {
				return null
			}
			return existingMapping.automaton
		}
	}

	def initializeAutomaton(EventPattern eventPattern) {
		var automaton = createAutomaton
		internalModel.automata += automaton
		automaton.eventPatternId = eventPattern.id
		automaton.states += createInitState
		automaton.states += createTrapState
		automaton
	}

	def createTrace(EventPattern eventPattern, Automaton automaton) {
		var trace = createTrace
		trace.automaton = automaton
		trace.eventPattern = eventPattern

		traceModel.traces += trace
	}
}