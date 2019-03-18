/*******************************************************************************
 * Copyright (c) 2004-2015 Denes Harmath, Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.helper.JavaTypesHelper;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.types.BottomTypeKey;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClosureType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type;
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.util.ASTStringProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.AggregatorUtil;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.aggregators.count;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * Transforms a {@link PatternBody}.
 * 
 * @since 1.1
 */
public class PatternBodyTransformer {

    private final Pattern pattern;
    private final String patternFQN;
    private final EMFTypeSystem typeSystem;
    private final EMFTypeInferrer typeInferrer;
    private final EObject bodySource;
    private final Map<ValueReference, String> parameterMapping;

    public PatternBodyTransformer(Pattern pattern) {
        this(pattern, pattern, Collections.emptyMap());
    }

    /**
     * Initializes a transformer with a specific root object used as a source. This is used to detect in the gather
     * calls whether we are in the caller or the generated embedded patterns.
     * 
     * @param pattern
     * @param bodySource
     * @since 2.0
     */
    public PatternBodyTransformer(Pattern pattern, EObject bodySource, Map<ValueReference, String> parameterMapping) {
        super();
        this.pattern = pattern;
        patternFQN = PatternLanguageHelper.getFullyQualifiedName(pattern);
        
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        typeSystem = injector.getInstance(EMFTypeSystem.class);
        typeInferrer = injector.getInstance(EMFTypeInferrer.class);
        this.bodySource = bodySource;
        this.parameterMapping = parameterMapping;
        
    }
    
    /**
     * Traverses the given {@link PatternBody}, making proper calls to the given {@link PatternModelAcceptor} during the
     * traversal, then returns the result of the acceptor.
     * 
     * @throws ViatraQueryRuntimeException
     */
    public <Result> Result transform(PatternBody body, PatternModelAcceptor<Result> acceptor) {
        try {
            preprocessVariables(body, acceptor);
            preprocessParameters(acceptor);
            gatherBodyConstraints(body, acceptor);
            return acceptor.getResult();
        } catch (SpecificationBuilderException e) {
            e.setPatternDescription(pattern);
            throw e;
        }
    }
    
    /**
     * Builds an embedded query specification from a given {@link Constraint}, making proper calls to the given
     * {@link PatternModelAcceptor}, then returns the result of the acceptor.
     * 
     * @throws ViatraQueryRuntimeException
     * @since 2.0
     */
    public <Result> Result transform(CallableRelation constraint, PatternModelAcceptor<Result> acceptor) {
        Preconditions.checkArgument(constraint instanceof Constraint, "Embedded patterns must be created from a single constraint.");
        try {
            List<String> parameterNames = new ArrayList<>(acceptor.createParameterMapping(constraint).values());
            for (String parameter : parameterNames) {
                acceptor.acceptVariable(parameter);
            }
            acceptor.acceptExportedParameters(parameterNames);
            
            acceptor.acceptConstraint((Constraint)constraint);
            gatherConstraint((Constraint)constraint, acceptor);
            return acceptor.getResult();
        } catch (SpecificationBuilderException e) {
            e.setPatternDescription(pattern);
            throw e;
        }
    }

    private void preprocessVariables(PatternBody body, PatternModelAcceptor<?> acceptor) {
        for (Variable variable : body.getVariables()) {
            acceptor.acceptVariable(variable.getName());
        }
    }

    private void preprocessParameters(PatternModelAcceptor<?> acceptor) {
        for (Variable variable : pattern.getParameters()) {
            if (variable.getType() instanceof ClassType) {
                EClassifier classifier = ((ClassType) variable.getType()).getClassname();
                IInputKey inputKey = typeSystem.classifierToInputKey(classifier);
                acceptor.acceptTypeConstraint(ImmutableList.of(variable.getName()), inputKey);
            } else if (variable.getType() instanceof JavaType) {
                JvmDeclaredType classRef = ((JavaType) variable.getType()).getClassRef();
                IInputKey inputKey = new JavaTransitiveInstancesKey(classRef.getIdentifier());
                acceptor.acceptTypeCheckConstraint(ImmutableList.of(variable.getName()), inputKey);
            }
        }
        acceptor.acceptExportedParameters(pattern.getParameters().stream().map(Variable::getName).collect(Collectors.toList()));
    }

    private void gatherBodyConstraints(PatternBody body, PatternModelAcceptor<?> acceptor) {
        EList<Constraint> constraints = body.getConstraints();
        for (Constraint constraint : constraints) {
            acceptor.acceptConstraint(constraint);
            gatherConstraint(constraint, acceptor);
        }
    }

