/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.validation.VariableReferenceCount.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableValue;
import org.eclipse.viatra.query.runtime.matchers.algorithms.UnionFind;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;

import com.google.inject.Inject;

/**
 * @since 2.0
 *
 */
public class VariableUsageCounter extends AbstractDeclarativeValidator {

    @Inject
    private IJvmModelAssociations associations;

    @Override
    public void register(EValidatorRegistrar reg) {
        // Overriding for composed validator
    }
    
    @Override
    protected List<EPackage> getEPackages() {
        List<EPackage> result = new ArrayList<>();
        result.add(PatternLanguagePackage.eINSTANCE);
        return result;
    }
    
    @Check(CheckType.NORMAL)
    public void checkVariableUsageCounters(PatternBody body) {
        UnionFind<Variable> variableUnions = calculateEqualVariables(body);
        Map<Set<Variable>, VariableReferenceCount> unifiedRefCounters = new HashMap<>();
        Map<Variable, VariableReferenceCount> individualRefCounters = new HashMap<>();
        calculateUsageCounts(body, variableUnions, individualRefCounters, unifiedRefCounters);
        for (Variable var : body.getVariables()) {
            if (var instanceof ParameterRef) {
                checkParameterUsageCounter((ParameterRef) var, individualRefCounters, unifiedRefCounters,
                        variableUnions, body);
            } else {
                checkLocalVariableUsageCounter(var, individualRefCounters, unifiedRefCounters, variableUnions);
            }
        }
    }

    private void checkParameterUsageCounter(ParameterRef var, Map<Variable, VariableReferenceCount> individualCounters,
            Map<Set<Variable>, VariableReferenceCount> unifiedRefCounters, UnionFind<Variable> variableUnions,
            PatternBody body) {
        Variable parameter = var.getReferredParam();
        VariableReferenceCount individualCounter = individualCounters.get(var);
        VariableReferenceCount unifiedCounter = unifiedRefCounters.get(variableUnions.getPartition(var));
        if (individualCounter.getReferenceCount() == 0) {
            error(String.format("Parameter '%s' is never referenced in body '%s'.", parameter.getName(),
                    getPatternBodyName(body)), parameter, PatternLanguagePackage.Literals.VARIABLE__NAME,
                    IssueCodes.SYMBOLIC_VARIABLE_NEVER_REFERENCED);
        } else if (unifiedCounter.getReferenceCount(ReferenceType.POSITIVE) == 0) {
            error(String.format("Parameter '%s' has no enumerable reference in body '%s'.", var.getName(),
                    getPatternBodyName(body)), parameter, PatternLanguagePackage.Literals.VARIABLE__NAME,
                    IssueCodes.SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE);
        }
    }

    private void checkLocalVariableUsageCounter(Variable var, Map<Variable, VariableReferenceCount> individualCounters,
            Map<Set<Variable>, VariableReferenceCount> unifiedRefCounters, UnionFind<Variable> variableUnions) {
        VariableReferenceCount individualCounter = individualCounters.get(var);
        VariableReferenceCount unifiedCounter = unifiedRefCounters.get(variableUnions.getPartition(var));
        if (individualCounter.getReferenceCount(ReferenceType.POSITIVE) == 1
                && individualCounter.getReferenceCount() == 1 && !PatternLanguageHelper.isNamedSingleUse(var)
                && !PatternLanguageHelper.isUnnamedSingleUseVariable(var)) {
            warning(String.format(
                    "Local variable '%s' is referenced only once. Is it mistyped? Start its name with '_' if intentional.",
                    var.getName()), var.getReferences().get(0), PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                    IssueCodes.LOCAL_VARIABLE_REFERENCED_ONCE);
        } else if (individualCounter.getReferenceCount() > 1 && PatternLanguageHelper.isNamedSingleUse(var)) {
            for (VariableReference ref : var.getReferences()) {
                error(String.format("Named single-use variable %s used multiple times.", var.getName()), ref,
                        PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                        IssueCodes.ANONYM_VARIABLE_MULTIPLE_REFERENCE);

            }
        } else if (unifiedCounter.getReferenceCount(ReferenceType.POSITIVE) == 0) {
            if (unifiedCounter.getReferenceCount(ReferenceType.NEGATIVE) == 0) {
                error(String.format(
                        "Local variable '%s' appears in uncountable reference(s) only, thus its value cannot be determined.",
                        var.getName()), var, PatternLanguagePackage.Literals.VARIABLE__NAME,
                        IssueCodes.LOCAL_VARIABLE_READONLY);
            } else if (individualCounter.getReferenceCount(ReferenceType.NEGATIVE) == 1
                    && individualCounter.getReferenceCount() == 1 && !PatternLanguageHelper.isNamedSingleUse(var)
                    && !PatternLanguageHelper.isUnnamedSingleUseVariable(var)) {
                warning(String.format(
                        "Local variable '%s' will be quantified because it is used only here. Acknowledge this by prefixing its name with '_'.",
                        var.getName()), var.getReferences().get(0),
                        PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                        IssueCodes.LOCAL_VARIABLE_QUANTIFIED_REFERENCE);
            } else if (unifiedCounter.getReferenceCount() > 1) {
                error(String.format(
                        "Local variable '%s' has no enumerable reference, thus its value cannot be determined.",
                        var.getName()), var.getReferences().get(0),
                        PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                        IssueCodes.LOCAL_VARIABLE_NO_POSITIVE_REFERENCE);
            }
        }
    }

