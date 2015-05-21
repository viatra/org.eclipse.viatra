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

import com.google.common.base.Preconditions
import java.util.Map
import org.apache.log4j.Logger
import org.eclipse.incquery.runtime.emf.EMFScope
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.specific.ConflictResolvers
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance
import org.eclipse.viatra.cep.core.api.patterns.ObservedComplexEventPattern
import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.engine.timing.TimingTable
import org.eclipse.viatra.cep.core.logging.LoggerUtils
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
import org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.xtend.lib.annotations.Accessors

class ModelHandlingRules {

	val extension EventDrivenTransformationRuleFactory ruleFactory = new EventDrivenTransformationRuleFactory
	val extension EvaluationPatterns evaluationPatterns = EvaluationPatterns::instance
	val extension Logger logger = LoggerUtils::instance.logger

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

	def registerRulesWithCustomPriorities() {
		val fixedPriorityResolver = ConflictResolvers.createFixedPriorityResolver();
		fixedPriorityResolver.setPriority(createEnabledTransitionRule.ruleSpecification, 100)
		fixedPriorityResolver.setPriority(createFinishedAutomatonRule.ruleSpecification, 50)
		fixedPriorityResolver.setPriority(createTokenInTrapStateRule.ruleSpecification, 10)
		fixedPriorityResolver.setPriority(createTokenEntersTimedZoneRule.ruleSpecification, 5)
		fixedPriorityResolver.setPriority(createTokenLeavesTimedZoneRule.ruleSpecification, 1)

		EventDrivenTransformation.forScope(new EMFScope(eventModelManager.resourceSet)).addRules(rules).setConflictResolver(
			fixedPriorityResolver).create()
	}

	val createEnabledTransitionRule = createRule().name("enabled transition rule").precondition(enabledTransition).
		action [
			// Preconditions::checkArgument(eventPattern instanceof ParameterizableComplexEventPattern)	//AND precompilation causes issue here
			eventModelManager.handleEvent(transition, eventToken)

			if (event instanceof ParameterizableEventInstance) {
				for (parameter : transition.parameters) {
					// obtain the value in the observed event instance on the given position
					val parameterValueToBind = (event as ParameterizableEventInstance).getParameter(parameter.position)

					// check for existing bindings in the parameter table with the given symbolic name
					val existingBinding = eventToken.parameterTable.parameterBindings.findFirst [ binding |
						binding.symbolicName.equalsIgnoreCase(parameter.symbolicName)
					]

					if (existingBinding == null) { // if there was no parameter binding yet, it will be recorded now
						val newBinding = AutomatonFactory::eINSTANCE.createParameterBinding
						newBinding.symbolicName = parameter.symbolicName
						newBinding.value = parameterValueToBind
						eventToken.parameterTable.parameterBindings.add(newBinding)
					} else {
						// if there was a parameter binding found, the values should match
						// otherwise return before the token could be fired
						if (!existingBinding.value.equals(parameterValueToBind)) {
							return
						}
					}
				}
			}

			eventModelManager.fireTransition(transition, eventToken)
		].build

	val createFinishedAutomatonRule = createRule().name("finished automaton rule").precondition(finishedAutomaton).
		action [
			Preconditions::checkArgument(automaton.finalStates.size == 1)
			automaton.eventTokens.remove(eventToken)
			var observedPattern = new ObservedComplexEventPattern(automaton, eventToken)
			eventModelManager.callbackOnPatternRecognition(observedPattern)
			eventModelManager.cepRealm.forwardObservedEventPattern(observedPattern)
		].build

	val createTokenInTrapStateRule = createRule().name("trap state rule").precondition(tokenInTrapState).action [
		debug(String::format("Event token found in the trap state for pattern %s.", eventPattern.id));
		// var failedPattern = new InTrapComplexEventPattern(automaton)
		// eventModelManager.cepRealm.forwardFailedEventPattern(failedPattern)
		automaton.eventTokens.remove(eventToken)
	].build

	val createTokenEntersTimedZoneRule = createRule().name("token enters timed zone rule").precondition(
		tokenEntersTimedZone).action [
		TimingTable.instance.enterTimedZone(timedZone, eventToken)
	].build

	val createTokenLeavesTimedZoneRule = createRule().name("token leaves timed zone rule").precondition(
		tokenLeavesTimedZone).action [
		val canLeave = TimingTable.instance.leaveTimedZone(timedZone, eventToken);
		if (!canLeave) {
			eventToken.setCurrentState(trapState)
		}
	].build
}
