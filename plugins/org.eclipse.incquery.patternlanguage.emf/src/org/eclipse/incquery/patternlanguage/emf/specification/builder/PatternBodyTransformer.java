/*******************************************************************************
 * Copyright (c) 2004-2015 Denes Harmath, Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Denes Harmath - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.specification.builder;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.incquery.patternlanguage.emf.specification.XBaseEvaluator;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatorExpression;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CountAggregator;
import org.eclipse.incquery.patternlanguage.patternLanguage.DoubleValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Transforms a {@link PatternBody}.
 * @since 1.1
 */
public class PatternBodyTransformer {

    private final Pattern pattern;
    private final String patternFQN;

    public PatternBodyTransformer(Pattern pattern) {
        super();
        this.pattern = pattern;
        patternFQN = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
    }

    /**
     * Traverses the given {@link PatternBody},
     * making proper calls to the given {@link PatternModelAcceptor} during the traversal,
     * then returns the result of the acceptor.
     */
    public <Result> Result transform(PatternBody body, PatternModelAcceptor<Result> acceptor) throws QueryInitializationException {
        try {
            preprocessVariables(body, acceptor);
            preprocessParameters(body, acceptor);
            gatherBodyConstraints(body, acceptor);
            return acceptor.getResult();
        } catch (SpecificationBuilderException e) {
            e.setPatternDescription(pattern);
            throw e;
        }
    }

    private void preprocessVariables(PatternBody body, PatternModelAcceptor<?> acceptor) {
        for (Variable variable : body.getVariables()) {
            acceptor.acceptVariable(variable);
        }
    }

    private void preprocessParameters(final PatternBody body, PatternModelAcceptor<?> acceptor) {
        EList<Variable> parameters = pattern.getParameters();
        for (Variable variable : parameters) {
            if (variable.getType() != null && variable.getType() instanceof ClassType) {
                EClassifier classifier = ((ClassType) variable.getType()).getClassname();
                IInputKey inputKey = classifierToInputKey(classifier);
                acceptor.acceptTypeConstraint(ImmutableList.of(variable.getName()), inputKey);
            }
        }
        acceptor.acceptExportedParameters(parameters);
    }

    private IInputKey classifierToInputKey(EClassifier classifier) {
        IInputKey key = classifier instanceof EClass ?
                new EClassTransitiveInstancesKey((EClass) classifier) :
                new EDataTypeInSlotsKey((EDataType) classifier);
        return key;
    }

    private void gatherBodyConstraints(PatternBody body, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        EList<Constraint> constraints = body.getConstraints();
        for (Constraint constraint : constraints) {
            acceptor.acceptConstraint(constraint);
            gatherConstraint(constraint, acceptor);
        }
    }

    private void gatherConstraint(Constraint constraint, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        if (constraint instanceof EClassifierConstraint) { // EMF-specific
            EClassifierConstraint classifierConstraint = (EClassifierConstraint) constraint;
            gatherClassifierConstraint(classifierConstraint, acceptor);
        } else if (constraint instanceof PatternCompositionConstraint) {
            PatternCompositionConstraint compositionConstraint = (PatternCompositionConstraint) constraint;
            gatherCompositionConstraint(compositionConstraint, acceptor);
        } else if (constraint instanceof CompareConstraint) {
            CompareConstraint compare = (CompareConstraint) constraint;
            gatherCompareConstraint(compare, acceptor);
        } else if (constraint instanceof PathExpressionConstraint) {
            PathExpressionConstraint pathExpression = (PathExpressionConstraint) constraint;
            gatherPathExpression(pathExpression, acceptor);
        } else if (constraint instanceof CheckConstraint) {
            final CheckConstraint check = (CheckConstraint) constraint;
            gatherCheckConstraint(check, acceptor);
        } else {
            throw new SpecificationBuilderException("Unsupported constraint type {1} in pattern {2}.", new String[] {
                    constraint.eClass().getName(), patternFQN }, "Unsupported constraint type", pattern);
        }
    }