    private void calculateUsageCounts(PatternBody body, UnionFind<Variable> variableUnions,
            Map<Variable, VariableReferenceCount> individualRefCounters,
            Map<Set<Variable>, VariableReferenceCount> unifiedRefCounters) {
        for (Variable var : body.getVariables()) {
            boolean isParameter = var instanceof ParameterRef;
            individualRefCounters.put(var, new VariableReferenceCount(Collections.singleton(var), isParameter));
        }
        for (Set<Variable> partition : variableUnions.getPartitions()) {
            boolean isParameter = false;
            for (Variable var : partition) {
                if (var instanceof ParameterRef) {
                    isParameter = true;
                    break;
                }
            }
            unifiedRefCounters.put(partition, new VariableReferenceCount(partition, isParameter));
        }

        TreeIterator<EObject> it = body.eAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof XExpression) {
                XExpression expression = (XExpression) obj;
                for (Variable var : PatternLanguageHelper.getReferencedPatternVariablesOfXExpression(expression,
                        associations)) {
                    individualRefCounters.get(var).incrementCounter(ReferenceType.READ_ONLY);
                    unifiedRefCounters.get(variableUnions.getPartition(var)).incrementCounter(ReferenceType.READ_ONLY);
                }
                it.prune();
            }
            if (obj instanceof VariableReference) {
                final VariableReference ref = (VariableReference) obj;
                final Variable var = ref.getVariable();
                final ReferenceType referenceClass = classifyReference(ref);
                individualRefCounters.get(var).incrementCounter(referenceClass);
                unifiedRefCounters.get(variableUnions.getPartition(var)).incrementCounter(referenceClass);
            }
        }
    }

    private UnionFind<Variable> calculateEqualVariables(PatternBody body) {
        UnionFind<Variable> unions = new UnionFind<>(body.getVariables());
        TreeIterator<EObject> it = body.eAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof CompareConstraint) {
                CompareConstraint constraint = (CompareConstraint) obj;
                if (constraint.getFeature() == CompareFeature.EQUALITY) {
                    ValueReference left = constraint.getLeftOperand();
                    ValueReference right = constraint.getRightOperand();
                    if (left instanceof VariableValue && right instanceof VariableValue) {
                        unions.union(((VariableValue) left).getValue().getVariable(),
                                ((VariableValue) right).getValue().getVariable());
                    }
                }
                it.prune();
            } else if (obj instanceof Constraint) {
                it.prune();
            }
        }
        return unions;
    }

    private String getPatternBodyName(PatternBody patternBody) {
        return (patternBody.getName() != null) ? patternBody.getName()
                : String.format("#%d", ((Pattern) patternBody.eContainer()).getBodies().indexOf(patternBody) + 1);
    }

    private ReferenceType classifyReference(VariableReference ref) {
        EObject parent = ref;
        while (parent != null && !(parent instanceof Constraint || parent instanceof AggregatedValue
                || parent instanceof FunctionEvaluationValue)) {
            parent = parent.eContainer();
        }

        if (parent instanceof CheckConstraint) {
            return ReferenceType.READ_ONLY;
        } else if (parent instanceof FunctionEvaluationValue) { // this should not be a variableReference, so probably
                                                                // this will not happen
            return ReferenceType.READ_ONLY;
        } else if (parent instanceof CompareConstraint) {
            CompareConstraint constraint = (CompareConstraint) parent;
            if (constraint.getFeature() == CompareFeature.EQUALITY) {
                final boolean leftIsVariable = constraint.getLeftOperand() instanceof VariableValue;
                final boolean rightIsVariable = constraint.getRightOperand() instanceof VariableValue;
                if (leftIsVariable && rightIsVariable) {
                    // A==A equivalence between unified variables...
                    // should be ignored in reference counting, except that it spoils quantification
                    return ReferenceType.READ_ONLY;
                } else if (leftIsVariable && !rightIsVariable) {
                    if (ref.equals(((VariableValue) constraint.getLeftOperand()).getValue())) { // this should always be
                                                                                                // true
                        return ReferenceType.POSITIVE;
                    } else
                        reportStrangeVariableRef(ref, constraint);
                } else if (rightIsVariable && !leftIsVariable) {
                    if (ref.equals(((VariableValue) constraint.getRightOperand()).getValue())) { // this should always
                                                                                                 // be true
                        return ReferenceType.POSITIVE;
                    } else
                        reportStrangeVariableRef(ref, constraint);
                } else
                    reportStrangeVariableRef(ref, constraint);
            } else if (constraint.getFeature() == CompareFeature.INEQUALITY) {
                return ReferenceType.READ_ONLY;
            } else
                reportStrangeVariableRef(ref, constraint);
        } else if (parent instanceof PatternCompositionConstraint
                && ((PatternCompositionConstraint) parent).isNegative()) {
            return ReferenceType.NEGATIVE;
        } else if (parent instanceof AggregatedValue) {
            return ReferenceType.NEGATIVE;
        } else if (parent instanceof TypeCheckConstraint) {
            return ReferenceType.READ_ONLY;
        }
        // Other constraints use positive references
        return ReferenceType.POSITIVE;
    }

    private void reportStrangeVariableRef(VariableReference ref, CompareConstraint constraint) {
        throw new IllegalStateException( // this should never come up
                "Strange reference to variable " + ref.getVar() + " in " + constraint.getClass().getName());
    }

}
