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

package org.eclipse.viatra.cep.vepl.jvmmodel

import com.google.common.collect.Lists
import com.google.inject.Inject
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.event.ActivationState
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern
import org.eclipse.viatra.cep.core.api.rules.IActionHandler
import org.eclipse.viatra.cep.core.api.rules.ICepRule
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern
import org.eclipse.viatra.cep.vepl.vepl.Rule
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

@SuppressWarnings("discouraged", "restriction")
class RuleGenerator {

	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider

	def public generateRulesAndJobs(List<Rule> rules, IJvmDeclaredTypeAcceptor acceptor) {
		var generatedRuleClassNames = Lists.newArrayList

		for (r : rules) {
			val rule = r as Rule

			//generate RULE class
			rule.generateRuleClass(acceptor)

			//generate JOB class
			rule.generateJobClass(acceptor)

			generatedRuleClassNames.add(rule.fqn)
		}
	}

	def private generateRuleClass(Rule rule, IJvmDeclaredTypeAcceptor acceptor) {
		acceptor.accept(rule.toClass(rule.fqn)).initializeLater [
			documentation = rule.documentation
			superTypes += rule.newTypeRef(ICepRule)
			val eventPatterns = rule.eventPatterns
			members += rule.toField("eventPatterns", rule.newTypeRef(List, it.newTypeRef(EventPattern))) [
				initializer = [append('''«referClass(rule, Lists)».newArrayList()''')]
			]
			members += rule.toField("job", rule.newTypeRef(Job, it.newTypeRef(IObservableComplexEventPattern))) [
				initializer = [
					append('''new ''').append('''«it.referClass(rule.jobClassName, rule)»''').append('''(''').append(
						'''«referClass(rule, CepActivationStates)».ACTIVE)''')]
			]
			members += rule.toConstructor [
				body = [
					append('''«enumerateAssignableEventPatterns(it, rule)»''')
				]
			]
			var patternsGetter = rule.toGetter("eventPatterns", rule.newTypeRef(List, it.newTypeRef(EventPattern)))
			var jobGetter = rule.toGetter("job", rule.newTypeRef(Job, it.newTypeRef(IObservableComplexEventPattern)))
			patternsGetter.addOverrideAnnotation(rule)
			jobGetter.addOverrideAnnotation(rule)
			members += patternsGetter
			members += jobGetter
		]
		FactoryManager.instance.add(rule.fqn)
	}

	def private generateJobClass(Rule appRule, IJvmDeclaredTypeAcceptor acceptor) {
		acceptor.accept(appRule.toClass(appRule.jobClassName)).initializeLater [
			documentation = appRule.documentation
			superTypes += appRule.newTypeRef(Job, it.newTypeRef(IObservableComplexEventPattern))
			members += appRule.toConstructor [
				parameters += appRule.toParameter("activationState", appRule.newTypeRef(ActivationState))
				body = [
					append(
						'''
						super(activationState);''')]
			]
			var executeMethod = appRule.toMethod("execute", appRule.newTypeRef("void")) [
				parameters += appRule.toParameter("activation",
					appRule.newTypeRef(typeof(Activation),
						cloneWithProxies(appRule.newTypeRef(IObservableComplexEventPattern)).wildCardExtends))
				parameters += appRule.toParameter("context", appRule.newTypeRef(Context))
				if (appRule.action != null) {
					body = appRule.action
				}
				if (appRule.actionHandler != null) {
					body = [
						generateActionHandlerBody(appRule)
					]
				}
			]
			executeMethod.addOverrideAnnotation(appRule)
			var errorMethod = appRule.toMethod("handleError", appRule.newTypeRef("void")) [
				parameters += appRule.toParameter("activation",
					appRule.newTypeRef(typeof(Activation),
						cloneWithProxies(appRule.newTypeRef(IObservableComplexEventPattern)).wildCardExtends))
				parameters += appRule.toParameter("exception", appRule.newTypeRef(Exception))
				parameters += appRule.toParameter("context", appRule.newTypeRef(Context))
				body = [
					append(
						'''
						//not gonna happen''')]
			]
			errorMethod.addOverrideAnnotation(appRule)
			members += executeMethod
			members += errorMethod
		]
	}

	def private generateActionHandlerBody(ITreeAppendable appendable, EObject ctx) {
		var actionHandler = (ctx as Rule).actionHandler
		appendable.append(
			'''
			«referClass(appendable, ctx, IActionHandler)» actionHandler = new «actionHandler»();
			actionHandler.handle(activation);'''
		)
	}

	def enumerateAssignableEventPatterns(ITreeAppendable appendable, Rule rule) {
		if (rule == null || rule.eventPatterns.empty) {
			return ""
		}

		for (ep : rule.eventPatterns) {
			appendable.append('''eventPatterns.add(new ''').append('''«appendable.referClass(ep.eventPattern.patternFqn, rule)»''').append('''());''')
			if(!ep.equals(rule.eventPatterns.last)){
				appendable.append('\n')	
			}
		}
	}
}
