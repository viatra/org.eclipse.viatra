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
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.events.Event
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.ExpressionTreeBuilder
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Leaf
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Node
import org.eclipse.viatra.cep.vepl.vepl.AndOperator
import org.eclipse.viatra.cep.vepl.vepl.AtLeastOne
import org.eclipse.viatra.cep.vepl.vepl.Atom
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity
import org.eclipse.viatra.cep.vepl.vepl.NegOperator
import org.eclipse.viatra.cep.vepl.vepl.OrOperator
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.TypesFactory
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.viatra.cep.vepl.vepl.Infinite

class ComplexGenerator {
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider
	@Inject AnonymousPatternManager anonManager = AnonymousPatternManager.instance
	@Inject ExpressionTreeBuilder expressionTreeBuilder = ExpressionTreeBuilder.instance
	private JvmTypeReferenceBuilder typeRefBuilder

	def public generateComplexEventPatterns(List<ComplexEventPattern> patterns, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		this.typeRefBuilder = typeRefBuilder
		anonManager.flush
		for (pattern : patterns) {
			pattern.generateComplexEventPattern(acceptor)
		}
	}

	def public generateComplexEventPattern(ComplexEventPattern pattern, IJvmDeclaredTypeAcceptor acceptor) {
		if (pattern.complexEventExpression == null) {
			return
		}

		val expressionTree = expressionTreeBuilder.buildExpressionTree(pattern.complexEventExpression)

		generateComplexEventPattern(pattern, expressionTree.root, pattern.patternFqn, acceptor)
	}

	def isRoot(Node node) {
		return node.parentNode == null
	}

	def public QualifiedName generateComplexEventPattern(
		ComplexEventPattern pattern,
		Node node,
		QualifiedName className,
		IJvmDeclaredTypeAcceptor acceptor
	) {
		var List<QualifiedName> compositionEvents = Lists::newArrayList

		for (child : node.children) {
			if (child instanceof Node) {
				val QualifiedName referredAnonymousPattern = generateComplexEventPattern(pattern, (child as Node),
					getAnonymousName(pattern, anonManager.nextIndex), acceptor);
				compositionEvents.add(referredAnonymousPattern)
			} else {
				val leaf = child as Leaf
				compositionEvents.add((leaf.expression as Atom).patternCall.eventPattern.patternFqn)
			}
		}

		val QualifiedName currentClassName = if (node.root) {
				pattern.patternFqn
			} else {
				getAnonymousName(pattern, anonManager.nextIndex)
			}

		val ComplexPatternType patternType = if (node.root) {
				ComplexPatternType::NORMAL
			} else {
				ComplexPatternType::ANONYMOUS
			}

		pattern.generateComplexEventPattern(node, currentClassName, compositionEvents, acceptor, patternType)

		return currentClassName
	}

