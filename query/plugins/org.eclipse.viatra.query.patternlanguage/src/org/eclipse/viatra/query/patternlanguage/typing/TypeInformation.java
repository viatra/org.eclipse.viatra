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
package org.eclipse.viatra.query.patternlanguage.typing;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.AbstractTypeJudgement;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.ConditionalJudgement;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.ParameterTypeJudgement;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.TypeConformJudgement;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.TypeJudgement;
import org.eclipse.viatra.query.patternlanguage.typing.judgements.XbaseExpressionTypeJudgement;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * This class is used to store type information for selected patterns
 * 
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class TypeInformation {

    private static class HashSetSupplier<T> implements Supplier<Set<T>> {
        @Override
        public Set<T> get() {
            return Sets.newHashSet();
        }
    }
    
    final Map<Expression, IInputKey> typeDeclarations = Maps.newHashMap();
    final SetMultimap<Expression, AbstractTypeJudgement> dependencies = Multimaps.newSetMultimap(
            Maps.<Expression, Collection<AbstractTypeJudgement>> newHashMap(),
            new HashSetSupplier<AbstractTypeJudgement>());
    final SetMultimap<Expression, AbstractTypeJudgement> typeJudgements = Multimaps.newSetMultimap(
            Maps.<Expression, Collection<AbstractTypeJudgement>> newHashMap(),
            new HashSetSupplier<AbstractTypeJudgement>());
    final Set<Pattern> processedPatterns = Sets.newHashSet();
    
    private SetMultimap<Expression, IInputKey> expressionTypes = Multimaps.newSetMultimap(
            Maps.<Expression, Collection<IInputKey>> newHashMap(), 
            new HashSetSupplier<IInputKey>());
    private ITypeSystem typeSystem;
    
    public boolean isProcessed(Pattern pattern) {
        return processedPatterns.contains(pattern);
    }

    public void setProcessed(Pattern pattern) {
        processedPatterns.add(pattern);
    }

    public TypeInformation(ITypeSystem typeSystem) {
        this.typeSystem = typeSystem;
    }

    private Expression replaceVariableReferences(Expression expression) {
        if (expression instanceof VariableReference) {
            return ((VariableReference) expression).getVariable();
        } else if (expression instanceof VariableValue) {
            return ((VariableValue) expression).getValue().getVariable();
        } else {
            return expression;
        }
    }

    /**
     * Describes that the selected expression is declared to have the selected type
     * @param expression
     * @param type
     */
    public void declareType(Expression expression, IInputKey type) {
        typeDeclarations.put(expression, type);
        provideType(new TypeJudgement(expression, type));
    }
    
    /**
     * Describes that a constraint ensures that a variable has a specific type
     * 
     * @param constraint
     */
    public void provideType(AbstractTypeJudgement constraint) {
        final Expression expression = replaceVariableReferences(constraint.getExpression());
        
        typeJudgements.put(expression, constraint);
        for (Expression dependency : constraint.getDependingExpressions()) {
            dependencies.put(replaceVariableReferences(dependency), constraint);
        }

        processConstraint(constraint, expression);
    }

    private void processConstraint(AbstractTypeJudgement constraint, Expression expression) {
        if (constraint instanceof TypeJudgement) {
            processConstraint((TypeJudgement) constraint, expression);
        } else if (constraint instanceof ParameterTypeJudgement) {
            processConstraint((ParameterTypeJudgement) constraint, expression);
        } else if (constraint instanceof TypeConformJudgement) {
            processConstraint((TypeConformJudgement) constraint, expression);
        } else if (constraint instanceof XbaseExpressionTypeJudgement) {
            processConstraint((XbaseExpressionTypeJudgement) constraint, expression);
        } else if (constraint instanceof ConditionalJudgement) {
            processConstraint((ConditionalJudgement) constraint, expression);
        } 
    }

    private void processConstraint(TypeJudgement constraint, Expression expression) {
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);
        final IInputKey newType = constraint.getType();
        if (newType != null) {
            final Set<IInputKey> mergedTypeInformation = typeSystem.addTypeInformation(knownTypes, newType);
        
            updateTypes(expression, mergedTypeInformation);
        }
    }

    private void processConstraint(ConditionalJudgement constraint, Expression expression) {
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);
        
        Expression condition = constraint.getConditionExpression();
        Set<IInputKey> conditionTypes = getMinimizedTypes(condition);
        if (conditionTypes.size() != 1) {
            return;
        }
        IInputKey actualConditionType = conditionTypes.iterator().next();
        IInputKey conditionType = constraint.getConditionType();
        boolean conforms = typeSystem.isConformant(conditionType, actualConditionType);
        if (conforms) {
            final Set<IInputKey> mergedTypeInformation = typeSystem.addTypeInformation(knownTypes, constraint.getType());
            dependencies.remove(expression, constraint);
            updateTypes(expression, mergedTypeInformation);
        }
    }
    
    private void processConstraint(TypeConformJudgement constraint, Expression expression) {
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);
        final Set<IInputKey> newType = expressionTypes.get(replaceVariableReferences(constraint.getConformsTo()));
        if (!newType.isEmpty()) {
            final Set<IInputKey> mergedTypeInformation = typeSystem.addTypeInformation(knownTypes, newType);
            
            updateTypes(expression, mergedTypeInformation);
            
        }
    }
    
    private void processConstraint(ParameterTypeJudgement constraint, Expression expression) {
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);
        final IInputKey newType = getType(replaceVariableReferences(constraint.getConformsTo()));
        if (newType != null) {
            final Set<IInputKey> mergedTypeInformation = typeSystem.addTypeInformation(knownTypes, ImmutableSet.of(newType));
        
            updateTypes(expression, mergedTypeInformation);
        }
    }

    private void processConstraint(XbaseExpressionTypeJudgement constraint, Expression expression) {
        if (Iterables.any(constraint.getDependingExpressions(), new Predicate<Expression>() {

            @Override
            public boolean apply(Expression input) {
                return getMinimizedTypes(input).size() != 1;
            }
        })) {
            return;
        }
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);
        final IInputKey newType = constraint.getExpressionType();
        if (newType != null) {
            final Set<IInputKey> mergedTypeInformation = typeSystem.addTypeInformation(knownTypes, newType);

            updateTypes(expression, mergedTypeInformation);
        }
    }

    private void updateTypes(Expression expression, Set<IInputKey> newInformation) {
        final Set<IInputKey> knownTypes = expressionTypes.get(expression);

        if (!newInformation.equals(knownTypes)) {
            //New type information provided
            expressionTypes.replaceValues(expression, newInformation);
            // Reevaluate dependent constraints
            for (AbstractTypeJudgement dependency : dependencies.get(expression)) {
                processConstraint(dependency, replaceVariableReferences(dependency.getExpression()));
            }
        }
    }
    
    private Set<IInputKey> getMinimizedTypes(Expression expression) {
        Expression escapedExpression = replaceVariableReferences(expression);
        if (typeDeclarations.containsKey(escapedExpression)) {
            return ImmutableSet.of(typeDeclarations.get(escapedExpression));
        }
        return typeSystem.minimizeTypeInformation(getAllTypes(escapedExpression), true);
    }
    
    public IInputKey getType(Expression expression) {
        if (CorePatternLanguageHelper.isParameter(expression) && typeSystem.isValidType(((Variable)expression).getType())) {
            return typeSystem.extractTypeDescriptor(((Variable)expression).getType());
        }
        final Set<IInputKey> allTypes = getMinimizedTypes(expression);
        if (allTypes.size() >= 1) {
            return allTypes.iterator().next();
        } else {
            return null;
        }
    }
    
    public Set<IInputKey> getAllTypes(Expression expression) {
        Expression escapedExpression = replaceVariableReferences(expression);
        Set<IInputKey> existingInformation = expressionTypes.get(escapedExpression);
        if (existingInformation == null || existingInformation.isEmpty()) {
            for (AbstractTypeJudgement judgement : typeJudgements.get(escapedExpression)) {
                provideType(judgement);
            }
            existingInformation = expressionTypes.get(escapedExpression);
        }
        return existingInformation;
    }
    
    /**
     * @since 1.4
     */
    public Set<IInputKey> getAllPossibleParameterTypes(Variable parameter) {
        Preconditions.checkArgument(CorePatternLanguageHelper.isParameter(parameter), "Variable must represent a pattern parameter.");
        return Sets.newHashSet(Iterables
                .filter(Iterables.transform(CorePatternLanguageHelper.getLocalReferencesOfParameter(parameter),
                        new Function<Variable, IInputKey>() {

                            @Override
                            public IInputKey apply(Variable input) {
                                return getType(input);
                            }
                        }), Predicates.notNull()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Judgements: \n");
        for (AbstractTypeJudgement judgement : typeJudgements.values()) {
            appendExpression(judgement.getExpression(), sb);
            sb.append(" :\n  ");
            appendJudgement(judgement, sb);
            sb.append("\n");
        }
        sb.append("\n Variables: \n");
        for (Variable ex : Iterables.filter(expressionTypes.keySet(), Variable.class)) {
            appendVariableName(ex, sb);
            sb.append(" |- ");
            sb.append(getType(ex));
            sb.append(" (");
            Joiner.on(" /\\ ").appendTo(sb, getAllTypes(ex));
            sb.append(") ");
            sb.append("\n");
        }
        sb.append("\n Dependencies: \n");
        for (Variable var : Iterables.filter(dependencies.keySet(), Variable.class)) {
            for (AbstractTypeJudgement judgement : dependencies.get(var)) {
                appendVariableName(var, sb);
                sb.append(" --> ");
                appendJudgement(judgement, sb);
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    private void appendJudgement(AbstractTypeJudgement judgement, StringBuilder sb) {
        if (judgement instanceof TypeJudgement) {
            TypeJudgement typeJudgement = (TypeJudgement) judgement;
            sb.append("Type Judgement ");
            appendExpression(typeJudgement.getExpression(), sb);
            sb.append(" :- ");
            sb.append(typeSystem.typeString(typeJudgement.getType()));
        } else if (judgement instanceof ParameterTypeJudgement) {
            ParameterTypeJudgement parameterTypeJudgement = (ParameterTypeJudgement) judgement;
            sb.append("Call Judgement ");
            appendExpression(parameterTypeJudgement.getExpression(), sb);
            sb.append(" :- ");
            appendExpression(parameterTypeJudgement.getConformsTo(), sb);
        } else if (judgement instanceof TypeConformJudgement) {
            TypeConformJudgement typeConformJudgement = (TypeConformJudgement) judgement;
            sb.append("Conform Judgement ");
            appendExpression(typeConformJudgement.getExpression(), sb);
            sb.append(" :- ");
            appendExpression(typeConformJudgement.getConformsTo(), sb);
        } else if (judgement instanceof XbaseExpressionTypeJudgement) {
            XbaseExpressionTypeJudgement expressionTypeJudgement = (XbaseExpressionTypeJudgement) judgement;
            sb.append("Xbase ");
            appendExpression(expressionTypeJudgement.getExpression(), sb);
            sb.append(" :- ");
            sb.append(typeSystem.typeString(expressionTypeJudgement.getExpressionType()));
        } else if (judgement instanceof ConditionalJudgement) {
            ConditionalJudgement conditional = (ConditionalJudgement) judgement;
            sb.append("if (");
            appendExpression(conditional.getConditionExpression(), sb);
            sb.append(" :- ");
            sb.append(typeSystem.typeString(conditional.getConditionType()));
            sb.append(") -> ");
            appendExpression(conditional.getExpression(), sb);
            sb.append(" :- ");
            sb.append(typeSystem.typeString(conditional.getType()));
        }
    }
    
    private void appendExpression(Expression ex, StringBuilder sb) {
        Expression redEx = replaceVariableReferences(ex);
        if (redEx instanceof Variable) {
            appendVariableName((Variable) redEx, sb);
        } else {
            sb.append(redEx);
        }
    }
    
    private void appendVariableName(Variable var, StringBuilder sb) {
        sb.append(var.getName());
        sb.append(" (");
        Pattern p = EcoreUtil2.getContainerOfType(var, Pattern.class);
        sb.append(p.getName());
        PatternBody b = EcoreUtil2.getContainerOfType(var, PatternBody.class);
        if (b != null) {
            sb.append("#");
            sb.append(p.getBodies().indexOf(b));
        }
        sb.append(")");
    }
}