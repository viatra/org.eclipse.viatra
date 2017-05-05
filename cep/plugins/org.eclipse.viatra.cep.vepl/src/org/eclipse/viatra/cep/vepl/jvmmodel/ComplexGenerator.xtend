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
import java.util.ArrayList
import java.util.List
import org.eclipse.internal.xtend.util.Pair
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.ExpressionTreeBuilder
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Leaf
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Node
import org.eclipse.viatra.cep.vepl.vepl.AndOperator
import org.eclipse.viatra.cep.vepl.vepl.AtLeastOne
import org.eclipse.viatra.cep.vepl.vepl.Atom
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ContextEnum
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
import org.eclipse.viatra.cep.vepl.vepl.Infinite
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity
import org.eclipse.viatra.cep.vepl.vepl.NegOperator
import org.eclipse.viatra.cep.vepl.vepl.OrOperator
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

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
        if (pattern.complexEventExpression == null || pattern.complexEventExpression.left == null) {
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
        var List<Pair<QualifiedName, List<String>>> compositionEvents = Lists.newArrayList

        for (child : node.children) {
            if (child instanceof Node) {
                val QualifiedName referredAnonymousPatternFqn = generateComplexEventPattern(pattern, (child as Node),
                    getAnonymousName(pattern, anonManager.nextIndex), acceptor);
                val compositionEvent = new Pair
                compositionEvent.first = referredAnonymousPatternFqn
                compositionEvent.second = null
                compositionEvents.add(compositionEvent)
            } else {
                val leaf = child as Leaf
                val eventPattern = (leaf.expression as Atom).patternCall.eventPattern
                if (!eventPattern.eIsProxy) { // defensive against partially compiled state
                    val compositionEvent = new Pair
                    compositionEvent.first = eventPattern.patternFqn;
    
                    var parameters = new ArrayList<String>
                    val paramList = (leaf.expression as Atom).patternCall.parameterList
                    if (paramList != null && !paramList.parameters.empty) {
                        for (parameter : paramList.parameters) {
                            parameters.add(parameter.name)
                        }
                    }
    
                    compositionEvent.second = parameters
                    compositionEvents.add(compositionEvent)
                }
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
        List<Pair<QualifiedName, List<String>>> compositionPatterns, IJvmDeclaredTypeAcceptor acceptor,
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
                            '''«referClass(typeRefBuilder, p.first, pattern)»''').append('''(), ''')
                        if (node.multiplicity instanceof Multiplicity) {
                            append('''«(node.multiplicity as Multiplicity).value»''')
                        } else if (node.multiplicity instanceof Infinite) {
                            append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)»''').append(
                                '''.eINSTANCE.createInfinite()''')
                        } else if (node.multiplicity instanceof AtLeastOne) {
                            append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)»''').append(
                                '''.eINSTANCE.createAtLeastOne()''')
                        } else {
                            append(
                                '''1''')
                        }
                        if (p.second != null && !p.second.empty) {
                            append(''', ''').append('''«referClass(typeRefBuilder, pattern, Lists)»''').
                                append('''.newArrayList(''')
                            append('''"«p.second.head»"''')
                            for (param : p.second.tail) {
                                append(''', "«param»"''')
                            }
                            append(''')''')
                        }
                        append(''');
                        ''')
                    }
                    if (node.timewindow != null) {
                        it.append(
                            '''
                        
                        ''').append('''«referClass(it, typeRefBuilder, pattern, Timewindow)»''').
                            append(''' timewindow = ''').
                            append('''«referClass(it, typeRefBuilder, pattern, EventsFactory)».eINSTANCE''').append(
                                '''.createTimewindow();
                            ''').append(
                                '''
                                timewindow.setTime(«node.timewindow.time»);
                                setTimewindow(timewindow);
                                    
                            ''')
                    }
                    if (pattern.parameters != null) it.append(
                        '''«FOR parameter : pattern.parameters.parameters»
                    getParameterNames().add("«parameter.name»");
                    «ENDFOR»
                    ''')
                    it.append('''
                    setId("«className.toLowerCase»");
                    ''')
                    it.append('''
                    setEventContext(''').append('''«referClass(it, typeRefBuilder, pattern, EventContext)»''').
                        append('''.''').append('''«pattern.deriveContext.literal»''').append(''');''')
                ]
            ]
        ]
        if (complexPatternType.normal) {
            FactoryManager.instance.add(className)
        } else if (complexPatternType.anonymous) {
            anonManager.add(className.toString)
            return className
        }
    }

    def deriveContext(ComplexEventPattern pattern) {
        val patternContext = pattern.context
        val defaultContext = (pattern.eContainer as EventModel).context

        if (!(patternContext.equals(ContextEnum.NOT_SET))) {
            contextMap.get(patternContext)
        } else {
            if (defaultContext.equals(ContextEnum.NOT_SET)) {
                EventContext.CHRONICLE
            } else {
                contextMap.get(defaultContext)
            }
        }
    }

    private def contextMap() {
        val map = Maps::newHashMap
        map.put(ContextEnum.NOT_SET, EventContext.NOT_SET)
        map.put(ContextEnum.CHRONICLE, EventContext.CHRONICLE)
        map.put(ContextEnum.IMMEDIATE, EventContext.IMMEDIATE)
        map.put(ContextEnum.STRICT, EventContext.STRICT_IMMEDIATE)
        map
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

    var firstCondition = true

    def getCondition() {
        if (firstCondition) '''if''' else '''else if'''
    }
}
