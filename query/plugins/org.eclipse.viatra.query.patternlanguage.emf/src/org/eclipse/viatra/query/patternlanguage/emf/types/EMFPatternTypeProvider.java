/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.types;

import static com.google.common.base.Objects.equal;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ComputationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ListValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.LiteralValueReference;
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
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.CompilerPhases;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.IResolvedTypes;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * A type provider for inferring the correct types for the pattern variables. It handles all constraints in the model
 * which can modify the outcome of the type, but it has some practical limitations, as the calculation of the proper
 * type can be time consuming in some cases.
 */
@Singleton
@SuppressWarnings("restriction")
@Deprecated
public class EMFPatternTypeProvider implements IEMFTypeProvider {

    @Inject
    private TypeReferences typeReferences;

    @Inject
    private Primitives primitives;

    @Inject
    private CompilerPhases compilerPhases;
    @Inject
    private IResourceScopeCache cache;

    @Inject
    private IErrorFeedback errorFeedback;

    @Inject
    private IBatchTypeResolver typeResolver;

    @Inject
    private IMetamodelProvider metamodelProvider;
    
    @Inject
    private NumberLiterals literals;

    private static final int RECURSION_CALLING_LEVEL_LIMIT = 5;

    @Override
    public JvmTypeReference getVariableType(final Variable variable) {
        return cache.get(variable, variable.eResource(), new Provider<JvmTypeReference>() {

            @Override
            public JvmTypeReference get() {
                return doGetVariableType(variable);
            }

        });
    }

    protected JvmTypeReference doGetVariableType(Variable variable) {
        EClassifier classifier = getClassifierForVariable(variable);
        return getJvmType(classifier, variable);
    }

    @Override
    public JvmTypeReference getJvmType(EClassifier classifier, EObject context) {
        if (classifier != null) {
            String className = metamodelProvider.getQualifiedClassName(classifier, context);
            if (!Strings.isNullOrEmpty(className)) {
                return getTypeReferenceForTypeName(className, context);
            }
        }
        // Return Object or EObject if no classifier can be found
        final Class<?> clazz = (classifier instanceof EClass) ? EObject.class : Object.class;
        return typeReferences.getTypeForName(clazz, context);
    }

    @Override
    public EClassifier getClassifierForVariable(Variable variable) {
        if (!variable.eIsProxy()) {
            EObject container = variable.eContainer();
            if (container instanceof Pattern) {
                return getClassifierForParameterVariable((Pattern) container, variable, 0);
            } else if (container instanceof PatternBody) {
                return getClassifierForVariableWithPatternBody((PatternBody) container, variable, 0, null);
            }
        }
        return null;
    }

    private Set<EClassifier> minimizeClassifiersList(Set<EClassifier> classifierList) {
        final Set<EClassifier> resultList = new HashSet<EClassifier>(classifierList);
        if (resultList.size() > 1) {
            for (EClassifier classifier : classifierList) {
                if ("EObject".equals(classifier.getName())
                        && EcorePackage.eNS_URI.equals(classifier.getEPackage().getNsURI())) {
                    resultList.remove(classifier);
                } else if (classifier instanceof EClass) {
                    for (EClass eClass : ((EClass) classifier).getEAllSuperTypes()) {
                        if (resultList.contains(eClass)) {
                            resultList.remove(eClass);
                        }
                    }
                } else if (classifier instanceof EDataType) {
                    final EDataType eDataType = (EDataType) classifier;
                    if (Iterables.any(Iterables.filter(resultList, EDataType.class), new Predicate<EDataType>() {

                        @Override
                        public boolean apply(EDataType dataType) {
                            if (dataType == null) {
                                return false;
                            } else if (dataType.equals(eDataType)) {
                                return false;
                            } else if (dataType.getInstanceClassName() != null
                                    && eDataType.getInstanceClassName() != null) {
                                return dataType.getInstanceClassName().equals(eDataType.getInstanceClassName())
                                        && resultList.contains(eDataType);
                            }
                            return false;
                        }
                    })) {
                        resultList.remove(eDataType);
                    }
                }
            }
        }

        return resultList;
    }

    private EClassifier getClassifierForParameterVariable(Pattern pattern, Variable parameterVariable,
            int recursionCallingLevel) {
        EClassifier explicitType = getExplicitClassifierForPatternParameterVariable(parameterVariable);
        if (explicitType != null) { // there is an explicit type, it overrides inference
            return explicitType;
        } else { // no explicit type, try to infer something from bodies
            return getClassifierForVariableWithPattern(pattern, parameterVariable, recursionCallingLevel);
        }
    }

