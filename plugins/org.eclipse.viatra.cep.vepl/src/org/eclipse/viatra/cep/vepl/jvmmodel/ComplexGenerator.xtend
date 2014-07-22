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
import com.google.common.collect.Maps
import com.google.inject.Inject
import java.util.List
import java.util.Map
import java.util.Map.Entry
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.events.Event
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory
import org.eclipse.viatra.cep.vepl.vepl.AndOperator
import org.eclipse.viatra.cep.vepl.vepl.AugmentedExpression
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ComplexExpressionAtom
import org.eclipse.viatra.cep.vepl.vepl.EventPattern
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
import org.eclipse.viatra.cep.vepl.vepl.NegOperator
import org.eclipse.viatra.cep.vepl.vepl.OrOperator
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter
import org.eclipse.viatra.cep.vepl.vepl.PlainExpression
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.TypesFactory
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

class ComplexGenerator {
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider
	@Inject AnonymousPatternManager anonManager = AnonymousPatternManager.instance
	@Inject EventPatternDisassembler disassembler = EventPatternDisassembler.instance

	def public generateComplexEventPatterns(List<ComplexEventPattern> patterns, IJvmDeclaredTypeAcceptor acceptor) {
		anonManager.flush
		for (pattern : patterns) {
			pattern.generateComplexEventPattern(acceptor)
		}
	}

	def public generateComplexEventPattern(ComplexEventPattern pattern, IJvmDeclaredTypeAcceptor acceptor) {
		var patternGroups = disassembler.decomposeComplexPattern(pattern.complexEventExpression).entries.toList

		if (!patternGroups.empty) {
			generateComplexEventPattern(pattern, patternGroups, null, acceptor)
		}
	}

	def public generateComplexEventPattern(
		ComplexEventPattern pattern,
		List<Entry<ComplexEventOperator, List<EventPattern>>> patternGroups,
		QualifiedName anonymousPatternFqn,
		IJvmDeclaredTypeAcceptor acceptor
	) {

		if (patternGroups.size > 1) { //anonymous classes will be generated
			val currentGroup = patternGroups.head
			val remaining = patternGroups.filter[pg|pg != currentGroup].toList
			var newAnonymousPatternFqn = pattern.generateComplexEventPattern(
				getAnonymousName(pattern, anonManager.nextIndex), currentGroup, anonymousPatternFqn, acceptor,
				ComplexPatternType::ANONYMOUS)
			generateComplexEventPattern(pattern, remaining, newAnonymousPatternFqn, acceptor)
		} else {
			pattern.generateComplexEventPattern(pattern.patternFqn, patternGroups.head, anonymousPatternFqn, acceptor,
				ComplexPatternType::NORMAL)
		}
	}

	def public generateComplexEventPattern(
		ComplexEventPattern pattern,
		QualifiedName className,
		Entry<ComplexEventOperator, List<EventPattern>> patternGroup,
		QualifiedName anonymousPatternFqn,
		IJvmDeclaredTypeAcceptor acceptor,
		ComplexPatternType complexPatternType
	) {
		acceptor.accept(pattern.toClass(className)).initializeLater [
			superTypes += pattern.newTypeRef(ParameterizableComplexEventPattern)
			members += pattern.toConstructor [
				body = [
					append(
						'''
							super();
						'''
					)
					append('''setOperator(''').append('''«referClass(it, pattern, EventsFactory)».eINSTANCE''').
						append('''.«patternGroup.key.factoryMethod»''').append(
							''');
								''')
					it.append(
						'''
							
							// composition events
						''')
					if (anonymousPatternFqn != null) {
						it.append('''getCompositionEvents().add(new ''').append(
							'''«referClass(anonymousPatternFqn, pattern)»''').append(
							'''());
								''')
					}
					for (p : patternGroup.value) {
						it.append('''getCompositionEvents().add(new ''').append(
							'''«referClass(p.patternFqn, pattern)»''').append(
							'''());
								''')
					}
					it.append(
						'''
						setId("«className.toLowerCase»");''')
				]
			]
			if (complexPatternType.normal) {
				members += (pattern as ComplexEventPattern).parameterBindingDispatcher
				members += (pattern as ComplexEventPattern).simpleBindingMethod
			}
		]
		if (complexPatternType.normal) {
			FactoryManager.instance.add(className)
		} else if (complexPatternType.anonymous) {
			anonManager.add(className.toString)
			return className
		}
	}

