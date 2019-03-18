/** 
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.common.base.Joiner
import java.util.Arrays
import java.util.List
import org.eclipse.emf.common.util.Enumerator
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternBodyTransformer
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternModelAcceptor
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator
import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryReflexiveTransitiveClosure
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtend2.lib.StringConcatenationClient.TargetStringConcatenation
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XNumberLiteral
import org.eclipse.xtext.xbase.compiler.output.ImportingStringConcatenation
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

/** 
 * {@link PatternModelAcceptor} implementation that generates body code for {@link IQuerySpecification} classes.
 * Implementation note: it extends {@link StringConcatenationClient} so that it can be used with {@link ImportingStringConcatenation}.
 * @since 1.1
 * @noreference
 */
class BodyCodeGenerator extends StringConcatenationClient {

    val Pattern pattern
    val PatternBody body
    val CallableRelation call
    extension val EMFPatternLanguageJvmModelInferrerUtil util
    val IErrorFeedback feedback
    val JvmTypeReferenceBuilder typeReferences
    static val INDENTATION = '''    '''

    new(Pattern pattern, PatternBody body, EMFPatternLanguageJvmModelInferrerUtil util, IErrorFeedback feedback,
        JvmTypeReferenceBuilder typeReferences) {
        this.pattern = pattern
        this.body = body
        this.util = util
        this.feedback = feedback
        this.typeReferences = typeReferences
        this.call = null
    }
    
    new(Pattern pattern, CallableRelation call, EMFPatternLanguageJvmModelInferrerUtil util, IErrorFeedback feedback,
        JvmTypeReferenceBuilder typeReferences) {
        this.pattern = pattern
        this.body = null
        this.call = call
        this.util = util
        this.feedback = feedback
        this.typeReferences = typeReferences
    }

