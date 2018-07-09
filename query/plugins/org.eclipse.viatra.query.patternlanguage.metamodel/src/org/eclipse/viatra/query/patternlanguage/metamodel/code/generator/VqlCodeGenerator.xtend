/**
 * Copyright (c) 2010-2018, Mocsai Krisztain, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Mocsai Krisztian - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.metamodel.code.generator

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.AggregatedValue
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.BooleanLiteral
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.CallableRelation
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.CheckConstraint
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.CompareConstraint
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.EClassifierReference
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.EnumValue
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.Expression
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.FunctionEvaluationValue
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.GraphPattern
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.GraphPatternBody
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.JavaClassReference
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.ListLiteral
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.NumberLiteral
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.ParameterRef
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternCall
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternCompositionConstraint
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternPackage
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.StringLiteral
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.UnaryType
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.Variable
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.Parameter
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.ParameterDirection
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.ClosureType

class VqlCodeGenerator {

    static val INDENTATION = '''    '''

    def generate(PatternPackage patternPackage) {
        val patternPackageCode = '''
            package «patternPackage.packageName»
            
            «FOR packageImport : patternPackage.packageImports»
                «packageImport.packageImportCode»
            «ENDFOR»
            
            «FOR graphPattern : patternPackage.patterns»
                «graphPattern.graphPatternCode»
            «ENDFOR»
        '''

        return patternPackageCode
    }

    private def packageImportCode(EPackage ePackage) {
        '''import "«ePackage.nsURI»"
        '''
    }

    private def graphPatternCode(GraphPattern graphPattern) {
        val bodySeperator = '''
        
        } or {'''

        '''
            «IF graphPattern.private»private «ENDIF»pattern «graphPattern.name»(
            «graphPattern.parameterListCode»
            ) {
            «FOR body : graphPattern.bodies SEPARATOR bodySeperator»
                «body.patternBodyCode»
            «ENDFOR»
            }
            
        '''
    }

    private def parameterListCode(GraphPattern graphPattern) {
        '''«FOR parameter : graphPattern.parameters SEPARATOR ","»
            «INDENTATION»«parameter.parameterCode»
            «ENDFOR»'''
    }

    private def parameterCode(Parameter parameter) {
        val direction = switch parameter.direction {
            case ParameterDirection.IN: '''in '''
            case ParameterDirection.OUT: '''out '''
            case ParameterDirection.INOUT: ''''''
        }

        '''«direction»«parameter.name»: «parameter.types.head.typeCode»'''
    }

    private def getTypeCode(UnaryType type) {
        switch type {
            EClassifierReference: '''«type.classifier.name»'''
            JavaClassReference: '''«type.className»'''
            // TODO It should be filtered out with validation
            default:
                errorCode("Type of the parameter should defined.")
        }
    }

    private def patternBodyCode(GraphPatternBody body) {
        '''
            «FOR variable : body.nodes.filter(Variable)»
              «FOR type : variable.types»
                «INDENTATION»«type.typeCode»(«variable.expressionCode»);
              «ENDFOR»
            «ENDFOR»
            «FOR constraint : body.constraints»
                «INDENTATION»«constraint.constraintCode»;
            «ENDFOR»
        '''
    }

    private dispatch def String constraintCode(CheckConstraint constraint) {
        '''check(«constraint.expression»)'''
    }

    private dispatch def String constraintCode(PatternCompositionConstraint constraint) {
        '''«IF constraint.negative»neg «ENDIF»«constraint.call.callableRelationCode»'''
    }

    private dispatch def String constraintCode(CompareConstraint constraint) {
        '''«constraint.leftOperand.expression.expressionCode» «constraint.feature» «constraint.rightOperand.expression.expressionCode»'''
    }

    private dispatch def String constraintCode(PathExpressionConstraint constraint) {
        val firstEdgeType = constraint.edgeTypes.head
        if (firstEdgeType === null) {
            // TODO It should be filtered out with validation 
            return '''«errorCode("PathExpressionConstraint should have one edgeType at least.")»'''
        }
        val feature = firstEdgeType.refname

        if (feature.eIsProxy) {
            // TODO It should be filtered out with validation 
            return '''«errorCode("Unresolvable edge type.")»'''
        }
        val mainClass = (feature.eContainer as EClass).name
        /* TODO referenceList should be type correct between each consecutive pair in edgeTypes
         * It should be filtered out with validation
         */
        val referenceList = '''«FOR edgeType : constraint.edgeTypes SEPARATOR "."»«edgeType.refname.name»«ENDFOR»'''

        '''«mainClass».«referenceList»(«constraint.src.expression.expressionCode», «constraint.dst.expression.expressionCode»)'''
    }

    private def String expressionCode(Expression exp) {
        switch exp {
            EnumValue: '''«exp.literal.literal»'''
            ParameterRef: '''«exp.referredParam.name»'''
            Variable: '''«exp.name»'''
            StringLiteral: '''"«exp.value»"'''
            NumberLiteral: '''«exp.value»'''
            BooleanLiteral: '''«exp.value.toString»'''
            ListLiteral: '''«FOR ref : exp.values SEPARATOR ", "»«ref.expression.expressionCode»«ENDFOR»'''
            FunctionEvaluationValue: '''eval(«exp.expression»)'''
            AggregatedValue: '''«exp.call.callableRelationCode»'''
        }

    }

    private def String callableRelationCode(CallableRelation callableRelation) {
        val closureType = switch callableRelation.transitive {
            case ClosureType.ORIGINAL: ''''''
            case ClosureType.REFLEXIVE_TRANSITIVE: '''*'''
            case ClosureType.TRANSITIVE: '''+'''
        }

        switch callableRelation {
            PathExpressionConstraint: '''«closureType»«callableRelation.constraintCode»'''
            PatternCall: '''«callableRelation.PatternCallCode(closureType)»'''
        }
    }

    private def PatternCallCode(PatternCall patternCall, String closureType) {
        val parameterList = '''«FOR parameterRef : patternCall.parameters SEPARATOR ", "»«parameterRef.expression.expressionCode»«ENDFOR»'''

        '''find «patternCall.patternRef.name»«closureType»(«parameterList»)'''
    }

    private def errorCode(String description) {
        '''<<<<<<<«description»>>>>>>>'''
    }

}