    private EClassifier getClassifierForVariableWithPattern(Pattern pattern, Variable variable,
            int recursionCallingLevel) {
        Set<EClassifier> intermediateResultList = new HashSet<EClassifier>();
        for (PatternBody body : pattern.getBodies()) {
            EClassifier classifier = getClassifierForVariableWithPatternBody(body, variable, recursionCallingLevel,
                    null);
            if (classifier != null) {
                intermediateResultList.add(classifier);
            }
        }

        if (!intermediateResultList.isEmpty()) {
            if (intermediateResultList.size() == 1) {
                return (EClassifier) intermediateResultList.toArray()[0];
            } else {
                Set<EClassifier> resultSuperTypes = null;
                for (EClassifier classifier : intermediateResultList) {
                    if (classifier instanceof EClass) {
                        if (resultSuperTypes == null) {
                            resultSuperTypes = new LinkedHashSet<EClassifier>();
                            resultSuperTypes.addAll(((EClass) classifier).getEAllSuperTypes());
                            resultSuperTypes.add(classifier);
                        } else {
                            Set<EClassifier> nextSet = new LinkedHashSet<EClassifier>();
                            nextSet.addAll(((EClass) classifier).getEAllSuperTypes());
                            nextSet.add(classifier);
                            resultSuperTypes.retainAll(nextSet);
                        }
                    } else {
                        return null;
                    }
                }
                if (!resultSuperTypes.isEmpty()) {
                    Object[] result = resultSuperTypes.toArray();
                    return (EClassifier) result[result.length - 1];
                }
            }
        }
        return null;
    }

    @Override
    public Set<EClassifier> getIrreducibleClassifiersForVariableInBody(PatternBody patternBody, Variable variable) {
        Set<EClassifier> possibleClassifiersList = getPotentialClassifiersForVariableWithPatternBody(patternBody,
                variable, 0, null);
        if (possibleClassifiersList.size() <= 1) {
            return possibleClassifiersList;
        } else {
            return minimizeClassifiersList(possibleClassifiersList);
        }
    }

    @Override
    public EClassifier getExplicitClassifierForPatternParameterVariable(Variable variable) {
        if (variable instanceof ParameterRef) {
            Variable referredParameter = ((ParameterRef) variable).getReferredParam();
            return getClassifierForType(referredParameter.getType());
        } else {
            return getClassifierForType(variable.getType());
        }
    }

    private EClassifier getClassifierForVariableWithPatternBody(PatternBody patternBody, Variable variable,
            int recursionCallingLevel, Variable injectiveVariablePair) {
        Set<EClassifier> possibleClassifiers = getPotentialClassifiersForVariableWithPatternBody(patternBody, variable,
                recursionCallingLevel, injectiveVariablePair);
        if (possibleClassifiers.isEmpty()) {
            return null;
        } else if (possibleClassifiers.size() == 1) {
            return (EClassifier) possibleClassifiers.toArray()[0];
        } else {
            Set<EClassifier> minimizedClassifiers = minimizeClassifiersList(possibleClassifiers);
            EClassifier classifier = getExplicitClassifierForPatternParameterVariable(variable);
            if (classifier != null && minimizedClassifiers.contains(classifier)) {
                return classifier;
            } else {
                return minimizedClassifiers.iterator().next();
            }
        }
    }

