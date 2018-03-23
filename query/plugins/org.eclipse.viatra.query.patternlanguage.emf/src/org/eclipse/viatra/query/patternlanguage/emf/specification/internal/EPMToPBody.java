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
package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.specification.GenericEMFPatternPQuery;
import org.eclipse.viatra.query.patternlanguage.emf.specification.GenericQuerySpecification;
import org.eclipse.viatra.query.patternlanguage.emf.specification.XBaseEvaluator;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.BoundAggregator;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IAggregatorFactory;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryReflexiveTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;

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
        List<ExportedParameter> exportedParameters = new ArrayList<>();
        for (Variable parameter : parameters) {
            final String parameterName = parameter.getName();
            PVariable pVariable = findPVariable(parameterName);
            // param should always exist as PParameters are created from the input Variable list of this method
            PParameter param = pBody.getPattern().getParameters().stream().
                    filter(Objects::nonNull).
                    filter(input -> Objects.equals(input.getName(), parameterName)).
                    findAny().
                    orElseThrow(() -> new IllegalStateException(String.format("Pattern %s does not have a parameter %s", 
                            pBody.getPattern().getFullyQualifiedName(), parameterName)));
            ExportedParameter exportedParameter = new ExportedParameter(pBody, pVariable, param);
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
    
    @Override
    public void acceptTypeCheckConstraint(List<String> variableNames, IInputKey inputKey) {
        new TypeFilterConstraint(pBody, getPVariableTuple(variableNames), inputKey);
    }

    private Tuple getPVariableTuple(List<String> variableNames) {
        return Tuples.flatTupleOf(variableNames.stream().map(this::findPVariable).toArray());
    }

    @Override
    public void acceptPositivePatternCall(List<String> argumentVariableNames, Pattern calledPattern) {
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new PositivePatternCall(pBody, pVariableTuple, calledPQuery);
    }

    private PQuery findCalledPQuery(Pattern patternRef) {
        IQuerySpecification<?> calledSpecification = patternMap
                .get(PatternLanguageHelper.getFullyQualifiedName(patternRef));
        if (calledSpecification == null) {
            // This should only happen in case of erroneous links, e.g. link to a proxy Pattern or similar
            // otherwise pattern would be found in the name map (see SpecificationBuilder logic)
            calledSpecification = new GenericQuerySpecification(new GenericEMFPatternPQuery(patternRef, true));
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
    public void acceptBinaryReflexiveTransitiveClosure(List<String> argumentVariableNames, Pattern calledPattern, IInputKey universeType) {
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        new BinaryReflexiveTransitiveClosure(pBody, pVariableTuple, calledPQuery, universeType);
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
    public void acceptExpressionEvaluation(XExpression expression, String outputVariableName) {
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
    
    @Override
    public void acceptAggregator(JvmType aggregatorType, JvmType aggregateParameterType, List<String> argumentVariableNames, Pattern calledPattern, String resultVariableName, int aggregatedColumn) {
        PVariable resultPVariable = findPVariable(resultVariableName);
        Tuple pVariableTuple = getPVariableTuple(argumentVariableNames);
        PQuery calledPQuery = findCalledPQuery(calledPattern);
        
        // TODO experimental, the method of demand loading should be discussed
        // what about logging and Exception handling?
        
        // try loading the aggregator class
        try {
            //XXX Beware of Class.forName, it may cause surprises in Eclipse environments
            Class<?> clazz = Class.forName(aggregatorType.getQualifiedName());
            Object instance = clazz.newInstance();
            if (instance instanceof IAggregatorFactory) {
                //XXX Beware of Class.forName, it may cause surprises in Eclipse environments
                Class<?> aggregatorDomainClass = Void.class;
                if (aggregateParameterType != null) {
                    aggregatorDomainClass = Class.forName(aggregateParameterType.getQualifiedName());
                }
                
                BoundAggregator aggregatorBinding = ((IAggregatorFactory) instance).getAggregatorLogic(aggregatorDomainClass);
                new AggregatorConstraint(aggregatorBinding, pBody, pVariableTuple, calledPQuery, resultPVariable, aggregatedColumn);
            } else {
                throw new SpecificationBuilderException("Invalid aggregator type {1}.",
                        new String[] { instance.getClass().getName() }, "Invalid aggregator type.", pattern);
            }
        } catch (ClassNotFoundException e ) {
            throw new SpecificationBuilderException("Aggregator class cannot be found.", new String[] {},
                    "Aggregator class cannot be found.", pattern);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new SpecificationBuilderException("Cannot instantiate aggregator.", new String[] {},
                    "Cannot instantiate aggregator.", pattern);
        }                
        
    }

    @Override
    public String createConstantVariable(XNumberLiteral numberLiteral) {
        NumberLiterals literals = new NumberLiterals();
        return createConstantVariable(literals.numberValue(numberLiteral, literals.getJavaType(numberLiteral)));
    }

}
