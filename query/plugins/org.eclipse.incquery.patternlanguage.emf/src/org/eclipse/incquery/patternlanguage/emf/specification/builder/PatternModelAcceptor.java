/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.specification.builder;

import java.util.List;

import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.xtext.xbase.XExpression;

/**
 * Defines the mapping of {@link PatternModel} elements during the transformation of a {@link PatternBody}.
 * It has the responsibility to maintain an internal representation of the {@link Variable}s
 * both present in the {@link PatternBody} and additional {@link Variable}s needed for the {@link Constraint}s in the {@link PatternBody}.
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
     * Registers a {@link Variable} and returns the name of the internal variable.
     */
    String acceptVariable(Variable variable);

    /**
     * Creates an internal virtual variable and returns its name.
     */
    String createVirtualVariable();

    /**
     * Creates an internal virtual variable, a constant constraint which binds it to the given value, and returns its name.
     */
    String createConstantVariable(Object value);

    /**
     * Accepts the given parameters as exported parameter constraints.
     */
    void acceptExportedParameters(List<Variable> parameters);

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
     * Accepts a {@link PositivePatternCall}.
     */
    void acceptPositivePatternCall(List<String> argumentVariableNames, Pattern calledPattern);  

    /**
     * Accepts a {@link NegativePatternCall}.
     */
    void acceptNegativePatternCall(List<String> argumentVariableNames, Pattern calledPattern);

    /**
     * Accepts a {@link BinaryTransitiveClosure}.
     */
    void acceptBinaryTransitiveClosure(List<String> argumentVariableNames, Pattern calledPattern);

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
    void acceptExpressionEvaluation(XExpression expression, String outputVariableName) throws SpecificationBuilderException;

    /**
     * Accepts a {@link PatternMatchCounter}.
     */
    void acceptPatternMatchCounter(List<String> argumentVariableNames, Pattern calledPattern, String resultVariableName);

}
