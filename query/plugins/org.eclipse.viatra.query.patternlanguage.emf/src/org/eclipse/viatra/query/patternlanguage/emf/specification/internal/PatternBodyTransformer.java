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

package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.helper.JavaTypesHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.JavaType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.NumberValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.TypeCheckConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.viatra.query.patternlanguage.util.AggregatorUtil;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.aggregators.count;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;

import com.google.common.base.Throwables;
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
    private EMFTypeSystem typeSystem;

    public PatternBodyTransformer(Pattern pattern) {
        super();
        this.pattern = pattern;
        patternFQN = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        typeSystem = injector.getInstance(EMFTypeSystem.class);
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

    private void preprocessVariables(PatternBody body, PatternModelAcceptor<?> acceptor) {
        for (Variable variable : body.getVariables()) {
            acceptor.acceptVariable(variable);
        }
    }

    private void preprocessParameters(PatternModelAcceptor<?> acceptor) {
        EList<Variable> parameters = pattern.getParameters();
        for (Variable variable : parameters) {
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
        acceptor.acceptExportedParameters(parameters);
    }

    private void gatherBodyConstraints(PatternBody body, PatternModelAcceptor<?> acceptor) {
        EList<Constraint> constraints = body.getConstraints();
        for (Constraint constraint : constraints) {
            acceptor.acceptConstraint(constraint);
            gatherConstraint(constraint, acceptor);
        }
    }

    private void gatherConstraint(Constraint constraint, PatternModelAcceptor<?> acceptor) {
        if (constraint instanceof EClassifierConstraint) { // EMF-specific
            EClassifierConstraint classifierConstraint = (EClassifierConstraint) constraint;
            gatherClassifierConstraint(classifierConstraint, acceptor);
        } else if (constraint instanceof TypeCheckConstraint) {
                TypeCheckConstraint typeConstraint = (TypeCheckConstraint) constraint;
                gatherTypeConstraint(typeConstraint, acceptor);
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
            throw new SpecificationBuilderException("Unsupported constraint type {1} in pattern {2}.",
                    new String[] { constraint.eClass().getName(), patternFQN }, "Unsupported constraint type", pattern);
        }
    }

    private void gatherPathExpression(PathExpressionConstraint pathExpression, PatternModelAcceptor<?> acceptor) {
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
            acceptor.acceptTypeConstraint(ImmutableList.of(currentSrcName), typeSystem.classifierToInputKey(headClassname));
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

    private void gatherPathSegment(Type segmentType, String srcName, String trgName, PatternModelAcceptor<?> acceptor) {
        if (segmentType instanceof ReferenceType) { // EMF-specific
            EStructuralFeature typeObject = ((ReferenceType) segmentType).getRefname();
            acceptor.acceptTypeConstraint(ImmutableList.of(srcName, trgName),
                    new EStructuralFeatureInstancesKey(typeObject));
            
            // new since 1.6, see Bug 512752
            //  target type constraint introduced to ensure scope semantics and reject dangling edges
            //  as EStructuralFeatureInstancesKey does not guarantee that target is in scope 
            EClassifier targetType = typeObject.getEType();
            if (targetType instanceof EClass) {
                acceptor.acceptTypeConstraint(ImmutableList.of(trgName),
                        new EClassTransitiveInstancesKey((EClass) targetType));
            } else if (targetType instanceof EDataType) {
                acceptor.acceptTypeConstraint(ImmutableList.of(trgName),
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

    private void gatherCompositionConstraint(PatternCompositionConstraint constraint, PatternModelAcceptor<?> acceptor) {
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
        return getVariableName(variable.getVariable(), acceptor);
    }

    private String getVariableName(Variable variable, PatternModelAcceptor<?> acceptor) {
        if (variable instanceof ParameterRef) // handle referenced parameter variables
            return getVariableName(((ParameterRef) variable).getReferredParam(), acceptor); // assumed to be non-null
        else {
            return variable.getName();
        }
    }

    private List<String> getVariableNames(List<? extends ValueReference> valueReferences,
            final PatternModelAcceptor<?> acceptor) {
        return valueReferences.stream().map(valueReference -> {
            try {
                return getVariableName(valueReference, acceptor);
            } catch (SpecificationBuilderException e) {
                throw Throwables.propagate(e);
            }
        }).collect(Collectors.toList());
    }

    private String getVariableName(ValueReference reference, PatternModelAcceptor<?> acceptor) {
        if (reference instanceof VariableValue) {
            return getVariableName(((VariableValue) reference).getValue(), acceptor);
        } else if (reference instanceof AggregatedValue) {
            return aggregate((AggregatedValue) reference, acceptor);
        } else if (reference instanceof FunctionEvaluationValue) {
            return eval((FunctionEvaluationValue) reference, acceptor);
        } else if (reference instanceof NumberValue) {
            XNumberLiteral literal = ((NumberValue) reference).getValue();
            return acceptor.createConstantVariable(literal);
        } else if (reference instanceof StringValue) {
            return acceptor.createConstantVariable(((StringValue) reference).getValue());
        } else if (reference instanceof EnumValue) {// EMF-specific
            return acceptor.createConstantVariable(((EnumValue) reference).getLiteral().getInstance());
        } else if (reference instanceof BoolValue) {
            return acceptor.createConstantVariable(CorePatternLanguageHelper.getValue(reference, Boolean.class));
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
        PatternCall call = reference.getCall();
        Pattern patternRef = call.getPatternRef();
        List<String> variableNames = getVariableNames(call.getParameters(), acceptor);
        if (JavaTypesHelper.is(reference.getAggregator(), count.class)) {
            acceptor.acceptPatternMatchCounter(variableNames, patternRef, resultVariableName);
        } else {
            acceptor.acceptAggregator(reference.getAggregator(), reference.getAggregateType(), variableNames,
                        patternRef, resultVariableName, AggregatorUtil.getAggregateVariableIndex(reference));
        }
        return resultVariableName;
    }

}