    override protected appendTo(TargetStringConcatenation target) {
        val acceptor = new PatternModelAcceptor<Void>() {

            var int lastId = 0

            override getResult() {
            }

            override acceptVariable(String variableName) {
                declareVariable(variableName, target)
                variableName
            }

            override createVirtualVariable() {
                val virtualVariableName = '''.virtual{«lastId»}'''
                lastId++
                declareVariable(virtualVariableName, target)
                virtualVariableName
            }

            override createConstantVariable(Object value) {
                val virtualVariable = createVirtualVariable
                target.append('''new ''')
                target.append(ConstantValue)
                target.append('''(body, «virtualVariable.escape», «value.outputConstant»);
                ''')
                virtualVariable
            }

            override createConstantVariable(boolean negative, XNumberLiteral numberLiteral) {
                val virtualVariable = createVirtualVariable
                target.append('''new ''')
                target.append(ConstantValue)
                target.append('''(body, «virtualVariable.escape», «IF negative»-«ENDIF»«numberLiteral.outputConstant»);
                ''')
                virtualVariable
            }

            private def StringConcatenationClient outputConstant(Object constant) {
                switch constant {
                    EEnumLiteral: {
                        val enumeration = constant.EEnum
                        val ePackage = enumeration.EPackage
                        '''getEnumLiteral("«ePackage.nsURI»", "«enumeration.name»", "«constant.name»").getInstance()'''
                    }
                    Enumerator: {
                        '''«constant.class.canonicalName».get("«constant.literal»")'''
                    }
                    XNumberLiteral: {
                        val literals = new NumberLiterals
                        '''«literals.toJavaLiteral(constant, true)»'''
                    }
                    String: '''"«constant»"'''
                    default: '''«constant»'''
                }
            }

            override acceptExportedParameters(List<String> parameterNames) {
                target.append('''
                body.setSymbolicParameters(''')
                target.append(Arrays)
                target.append('''.<''')
                target.append(ExportedParameter)
                target.append('''>asList(
                            ''')
                parameterNames.forEach[parameterName, index |
                    target.append('''   new ''')
                    target.append(ExportedParameter)
                    target.append('''(body, «parameterName.escape», «parameterName.PParameterName»)''')
                    if (index < parameterNames.length - 1) { // XXX separator logic
                        target.append(',')
                    }
                    target.append('\n')
                ]
                target.append('''));
                ''')
            }

            override acceptConstraint(Constraint constraint) {
                val stringRepresentation = NodeModelUtils::getNode(constraint)?.text?.replaceAll('\r\n?|\n','')
                if (stringRepresentation !== null) {
                    target.append('''// «stringRepresentation»
                    ''')
                }
            }

            override acceptTypeConstraint(List<String> variableNames, IInputKey key) {
                target.append('''new ''')
                target.append(TypeConstraint)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«variableNames.output»), ''')
                target.appendInputKey(key, false)
                target.append(''');
                ''')
            }

            override acceptTypeCheckConstraint(List<String> variableNames, IInputKey key) {
                target.append('''new ''')
                target.append(TypeFilterConstraint)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«variableNames.output»), ''')
                target.appendInputKey(key, false)
                target.append(''');
                ''')
            }

            private def output(List<String> variableNames) {
                Joiner.on(", ").join(variableNames.map[escape])
            }

            override acceptPositivePatternCall(List<String> argumentVariableNames, Pattern calledPattern) {
                target.append('''new ''')
                target.append(PositivePatternCall)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                referPQuery(calledPattern, pattern, target)
                target.append(''');
                ''')
            }

            private def referPQuery(Pattern calledPattern, Pattern callerPattern, TargetStringConcatenation target) {
                if (calledPattern == callerPattern) {
                    target.append('''this''')
                } else {
                    target.append(calledPattern.findInferredSpecification)
                    target.append('''.instance().getInternalQueryRepresentation()''')
                }
            }
            
            private def referOrEmbedPQuery(CallableRelation call, Pattern callerPattern, TargetStringConcatenation target) {
                if (call instanceof PatternCall) {
                    referPQuery(call.patternRef, callerPattern, target)
                } else {
                    target.append('''new ''')
                    target.append(call.findInferredClass(BaseGeneratedEMFPQuery))
                    target.append('''()''')
                }
                
            }

            override acceptNegativePatternCall(List<String> argumentVariableNames, CallableRelation call) {
                target.append('''new ''')
                target.append(NegativePatternCall)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                referOrEmbedPQuery(call, pattern, target)
                target.append(''');
                ''')
            }

            override acceptBinaryTransitiveClosure(List<String> argumentVariableNames, CallableRelation call) {
                target.append('''new ''')
                target.append(BinaryTransitiveClosure)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                referOrEmbedPQuery(call, pattern, target)
                target.append(''');
                ''')
            }
            
            override acceptBinaryReflexiveTransitiveClosure(List<String> argumentVariableNames, CallableRelation call, IInputKey universeType) {
                target.append('''new ''')
                target.append(BinaryReflexiveTransitiveClosure)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                referOrEmbedPQuery(call, pattern, target)
                target.append(''', ''')
                target.appendInputKey(universeType, false)
                target.append(''');
                ''')
            }

            override acceptEquality(String leftOperandVariableName, String rightOperandVariableName) {
                target.append('''new ''')
                target.append(Equality)
                target.append('''(body, «leftOperandVariableName.escape», «rightOperandVariableName.escape»);
                ''')
            }

            override acceptInequality(String leftOperandVariableName, String rightOperandVariableName) {
                target.append('''new ''')
                target.append(Inequality)
                target.append('''(body, «leftOperandVariableName.escape», «rightOperandVariableName.escape»);
                ''')
            }

            override acceptExpressionEvaluation(XExpression expression, String outputVariableName) {
                val inputParameterNames = PatternLanguageHelper.getUsedVariables(expression, body.getVariables()).map[name]
                target.append(
                        '''
                «if (inputParameterNames.empty) {
                            feedback.reportError(expression, "No parameters defined", EMFPatternLanguageJvmModelInferrer.SPECIFICATION_BUILDER_CODE, Severity.WARNING, IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
                        }»
                new ''')
                target.append(ExpressionEvaluation)
                target.append('''(body, new ''')
                target.append(IExpressionEvaluator)
                target.append('''() {''')

                target.newLine
                target.newLine

                target.append('''
                «INDENTATION»@Override
                public String getShortDescription() {
                    return "Expression evaluation from pattern «pattern.name»";
                }
                
                @Override
                public Iterable<String> getInputParameterNames() {
                    return ''', INDENTATION)
                target.append(Arrays)
                target.
                    append('''.asList(«FOR name : inputParameterNames SEPARATOR ", "»"«name»"«ENDFOR»);''')
                target.append('''}''', INDENTATION)

                target.newLine
                target.newLine

                target.append('''
                «INDENTATION»@Override
                public Object evaluateExpression(''', INDENTATION)
                target.append(IValueProvider)
                target.append(''' provider) throws Exception {''')
                target.newLine
                val variables = variables(expression)
                for (variable : variables) {
                    val type = variable.calculateType.eraseGenerics
                    target.append(INDENTATION)
                    target.append(INDENTATION)
                    target.append(type)
                    target.append(''' «variable.name» = (''')
                    target.append(type)
                    target.append(''') provider.getValue("«variable.name»");''')
                    target.newLine
                }
                target.append(INDENTATION)
                target.append(INDENTATION)
                target.
                    append('''return «expressionMethodName(expression)»(«FOR variable : variables SEPARATOR ', '»«variable.name»«ENDFOR»);''')
                target.newLine

                target.append(INDENTATION)
                target.append("}")
                target.newLine

                target.
                    append('''}, «IF outputVariableName !== null » «outputVariableName.escape» «ELSE» null«ENDIF»); ''')
                target.newLine
            }

            private def eraseGenerics(JvmTypeReference reference) {
                if (reference instanceof JvmParameterizedTypeReference) {
                    typeReferences.typeRef(reference.type, reference.arguments.map[typeReferences.wildcard])
                } else {
                    reference
                }

            }

            override acceptPatternMatchCounter(List<String> argumentVariableNames, CallableRelation call,
                String resultVariableName) {
                target.append('''new ''')
                target.append(PatternMatchCounter)
                target.append('''(body, ''')
                target.append(Tuples)
                target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                referOrEmbedPQuery(call, pattern, target)
                target.append(''', «resultVariableName.escape»);
                ''')
            }

            override acceptAggregator(JvmType aggregatorFactoryType, JvmType aggregatorParameterType,
                List<String> argumentVariableNames, CallableRelation call, String resultVariableName,
                int aggregatedColumn) {
                    target.append('''new ''')
                    target.append(AggregatorConstraint)
                    target.append('''(''')
                    target.append('''new ''')
                    target.append(aggregatorFactoryType)
                    target.append('''().getAggregatorLogic(''')
                    if (aggregatorParameterType === null) {
                        target.append(Void)
                    } else {
                        target.append(aggregatorParameterType)
                    }
                    target.append('''.class), body, ''')
                    target.append(Tuples)
                    target.append('''.flatTupleOf(«argumentVariableNames.output»), ''')
                    referOrEmbedPQuery(call, pattern, target)
                    target.append(''', «resultVariableName.escape», «aggregatedColumn»);
                    ''')
                }

            } // PatternModelAcceptor
            if (body !== null) {
                new PatternBodyTransformer(pattern).transform(body, acceptor)
            } else /*if (call !== null)*/ {
                new PatternBodyTransformer(pattern, call, acceptor.createParameterMapping(call)).transform(call, acceptor)
            }
        }

        /**
         * Generates a {@link PVariable} declaration with the given name.
         */
        private static def declareVariable(String variableName, TargetStringConcatenation target) {
            target.append(PVariable)
            target.append(''' «variableName.escape» = body.getOrCreateVariableByName("«variableName»");
            ''')
        }

        private static def escape(String name) {
            "var_" + name.replaceAll("[\\.\\{\\}<>#]", "_")
        }

    }
    