/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaConstantValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryReflexiveTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;

/**
 * Defines the mapping of {@link PatternModel} elements during the transformation of a {@link PatternBody}.
 * It has the responsibility to maintain an internal representation of the {@link String}s
 * both present in the {@link PatternBody} and additional {@link String}s needed for the {@link Constraint}s in the {@link PatternBody}.
 * The methods use variable names which identify these internal variables.
 * <p>
 * WARNING! Implementations of this interface are typically stateful, so don't reuse instances of them!
 * @param <Result> the type of the result
 * @since 1.1 
 */
public interface PatternModelAcceptor<Result> {

    /**
     * Returns the constructed object after traversing a {@link PatternBody}.
     */
    Result getResult();

    /**
     * Registers a {@link String} and returns the name of the internal variable.
     */
    String acceptVariable(String variableName);

    /**
     * Creates an internal virtual variable and returns its name.
     */
    String createVirtualVariable();

    /**
     * Creates an internal virtual variable, a constant constraint which binds it to the given value, and returns its name.
     */
    String createConstantVariable(Object value);
    
    /**
     * Creates an internal virtual variable, a constant constraint which binds it to the given value, and returns its name.
     * @since 1.7
     * @deprecated Use {@link #createConstantVariable(boolean, XNumberLiteral)} instead
     */
    @Deprecated
    default String createConstantVariable(XNumberLiteral numberLiteral) {
        return createConstantVariable(false, numberLiteral);
    }
    /**
     * Creates an internal virtual variable, a constant constraint which binds it to the given value, and returns its name.
     * @since 1.7
     */
    String createConstantVariable(boolean negative, XNumberLiteral numberLiteral);
    
    /**
     * Creates an internal virtual variable, a constant constraint which binds it to the given Java constant, and returns its name.
     * @since 2.6
     */
    String createConstantVariable(JavaConstantValue value);

    /**
     * Accepts the given parameters as exported parameter constraints.
     */
    void acceptExportedParameters(List<String> parameters);

    /**
     * Accepts a general {@link Constraint}.
     * This is called for every {@link Constraint} before the acceptor method depending on the actual runtime type of the {@link Constraint}. 
     */
    void acceptConstraint(Constraint constraint);

    /**
     * Accepts a {@link TypeConstraint}.
     */
    void acceptTypeConstraint(List<String> variableNames, IInputKey inputKey);
    
    /**
     * Accepts a {@link TypeFilterConstraint}.
     * @since 1.4
     */
    void acceptTypeCheckConstraint(List<String> variableNames, IInputKey inputKey);

    /**
     * Accepts a {@link PositivePatternCall}.
     */
    void acceptPositivePatternCall(List<String> argumentVariableNames, Pattern calledPattern);
    
    /**
     * Accepts a {@link NegativePatternCall} over an embedded constraint.
     * @since 2.0
     */
    void acceptNegativePatternCall(List<String> argumentVariableNames, CallableRelation embeddedConstraint);
    
    /**
     * Accepts a {@link BinaryTransitiveClosure} over an embedded constraint.
     * @since 2.0
     */
    void acceptBinaryTransitiveClosure(List<String> argumentVariableNames, CallableRelation embeddedConstraint);
    
    /**
     * Initializes a {@link BinaryReflexiveTransitiveClosure} instance over an embedded constraint.
     * @since 2.0
     */
    void acceptBinaryReflexiveTransitiveClosure(List<String> argumentVariableNames, CallableRelation embeddedConstraint, IInputKey universeType);

    /**
     * Accepts an {@link Equality}.
     */
    void acceptEquality(String leftOperandVariableName, String rightOperandVariableName);

    /**
     * Accepts an {@link Inequality}.
     */
    void acceptInequality(String leftOperandVariableName, String rightOperandVariableName);

    /**
     * Accepts an {@link ExpressionEvaluation}.
     */
    void acceptExpressionEvaluation(XExpression expression, String outputVariableName, boolean isUnwinding);

    /**
     * Accepts a {@link AbstractAggregator} over an embedded constraint.
     * @since 2.0
     */
    void acceptAggregator(JvmType aggregatorType, JvmType aggregateParameterType, List<String> argumentVariableNames,
            CallableRelation embeddedConstraint, String resultVariableName, int aggregatedColumn);
    
    /**
     * @since 2.0
     */
    void acceptPatternMatchCounter(List<String> argumentVariableNames, CallableRelation embeddedConstraint,
            String resultVariableName);

    default LinkedHashMap<ValueReference, String> createParameterMapping(CallableRelation relation) {
        LinkedHashMap<ValueReference, String> parameterMapping = new LinkedHashMap<>();
        
        List<ValueReference> parameters = PatternLanguageHelper.getCallParameters(relation);
        for (int i=0; i < parameters.size(); i++) {
            parameterMapping.put(parameters.get(i), "p" + Integer.toString(i));
        }
        return parameterMapping;
    }
}
