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
import org.eclipse.viatra.cep.core.api.patterns.InTrapComplexEventPattern
import org.eclipse.viatra.cep.core.api.patterns.ObservedComplexEventPattern
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern
import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.logging.LoggerUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState
import org.eclipse.viatra2.emf.runtime.rules.EventDrivenTransformationRuleGroup
import org.eclipse.viatra2.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra2.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra2.emf.runtime.transformation.eventdriven.RuleOrderBasedFixedPriorityResolver

class ModelHandlingWithViatraApi2 {
	extension EventDrivenTransformationRuleFactory ruleFactory = new EventDrivenTransformationRuleFactory

	@Property IEventModelManager eventModelManager;
	@Property Map<RuleSpecification<?>, Integer> modelHandlers;

	val logger = LoggerUtils::instance.logger

	new(IEventModelManager eventModelManager) {
		this.eventModelManager = eventModelManager;
	}

	def getRules() {
		new EventDrivenTransformationRuleGroup(
			createEnabledTransitionRule,
			createFinishedAutomatonRule,
			createTokenInTrapStateRule
		)
	}

	def registerRules() {
		EventDrivenTransformation.forResource(eventModelManager.resourceSet).addRules(rules).create()
	}

	def registerRulesWithCustomPriorities() {
		val fixedPriorityResolver = ConflictResolvers.createFixedPriorityResolver();
		fixedPriorityResolver.setPriority(createEnabledTransitionRule.ruleSpecification, 100)
		fixedPriorityResolver.setPriority(createFinishedAutomatonRule.ruleSpecification, 50)
		fixedPriorityResolver.setPriority(createTokenInTrapStateRule.ruleSpecification, 0)

		EventDrivenTransformation.forResource(eventModelManager.resourceSet).addRules(rules).
			setConflictResolver(fixedPriorityResolver).create()
	}

	def registerRulesWithAutomatedPriorities() {
		val resolver = new RuleOrderBasedFixedPriorityResolver()
		resolver.setPrioritiesFromScratch(new ArrayList(rules.ruleSpecifications))

		EventDrivenTransformation.forResource(eventModelManager.resourceSet).addRules(rules).
			setConflictResolver(resolver).create()
	}

	val createEnabledTransitionRule = ruleFactory.createRule().name("enabled transition rule").precondition(
		EnabledTransitionMatcher::querySpecification).action [
		var eventPattern = ((t.eContainer() as State).eContainer() as Automaton).getEventPattern();
		if (eventPattern instanceof ParameterizableComplexEventPattern) {
			if (!((eventPattern as ParameterizableComplexEventPattern).evaluateParameterBindigs(e))) {
				return;
			}
		}
		eventModelManager.fireTransition(t, et, e)
	].build

	val createFinishedAutomatonRule = ruleFactory.createRule().name("finished automaton rule").precondition(
		FinishedAutomatonMatcher::querySpecification).action [
		eventModelManager.finalStatesForAutomata.get(automaton).eventTokens.remove(0)
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
		var eventPattern = (currentState.eContainer() as Automaton).getEventPattern();
		var failedPattern = new InTrapComplexEventPattern(eventPattern)
		eventModelManager.cepRealm.forwardFailedEventPattern(failedPattern)
		currentState.eventTokens.clear
	].build
}