	def generateComplexEventPattern(ComplexEventPattern pattern, Node node, QualifiedName className,
		List<QualifiedName> compositionPatterns, IJvmDeclaredTypeAcceptor acceptor,
		ComplexPatternType complexPatternType) {
		acceptor.accept(pattern.toClass(className)) [
			superTypes += typeRefBuilder.typeRef(ParameterizableComplexEventPattern)
			members += pattern.toConstructor [
				body = [
					append(
						'''
							super();
						'''
					)
					append('''setOperator(''').append(
						'''«referClass(it, typeRefBuilder, pattern, EventsFactory)».eINSTANCE''').append(
						'''.«node.operator.factoryMethod»''').append(
						''');
							''')
					append(
						'''
							
							// contained event patterns
						''')
					for (p : compositionPatterns) {
						append('''addEventPatternRefrence(new ''').append(
							'''«referClass(typeRefBuilder, p, pattern)»''').append('''(), ''')
						if (node.multiplicity instanceof Multiplicity) {
							append('''«(node.multiplicity as Multiplicity).value»''').append(
								''');
									''')
						} else if (node.multiplicity instanceof Infinite) {
							append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)»''').append(
								'''.eINSTANCE.createInfinite()''').append(
								''');
									''')
						} else if (node.multiplicity instanceof AtLeastOne) {
							append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)»''').append(
								'''.eINSTANCE.createAtLeastOne()''').append(
								''');
									''')
						} else {
							append(
								'''1);
									''')
						}
					}
					if (node.timewindow != null) {
						it.append(
							'''
						
						''').append('''«referClass(it, typeRefBuilder, pattern, Timewindow)»''').append(''' timewindow = ''').
							append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)».eINSTANCE''').
							append(
								'''.createTimewindow();
									''').append(
								'''
									timewindow.setTime(«node.timewindow.time»);
									setTimewindow(timewindow);
										
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

	def expandMultiplicity(Node node, ITreeAppendable treeAppendable, ComplexEventPattern pattern) {
		val multiplicity = node.multiplicity
		switch (multiplicity) {
			Multiplicity case multiplicity:
				treeAppendable.append('''«multiplicity.value»''')
			Infinite case multiplicity:
				treeAppendable.append('''«referClass(treeAppendable, typeRefBuilder, pattern, EventsFactory)»''').
					append('''.eINSTANCE().createInfinite()''').append(''';''')
			AtLeastOne case multiplicity:
				treeAppendable.append('''«referClass(treeAppendable, typeRefBuilder, pattern, EventsFactory)»''').
					append('''.eINSTANCE().createAtLeastOne()''').append(''';''')
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

	def Iterable<? extends JvmMember> getParameterBindingDispatcher(ComplexEventPattern pattern) {
		val method = TypesFactory.eINSTANCE.createJvmOperation
		method.simpleName = "evaluateParameterBindings"
		method.setVisibility(JvmVisibility.PUBLIC)
		method.returnType = typeRefBuilder.typeRef("boolean")
		method.parameters.add(pattern.toParameter("event", typeRefBuilder.typeRef(Event)))
		method.setBody [
			append(
				'''
				if(event instanceof ''').append('''«referClass(it, typeRefBuilder, method, ParameterizableEventInstance)»''').
				append(
					'''){
						''').append(
					'''
							return evaluateParameterBindings((ParameterizableEventInstance) event);
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
		method.simpleName = "evaluateParameterBindings"
		method.setVisibility(JvmVisibility.PUBLIC)
		method.returnType = typeRefBuilder.typeRef("boolean")
		method.parameters.add(pattern.toParameter("event", typeRefBuilder.typeRef(ParameterizableEventInstance)))
		val expression = pattern.complexEventExpression
		method.setBody [
			append(
				'''«referClass(it, typeRefBuilder, pattern, Map, typeRefBuilder.typeRef("String"),
					typeRefBuilder.typeRef("Object"))»''').append(''' params = ''').append(
				'''«referClass(it, typeRefBuilder, pattern, Maps)»''')
			append(
				'''.newHashMap();
					''')
			it.getParameterMapping(expression, method)
			append(
				'''return evaluateParamBinding(params, event);
					''')
		]

		return Lists.newArrayList(method)
	}

	var firstCondition = true

	def getCondition() {
		if (firstCondition) '''if''' else '''else if'''
	}

	def void getParameterMapping(ITreeAppendable appendable, ComplexEventExpression expression, EObject ctx) {
		if (expression instanceof Atom) {
			printParameterMapping(appendable, expression as Atom, ctx)
		} else {
			appendable.getParameterMapping(expression.left, ctx)
		}
		for (right : expression.right) {
			appendable.getParameterMapping(right.expression, ctx)
		}
	}

	def void printParameterMapping(ITreeAppendable appendable, Atom atom, EObject ctx) {
		if (atom.patternCall.parameterList != null && !atom.patternCall.parameterList.parameters.empty) {
			appendable.append(
				'''
				«condition» (event instanceof ''').append(
				'''«appendable.referClass(typeRefBuilder, atom.patternCall.eventPattern.classFqn, ctx)»''').append(
				'''){
					''')
			var i = 0
			for (param : atom.patternCall.parameterList.parameters.filter[p|!p.ignorable]) {
				appendable.append('''	Object value«i» = ((''').append(
					'''«appendable.referClass(typeRefBuilder, atom.patternCall.eventPattern.classFqn, ctx)»''').append(
					''') event).getParameter(«atom.patternCall.parameterList.parameters.indexOf(param)»);
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
