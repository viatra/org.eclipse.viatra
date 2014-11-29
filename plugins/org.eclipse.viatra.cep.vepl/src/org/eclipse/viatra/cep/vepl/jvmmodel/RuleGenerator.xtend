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
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder

@SuppressWarnings("discouraged", "restriction")
class RuleGenerator {

	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider
	private JvmTypeReferenceBuilder typeRefBuilder

	def public generateRulesAndJobs(List<Rule> rules, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		this.typeRefBuilder = typeRefBuilder

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
		acceptor.accept(rule.toClass(rule.fqn)) [
			documentation = rule.documentation
			superTypes += typeRefBuilder.typeRef(ICepRule)
			val eventPatterns = rule.eventPatterns
			members += rule.toField("eventPatterns", typeRefBuilder.typeRef(List, typeRefBuilder.typeRef(EventPattern))) [
				initializer = [append('''«referClass(typeRefBuilder, rule, Lists)».newArrayList()''')]
			]
			members += rule.toField("job",
				typeRefBuilder.typeRef(Job, typeRefBuilder.typeRef(IObservableComplexEventPattern))) [
				initializer = [
					append('''new ''').append('''«it.referClass(typeRefBuilder, rule.jobClassName, rule)»''').
						append('''(''').append('''«referClass(typeRefBuilder, rule, CepActivationStates)».ACTIVE)''')]
			]
			members += rule.toConstructor [
				body = [
					append('''«enumerateAssignableEventPatterns(it, rule)»''')
				]
			]
			var patternsGetter = rule.toGetter("eventPatterns",
				typeRefBuilder.typeRef(List, typeRefBuilder.typeRef(EventPattern)))
			var jobGetter = rule.toGetter("job",
				typeRefBuilder.typeRef(Job, typeRefBuilder.typeRef(IObservableComplexEventPattern)))
			patternsGetter.addOverrideAnnotation(rule)
			jobGetter.addOverrideAnnotation(rule)
			members += patternsGetter
			members += jobGetter
		]
		FactoryManager.instance.add(rule.fqn)
	}

	def private generateJobClass(Rule appRule, IJvmDeclaredTypeAcceptor acceptor) {
		acceptor.accept(appRule.toClass(appRule.jobClassName)) [
			documentation = appRule.documentation
			superTypes += typeRefBuilder.typeRef(Job, typeRefBuilder.typeRef(IObservableComplexEventPattern))
			members += appRule.toConstructor [
				parameters += appRule.toParameter("activationState", typeRefBuilder.typeRef(ActivationState))
				body = [
					append(
						'''
						super(activationState);''')]
			]
			var executeMethod = appRule.toMethod("execute", typeRefBuilder.typeRef("void")) [
				parameters += appRule.toParameter("activation",
					typeRefBuilder.typeRef(typeof(Activation),
						cloneWithProxies(typeRefBuilder.typeRef(IObservableComplexEventPattern)).wildCardExtends))
				parameters += appRule.toParameter("context", typeRefBuilder.typeRef(Context))
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
			var errorMethod = appRule.toMethod("handleError", typeRefBuilder.typeRef("void")) [
				parameters += appRule.toParameter("activation",
					typeRefBuilder.typeRef(typeof(Activation),
						cloneWithProxies(typeRefBuilder.typeRef(IObservableComplexEventPattern)).wildCardExtends))
				parameters += appRule.toParameter("exception", typeRefBuilder.typeRef(Exception))
				parameters += appRule.toParameter("context", typeRefBuilder.typeRef(Context))
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
			«referClass(appendable, typeRefBuilder, ctx, IActionHandler)» actionHandler = new «actionHandler»();
			actionHandler.handle(activation);'''
		)
	}

	def enumerateAssignableEventPatterns(ITreeAppendable appendable, Rule rule) {
		if (rule == null || rule.eventPatterns.empty) {
			return ""
		}

		for (ep : rule.eventPatterns) {
			appendable.append('''eventPatterns.add(new ''').append(
				'''«appendable.referClass(typeRefBuilder, ep.eventPattern.patternFqn, rule)»''').append('''());''')
			if (!ep.equals(rule.eventPatterns.last)) {
				appendable.append('\n')
			}
		}
	}
}
