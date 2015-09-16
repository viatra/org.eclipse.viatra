/*******************************************************************************
 * Copyright (c) 2010-2015, Gabor Bergmann, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *    Denes Harmath - fix 464120
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.specification.builder;

import java.util.List;

import org.eclipse.incquery.patternlanguage.emf.specification.GenericEMFPatternPQuery;
import org.eclipse.incquery.patternlanguage.emf.specification.GenericQuerySpecification;
import org.eclipse.incquery.patternlanguage.emf.specification.XBaseEvaluator;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * {@link PatternModelAcceptor} implementation that constructs a {@link PBody}.
 */
public class EPMToPBody implements PatternModelAcceptor<PBody> {

    private final Pattern pattern;
    private final PBody pBody;
    private final NameToSpecificationMap patternMap;

    public EPMToPBody(Pattern pattern, PQuery query, NameToSpecificationMap patternMap) {
        this.pattern = pattern;
        pBody = new PBody(query);
        this.patternMap = patternMap;
    }

    public PBody getResult() {
        return pBody;
    }

    @Override
    public String acceptVariable(Variable variable) {
        return pBody.getOrCreateVariableByName(variable.getName()).getName();
    }

    @Override
    public String createVirtualVariable() {
        return pBody.newVirtualVariable().getName();
    }
    
    @Override
    public String createConstantVariable(Object value) {
        return pBody.newConstantVariable(value).getName();
    }

    @Override
    public void acceptExportedParameters(List<Variable> parameters) {
        List<ExportedParameter> exportedParameters = Lists.newArrayList();
        for (Variable parameter : parameters) {
            String parameterName = parameter.getName();
            PVariable pVariable = findPVariable(parameterName);
            ExportedParameter exportedParameter = new ExportedParameter(pBody, pVariable, parameterName);
            exportedParameters.add(exportedParameter);
        }
        pBody.setSymbolicParameters(exportedParameters);
    }

    private PVariable findPVariable(String variableName) {
        return pBody.getVariableByNameChecked(variableName);
    }

    @Override
    public void acceptConstraint(Constraint constraint) {
    }

    @Override
    public void acceptTypeConstraint(List<String> variableNames, IInputKey inputKey) {
        new TypeConstraint(pBody, getPVariableTuple(variableNames), inputKey);
    }

    private FlatTuple getPVariableTuple(List<String> variableNames) {
        List<PVariable> pVariables = Lists.transform(variableNames, new Function<String, PVariable>() {
            @Override
            public PVariable apply(String variableName) {
                return findPVariable(variableName);
            }
        });
        return new FlatTuple(pVariables.toArray());
    }
    
    @Override
    public void acceptPositivePatternCall(List<String> argumentVariableNames, Pattern calledPattern) {
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new PositivePatternCall(pBody, pVariableTuple, calledPQuery);
    }

    private PQuery findCalledPQuery(Pattern patternRef) {
        IQuerySpecification<?> calledSpecification = patternMap.get(CorePatternLanguageHelper.getFullyQualifiedName(patternRef));
        if (calledSpecification == null) {
            // This should only happen in case of erroneous links, e.g. link to a proxy Pattern or similar
            // otherwise pattern would be found in the name map (see SpecificationBuilder logic)
            try {
                calledSpecification = new GenericQuerySpecification(new GenericEMFPatternPQuery(patternRef, true));
            } catch (QueryInitializationException e) {
                // Cannot happen, as initialization is delayed
                throw new RuntimeException(e);
            }
        }
        return calledSpecification.getInternalQueryRepresentation();
    }

    @Override
    public void acceptNegativePatternCall(List<String> argumentVariableNames, Pattern calledPattern) {
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new NegativePatternCall(pBody, pVariableTuple, calledPQuery);
    }

    @Override
    public void acceptBinaryTransitiveClosure(List<String> argumentVariableNames, Pattern calledPattern) {
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new BinaryTransitiveClosure(pBody, pVariableTuple, calledPQuery);
    }

    @Override
    public void acceptEquality(String leftOperandVariableName, String rightOperandVariableName) {
        PVariable left = findPVariable(leftOperandVariableName);
        PVariable right = findPVariable(rightOperandVariableName);
        new Equality(pBody, left, right);
    }

    @Override
    public void acceptInequality(String leftOperandVariableName, String rightOperandVariableName) {
        PVariable left = findPVariable(leftOperandVariableName);
        PVariable right = findPVariable(rightOperandVariableName);
        new Inequality(pBody, left, right);
    }

    @Override
    public void acceptExpressionEvaluation(XExpression expression, String outputVariableName) throws SpecificationBuilderException {
        XBaseEvaluator evaluator = new XBaseEvaluator(expression, pattern);
        PVariable outputPVariable = outputVariableName == null ? null : findPVariable(outputVariableName);
        new ExpressionEvaluation(pBody, evaluator, outputPVariable);
    }

    @Override
    public void acceptPatternMatchCounter(List<String> argumentVariableNames, Pattern calledPattern, String resultVariableName) {
        PVariable resultPVariable = findPVariable(resultVariableName);
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new PatternMatchCounter(pBody, pVariableTuple, calledPQuery, resultPVariable);
    }

}
