/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageConfigurationConstants;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.emf.helper.JavaTypesHelper;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.internal.DuplicationChecker;
import org.eclipse.viatra.query.patternlanguage.emf.types.BottomTypeKey;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.util.IExpectedPackageNameProvider;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClosureType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.UnaryTypeConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.util.ASTStringProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.AggregatorUtil;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IAggregatorFactory;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives.Primitive;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.LiveShadowedResourceDescriptions;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.XbasePackage;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.IResolvedTypes;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Validators for Core Pattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate parameter in pattern declaration</li>
 * <li>Duplicate pattern definition (name duplication only, better calculation is needed)</li>
 * <li>Pattern call parameter checking (only the number of the parameters, types not supported yet)</li>
 * <li>Empty PatternBody check</li>
 * <li>Check for recursive pattern calls</li>
 * </ul>
 *
 * @author Mark Czotter
 * @author Tamas Szabo (itemis AG)
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class PatternLanguageValidator extends AbstractDeclarativeValidator implements IIssueCallback {
    
    public static final String DUPLICATE_VARIABLE_MESSAGE = "Duplicate parameter ";
    public static final String DUPLICATE_PATTERN_DEFINITION_MESSAGE = "Duplicate pattern %s (the shadowing pattern is in %s)";
    /**
     * @since 1.7
     */
    public static final String CONFLICTING_SPECIFICATION_NAME_MESSAGE = "Generated query specification name %s would conflict with generated query group name for file %s";
    public static final String UNKNOWN_ANNOTATION_ATTRIBUTE = "Undefined annotation attribute ";
    public static final String MISSING_ANNOTATION_ATTRIBUTE = "Required attribute missing ";
    public static final String ANNOTATION_PARAMETER_TYPE_ERROR = "Invalid parameter type %s. Expected %s";
    public static final String TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL = "The pattern %s is not of binary arity (it has %d parameters), therefore transitive closure is not supported.";
    public static final String TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION = "Transitive closure of %s is currently only allowed in simple positive pattern calls (no negation or aggregation).";
    public static final String RECURSIVE_PATTERN_CALL = "Recursive pattern call: %s";
    /**
     * @since 2.0
     */
    public static final String RECURSIVE_PATTERN_CALL_TRANSITIVE = "Transitive closure is not supported in recursive pattern calls: %s";
    /**
     * @since 2.0
     */
    public static final String RECURSIVE_PATTERN_CALL_NEGATIVE = "Negative pattern calls are not supported in recursive pattern calls: %s";
    /**
     * @since 1.4
     */
    public static final String INVALID_AGGREGATE_MESSAGE = "Aggregate variables can only be used in aggregators.";
    /**
     * @since 1.4
     */
    public static final String UNEXPECTED_AGGREGATE_MESSAGE = "Aggregate variable %s not expected in aggregator %s.";
    /**
     * @since 1.4
     */
    public static final String EXACTLY_ONE_AGGREGATE_MESSAGE = "Exactly one variable must be aggregate for the aggregator %s.";
    /**
     * @since 1.4
     */
    public static final String MISSING_AGGREGATE_MESSAGE = "Missing aggregate parameter for the aggregator %s.";
    /**
     * @since 1.4
     */
    public static final String VARIABLE_NAME_DUBIUS_REUSE_MESSAGE_SINGLEUSE = "Dubius variable naming: Single use variable %s shares its name with the variable %s";
    /**
     * @since 1.4
     */
    public static final String VARIABLE_NAME_DUBIUS_REUSE_MESSAGE_AGGREGATE = "Dubius variable naming: Aggregate variable %s shares its name with the variable %s";

    @Inject
    private PatternAnnotationProvider annotationProvider;

    @Inject
    private ITypeSystem typeSystem;

    @Inject
    private ITypeInferrer typeInferrer;

    @Inject
    private IBatchTypeResolver typeResolver;

    @Inject
    private IResourceScopeCache cache;
    @Inject
    private LiveShadowedResourceDescriptions resourceDescriptions;
    @Inject
    private IQualifiedNameProvider nameProvider;
    @Inject
    private IExpectedPackageNameProvider packageNameProvider;
    @Inject
    private TypeReferences typeReferences;
    @Inject
    private NumberLiterals numberLiterals;
    @Inject
    private DuplicationChecker duplicateChecker;
    @Inject
    private JvmModelAssociator associator;
    @Inject
    private PureWhitelist whitelist;

    private boolean queryGroupGenerationEnabled = true; 
    
    
    /**
     * @since 2.3
     */
    @Inject
    public void enableQueryGroupGeneration(@Named(EMFPatternLanguageConfigurationConstants.GENERATE_QUERY_GROUP_KEY) boolean queryGroupGenerationEnabled) {
        this.queryGroupGenerationEnabled = queryGroupGenerationEnabled;
    }

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
    
    /**
     * Checks if an aggregate {@link VariableReference} is used only in the right context, that is, in an
     * {@link AggregatedValue} with an aggregator requiring aggregator parameters.
     * 
     * @param value
     *            the {@link VariableReference} instance
     * @since 1.4
     */
    @Check
    public void checkValueReference(VariableReference value) {
        if (value.isAggregator()) {
            AggregatedValue container = EcoreUtil2.getContainerOfType(value, AggregatedValue.class);
            if (container == null) {
                error(INVALID_AGGREGATE_MESSAGE, PatternLanguagePackage.Literals.VARIABLE_REFERENCE__AGGREGATOR,
                        IssueCodes.INVALID_AGGREGATE_CONTEXT);
            }
        }
    }

    /**
     * Checks if an aggregator expression has the correct number (0 or 1) aggregate variables.
     * 
     * @param expression
     *            the aggregator expression
     * @since 1.4
     */
    @Check
    public void checkAggregatorExpression(AggregatedValue expression) {
        JvmDeclaredType aggregator = expression.getAggregator();
        final Class<IAggregatorFactory> clazz = IAggregatorFactory.class;
        if (aggregator != null && !aggregator.eIsProxy()) {
            if (typeReferences.is(aggregator, clazz)) {
                return;
            }
            Iterator<JvmTypeReference> it = aggregator.getSuperTypes().iterator();
            if (Iterators.all(it, input -> input == null || input.eIsProxy() || !typeReferences.is(input, clazz))) {
                error(String.format("%s is not an aggregator definition.", aggregator.getSimpleName()),
                        PatternLanguagePackage.Literals.AGGREGATED_VALUE__AGGREGATOR, IssueCodes.INVALID_AGGREGATOR);
                return;
            }
            List<VariableReference> references = AggregatorUtil.getAllAggregatorVariables(expression);
            if (AggregatorUtil.mustHaveAggregatorVariables(expression)) {
                if (references.isEmpty()) {
                    error(String.format(MISSING_AGGREGATE_MESSAGE, aggregator.getSimpleName()), expression, PatternLanguagePackage.Literals.AGGREGATED_VALUE__CALL,
                            IssueCodes.INVALID_AGGREGATOR_PARAMETER);
                }
                if (references.size() > 1) {
                    for (VariableReference reference : references) {
                        error(String.format(EXACTLY_ONE_AGGREGATE_MESSAGE, aggregator.getSimpleName()), reference, null,
                                IssueCodes.INVALID_AGGREGATOR_PARAMETER);
                    }
                }
            } else {
                for (VariableReference reference : references) {
                    error(String.format(UNEXPECTED_AGGREGATE_MESSAGE, reference.getVar(), aggregator.getSimpleName()), reference, null,
                            IssueCodes.INVALID_AGGREGATOR_PARAMETER);
                }
            }
            
        }
    }
    /**
     * Check for mistyped aggregator expressions
     * @since 1.7
     */
    @Check
    public void checkAggregatorCallTypes(AggregatedValue expression) {
        if (expression.getCall() instanceof PatternCall) {
            Pattern calledPattern = ((PatternCall)expression.getCall()).getPatternRef();
            if (calledPattern == null || calledPattern.eIsProxy()) {
                // If the called pattern is not available, type checking can be ignored
                return;
            }
        }
        List<ValueReference> callVariables = PatternLanguageHelper.getCallParameters(expression.getCall());
        List<IInputKey> expectedTypes = PatternLanguageHelper.calculateExpectedTypes(expression.getCall(), typeSystem, typeInferrer);
        //maxIndex is used to avoid overindexing in case of incorrect number of parameters
        int maxIndex = Math.min(callVariables.size(), expectedTypes.size());
        produceParameterTypeWarnings(callVariables, expectedTypes, maxIndex);
    }

    @Check
    public void checkEmbeddedAggregatorLength(AggregatedValue value) {
        if (value.getCall() instanceof PathExpressionConstraint) {
            PathExpressionConstraint constraint = (PathExpressionConstraint) value.getCall();
            if (constraint.getEdgeTypes().size() > 1) {
                warning("Aggregating feature chains might provide unexpected results.", value, null, IssueCodes.AGGREGATED_FEATURE_CHAIN);
            }
        }
    }
    
    @Check
    public void checkPatternParameters(Pattern pattern) {
        if (pattern.getParameters().isEmpty()) {
            warning("Parameterless patterns can only be used to check for existence of a condition.",
                    PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.MISSING_PATTERN_PARAMETERS);
            // As no duplicate parameters are available, returning now
            return;
        }
        for (int i = 0; i < pattern.getParameters().size(); ++i) {
            String leftParameterName = pattern.getParameters().get(i).getName();
            for (int j = i + 1; j < pattern.getParameters().size(); ++j) {
                if (Strings.equal(leftParameterName, pattern.getParameters().get(j).getName())) {
                    error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
                            PatternLanguagePackage.Literals.PATTERN__PARAMETERS, i,
                            IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
                    error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
                            PatternLanguagePackage.Literals.PATTERN__PARAMETERS, j,
                            IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
                }
            }
        }
    }

    @Check
    public void checkPrivatePatternCall(PatternCall call) {
        final Pattern calledPattern = call.getPatternRef();
        if (calledPattern != null && calledPattern.getModifiers() != null) {
            if (PatternLanguageHelper.isPrivate(calledPattern) && calledPattern.eResource() != call.eResource()){
                error(String.format("The pattern %s is not visible.", getFormattedPattern(calledPattern)),
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF, IssueCodes.PRIVATE_PATTERN_CALLED);
            }
        }
    }

    @Check
    public void checkPatternCallParameters(PatternCall call) {
        if (call.getPatternRef() != null && call.getPatternRef().getName() != null && call.getParameters() != null) {
            final int definitionParameterSize = call.getPatternRef().getParameters().size();
            final int callParameterSize = call.getParameters().size();
            if (definitionParameterSize != callParameterSize) {
                error("The pattern " + getFormattedPattern(call.getPatternRef())
                        + " is not applicable for the arguments(" + getFormattedArgumentsList(call.getParameters()) + ")",
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF,
                        IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
            }
        }
    }
    
    private EStructuralFeature getParameterFeature(CallableRelation call) {
        if (call instanceof PatternCall) {
            return PatternLanguagePackage.Literals.PATTERN_CALL__PARAMETERS;
        } else if (call instanceof UnaryTypeConstraint) {
            return PatternLanguagePackage.Literals.UNARY_TYPE_CONSTRAINT__VAR;
        } else if (call instanceof PathExpressionConstraint) {
            return PatternLanguagePackage.Literals.PATH_EXPRESSION_CONSTRAINT__SRC;
        } else {
            throw new IllegalArgumentException("Unknown callable relation type " + call.eClass().getName());
        }
    }
    
    @Check
    public void checkApplicabilityOfTransitiveClosureInPatternCall(CallableRelation call) {
        if (PatternLanguageHelper.isTransitive(call)) {
            final List<IInputKey> expectedTypes = PatternLanguageHelper.calculateExpectedTypes(call, typeSystem, typeInferrer);
            final int arity = expectedTypes.size();
            if (2 != arity) {
                error(String.format(TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL, getFormattedCall(call),
                        arity), PatternLanguagePackage.Literals.CALLABLE_RELATION__TRANSITIVE,
                        IssueCodes.TRANSITIVE_PATTERNCALL_ARITY);
            } else {

                IInputKey type1 = expectedTypes.get(0);
                IInputKey type2 = expectedTypes.get(1);
                if (!typeSystem.isConformant(type1, type2) && !typeSystem.isConformant(type2, type1)) {
                    error(String.format(
                            "The parameter types %s and %s are not compatible, so no transitive references can exist in instance models.",
                            typeSystem.typeString(type1), typeSystem.typeString(type2)),
                            getParameterFeature(call),
                            IssueCodes.TRANSITIVE_PATTERNCALL_TYPE);
                }
                
                if (call.getTransitive() == ClosureType.REFLEXIVE_TRANSITIVE && !type1.isEnumerable()) {
                    error(String.format(
                            "Reflexive transitive closure is not supported over non enumerable type %s.",
                            typeSystem.typeString(type1)),
                            getParameterFeature(call),
                            0,
                            IssueCodes.TRANSITIVE_PATTERNCALL_TYPE);
                }
            }
            
            final EObject eContainer = call.eContainer();
            if ((eContainer instanceof PatternCompositionConstraint && ((PatternCompositionConstraint)eContainer).isNegative())
                    ||
                 eContainer instanceof AggregatedValue) {
                error(String.format(TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION, getFormattedCall(call)),
                        PatternLanguagePackage.Literals.CALLABLE_RELATION__TRANSITIVE,
                        IssueCodes.TRANSITIVE_PATTERNCALL_NOT_APPLICABLE);
            }
        }
    }

    @Check
    public void checkPatterns(PatternModel model) {
        resourceDescriptions.setContext(model);
        if (model.getPatterns() != null) {
            for (Pattern pattern : model.getPatterns()) {
                boolean isDuplicateFound = false;
                for (IEObjectDescription shadowingPatternDescription : duplicateChecker.findDuplicates(pattern)) {
                    isDuplicateFound = true;
                    QualifiedName fullyQualifiedName = nameProvider.getFullyQualifiedName(pattern);
                    URI otherResourceUri = shadowingPatternDescription.getEObjectURI().trimFragment();
                    String otherResourcePath = otherResourceUri.toPlatformString(true);
                    if (otherResourcePath == null) {
                        otherResourcePath = otherResourceUri.toFileString();
                    }
                    error(String.format(DUPLICATE_PATTERN_DEFINITION_MESSAGE, fullyQualifiedName,
                            otherResourcePath), pattern, PatternLanguagePackage.Literals.PATTERN__NAME,
                            IssueCodes.DUPLICATE_PATTERN_DEFINITION);
                }
                if (!isDuplicateFound && queryGroupGenerationEnabled) {
                    EObject _jvmElement = associator.getPrimaryJvmElement(pattern);
                    if (_jvmElement instanceof JvmDeclaredType) {
                        String qualifiedName = ((JvmDeclaredType) _jvmElement).getQualifiedName();
                        for (IEObjectDescription shadowingGroupDescription : duplicateChecker.findShadowingClasses(pattern, qualifiedName, PatternLanguagePackage.Literals.PATTERN_MODEL)) {
                            QualifiedName fullyQualifiedName = nameProvider.getFullyQualifiedName(pattern);
                            URI otherResourceUri = shadowingGroupDescription.getEObjectURI().trimFragment();
                            String otherResourcePath = otherResourceUri.toPlatformString(true);
                            if (otherResourcePath == null) {
                                otherResourcePath = otherResourceUri.toFileString();
                            }
                            error(String.format(CONFLICTING_SPECIFICATION_NAME_MESSAGE, fullyQualifiedName,
                                    otherResourcePath), pattern, PatternLanguagePackage.Literals.PATTERN__NAME,
                                    IssueCodes.DUPLICATE_PATTERN_DEFINITION);
                        }
                    }
                }
            }
        }
    }

    @Check
    public void checkPatternBody(PatternBody body) {
        if (body.getConstraints().isEmpty()) {
            String bodyName = getName(body);
            if (bodyName == null) {
                Pattern pattern = ((Pattern) body.eContainer());
                String patternName = pattern.getName();
                error("A patternbody of " + patternName + " is empty", body,
                        PatternLanguagePackage.Literals.PATTERN_BODY__CONSTRAINTS, IssueCodes.PATTERN_BODY_EMPTY);
            } else {
                error("The patternbody " + bodyName + " cannot be empty", body,
                        PatternLanguagePackage.Literals.PATTERN_BODY__NAME, IssueCodes.PATTERN_BODY_EMPTY);
            }
        }
    }

    @Check(CheckType.NORMAL)
    public void checkAnnotation(Annotation annotation) {
        if (annotationProvider.hasValidator(annotation.getName())) {
            IPatternAnnotationValidator validator = annotationProvider.getValidator(annotation.getName());
            executeDefaultAnnotationValidation(annotation, validator);
            // Execute extra validation
            validator.getAdditionalValidator()
                    .ifPresent(v -> v.executeAdditionalValidation(annotation, this));
        } else {
            warning("Unknown annotation " + annotation.getName(), PatternLanguagePackage.Literals.ANNOTATION__NAME,
                    IssueCodes.UNKNOWN_ANNOTATION);
        }
    }

    private void executeDefaultAnnotationValidation(Annotation annotation, IPatternAnnotationValidator validator) {
        if (validator.isDeprecated()) {
            warning(String.format("Annotation %s is deprecated.", annotation.getName()), annotation,
                    PatternLanguagePackage.Literals.ANNOTATION__NAME, IssueCodes.DEPRECATION);
        }
        // Check for unknown annotation attributes
        for (AnnotationParameter unknownParameter : validator.getUnknownAttributes(annotation)) {
            error(UNKNOWN_ANNOTATION_ATTRIBUTE + unknownParameter.getName(), unknownParameter,
                    PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME,
                    annotation.getParameters().indexOf(unknownParameter), IssueCodes.UNKNOWN_ANNOTATION_PARAMETER);
        }
        // Check for missing mandatory attributes
        for (String missingAttribute : validator.getMissingMandatoryAttributes(annotation)) {
            error(MISSING_ANNOTATION_ATTRIBUTE + missingAttribute, annotation,
                    PatternLanguagePackage.Literals.ANNOTATION__PARAMETERS,
                    IssueCodes.MISSING_REQUIRED_ANNOTATION_PARAMETER);
        }
        // Check for annotation parameter types
        for (AnnotationParameter parameter : annotation.getParameters()) {
            if (validator.isDeprecated(parameter.getName())) {
                warning(String.format("Annotation parameter %s is deprecated.", parameter.getName()), parameter,
                        PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME, IssueCodes.DEPRECATION);
            }
            Class<? extends ValueReference> expectedParameterType = validator.getExpectedParameterType(parameter);
            if (expectedParameterType != null && parameter.getValue() != null
                    && !expectedParameterType.isAssignableFrom(parameter.getValue().getClass())) {
                error(String.format(ANNOTATION_PARAMETER_TYPE_ERROR, getTypeName(parameter.getValue().getClass()),
                        getTypeName(expectedParameterType)), parameter,
                        PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME,
                        annotation.getParameters().indexOf(parameter), IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
            } else if (parameter.getValue() instanceof VariableReference) {
                VariableReference reference = (VariableReference) parameter.getValue();
                if (reference.getVariable() == null) {
                    error(String.format("Unknown variable %s", reference.getVar()), parameter,
                            PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__VALUE,
                            annotation.getParameters().indexOf(parameter),
                            IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
                }
            } else if (parameter.getValue() instanceof ListValue) {
                ListValue listValue = (ListValue) (parameter.getValue());
                for (VariableReference reference : Iterables.filter(listValue.getValues(), VariableReference.class)) {
                    if (reference.getVariable() == null) {
                        error(String.format("Unknown variable %s", reference.getVar()), listValue,
                                PatternLanguagePackage.Literals.LIST_VALUE__VALUES,
                                listValue.getValues().indexOf(reference), IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
                    }
                }
            }
        }
    }

    @Check
    public void checkCompareConstraints(CompareConstraint constraint) {
        ValueReference op1 = constraint.getLeftOperand();
        ValueReference op2 = constraint.getRightOperand();
        if (op1 == null || op2 == null) {
            return;
        }

        boolean op1Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op1.eClass());
        boolean op2Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op2.eClass());
        boolean op1Variable = PatternLanguagePackage.Literals.VARIABLE_REFERENCE.isSuperTypeOf(op1.eClass());
        boolean op2Variable = PatternLanguagePackage.Literals.VARIABLE_REFERENCE.isSuperTypeOf(op2.eClass());

        // If both operands are constant literals, issue a warning
        if (op1Constant && op2Constant) {
            warning("Both operands are constants - constraint is always true or always false.",
                    PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
                    IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
            warning("Both operands are constants - constraint is always true or always false.",
                    PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
                    IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        }
        // If both operands are the same, issues a warning
        if (op1Variable && op2Variable) {
            VariableReference op1v = (VariableReference) op1;
            VariableReference op2v = (VariableReference) op2;
            if (op1v.getVar().equals(op2v.getVar())) {
                warning("Comparing a variable with itself.",
                        PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
                        IssueCodes.SELF_COMPARE_CONSTRAINT);
                warning("Comparing a variable with itself.",
                        PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
                        IssueCodes.SELF_COMPARE_CONSTRAINT);
            }
        }
        
        // Ensure 
        boolean op1Eval = PatternLanguagePackage.Literals.FUNCTION_EVALUATION_VALUE.isSuperTypeOf(op1.eClass());
        boolean op2Eval = PatternLanguagePackage.Literals.FUNCTION_EVALUATION_VALUE.isSuperTypeOf(op2.eClass());
        
        if (op1Eval && op2Variable) {
            checkEvalInCompare(constraint, (VariableReference) op2, (FunctionEvaluationValue) op1);
        } else if (op2Eval && op1Variable) {
            checkEvalInCompare(constraint, (VariableReference) op1, (FunctionEvaluationValue) op2);
        }
        
    }

    private void checkEvalInCompare(CompareConstraint constraint, VariableReference reference, FunctionEvaluationValue eval) {
        List<Variable> evalInputVariables = PatternLanguageHelper.getUsedVariables(eval.getExpression(), EcoreUtil2
                .getContainerOfType(constraint, PatternBody.class).getVariables());
        if (evalInputVariables.contains(reference.getVariable())) {
            if (constraint.getFeature() == CompareFeature.EQUALITY) {
                error("Return value of an eval expression cannot be stored in one of its parameters.", reference, 
                    PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                    IssueCodes.EVAL_INCORRECT_RETURNVALUE);
            } else {
                warning("Return value of an eval expression should not be compared with one of its parameters.", reference, 
                        PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR,
                        IssueCodes.EVAL_INCORRECT_RETURNVALUE);
            }
        }
    }
    
    @Check
    public void checkRecursivePatternCall(PatternCall call) {
        Map<PatternCall, Set<PatternCall>> graph = cache.get(call.eResource(), call.eResource(),
                new CallGraphProvider(call.eResource()));

        LinkedList<PatternCall> result = dfsCheckCycle(call, graph);

        if (result != null) {
            StringBuilder buffer = new StringBuilder();

            boolean first = true;
            for (PatternCall elem : result) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(" -> ");
                }
                buffer.append(prettyPrintPatternCall(elem));
            }

            if (isNegativePatternCall(call)) {
                error(String.format(RECURSIVE_PATTERN_CALL_NEGATIVE, buffer.toString()), call,
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF, IssueCodes.RECURSIVE_PATTERN_CALL);
            } else if (PatternLanguageHelper.isTransitive(call)) {
                error(String.format(RECURSIVE_PATTERN_CALL_TRANSITIVE, buffer.toString()), call,
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF, IssueCodes.RECURSIVE_PATTERN_CALL);
            } else {
                warning(String.format(RECURSIVE_PATTERN_CALL, buffer.toString()), call,
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF, IssueCodes.RECURSIVE_PATTERN_CALL);
            }
        }
    }

    private LinkedList<PatternCall> dfsCheckCycle(PatternCall source, Map<PatternCall, Set<PatternCall>> graph) {
        LinkedList<PatternCall> path = new LinkedList<>();
        path.add(source);
        return dfsCheckCycle(source, path, new HashSet<PatternCall>(), graph);
    }

    /**
     * Contract: (1) path is the current path from source to the last element in path (2) the first element of path is
     * source (3) seen is maintained globally within the recursive calls of dfsCheckCycle
     */
    private LinkedList<PatternCall> dfsCheckCycle(PatternCall source, LinkedList<PatternCall> path,
            Set<PatternCall> seen, Map<PatternCall, Set<PatternCall>> graph) {
        PatternCall current = path.getLast();

        if (!seen.contains(current)) {
            seen.add(current);
            for (PatternCall target : graph.get(current)) {
                path.add(target);
                if (target == source) {
                    return path;
                }
                LinkedList<PatternCall> intermediate = dfsCheckCycle(source, path, seen, graph);
                if (intermediate != null) {
                    return intermediate;
                } else {
                    path.removeLast();
                }
            }
        }

        // this means that no cycle has been found
        return null;
    }

    private boolean isNegativePatternCall(PatternCall call) {
        return (call.eContainer() instanceof PatternCompositionConstraint
                && ((PatternCompositionConstraint) call.eContainer()).isNegative());
    }

    private String prettyPrintPatternCall(PatternCall call) {
        return (isNegativePatternCall(call) ? "neg " : "") + call.getPatternRef().getName();
    }

    private static final Comparator<PatternCall> patternCallComparator = (p1, p2) -> {
        Pattern pr1 = p1.getPatternRef();
        Pattern pr2 = p2.getPatternRef();
        if (pr1.eIsProxy() && !pr2.eIsProxy()) {
            return -1;
        } else if (!pr1.eIsProxy() && pr2.eIsProxy()) {
            return +1;
        } else if (pr1.eIsProxy() && pr2.eIsProxy()) {
            return 0;
        }
        return pr1.getName().compareTo(pr2.getName());
    };

    private static class CallGraphProvider implements Provider<Map<PatternCall, Set<PatternCall>>> {

        private Resource resource;

        public CallGraphProvider(Resource resource) {
            this.resource = resource;
        }

        @Override
        public Map<PatternCall, Set<PatternCall>> get() {
            Map<PatternCall, Set<PatternCall>> graph = new HashMap<>();
            TreeIterator<EObject> resourceIterator = resource.getAllContents();
            Set<PatternCall> knownCalls = Sets.newHashSet(Iterators.filter(resourceIterator, PatternCall.class));
            Set<PatternCall> unprocessedCalls = Sets.difference(knownCalls, graph.keySet());

            while (!unprocessedCalls.isEmpty()) {
                PatternCall source = unprocessedCalls.iterator().next();
                Set<PatternCall> targets = new TreeSet<>(patternCallComparator);
                graph.put(source, targets);

                Pattern calledPattern = source.getPatternRef();
                if (calledPattern != null && !calledPattern.eIsProxy()) {
                    TreeIterator<EObject> headIterator = calledPattern.eAllContents();
                    while (headIterator.hasNext()) {
                        EObject headContent = headIterator.next();
                        if (headContent instanceof PatternCall) {
                            PatternCall target = (PatternCall) headContent;
                            targets.add(target);
                        }
                    }
                    knownCalls.addAll(targets);
                }
            }

            return graph;
        }
    }

    private String getName(PatternBody body) {
        if (body.getName() != null && !body.getName().isEmpty()) {
            return "'" + body.getName() + "'";
        }
        return null;
    }

    private String getTypeName(Class<? extends ValueReference> typeClass) {
        if (NumberValue.class.isAssignableFrom(typeClass)) {
            return "Number";
        } else if (BoolValue.class.isAssignableFrom(typeClass)) {
            return "Boolean";
        } else if (StringValue.class.isAssignableFrom(typeClass)) {
            return "String";
        } else if (ListValue.class.isAssignableFrom(typeClass)) {
            return "List";
        } else if (VariableReference.class.isAssignableFrom(typeClass)) {
            return "Variable";
        }
        return "UNDEFINED";
    }

    private String getConstantAsString(ValueReference ref) {
        if (ref instanceof NumberValue) {
            return numberLiterals.toJavaLiteral(((NumberValue) ref).getValue());
        } else if (ref instanceof BoolValue) {
            return Boolean.toString(PatternLanguageHelper.getValue(ref, Boolean.class));
        } else if (ref instanceof StringValue) {
            return "\"" + ((StringValue) ref).getValue() + "\"";
        } else if (ref instanceof ListValue) {
            StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            for (Iterator<ValueReference> iter = ((ListValue) ref).getValues().iterator(); iter.hasNext();) {
                sb.append(getConstantAsString(iter.next()));
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
            return sb.toString();
        } else if (ref instanceof VariableReference) {
            return ((VariableReference) ref).getVar();
        }
        return "UNDEFINED";
    }

    private String getFormattedCall(CallableRelation relation) {
        if (relation instanceof PatternCall) {
            return getFormattedPattern(((PatternCall) relation).getPatternRef());
        } else {
            return ASTStringProvider.INSTANCE.doSwitch(relation);
        }
    }
    
    private String getFormattedPattern(Pattern pattern) {
        return pattern.getParameters().stream().map(Variable::getName)
                .collect(Collectors.joining(", ", pattern.getName() + "(", ")"));
    }

    protected String getFormattedArgumentsList(List<ValueReference> arguments) {
        return arguments.stream().map(this::getConstantAsString).collect(Collectors.joining(", "));
    }

    @Check
    public void checkPackageDeclaration(PatternModel model) {
        String declaredPackage = packageNameProvider.getExpectedPackageName(model);
        String actualPackage = model.getPackageName();

        if (declaredPackage != null && !Strings.equal(actualPackage, declaredPackage)) {
            error(String.format("The package declaration '%s' does not match the container '%s'",
                    Strings.emptyIfNull(declaredPackage), Strings.emptyIfNull(actualPackage)),
                    PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME, IssueCodes.PACKAGE_NAME_MISMATCH);
        }

        if (actualPackage != null && !actualPackage.equals(actualPackage.toLowerCase())) {
            error("Only lowercase package names supported", PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
                    IssueCodes.PACKAGE_NAME_MISMATCH);
        }
    }

    @Check
    public void checkReturnTypeOfCheckConstraints(CheckConstraint checkConstraint) {
        XExpression xExpression = checkConstraint.getExpression();
        if (xExpression != null) {
            final IResolvedTypes resolvedType = typeResolver.resolveTypes(xExpression);
            LightweightTypeReference type = resolvedType.getReturnType(xExpression);

            if (type.getPrimitiveIfWrapperType().getPrimitiveKind() != Primitive.Boolean) {
                error("Check expressions must return boolean instead of " + type.getSimpleName(), checkConstraint,
                        PatternLanguagePackage.Literals.CHECK_CONSTRAINT__EXPRESSION, IssueCodes.CHECK_MUST_BE_BOOLEAN);
            }
        }
    }

    @Check(CheckType.NORMAL)
    public void checkVariableNames(PatternBody body) {
        for (Variable var1 : body.getVariables()) {
            Variable otherVar = null;
            for (Variable var2 : body.getVariables()) {
                if (PatternLanguageHelper.isNamedSingleUse(var1) && var1.getName().substring(1).equals(var2.getName())) {
                    otherVar = var2;
                }
            }
            if (otherVar != null) {
                boolean isAggregate = PatternLanguageHelper.hasAggregateReference(var1);
                // Local variables do not have source location
                if (isAggregate) {
                    error(String.format(VARIABLE_NAME_DUBIUS_REUSE_MESSAGE_AGGREGATE, var1.getName(),
                            otherVar.getName()), PatternLanguageHelper.getReferences(var1).findAny().get(),
                            PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE,
                            IssueCodes.DUBIUS_VARIABLE_NAME);
                } else {
                    warning(String.format(VARIABLE_NAME_DUBIUS_REUSE_MESSAGE_SINGLEUSE, var1.getName(),
                            otherVar.getName()), PatternLanguageHelper.getReferences(var1).findAny().get(),
                            PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE,
                            IssueCodes.DUBIUS_VARIABLE_NAME);
                }
            }
        }
    }



    // private int getReferenceCount(Variable var, ReferenceType type, Map<Variable, VariableReferenceCount>
    // refCounters,
    // UnionFind<Variable> variableUnions) {
    // int sum = 0;
    // for (Variable unionVar : variableUnions.getPartition(var)) {
    // sum += refCounters.get(unionVar).getReferenceCount(type);
    // }
    // return sum;
    // }

    @Check(CheckType.NORMAL)
    public void checkForImpureJavaCallsInCheckConstraints(CheckConstraint checkConstraint) {
        if (checkConstraint.getExpression() != null) {
            checkForImpureJavaCallsInternal(checkConstraint.getExpression());
        }
    }

    @Check(CheckType.NORMAL)
    public void checkForImpureJavaCallsInEvalExpressions(FunctionEvaluationValue eval) {
        if (eval.getExpression() != null) {
            checkForImpureJavaCallsInternal(eval.getExpression());
        }
    }

    private void checkForImpureJavaCallsInternal(XExpression xExpression) {
        Iterator<EObject> eAllContents = Iterators.concat(Iterators.singletonIterator(xExpression),
                xExpression.eAllContents());
        while (eAllContents.hasNext()) {
            EObject nextEObject = eAllContents.next();
            if (nextEObject instanceof XMemberFeatureCall) {
                XMemberFeatureCall xFeatureCall = (XMemberFeatureCall) nextEObject;
                JvmIdentifiableElement jvmIdentifiableElement = xFeatureCall.getFeature();
                if (jvmIdentifiableElement instanceof JvmOperation) {
                    JvmOperation jvmOperation = (JvmOperation) jvmIdentifiableElement;
                    if (!jvmOperation.eIsProxy() && !isPure(jvmOperation)) {
                        warning("Impure method call " + jvmOperation.getQualifiedName(), xFeatureCall,
                                XbasePackage.Literals.XABSTRACT_FEATURE_CALL__FEATURE,
                                IssueCodes.CHECK_WITH_IMPURE_JAVA_CALLS);
                    }
                }
            }
        }
    }

    private boolean isPure(JvmOperation jvmOperation) {
        return JavaTypesHelper.hasPureAnnotation(jvmOperation) || whitelist.contains(jvmOperation);
    }
    
    @Check(CheckType.NORMAL)
    public void checkNegativeCallParameters(PatternCompositionConstraint constraint) {
        Predicate<ValueReference> isSingleUseVariable = input -> 
            input instanceof VariableReference ? ((VariableReference) input).getVar().startsWith("_") : false;
        if (constraint.isNegative()) {
            List<ValueReference> callVariables = PatternLanguageHelper.getCallParameters(constraint.getCall());
            List<IInputKey> expectedTypes = PatternLanguageHelper.calculateExpectedTypes(constraint.getCall(), typeSystem, typeInferrer);
            //maxIndex is used to avoid overindexing in case of incorrect number of parameters
            int maxIndex = Math.min(callVariables.size(), expectedTypes.size());
            
            if (Iterables.all(callVariables, isSingleUseVariable)) {
                warning("This negative pattern call is a global constraint: "
                        + "it expresses that there are no matches of the called pattern at all. "
                        + "Make sure this is intentional!",
                        PatternLanguagePackage.Literals.PATTERN_COMPOSITION_CONSTRAINT__CALL,
                        IssueCodes.NEGATIVE_PATTERN_CALL_WITH_ONLY_SINGLE_USE_VARIABLES);
            }
            
            produceParameterTypeWarnings(callVariables, expectedTypes, maxIndex);
        }
    }
    
    private void produceParameterTypeWarnings(List<ValueReference> callVariables, List<IInputKey> expectedTypes,
            int maxIndex) {
        for (int i = 0; i < maxIndex; i++ ) {
            IInputKey actualType = typeInferrer.getType(callVariables.get(i));
            IInputKey expectedType = expectedTypes.get(i);
            
            if (actualType != null && expectedType != null &&
                    actualType != BottomTypeKey.INSTANCE && expectedType != BottomTypeKey.INSTANCE &&
                    // If the expression matches the parameter type, it is valid 
                    !( typeSystem.isConformant(expectedType, actualType) 
                    // The inverse relation is also valid: the neg only applies to a subset of the class 
                    || typeSystem.isConformant(actualType, expectedType))) {
                // Parameter variable will never match pattern, suggest mistyping
                warning(String.format(
                        "Expression type %s does not match type of the parameter type %s of the called pattern.",
                        typeSystem.typeString(actualType), typeSystem.typeString(expectedType)),
                        callVariables.get(i), null, IssueCodes.MISTYPED_PARAMETER);
            }
        }
    }

    @Override
    public void warning(String message, EObject source, EStructuralFeature feature, String code, String... issueData) {
        super.warning(message, source, feature, code, issueData);
    }

    @Override
    public void error(String message, EObject source, EStructuralFeature feature, String code, String... issueData) {
        super.error(message, source, feature, code, issueData);
    }
}
