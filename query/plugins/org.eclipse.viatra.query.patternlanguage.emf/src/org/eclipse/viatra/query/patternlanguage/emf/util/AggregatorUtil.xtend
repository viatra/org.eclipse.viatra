/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util

import com.google.common.collect.ImmutableList
import java.util.List
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmTypeAnnotationValue
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.AggregatorType
import org.eclipse.viatra.query.patternlanguage.emf.helper.JavaTypesHelper
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

/** 
 * @author Tamas Szabo, Zoltan Ujhelyi
 * @since 2.0
 */
class AggregatorUtil {

    private static final String PARAMETER_TYPES_NAME = "parameterTypes";
    private static final String RETURN_TYPES_NAME = "returnTypes";

    private static def List<JvmType> getAggregatorType(JvmDeclaredType aggregatorType, String typeString) {
        val annotationType = aggregatorType.annotations.findFirst[annotation.qualifiedName == AggregatorType.name]
        val annotationValue = annotationType?.explicitValues?.findFirst [
            it.valueName == typeString
        ]
        if (annotationValue instanceof JvmTypeAnnotationValue) {
            annotationValue.values.map[it.type]
        }
    }

    static def List<JvmType> getReturnTypes(JvmDeclaredType aggregatorType) {
        getAggregatorType(aggregatorType, RETURN_TYPES_NAME)
    }

    static def List<JvmType> getParameterTypes(JvmDeclaredType aggregatorType) {
        getAggregatorType(aggregatorType, PARAMETER_TYPES_NAME)
    }

    /**
     * An aggregator expression may only have aggregated value as parameters if the corresponding {@link AggregatorType} annotation
     * does not define a single Void parameter. However, in that case, it _must_ have an aggregate parameter.
     */
    static def boolean mustHaveAggregatorVariables(AggregatedValue value) {
        val types = getParameterTypes(value.aggregator)
        types !== null && (types.size > 1 || (types.size == 1 && !JavaTypesHelper.is(types.get(0), Void)))
    }

    static def int getAggregateVariableIndex(AggregatedValue value) {
        var index = 0
        for (param : PatternLanguageHelper.getCallParameters(value.getCall())) {
            if (param instanceof VariableReference && (param as VariableReference).isAggregator) {
                return index
            }
            index++
        }
        return -1
    }

    static val aggregator = [VariableReference v| v.isAggregator]

    /**
     * Returns the aggregate variable the aggregator should work with. Given in a well-formed AggregatedValue only a
     * single aggregate variable should be present, this should be unique. 
     */
    static def VariableReference getAggregatorVariable(AggregatedValue value) {
        PatternLanguageHelper.getCallParameters(value.getCall()).filter(VariableReference).findFirst(aggregator)
    }

    /**
     * Returns all aggregate variables of the AggregatedValue. If the AggregatedValue has more aggregate variables,
     * it represents an error in the specification.
     */
    static def List<VariableReference> getAllAggregatorVariables(AggregatedValue value) {
        ImmutableList.copyOf(PatternLanguageHelper.getCallParameters(value.getCall()).filter(VariableReference).filter(aggregator))
    }

}