	def boolean isNormal(ComplexPatternType complexPatternType) {
		return complexPatternType.equals(ComplexPatternType::NORMAL)
	}

	def boolean isAnonymous(ComplexPatternType complexPatternType) {
		return complexPatternType.equals(ComplexPatternType::ANONYMOUS)
	}

	def dispatch getFactoryMethod(FollowsOperator operator) {
		return "createFOLLOWS()"
	}

	def dispatch getFactoryMethod(OrOperator operator) {
		return "createOR()"
	}

	def dispatch getFactoryMethod(AndOperator operator) {
		return "createAND()"
	}

	def dispatch getFactoryMethod(UntilOperator operator) {
		return "createUNTIL()"
	}

	def dispatch getFactoryMethod(NegOperator operator) {
		return "createNEG()"
	}

	def unwrapExpression(ComplexEventExpression expression) {
		switch (expression) {
			PlainExpression:
				return expression
			AugmentedExpression: {
				return expression.expression
			}
		}
	}

	def Iterable<? extends JvmMember> getParameterBindingDispatcher(ComplexEventPattern pattern) {
		val method = TypesFactory.eINSTANCE.createJvmOperation
		method.simpleName = "evaluateParameterBindigs"
		method.setVisibility(JvmVisibility.PUBLIC)
		method.returnType = pattern.newTypeRef("boolean")
		method.parameters.add(method.toParameter("event", pattern.newTypeRef(Event)))
		method.setBody [
			append(
				'''
				if(event instanceof ''').append('''«referClass(it, method, ParameterizableEventInstance)»''').append(
				'''){
					''').append(
				'''
						return evaluateParameterBindigs((ParameterizableEventInstance) event);
					}
				''').append(
				'''
				return true;''')
		]

		method.addOverrideAnnotation(pattern)
		return Lists.newArrayList(method)
	}

	def Iterable<? extends JvmMember> getSimpleBindingMethod(ComplexEventPattern pattern) {
		val method = TypesFactory.eINSTANCE.createJvmOperation
		method.simpleName = "evaluateParameterBindigs"
		method.setVisibility(JvmVisibility.PUBLIC)
		method.returnType = pattern.newTypeRef("boolean")
		method.parameters.add(method.toParameter("event", pattern.newTypeRef(ParameterizableEventInstance)))
		val expression = pattern.complexEventExpression.unwrapExpression
		method.setBody [
			append('''«referClass(it, pattern, Map, pattern.newTypeRef("String"), pattern.newTypeRef("Object"))»''').
				append(''' params = ''').append('''«referClass(it, pattern, Maps)»''')
			append(
				'''.newHashMap();
					''')
			//			getParameterMapping(expression.unwrapCompositionEventsWithParameterList, method).append(
			//				'''
			//				return true;''')
			it.getParameterMapping(expression.headExpressionAtom, method, "if")
			for (expressionAtom : expression.tailExpressionAtoms) {
				it.getParameterMapping(expressionAtom.expressionAtom, method, "else if")
			}
			append(
				'''return evaluateParamBinding(params, event);
					''')
		]

		return Lists.newArrayList(method)
	}

	def getParameterMapping(ITreeAppendable appendable, ComplexExpressionAtom expressionAtom, EObject ctx,
		String condition) {
		if (expressionAtom.patternCall.parameterList != null &&
			!expressionAtom.patternCall.parameterList.parameters.empty) {
			appendable.append(
				'''
				«condition» (event instanceof ''').append(
				'''«appendable.referClass(expressionAtom.patternCall.eventPattern.classFqn, ctx)»''').append(
				'''){
					''')

			var i = 0
			for (param : expressionAtom.patternCall.parameterList.parameters.filter[p|!p.ignorable]) {
				appendable.append('''	Object value«i» = ((''').append(
					'''«appendable.referClass(expressionAtom.patternCall.eventPattern.classFqn, ctx)»''').append(
					''') event).getParameter(«i»);
						''')
				appendable.append(
					'''	params.put("«param.name»", value«i»);
						''')
				i = i + 1
			}
			appendable.append(
				'''}
					''')
		}
	}

	def isIgnorable(PatternCallParameter parameter) {
		if (parameter.name.startsWith("_")) {
			return true
		}
		return false
	}
}
