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

import org.eclipse.viatra.cep.core.experimental.mtcompiler.AndPatternMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.ComplexAndTransitionMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.ComplexFollowsTransitionMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.ComplexOrTransitionMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.FollowsPatternMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.OrPatternMatcher
import org.eclipse.viatra.cep.core.experimental.mtcompiler.builders.BuilderPrimitives
import org.eclipse.viatra.cep.core.experimental.mtcompiler.builders.ComplexMappingUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class ComplexMappingRules extends MappingRules {

	val extension ComplexMappingUtils complexMappingUtils
	val extension BuilderPrimitives builderPrimitives

	new(InternalModel internalModel, TraceModel traceModel) {
		super(internalModel, traceModel)
		complexMappingUtils = new ComplexMappingUtils(traceModel)
		builderPrimitives = new BuilderPrimitives(traceModel)
	}

	override getAllRules() {
		return #[
			followsPattern2AutomatonRule,
			orPattern2AutomatonRule,
			followUnfoldRule,
			orUnfoldRule,
			andPattern2AutomatonRule,
			andUnfoldRule
//			, notPattern2AutomatonRule, notUnfoldRule
		]
	}

	val followsPattern2AutomatonRule = createRule(FollowsPatternMatcher::querySpecification) [
		var automaton = eventPattern.initializeAutomaton
		val finalState = createFinalState
		automaton.states += finalState

		automaton.buildFollowsPath(eventPattern, automaton.initialState, finalState)

		createTrace(eventPattern, automaton)
	]

	val followUnfoldRule = createRule(ComplexFollowsTransitionMatcher::querySpecification) [
		val referredEventPattern = transition.guards.head.eventType as ComplexEventPattern

		automaton.buildFollowsPath(referredEventPattern, transition.preState, transition.postState)

		removeTransition(transition)
	]

	val orPattern2AutomatonRule = createRule(OrPatternMatcher::querySpecification) [
		var automaton = eventPattern.initializeAutomaton
		val finalState = createFinalState
		automaton.states += finalState

		automaton.buildOrPath(eventPattern, automaton.initialState, finalState)

		createTrace(eventPattern, automaton)
	]

	val orUnfoldRule = createRule(ComplexOrTransitionMatcher::querySpecification) [
		val referredEventPattern = transition.guards.head.eventType as ComplexEventPattern

		automaton.buildOrPath(referredEventPattern, transition.preState, transition.postState)

		removeTransition(transition)
	]

	val andPattern2AutomatonRule = createRule(AndPatternMatcher::querySpecification) [
		var automaton = eventPattern.initializeAutomaton
		val finalState = createFinalState
		automaton.states += finalState

		automaton.buildAndPath(eventPattern, automaton.initialState, finalState)

		createTrace(eventPattern, automaton)
	]

	val andUnfoldRule = createRule(ComplexAndTransitionMatcher::querySpecification) [
		val referredEventPattern = transition.guards.head.eventType as ComplexEventPattern

		automaton.buildAndPath(referredEventPattern, transition.preState, transition.postState)

		transition.preState.outTransitions.remove(transition)
	]

//	val notPattern2AutomatonRule = createRule(NotPatternMatcher::querySpecification) [
//		var automaton = eventPattern.initializeAutomaton
//		val finalState = createFinalState
//		automaton.states += finalState
//
//		automaton.buildNotPath(eventPattern, automaton.initialState, finalState)
//
//		createTrace(eventPattern, automaton)
//	]
//
//	val notUnfoldRule = createRule(ComplexNotTransitionMatcher::querySpecification) [
//		val referredEventPattern = transition.guards.head.eventType as ComplexEventPattern
//
//		automaton.buildNotPath(referredEventPattern, transition.preState, transition.postState)
//
//		transition.preState.outTransitions.remove(transition)
//	]
}