    private Set<EClassifier> getPotentialClassifiersForVariableWithPatternBody(PatternBody patternBody,
            Variable variable, int recursionCallingLevel, Variable injectiveVariablePair) {
        Set<EClassifier> possibleClassifiersList = new HashSet<EClassifier>();
        EClassifier classifier = null;

        // Calculate explicit type with just the variable only (works only for parameters)
        classifier = getExplicitClassifierForPatternParameterVariable(variable);
        if (classifier != null) {
            possibleClassifiersList.add(classifier);
        }

        // Calculate it from the constraints
        for (Constraint constraint : patternBody.getConstraints()) {
            if (constraint instanceof EClassifierConstraint) {
                EClassifierConstraint eClassifierConstraint = (EClassifierConstraint) constraint;
                if (isEqualVariables(variable, eClassifierConstraint.getVar())) {
                    Type type = eClassifierConstraint.getType();
                    classifier = getClassifierForType(type);
                    if (classifier != null) {
                        possibleClassifiersList.add(classifier);
                    }
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                final PathExpressionHead pathExpressionHead = ((PathExpressionConstraint) constraint).getHead();
                // Src is the first parameter (example: E in EClass.name(E, N))
                final VariableReference firstvariableReference = pathExpressionHead.getSrc();
                if (isEqualVariables(variable, firstvariableReference)) {
                    Type type = pathExpressionHead.getType();
                    classifier = getClassifierForType(type);
                    if (classifier != null) {
                        possibleClassifiersList.add(classifier);
                    }
                }
                final ValueReference valueReference = pathExpressionHead.getDst();
                if (valueReference instanceof VariableValue) {
                    final VariableReference secondVariableReference = ((VariableValue) valueReference).getValue();
                    if (isEqualVariables(variable, secondVariableReference)) {
                        Type type = getTypeFromPathExpressionTail(pathExpressionHead.getTail());
                        classifier = getClassifierForType(type);
                        if (classifier != null) {
                            possibleClassifiersList.add(classifier);
                        }
                    }
                }
            } else if (constraint instanceof CompareConstraint) {
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                    ValueReference leftValueReference = compareConstraint.getLeftOperand();
                    ValueReference rightValueReference = compareConstraint.getRightOperand();
                    if (leftValueReference instanceof VariableValue) {
                        VariableValue leftVariableValue = (VariableValue) leftValueReference;
                        if (isEqualVariables(variable, leftVariableValue.getValue())) {
                            classifier = getClassifierForValueReference(rightValueReference, patternBody, variable,
                                    recursionCallingLevel, injectiveVariablePair);
                            if (classifier != null) {
                                possibleClassifiersList.add(classifier);
                            }
                        }
                    }
                    if (rightValueReference instanceof VariableValue) {
                        VariableValue rightVariableValue = (VariableValue) rightValueReference;
                        if (isEqualVariables(variable, rightVariableValue.getValue())) {
                            classifier = getClassifierForValueReference(leftValueReference, patternBody, variable,
                                    recursionCallingLevel, injectiveVariablePair);
                            if (classifier != null) {
                                possibleClassifiersList.add(classifier);
                            }
                        }
                    }
                }
            } else if (constraint instanceof PatternCompositionConstraint
                    && recursionCallingLevel < RECURSION_CALLING_LEVEL_LIMIT) {
                PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                boolean isNegative = patternCompositionConstraint.isNegative();
                if (!isNegative) {
                    PatternCall patternCall = patternCompositionConstraint.getCall();
                    int parameterIndex = 0;
                    for (ValueReference valueReference : patternCall.getParameters()) {
                        if (valueReference instanceof VariableValue) {
                            VariableValue variableValue = (VariableValue) valueReference;
                            VariableReference variableReference = variableValue.getValue();
                            if (isEqualVariables(variable, variableReference)) {
                                Pattern pattern = patternCall.getPatternRef();
                                EList<Variable> parameters = pattern.getParameters();
                                // In case of incorrect number of parameters we might check for non-existing parameters
                                if (parameters.size() > parameterIndex) {
                                    Variable variableInCalledPattern = parameters.get(parameterIndex);
                                    EClassifier variableClassifier = getClassifierForParameterVariable(pattern,
                                            variableInCalledPattern, recursionCallingLevel + 1);
                                    if (variableClassifier != null) {
                                        possibleClassifiersList.add(variableClassifier);
                                    }
                                }
                            }
                        }
                        parameterIndex++;
                    }
                }
            }
        }

        return possibleClassifiersList;
    }