    private void gatherConstraint(Constraint constraint, PatternModelAcceptor<?> acceptor) {
        if (constraint instanceof EClassifierConstraint) {
            EClassifierConstraint classifierConstraint = (EClassifierConstraint) constraint;
            if (!Objects.equals(classifierConstraint, bodySource) && PatternLanguageHelper.isTransitive(classifierConstraint)) {
                gatherTransitiveClosure(classifierConstraint, acceptor);
            } else {
                gatherClassifierConstraint(classifierConstraint, acceptor);
            }
        } else if (constraint instanceof TypeCheckConstraint) {
            TypeCheckConstraint typeConstraint = (TypeCheckConstraint) constraint;
            if (!Objects.equals(typeConstraint, bodySource) && PatternLanguageHelper.isTransitive(typeConstraint)) {
                gatherTransitiveClosure(typeConstraint, acceptor);
            } else {
                gatherTypeConstraint(typeConstraint, acceptor);
            }
        } else if (constraint instanceof PatternCompositionConstraint) {
            PatternCompositionConstraint compositionConstraint = (PatternCompositionConstraint) constraint;
            gatherCompositionConstraint(compositionConstraint, acceptor);
        } else if (constraint instanceof CompareConstraint) {
            CompareConstraint compare = (CompareConstraint) constraint;
            gatherCompareConstraint(compare, acceptor);
        } else if (constraint instanceof PathExpressionConstraint) {
            PathExpressionConstraint pathExpression = (PathExpressionConstraint) constraint;
            if (!Objects.equals(pathExpression, bodySource) && PatternLanguageHelper.isTransitive(pathExpression)) {
                gatherTransitiveClosure(pathExpression, acceptor);
            } else {
                gatherPathExpression(pathExpression, acceptor);
            }
        } else if (constraint instanceof CheckConstraint) {
            final CheckConstraint check = (CheckConstraint) constraint;
            gatherCheckConstraint(check, acceptor);
        } else {
            throw new SpecificationBuilderException("Unsupported constraint type {1} in pattern {2}.",
                    new String[] { constraint.eClass().getName(), patternFQN }, "Unsupported constraint type", pattern);
        }
    }

    private void gatherPathExpression(PathExpressionConstraint pathExpression, PatternModelAcceptor<?> acceptor) {
        VariableReference src = pathExpression.getSrc();
        ValueReference dst = pathExpression.getDst();
        if (src == null || dst == null) {
            return;
        }

        String currentSrcName = getVariableName(src, acceptor);
        String finalDstName = getVariableName(dst, acceptor);

        // type constraint on source
        Type headType = pathExpression.getSourceType();
        if (headType instanceof ClassType) {
            EClassifier headClassname = ((ClassType) headType).getClassname();
            acceptor.acceptTypeConstraint(ImmutableList.of(currentSrcName), typeSystem.classifierToInputKey(headClassname));
        } else {
            throw new SpecificationBuilderException("Unsupported path expression head type {1} in pattern {2}: {3}",
                    new String[] { headType.eClass().getName(), patternFQN, typeStr(headType) },
                    "Unsupported navigation source", pattern);
        }

        // process each segment
        for(Type currentPathSegmentType : pathExpression.getEdgeTypes()) {
            String intermediateName = acceptor.createVirtualVariable();
            gatherPathSegment(currentPathSegmentType, currentSrcName, intermediateName, acceptor);

            currentSrcName = intermediateName;
        }
        // link the final step to the overall destination
        acceptor.acceptEquality(currentSrcName, finalDstName);
    }

    private void gatherPathSegment(Type segmentType, String srcName, String trgName, PatternModelAcceptor<?> acceptor) {
        if (segmentType instanceof ReferenceType) { // EMF-specific
            EStructuralFeature typeObject = ((ReferenceType) segmentType).getRefname();
            acceptor.acceptTypeConstraint(ImmutableList.of(srcName, trgName),
                    new EStructuralFeatureInstancesKey(typeObject));
            
            // new since 1.6, see Bug 512752
            //  target type constraint introduced to ensure scope semantics and reject dangling edges
            //  as EStructuralFeatureInstancesKey does not guarantee that target is in scope 
            EClassifier targetType = typeObject.getEType();
            if (targetType.eIsProxy()) {
                // Avoid writing unresolved proxies, see bug 531295
                acceptor.acceptTypeCheckConstraint(Collections.singletonList(trgName), BottomTypeKey.INSTANCE);
            } else if (targetType instanceof EClass) {
                acceptor.acceptTypeConstraint(Collections.singletonList(trgName),
                        new EClassTransitiveInstancesKey((EClass) targetType));
            } else if (targetType instanceof EDataType) {
                acceptor.acceptTypeConstraint(Collections.singletonList(trgName),
                        new EDataTypeInSlotsKey((EDataType) targetType));
            }
            
            // source type is gathered separately, no action required here
        } else
            throw new SpecificationBuilderException("Unsupported path segment type {1} in pattern {2}: {3}",
                    new String[] { segmentType.eClass().getName(), patternFQN, typeStr(segmentType) },
                    "Unsupported navigation step", pattern);
    }