    private void gatherPathExpression(PathExpressionConstraint pathExpression, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        PathExpressionHead head = pathExpression.getHead();
        VariableReference src = head.getSrc();
        ValueReference dst = head.getDst();
        if (src == null || dst == null) {
            return;
        }
        
        String currentSrcName = getVariableName(src, acceptor);
        String finalDstName = getVariableName(dst, acceptor);
        PathExpressionTail currentTail = head.getTail();

        // type constraint on source
        Type headType = head.getType();
        if (headType instanceof ClassType) {
            EClassifier headClassname = ((ClassType) headType).getClassname();
            acceptor.acceptTypeConstraint(ImmutableList.of(currentSrcName), classifierToInputKey(headClassname));
        } else {
            throw new SpecificationBuilderException("Unsupported path expression head type {1} in pattern {2}: {3}",
                    new String[] { headType.eClass().getName(), patternFQN, typeStr(headType) },
                    "Unsupported navigation source", pattern);
        }

        // process each segment
        while (currentTail != null) {
            Type currentPathSegmentType = currentTail.getType();
            currentTail = currentTail.getTail();

            String intermediateName = acceptor.createVirtualVariable();
            gatherPathSegment(currentPathSegmentType, currentSrcName, intermediateName, acceptor);

            currentSrcName = intermediateName;
        }
        // link the final step to the overall destination
        acceptor.acceptEquality(currentSrcName, finalDstName);
    }

    private void gatherPathSegment(Type segmentType, String srcName, String trgName, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        if (segmentType instanceof ReferenceType) { // EMF-specific
            EStructuralFeature typeObject = ((ReferenceType) segmentType).getRefname();
            acceptor.acceptTypeConstraint(ImmutableList.of(srcName, trgName), new EStructuralFeatureInstancesKey(typeObject));
        } else
            throw new SpecificationBuilderException("Unsupported path segment type {1} in pattern {2}: {3}", new String[] {
                    segmentType.eClass().getName(), patternFQN, typeStr(segmentType) }, "Unsupported navigation step",
                    pattern);
    }

    /**
     * @return the string describing a metamodel type, for debug / exception purposes
     */
    private String typeStr(Type type) {
        return type.getTypename() == null ? "(null)" : type.getTypename();
    }

    private void gatherCompareConstraint(CompareConstraint compare, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        String left = getVariableName(compare.getLeftOperand(), acceptor);
        String right = getVariableName(compare.getRightOperand(), acceptor);
        switch (compare.getFeature()) {
        case EQUALITY:
            acceptor.acceptEquality(left, right);
            break;
        case INEQUALITY:
            acceptor.acceptInequality(left, right);
            break;
        }
    }

    private void gatherCompositionConstraint(PatternCompositionConstraint constraint, PatternModelAcceptor<?> acceptor)
            throws SpecificationBuilderException {
        PatternCall call = constraint.getCall();
        Pattern patternRef = call.getPatternRef();
        List<String> variableNames = getVariableNames(call.getParameters(), acceptor);
        if (!call.isTransitive()) {
            if (constraint.isNegative())
                acceptor.acceptNegativePatternCall(variableNames, patternRef);
            else
                acceptor.acceptPositivePatternCall(variableNames, patternRef);
        } else {
            if (call.getParameters().size() != 2)
                throw new SpecificationBuilderException(
                        "Transitive closure of {1} in pattern {2} is unsupported because called pattern is not binary.",
                        new String[] { CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN },
                        "Transitive closure only supported for binary patterns.", pattern);
            else if (constraint.isNegative())
                throw new SpecificationBuilderException("Unsupported negated transitive closure of {1} in pattern {2}",
                        new String[] { CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN },
                        "Unsupported negated transitive closure", pattern);
            else
                acceptor.acceptBinaryTransitiveClosure(variableNames, patternRef);
        }
    }

