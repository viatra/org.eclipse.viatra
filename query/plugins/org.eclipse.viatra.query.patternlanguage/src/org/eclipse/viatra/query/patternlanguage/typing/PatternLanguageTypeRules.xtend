/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.typing

import com.google.inject.Inject
import java.util.List
import java.util.logging.Logger
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AggregatedValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.BoolValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CheckConstraint
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareFeature
import org.eclipse.viatra.query.patternlanguage.patternLanguage.DoubleValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.IntValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ListValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCompositionConstraint
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.TypeCheckConstraint
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue
import org.eclipse.viatra.query.patternlanguage.typing.judgements.ConditionalJudgement
import org.eclipse.viatra.query.patternlanguage.typing.judgements.ParameterTypeJudgement
import org.eclipse.viatra.query.patternlanguage.typing.judgements.TypeConformJudgement
import org.eclipse.viatra.query.patternlanguage.typing.judgements.TypeJudgement
import org.eclipse.viatra.query.patternlanguage.typing.judgements.XbaseExpressionTypeJudgement
import org.eclipse.viatra.query.patternlanguage.util.AggregatorUtil
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver
import org.eclipse.viatra.query.patternlanguage.patternLanguage.JavaType

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 */
class PatternLanguageTypeRules {
   
   @Inject ITypeSystem typeSystem
   @Inject IBatchTypeResolver typeResolver
   @Inject Logger logger
   
   def dispatch void inferTypes(Pattern pattern, TypeInformation information) {
       pattern.parameters.forEach[parameter|
            if (typeSystem.isValidType(parameter.type)) {
                val typeKey = typeSystem.extractTypeDescriptor(parameter.type)
                information.declareType(parameter, typeKey)
           }
           CorePatternLanguageHelper.getLocalReferencesOfParameter(parameter).forEach[ref |
               information.provideType(new TypeConformJudgement(ref, parameter) {
                   
                override getDependingExpressions() {
                    #{}
                }
                
               })
               information.provideType(new TypeConformJudgement(parameter, ref))
           ]
       ]
   }
   
   def dispatch void inferTypes(CheckConstraint constraint, TypeInformation information) {
       // There are no type rules for check constraints
   }
   
   def dispatch void inferTypes(CompareConstraint constraint, TypeInformation information) {
       if (constraint.feature == CompareFeature.EQUALITY && constraint.leftOperand != null && constraint.rightOperand != null) {
           information.provideType(new TypeConformJudgement(constraint.leftOperand, constraint.rightOperand))
           information.provideType(new TypeConformJudgement(constraint.rightOperand, constraint.leftOperand))
       }
   }
   
   def dispatch void inferTypes(PatternCompositionConstraint constraint, TypeInformation information) {
       if (!constraint.isNegative) {
           // No type information can be inferred from negative calls
           val call = constraint.call
           inferCallTypes(call, information)
       }
   }
   
   private def void inferCallTypes(PatternCall call, TypeInformation information) {
       val pattern = call.patternRef
       for (var i=0; i<Math.min(call.parameters.size(), pattern.parameters.size()); i++) {
           information.provideType(new ParameterTypeJudgement(call.parameters.get(i), pattern.parameters.get(i)))
       }
   }
   
   def dispatch void inferTypes(TypeCheckConstraint constraint, TypeInformation information) {
        val constraintType = constraint.type
        if (constraintType instanceof JavaType && typeSystem.isValidType(constraintType)) {
            val sourceType = typeSystem.extractTypeDescriptor(constraintType)
            if (sourceType != null) {
                information.provideType(new TypeJudgement(constraint.^var, sourceType))
            }
        }
    }
   
   def dispatch void inferTypes(PathExpressionConstraint constraint, TypeInformation information) {
       if (!typeSystem.isValidType(constraint.head.type)) {
           return
       }
       val sourceType = typeSystem.extractTypeDescriptor(constraint.head.type)
       var tail = constraint.head.tail
       while (tail.tail != null) {
           tail = tail.tail
       }
       
       if (!typeSystem.isValidType(tail.type)) {
           return
       }
        
       val targetType = typeSystem.extractTypeDescriptor(tail.type)
       if (sourceType != null && targetType != null) {
           information.provideType(new TypeJudgement(constraint.head.src, sourceType))
           information.provideType(new TypeJudgement(constraint.head.dst, targetType))
       }
   }
   
   def dispatch void inferTypes(AggregatedValue reference, TypeInformation information) {
       inferCallTypes(reference.call, information)
        if (reference == null || reference.aggregator == null) {
            //Unresolved aggregator type, not a type error
            return
        }
   		val values = AggregatorUtil.getAllAggregatorVariables(reference)
   		if (values.size == 0) {
   		    if (AggregatorUtil.mustHaveAggregatorVariables(reference)) {
                //Incorrect aggregation; reported separately
                return;
            }
            val returnTypes = AggregatorUtil.getReturnTypes(reference.aggregator)
            if (returnTypes == null || returnTypes.size != 1) {
                logger.warning(
                    String.format("Return type for aggregator %s is non uniquely specified.",
                        reference.aggregator.simpleName
                    )
                )
                return
            } 
            val returnType = (returnTypes).get(0)
            information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(returnType.identifier)))
   		} else {
            if (values.size != 1 || !AggregatorUtil.mustHaveAggregatorVariables(reference)) {
                //Incorrect aggregation; reported separately
                return;
            }
            val parameterTypes = AggregatorUtil.getParameterTypes(reference.aggregator)
            val returnTypes = AggregatorUtil.getReturnTypes(reference.aggregator)
            if (returnTypes == null || returnTypes.size != parameterTypes.size) {
                logger.warning(String.format(
                    "Incorrect aggregator type annotation for aggregator %s: Different number of parameters and return types",
                    reference.aggregator.identifier
                ))
                return
            } 
            for (var i=0; i < returnTypes.size; i++) {
                information.provideType(new ConditionalJudgement(
                    reference, 
                    new JavaTransitiveInstancesKey(returnTypes.get(i).identifier),
                    reference.call.parameters.get(AggregatorUtil.getAggregateVariableIndex(reference)), 
                    new JavaTransitiveInstancesKey(parameterTypes.get(i).identifier)
                ))
            }
   		}    
   }
   
   def dispatch void inferTypes(Expression reference, TypeInformation information) {
       // No type judgement from abstract expression class
   }
   
   def dispatch void inferTypes(FunctionEvaluationValue reference, TypeInformation information) {
       information.provideType(new XbaseExpressionTypeJudgement(reference, reference.expression, typeResolver))
   }
   
   def dispatch void inferTypes(BoolValue reference, TypeInformation information) {
       information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(Boolean)))
   }
   
   def dispatch void inferTypes(DoubleValue reference, TypeInformation information) {
       information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(Double)))
   }
   
   def dispatch void inferTypes(IntValue reference, TypeInformation information) {
       information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(Integer)))
   }
   
   def dispatch void inferTypes(ListValue reference, TypeInformation information) {
       information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(List)))
   }
   
   def dispatch void inferTypes(StringValue reference, TypeInformation information) {
       information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(String)))
   }
   
   def dispatch void inferTypes(VariableValue reference, TypeInformation information) {
        // Variables are either parameters that are handled in inferTypes(Pattern, TypeInformation) or have no type declarations
   }
}