    /**
     * @return the string describing a metamodel type, for debug / exception purposes
     */
    private String typeStr(Type type) {
        return NodeModelUtils.getNode(type).getText();
        //return type.getTypename() == null ? "(null)" : type.getTypename();
    }

    private void gatherCompareConstraint(CompareConstraint compare, PatternModelAcceptor<?> acceptor) {
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

    private void gatherTransitiveClosure(CallableRelation relation, PatternModelAcceptor<?> acceptor) {
        final List<ValueReference> parameters = PatternLanguageHelper.getCallParameters(relation);
        List<String> variableNames = getVariableNames(relation, acceptor);
        if (relation.getTransitive() == ClosureType.REFLEXIVE_TRANSITIVE) {
            verifyTransitiveCall(relation, parameters);
            final IInputKey universeType = typeInferrer.getType(parameters.get(0));
            verifyReflexiveCall(relation, parameters, universeType);
            acceptor.acceptBinaryReflexiveTransitiveClosure(variableNames, relation, universeType);
        } else if (relation.getTransitive() == ClosureType.TRANSITIVE) {
            verifyTransitiveCall(relation, parameters);
            acceptor.acceptBinaryTransitiveClosure(variableNames, relation);
        }
    }
    
    private void gatherCompositionConstraint(PatternCompositionConstraint constraint, PatternModelAcceptor<?> acceptor) {
        CallableRelation call = constraint.getCall();
        List<String> variableNames = getVariableNames(call, acceptor);
        if (call.getTransitive() == ClosureType.REFLEXIVE_TRANSITIVE || call.getTransitive() == ClosureType.TRANSITIVE) {
            gatherTransitiveClosure(call, acceptor);
        } else if (constraint.isNegative()) {
            acceptor.acceptNegativePatternCall(variableNames, constraint.getCall());
        } else {
            if ((constraint.getCall() instanceof PatternCall)) {
                PatternCall patternCall = (PatternCall) constraint.getCall();
                acceptor.acceptPositivePatternCall(variableNames, patternCall.getPatternRef());
            } else {
                throw new SpecificationBuilderException("Embedded positive pattern call is not supported",
                        new String[0], "Embedded positive pattern call is not supported", pattern);
            }
        }
    }

    private void verifyTransitiveCall(CallableRelation call, List<ValueReference> parameters) {
        if (parameters.size() != 2) {
            throw new SpecificationBuilderException(
                    "Transitive closure of {1} in pattern {2} is unsupported because called pattern is not binary.",
                    new String[] {ASTStringProvider.INSTANCE.doSwitch(call), patternFQN},
                    "Transitive closure only supported for binary patterns.", pattern);
        } else if (PatternLanguageHelper.isNegative(call)) {
            throw new SpecificationBuilderException("Unsupported negated transitive closure of {1} in pattern {2}",
                    new String[] {ASTStringProvider.INSTANCE.doSwitch(call), patternFQN},
                    "Unsupported negated transitive closure", pattern);
        }
    }
    
    private void verifyReflexiveCall(CallableRelation call, List<ValueReference> parameters, IInputKey universeType) {
        if (!universeType.isEnumerable()) {
            throw new SpecificationBuilderException(
                    "Reflexive transitive closure of {1} in pattern {2} is unsupported because parameter type {3} is not enumerable.",
                    new String[] {ASTStringProvider.INSTANCE.doSwitch(call), patternFQN, universeType.getPrettyPrintableName()},
                    "Reflexive transitive closure only supported for patterns with enumerable parameters.", pattern);
        }
    }

    private void gatherClassifierConstraint(EClassifierConstraint constraint, PatternModelAcceptor<?> acceptor) {
        String variableName = getVariableName(constraint.getVar(), acceptor);
        EClassifier classname = ((ClassType) constraint.getType()).getClassname();
        IInputKey inputKey = typeSystem.classifierToInputKey(classname); 
        acceptor.acceptTypeConstraint(ImmutableList.of(variableName), inputKey);
    }
    
    private void gatherTypeConstraint(TypeCheckConstraint constraint, PatternModelAcceptor<?> acceptor) {
        String variableName = getVariableName(constraint.getVar(), acceptor);
        String className = ((JavaType)constraint.getType()).getClassRef().getIdentifier();
        IInputKey inputKey = new JavaTransitiveInstancesKey(className);
        acceptor.acceptTypeCheckConstraint(ImmutableList.of(variableName), inputKey);
    }

    private void gatherCheckConstraint(final CheckConstraint check, PatternModelAcceptor<?> acceptor) {
        XExpression expression = check.getExpression();
        acceptor.acceptExpressionEvaluation(expression, null);
    }

    private String getVariableName(VariableReference variable, PatternModelAcceptor<?> acceptor) {
        // Warning! variable.getVar() does not differentiate between
        // multiple anonymous variables ('_')
        if (parameterMapping.containsKey(variable)) {
            return parameterMapping.get(variable);
        } else {
            return getVariableName(variable.getVariable(), acceptor);
        }
    }

    private String getVariableName(Variable variable, PatternModelAcceptor<?> acceptor) {
        if (variable instanceof ParameterRef) // handle referenced parameter variables
            return getVariableName(((ParameterRef) variable).getReferredParam(), acceptor); // assumed to be non-null
        else {
            return variable.getName();
        }
    }

    private List<String> getVariableNames(CallableRelation call, PatternModelAcceptor<?> acceptor) {
        return PatternLanguageHelper.getCallParameters(call).stream().map(value -> getVariableName(value, acceptor))
                .collect(Collectors.toList());
    }

    private String getVariableName(ValueReference reference, PatternModelAcceptor<?> acceptor) {
        if (parameterMapping.containsKey(reference)) {
            return parameterMapping.get(reference);
        } else if (reference instanceof VariableReference) {
            return getVariableName(((VariableReference) reference), acceptor);
        } else if (reference instanceof AggregatedValue) {
            return aggregate((AggregatedValue) reference, acceptor);
        } else if (reference instanceof FunctionEvaluationValue) {
            return eval((FunctionEvaluationValue) reference, acceptor);
        } else if (reference instanceof NumberValue) {
            XNumberLiteral literal = ((NumberValue) reference).getValue();
            return acceptor.createConstantVariable(((NumberValue) reference).isNegative(), literal);
        } else if (reference instanceof StringValue) {
            return acceptor.createConstantVariable(((StringValue) reference).getValue());
        } else if (reference instanceof EnumValue) {// EMF-specific
            return acceptor.createConstantVariable(((EnumValue) reference).getLiteral().getInstance());
        } else if (reference instanceof BoolValue) {
            return acceptor.createConstantVariable(PatternLanguageHelper.getValue(reference, Boolean.class));
        } else
            throw new SpecificationBuilderException(
                    "Unsupported value reference of type {1} from EPackage {2} currently unsupported by pattern builder in pattern {3}.",
                    new String[] { reference != null ? reference.eClass().getName() : "(null)",
                            reference != null ? reference.eClass().getEPackage().getNsURI() : "(null)",
                            pattern.getName() },
                    "Unsupported value expression", pattern);
    }

    private String eval(FunctionEvaluationValue eval, PatternModelAcceptor<?> acceptor) {
        String outputVariableName = acceptor.createVirtualVariable();

        XExpression expression = eval.getExpression();
        acceptor.acceptExpressionEvaluation(expression, outputVariableName);

        return outputVariableName;
    }

    private String aggregate(AggregatedValue reference, PatternModelAcceptor<?> acceptor) {
        String resultVariableName = acceptor.createVirtualVariable();
        CallableRelation call = reference.getCall();
        List<String> variableNames = getVariableNames(call, acceptor);
        if (JavaTypesHelper.is(reference.getAggregator(), count.class)) {
            acceptor.acceptPatternMatchCounter(variableNames, call, resultVariableName);
        } else {
            acceptor.acceptAggregator(reference.getAggregator(), reference.getAggregateType(), variableNames,
                        call, resultVariableName, AggregatorUtil.getAggregateVariableIndex(reference));
        }
        return resultVariableName;
    }

}