    private void gatherClassifierConstraint(EClassifierConstraint constraint, PatternModelAcceptor<?> acceptor) {
        String variableName = getVariableName(constraint.getVar(), acceptor);
        EClassifier classname = ((ClassType) constraint.getType()).getClassname();
        IInputKey inputKey = classifierToInputKey(classname);
        acceptor.acceptTypeConstraint(ImmutableList.of(variableName), inputKey);
    }

    private void gatherCheckConstraint(final CheckConstraint check, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        XExpression expression = check.getExpression();
        acceptor.acceptExpressionEvaluation(expression, null);
    }

    private String getVariableName(VariableReference variable, PatternModelAcceptor<?> acceptor) {
        // Warning! variable.getVar() does not differentiate between
        // multiple anonymous variables ('_')
        return getVariableName(variable.getVariable(), acceptor);
    }

    private String getVariableName(Variable variable, PatternModelAcceptor<?> acceptor) {
        if (variable instanceof ParameterRef) // handle referenced parameter variables
            return getVariableName(((ParameterRef) variable).getReferredParam(), acceptor); // assumed to be non-null
        else {
            return variable.getName();
        }
    }
    
    private List<String> getVariableNames(List<? extends ValueReference> valueReferences, final PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        return ImmutableList.copyOf( // XXX transformation must be performed eagerly because it can cause side effects 
            Lists.transform(valueReferences, new Function<ValueReference, String>() {
                @Override
                public String apply(ValueReference valueReference) {
                    try {
                        return getVariableName(valueReference, acceptor);
                    } catch (SpecificationBuilderException e) {
                        throw Throwables.propagate(e);
                    }
                }
            })
        );
    }

    private String getVariableName(ValueReference reference, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        if (reference instanceof VariableValue)
            return getVariableName(((VariableValue) reference).getValue(), acceptor);
        else if (reference instanceof AggregatedValue)
            return aggregate((AggregatedValue) reference, acceptor);
        else if (reference instanceof FunctionEvaluationValue)
            return eval((FunctionEvaluationValue) reference, acceptor);
        else if (reference instanceof IntValue)
            return acceptor.createConstantVariable(((IntValue) reference).getValue());
        else if (reference instanceof StringValue)
            return acceptor.createConstantVariable(((StringValue) reference).getValue());
        else if (reference instanceof EnumValue) // EMF-specific
            return acceptor.createConstantVariable(((EnumValue) reference).getLiteral().getInstance());
        else if (reference instanceof DoubleValue) {
            return acceptor.createConstantVariable(((DoubleValue) reference).getValue());
        } else if (reference instanceof BoolValue) {
            return acceptor.createConstantVariable(((BoolValue) reference).isValue());
        } else
            throw new SpecificationBuilderException(
                    "Unsupported value reference of type {1} from EPackage {2} currently unsupported by pattern builder in pattern {3}.",
                    new String[] { reference != null ? reference.eClass().getName() : "(null)",
                            reference != null ? reference.eClass().getEPackage().getNsURI() : "(null)",
                            pattern.getName() }, "Unsupported value expression", pattern);
    }

    private String eval(FunctionEvaluationValue eval, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        String outputVariableName = acceptor.createVirtualVariable();

        XExpression expression = eval.getExpression();
        acceptor.acceptExpressionEvaluation(expression, outputVariableName);

        return outputVariableName;
    }

    private String aggregate(AggregatedValue reference, PatternModelAcceptor<?> acceptor) throws SpecificationBuilderException {
        String resultVariableName = acceptor.createVirtualVariable();

        PatternCall call = reference.getCall();
        Pattern patternRef = call.getPatternRef();
        List<String> variableNames = getVariableNames(call.getParameters(), acceptor);

        AggregatorExpression aggregator = reference.getAggregator();
        if (aggregator instanceof CountAggregator) {
            acceptor.acceptPatternMatchCounter(variableNames, patternRef, resultVariableName);
        } else {
            throw new SpecificationBuilderException("Unsupported aggregator expression type {1} in pattern {2}.",
                    new String[] { aggregator.eClass().getName(), patternFQN }, "Unsupported aggregator expression",
                    pattern);
        }
        return resultVariableName;
    }

}
