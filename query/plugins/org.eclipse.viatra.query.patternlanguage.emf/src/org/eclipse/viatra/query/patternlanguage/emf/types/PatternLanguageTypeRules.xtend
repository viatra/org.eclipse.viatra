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
package org.eclipse.viatra.query.patternlanguage.emf.types

import com.google.inject.Inject
import java.util.List
import java.util.logging.Logger
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature
import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableValue
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.ConditionalJudgement
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.ParameterTypeJudgement
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.TypeConformJudgement
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.TypeJudgement
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.XbaseExpressionTypeJudgement
import org.eclipse.viatra.query.patternlanguage.emf.util.AggregatorUtil
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue
import java.util.HashSet

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
class PatternLanguageTypeRules {
   
   @Inject ITypeSystem typeSystem
   @Inject IBatchTypeResolver typeResolver
   @Inject NumberLiterals literals
   @Inject Logger logger
   
   /**
    * @since 1.7
    */
   def void loadParameterVariableTypes(Pattern pattern, TypeInformation information) {
       pattern.parameters.forEach[parameter|
            val typeKey = typeSystem.extractTypeDescriptor(parameter.type)
            information.declareType(parameter, typeKey)
        ]
   }
   
   def dispatch void inferTypes(Pattern pattern, TypeInformation information) {
       pattern.parameters.forEach[parameter|
            if (typeSystem.isValidType(parameter.type)) {
                val typeKey = typeSystem.extractTypeDescriptor(parameter.type)
                information.declareType(parameter, typeKey)
           }
           PatternLanguageHelper.getLocalReferencesOfParameter(parameter).forEach[ref |
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
       if (constraint.feature === CompareFeature.EQUALITY && constraint.leftOperand !== null && constraint.rightOperand !== null) {
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
            if (sourceType !== null) {
                information.provideType(new TypeJudgement(constraint.^var, sourceType))
            }
        }
    }
   
   def dispatch void inferTypes(PathExpressionConstraint constraint, TypeInformation information) {
       val sourceType = if (!typeSystem.isValidType(constraint.sourceType)) {
           BottomTypeKey.INSTANCE
       } else {
           typeSystem.extractTypeDescriptor(constraint.sourceType)           
       }
       
       var tailType = constraint.edgeTypes.last
       val targetType = if (!typeSystem.isValidType(tailType)) {
           BottomTypeKey.INSTANCE
       } else {
           typeSystem.extractTypeDescriptor(tailType)
       }
        
       if (sourceType !== null && targetType !== null) {
           information.provideType(new TypeJudgement(constraint.src, sourceType))
           information.provideType(new TypeJudgement(constraint.dst, targetType))
       }
   }
   
   def dispatch void inferTypes(AggregatedValue reference, TypeInformation information) {
        if (reference?.aggregator === null) {
            //Unresolved aggregator type, not a type error
            return
        }
        if (reference.call?.patternRef === null) {
            //Unresolved called pattern, not a type error
            return
        }
           val values = AggregatorUtil.getAllAggregatorVariables(reference)
           if (values.size === 0) {
               if (AggregatorUtil.mustHaveAggregatorVariables(reference)) {
                //Incorrect aggregation; reported separately
                return;
            }
            val returnTypes = AggregatorUtil.getReturnTypes(reference.aggregator)
            if (returnTypes === null || returnTypes.size !== 1) {
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
            if (values.size !== 1 || !AggregatorUtil.mustHaveAggregatorVariables(reference)) {
                //Incorrect aggregation; reported separately
                return;
            }
            val parameterTypes = AggregatorUtil.getParameterTypes(reference.aggregator)
            val returnTypes = AggregatorUtil.getReturnTypes(reference.aggregator)
            if (returnTypes === null || returnTypes.size !== parameterTypes.size) {
                logger.warning(String.format(
                    "Incorrect aggregator type annotation for aggregator %s: Different number of parameters and return types",
                    reference.aggregator.identifier
                ))
                return
            } 

            val returnTypeSet = new HashSet(returnTypes)
            val boolean returnTypeUnique = returnTypeSet.size == returnTypes.size

            val index = AggregatorUtil.getAggregateVariableIndex(reference)
            for (var i=0; i < returnTypes.size; i++) {
                information.provideType(new ConditionalJudgement(
                    reference, 
                    new JavaTransitiveInstancesKey(returnTypes.get(i).identifier),
                    reference.call.parameters.get(index), 
                    new JavaTransitiveInstancesKey(parameterTypes.get(i).identifier)
                ))
                // If return types are not unique for each source type, do not provide backward conditions
                if (returnTypeUnique) {
                    information.provideType(new ConditionalJudgement(
                        reference.call.parameters.get(index), 
                        new JavaTransitiveInstancesKey(parameterTypes.get(i).identifier),
                        reference, 
                        new JavaTransitiveInstancesKey(returnTypes.get(i).identifier)
                    ))
                }
            }
            
            // Aggregate variable needs to be connected to called pattern;
            // Other variables are not connected, similar to negative pattern calls
            if (index < reference.call.parameters.size && index < reference.call.patternRef.parameters.size) {
                // Only provide type judgement if appropriate number of parameters exists
                information.provideType(
                    new ParameterTypeJudgement(reference.call.parameters.get(index), reference.call.patternRef.parameters.get(index))
                )
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
   
   /**
    * @since 1.5
    */
   def dispatch void inferTypes(NumberValue reference, TypeInformation information) {
       if (reference.value !== null && !reference.value.eIsProxy) {
           val type = literals.getJavaType(reference.value)
           information.provideType(new TypeJudgement(reference, new JavaTransitiveInstancesKey(type)))
       }
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
