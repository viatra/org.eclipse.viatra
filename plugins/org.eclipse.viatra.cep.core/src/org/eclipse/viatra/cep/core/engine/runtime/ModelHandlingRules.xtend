/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.core.engine.runtime

import java.util.ArrayList
import java.util.Map
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.specific.ConflictResolvers
import org.eclipse.viatra.cep.core.api.patterns.ObservedComplexEventPattern
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern
import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.engine.timing.TimingTable
import org.eclipse.viatra.cep.core.logging.LoggerUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState
import org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.RuleOrderBasedFixedPriorityResolver

import static extension org.eclipse.viatra.cep.core.utils.AutomatonUtils.*
import org.eclipse.xtend.lib.annotations.Accessors

class ModelHandlingRules {

	val EventDrivenTransformationRuleFactory ruleFactory = new EventDrivenTransformationRuleFactory
	val logger = LoggerUtils::instance.logger

	@Accessors IEventModelManager eventModelManager;
	@Accessors Map<RuleSpecification<?>, Integer> modelHandlers;

	new(IEventModelManager eventModelManager) {
		this.eventModelManager = eventModelManager;
	}

	def getRules() {
		new EventDrivenTransformationRuleGroup(
			createEnabledTransitionRule,
			createFinishedAutomatonRule,
			createTokenInTrapStateRule,
			createTokenEntersTimedZoneRule,
			createTokenLeavesTimedZoneRule
		)
	}

	def registerRules() {
		EventDrivenTransformation.forSource(eventModelManager.resourceSet).addRules(rules).create()
	}

	def registerRulesWithCustomPriorities() {
		val fixedPriorityResolver = ConflictResolvers.createFixedPriorityResolver();
		fixedPriorityResolver.setPriority(createEnabledTransitionRule.ruleSpecification, 100)
		fixedPriorityResolver.setPriority(createFinishedAutomatonRule.ruleSpecification, 50)
		fixedPriorityResolver.setPriority(createTokenInTrapStateRule.ruleSpecification, 10)
		fixedPriorityResolver.setPriority(createTokenEntersTimedZoneRule.ruleSpecification, 5)
		fixedPriorityResolver.setPriority(
			createTokenLeavesTimedZoneRule.ruleSpecification,
			1
		)

		EventDrivenTransformation.forSource(eventModelManager.resourceSet).addRules(rules).
			setConflictResolver(fixedPriorityResolver).create()
	}

	def registerRulesWithAutomatedPriorities() {
		val resolver = new RuleOrderBasedFixedPriorityResolver()
		resolver.setPrioritiesFromScratch(new ArrayList(rules.ruleSpecifications))

		EventDrivenTransformation.forSource(eventModelManager.resourceSet).addRules(rules).
			setConflictResolver(resolver).create()
	}

	val createEnabledTransitionRule = ruleFactory.createRule().name("enabled transition rule").precondition(
		EnabledTransitionMatcher::querySpecification).action [
		var eventPattern = ((t.eContainer() as State).eContainer() as Automaton).eventPattern
		if (eventPattern instanceof ParameterizableComplexEventPattern) {
			if (!((eventPattern as ParameterizableComplexEventPattern).evaluateParameterBindings(e))) {
				return
			}
		}
		eventModelManager.fireTransition(t, et)
	].build

	val createFinishedAutomatonRule = ruleFactory.createRule().name("finished automaton rule").precondition(
		FinishedAutomatonMatcher::querySpecification).action [
		automaton.finalState.eventTokens.remove(0)
		var observedPattern = new ObservedComplexEventPattern(automaton.eventPattern)
		eventModelManager.callbackOnPatternRecognition(observedPattern)
		eventModelManager.cepRealm.forwardObservedEventPattern(observedPattern)
	].build

	val createTokenInTrapStateRule = ruleFactory.createRule().name("trap state rule").precondition(
		TokenInTrapStateMatcher::querySpecification).action [
		var currentState = et.currentState
		if (!(currentState instanceof TrapState)) {
			return
		}
		logger.debug(
			String::format("Event token found in the trap state for pattern %s.",
				(et.currentState.eContainer as Automaton).eventPattern.id));
		//		var eventPattern = (currentState.eContainer() as Automaton).getEventPattern();
		//		var failedPattern = new InTrapComplexEventPattern(eventPattern)
		//		eventModelManager.cepRealm.forwardFailedEventPattern(failedPattern)
		currentState.eventTokens.clear
	].build

	val createTokenEntersTimedZoneRule = ruleFactory.createRule().name("token enters timed zone rule").
		precondition(TokenEntersTimedZoneMatcher::querySpecification).action [
			TimingTable.instance.enterTimedZone(tz, et)
		].build

	val createTokenLeavesTimedZoneRule = ruleFactory.createRule().name("token leaves timed zone rule").
		precondition(TokenLeavesTimedZoneMatcher::querySpecification).action [
			val canLeave = TimingTable.instance.leaveTimedZone(tz, et);
			if (!canLeave) {
				val automaton = et.eContainer as Automaton
				et.setCurrentState(automaton.trapState)
			}
		].build
}