    private EClassifier getClassifierForValueReference(ValueReference valueReference, PatternBody patternBody,
            Variable variable, int recursionCallingLevel, Variable injectiveVariablePair) {
        if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue
                || valueReference instanceof EnumValue) {
            return getClassifierForLiteralComputationEnumValueReference(valueReference);
        } else if (valueReference instanceof VariableValue) {
            VariableValue variableValue = (VariableValue) valueReference;
            Variable newPossibleInjectPair = variableValue.getValue().getVariable();
            if (!newPossibleInjectPair.equals(injectiveVariablePair)) {
                return getClassifierForVariableWithPatternBody(patternBody, newPossibleInjectPair,
                        recursionCallingLevel, variable);
            }
        }
        return null;
    }

    @Override
    public EClassifier getClassifierForType(Type type) {
        EClassifier result = null;
        if (type != null) {
            if (type instanceof ClassType) {
                result = ((ClassType) type).getClassname();
            } else if (type instanceof ReferenceType) {
                EStructuralFeature feature = ((ReferenceType) type).getRefname();
                if (feature instanceof EAttribute) {
                    EAttribute attribute = (EAttribute) feature;
                    result = attribute.getEAttributeType();
                } else if (feature instanceof EReference) {
                    EReference reference = (EReference) feature;
                    result = reference.getEReferenceType();
                }
            }
        }
        return result;
    }

    private boolean isEqualVariables(Variable variable, VariableReference variableReference) {
        if (variable != null && variableReference != null) {
            final Variable variableReferenceVariable = variableReference.getVariable();
            if (equal(variable, variableReferenceVariable)
                    || equal(variable.getName(), variableReferenceVariable.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EClassifier getClassifierForLiteralComputationEnumValueReference(ValueReference valueReference) {
        if (valueReference instanceof LiteralValueReference) {
            if (valueReference instanceof NumberValue) {
                Class<? extends Number> javaType = literals.getJavaType(((NumberValue) valueReference).getValue());
                if (javaType.isAssignableFrom(Integer.class)) {
                    return EcorePackage.Literals.EINT;
                } else if (javaType.isAssignableFrom(Double.class)) {
                    return EcorePackage.Literals.EDOUBLE;
                }
            } else if (valueReference instanceof StringValue) {
                return EcorePackage.Literals.ESTRING;
            } else if (valueReference instanceof BoolValue) {
                return EcorePackage.Literals.EBOOLEAN;
            } else if (valueReference instanceof ListValue) {
                return null;
            }
        } else if (valueReference instanceof AggregatedValue) {
            return EcorePackage.Literals.EINT;
        } else if (valueReference instanceof FunctionEvaluationValue) {
            FunctionEvaluationValue eval = (FunctionEvaluationValue) valueReference;
            final XExpression xExpression = eval.getExpression();
            // XXX If type cannot be calculated, use Java Object
            EDataType dataType = EcorePackage.Literals.EJAVA_OBJECT;
            if (!compilerPhases.isIndexing(xExpression)) {
                final IResolvedTypes resolvedTypes = typeResolver.resolveTypes(xExpression);
                final LightweightTypeReference type = resolvedTypes.getReturnType(xExpression);
                if (type != null) {
                    dataType = EcoreFactory.eINSTANCE.createEDataType();
                    dataType.setName(type.getSimpleName());
                    dataType.setInstanceClassName(type.getJavaIdentifier());
                }
            }
            return dataType;
        } else if (valueReference instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) valueReference;
            return enumValue.getEnumeration();
        }
        return null;
    }

    @Override
    public Type getTypeFromPathExpressionTail(PathExpressionTail pathExpressionTail) {
        if (pathExpressionTail == null) {
            return null;
        }
        if (pathExpressionTail.getTail() != null) {
            return getTypeFromPathExpressionTail(pathExpressionTail.getTail());
        }
        return pathExpressionTail.getType();
    }

    @Override
    public Map<PathExpressionTail, EStructuralFeature> getAllFeaturesFromPathExpressionTail(
            PathExpressionTail pathExpressionTail) {
        Map<PathExpressionTail, EStructuralFeature> types = Maps.newHashMap();
        getAllFeaturesFromPathExpressionTail(pathExpressionTail, types);
        return types;
    }

    private void getAllFeaturesFromPathExpressionTail(PathExpressionTail pathExpressionTail,
            Map<PathExpressionTail, EStructuralFeature> types) {
        if (pathExpressionTail != null) {
            Type type = pathExpressionTail.getType();
            if (type instanceof ReferenceType) {
                ReferenceType referenceType = (ReferenceType) type;
                EStructuralFeature refname = referenceType.getRefname();
                if (refname != null) {
                    types.put(pathExpressionTail, refname);
                }
            }
            getAllFeaturesFromPathExpressionTail(pathExpressionTail.getTail(), types);
        }
    }

    private JvmTypeReference getTypeReferenceForTypeName(String typeName, EObject context) {
        JvmTypeReference typeRef = typeReferences.getTypeForName(typeName, context);
        JvmTypeReference typeReference = primitives.asWrapperTypeIfPrimitive(typeRef);

        if (typeReference == null) {
            EObject errorContext = context;
            String contextName = context.toString();
            if (context instanceof Variable && ((Variable) context).eContainer() instanceof PatternBody
                    && ((Variable) context).getReferences().size() > 0) {
                contextName = ((Variable) context).getName();
                errorContext = ((Variable) context).getReferences().get(0);
            }
            errorFeedback.reportError(errorContext,
                    String.format(
                            "Cannot resolve corresponding Java type for variable %s. Are the required bundle dependencies set?",
                            contextName),
                    EMFPatternLanguageJvmModelInferrer.INVALID_TYPEREF_CODE, Severity.WARNING,
                    IErrorFeedback.JVMINFERENCE_ERROR_TYPE);
        }
        return typeReference;
    }

}
