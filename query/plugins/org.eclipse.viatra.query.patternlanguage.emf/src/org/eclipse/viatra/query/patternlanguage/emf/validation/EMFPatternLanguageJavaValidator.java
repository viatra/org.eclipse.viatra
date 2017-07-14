/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter, Zoltan Ujhelyi - initial API and implementation
 *   Andras Okros - new validators added
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import static org.eclipse.xtext.xbase.validation.IssueCodes.IMPORT_UNUSED;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.lang.model.SourceVersion;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra.query.patternlanguage.emf.ResolutionException;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ComputationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ExecutionType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.LiteralValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Modifiers;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.viatra.query.patternlanguage.typing.BottomTypeKey;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.algorithms.UnionFind;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmEnumerationType;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Inject;

/**
 * Validators for EMFPattern Language:
 * <ul>
 * <li>Duplicate import of EPackages</li>
 * <li>Enum types</li>
 * <li>Unused variables</li>
 * <li>Type checking for parameters and body variables</li>
 * <li>Type checking for literal and computational values in pattern calls, path expressions and compare constraints
 * <li>Pattern body searching for isolated constraints (cartesian products)</li>
 * <li>Non-EDataTypes in check expression</li>
 * </ul>
 * 
 * @noreference
 */
public class EMFPatternLanguageJavaValidator extends AbstractEMFPatternLanguageJavaValidator {

    private static final class InputKeyToData implements Function<IInputKey, String> {
        
        EMFTypeSystem typeSystem;
        
        public InputKeyToData(EMFTypeSystem typeSystem) {
            this.typeSystem = typeSystem;
        }
        
        @Override
        public String apply(IInputKey input) {
            if (input instanceof EClassTransitiveInstancesKey) {
                return ((EClassTransitiveInstancesKey) input).getEmfKey().getName();
            } else if (input instanceof EDataTypeInSlotsKey) {
                EDataType datatype = ((EDataTypeInSlotsKey) input).getEmfKey();
                if (datatype instanceof EEnum) {
                    return datatype.getName();
                } else {
                    // In case of non-enum datatypes use corresponding Java type instead
                    Class<?> clazz = typeSystem.getJavaClass((EDataTypeInSlotsKey) input);
                    return EMFIssueCodes.JAVA_TYPE_PREFIX + new JavaTransitiveInstancesKey(clazz).getWrappedKey();
                }
            } else if (input instanceof JavaTransitiveInstancesKey) {
                return EMFIssueCodes.JAVA_TYPE_PREFIX + ((JavaTransitiveInstancesKey) input).getWrappedKey();
            }
            return null;
        }
    }

    private static final class SamePackageUri implements Predicate<PackageImport> {
        private final String nsUri;

        private SamePackageUri(String nsUri) {
            this.nsUri = nsUri;
        }

        @Override
        public boolean apply(PackageImport importDecl) {
            return importDecl != null && nsUri.equals(importDecl.getEPackage().getNsURI());
        }
    }
    
    private static final class EClassType implements Predicate<IInputKey> {

        @Override
        public boolean apply(IInputKey input) {
            return input instanceof EClassTransitiveInstancesKey;
        }
        
    }

    private static Type getTypeFromPathExpressionTail(PathExpressionTail pathExpressionTail) {
        if (pathExpressionTail == null) {
            return null;
        }
        if (pathExpressionTail.getTail() != null) {
            return getTypeFromPathExpressionTail(pathExpressionTail.getTail());
        }
        return pathExpressionTail.getType();
    }

    private final Function<IInputKey, String> TYPENAME = new Function<IInputKey, String>() {

        @Override
        public String apply(IInputKey input) {
            return typeSystem.typeString(input);
        }
    };
    
    private final Predicate<EClass> NOTNULL_NOTPROXY = new Predicate<EClass>() {

        @Override
        public boolean apply(EClass input) {
            return input != null  && !input.eIsProxy();
        }
    };
    
    @Inject
    private IMetamodelProvider metamodelProvider;

    @Inject
    private ITypeInferrer typeInferrer;

    @Inject
    private EMFTypeSystem typeSystem;

    @Inject
    private IJvmModelAssociations associations;

    @Inject
    private TypeReferences typeReferences;
    
    @Inject
    private Logger logger;
    
    @Inject
    private EMFPatternLanguageJvmModelInferrerUtil inferrerUtil;

    private static class CustomMethodWrapper extends MethodWrapper{

        private Logger logger;

        protected CustomMethodWrapper(AbstractDeclarativeValidator instance, Method m, Logger logger) {
            super(instance, m);
            this.logger = logger;
        }

        @Override
        protected void handleInvocationTargetException(Throwable targetException, State state) {
            // superclass ignores NPEs, instead we should at least log them
            if (targetException instanceof NullPointerException) {
                logger.warn("Unexpected validation error", targetException);
            }
            super.handleInvocationTargetException(targetException, state);
        }

    }
    
    @Override
    protected MethodWrapper createMethodWrapper(AbstractDeclarativeValidator instanceToUse, Method method) {
        return new CustomMethodWrapper(instanceToUse, method, logger);
    }
    
    @Override
    protected List<EPackage> getEPackages() {
        // PatternLanguagePackage must be added to the defaults, otherwise the core language validators not used in the
        // validation process
        List<EPackage> result = super.getEPackages();
        result.add(PatternLanguagePackage.eINSTANCE);
        return result;
    }

    @Check
    public void checkDuplicatePackageImports(PatternModel patternModel) {
        List<PackageImport> importPackages = EMFPatternLanguageHelper.getAllPackageImports(patternModel);
        for (int i = 0; i < importPackages.size(); ++i) {
            EPackage leftPackage = importPackages.get(i).getEPackage();
            for (int j = i + 1; j < importPackages.size(); ++j) {
                EPackage rightPackage = importPackages.get(j).getEPackage();
                if (leftPackage.equals(rightPackage)) {
                    warning("Duplicate import of " + leftPackage.getNsURI(),
                            EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, i,
                            EMFIssueCodes.DUPLICATE_IMPORT);
                    warning("Duplicate import of " + rightPackage.getNsURI(),
                            EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, j,
                            EMFIssueCodes.DUPLICATE_IMPORT);
                }
            }
        }
    }

    @Check
    public void checkPackageImportGeneratedCode(PackageImport packageImport) {
        if (packageImport.getEPackage() != null && packageImport.getEPackage().getNsURI() != null && !metamodelProvider
                .isGeneratedCodeAvailable(packageImport.getEPackage(), packageImport.eResource().getResourceSet())) {
            warning(String.format(
                    "The generated code of the Ecore model %s cannot be found. Check the org.eclipse.emf.ecore.generated_package extension in the model project or consider setting up a generator model for the generated code to work.",
                    packageImport.getEPackage().getNsURI()),
                    EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
                    EMFIssueCodes.IMPORT_WITH_GENERATEDCODE);
        }
    }

    @Check
    public void checkParametersNamed(Pattern pattern) {
        for (Variable var : pattern.getParameters()) {
            if (var.getName() != null && var.getName().startsWith("_")) {
                error("Parameter name must not start with _", var, PatternLanguagePackage.Literals.VARIABLE__NAME,
                        EMFIssueCodes.SINGLEUSE_PARAMETER);
            }
        }
    }

    @Check
    public void checkEnumValues(EnumValue value) {
        if (value.eContainer() instanceof PathExpressionHead) {
            // If container is PathExpression check for enum type assignability
            EEnum enumType = value.getEnumeration();
            if (enumType == null && value.getLiteral() != null) {
                enumType = value.getLiteral().getEEnum();
            }
            PathExpressionHead expression = (PathExpressionHead) value.eContainer();
            try {
                EEnum expectedType = EMFPatternLanguageScopeHelper.calculateEnumerationType(expression);
                if (enumType != null && !expectedType.equals(enumType)) {
                    error(String.format("Inconsistent enumeration types: found %s but expected %s", enumType.getName(),
                            expectedType.getName()), value, EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION,
                            EMFIssueCodes.INVALID_ENUM_LITERAL);
                }
            } catch (ResolutionException e) {
                // EClassifier type = EMFPatternLanguageScopeHelper.calculateExpressionType(expression);
                String name = (enumType == null) ? "<UNKNOWN>" : enumType.getName();
                error(String.format("Invalid enumeration constant %s", name), value,
                        EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION, EMFIssueCodes.INVALID_ENUM_LITERAL);
            }
        }
    }

    @Check
    public void checkVariableType(Variable variable) {
        if (CorePatternLanguageHelper.isParameter(variable)) {
            checkParameterTypes(variable);
        } else {
            checkPatternVariablesType(variable);
        }
    }
    
    /**
     * A variable's type can come from different sources: parameter's type definition, type definitions in the pattern
     * bodies or calculated from path expression constraints or find calls. In these situations one variable might have
     * conflicting type definitions. In conflicting situations if a variable's multiple types have a common subtype
     * (which would ensure a pattern match runtime) and has a type defined as a parameter, than this type will be
     * selected. In other cases we don't select a random type from the possibilities, the validator returns with an
     * error. Note, if the multiple type definitions are related in subtype-supertype relations than the most specific
     * is selected naturally (this is not even a warning).
     * 
     * @param pattern
     */
    private void checkPatternVariablesType(Variable variable) {
        Set<IInputKey> allPossibleTypes = typeInferrer.getAllPossibleTypes(variable);
        Set<EClassifier> possibleClassifiers = Sets.newHashSet(
                Iterables.filter(Iterables.transform(allPossibleTypes, new Function<IInputKey, EClassifier>() {

                    @Override
                    public EClassifier apply(IInputKey input) {
                        if (input instanceof EClassTransitiveInstancesKey) {
                            return ((EClassTransitiveInstancesKey) input).getEmfKey();
                        } else if (input instanceof EDataTypeInSlotsKey) {
                            return ((EDataTypeInSlotsKey) input).getEmfKey();
                        }
                        return null;
                    }
                }), Predicates.notNull()));
        // We only need to give warnings/errors if there are multiple possible types
        if (allPossibleTypes.size() <= 1 || allPossibleTypes.contains(BottomTypeKey.INSTANCE)) {
            return;
        }

        Set<String> typeNameSet = Sets.newHashSet(Iterables.transform(allPossibleTypes, TYPENAME));

        Set<String> classifierNamesSet = new HashSet<>();
        Set<String> classifierPackagesSet = new HashSet<>();
        for (EClassifier classifier : possibleClassifiers) {
            classifierNamesSet.add(classifier.getName());
            if (classifier.getEPackage() != null) {
                classifierPackagesSet.add(classifier.getEPackage().getName());
            }
        }

        // If the String sets contains only 1 elements than it is an error
        // There is some element which is defined multiple types within the ecores
        if (possibleClassifiers.size() > 1 && classifierNamesSet.size() == 1 && classifierPackagesSet.size() <= 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("Variable ");
            sb.append(variable.getName());
            sb.append(" has a type ");
            sb.append(classifierNamesSet.iterator().next());
            sb.append(" which has multiple definitions: ");
            for (EClassifier cls : possibleClassifiers) {
                sb.append(" '");
                if (cls.eIsProxy()) {
                    sb.append(((BasicEObjectImpl) cls).eProxyURI());
                } else {
                    sb.append(cls.eResource().getURI());
                }
                sb.append("' -- ");
            }
            error(sb.toString(), variable.getReferences().get(0), null,
                    EMFIssueCodes.VARIABLE_TYPE_MULTIPLE_DECLARATION);
        }
        final IInputKey declaredType = typeInferrer.getDeclaredType(variable);
        final PatternModel patternModel = EcoreUtil2.getContainerOfType(variable, PatternModel.class);
        if (declaredType == null) {
            if (!typeSystem.hasCommonSubtype(allPossibleTypes,
                    EMFPatternLanguageHelper.getEPackageImportsIterable(patternModel))) {
                if (variable instanceof ParameterRef) {
                    error("Ambiguous variable type definitions: " + typeNameSet
                            + ", type cannot be selected. Please specify the one to be used as the parameter type"
                            + " by adding it to the parameter definition.", ((ParameterRef) variable).getReferredParam(), null,
                            EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
                } else {
                    error("Inconsistent variable type definitions: " + typeNameSet + ", type cannot be selected.",
                            variable.getReferences().get(0), null, EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
                }
            }
        } else {
            // Check whether all types conforms to declared type
            Iterable<IInputKey> unconformTypes = Iterables.filter(allPossibleTypes, new Predicate<IInputKey>() {

                @Override
                public boolean apply(IInputKey input) {
                    return !typeSystem.isConformant(declaredType, input);
                }
            });
            Iterable<String> inconsistentTypes = Iterables.transform(Iterables.filter(unconformTypes, new Predicate<IInputKey>(){

                @Override
                public boolean apply(IInputKey input) {
                    return !typeSystem.hasCommonSubtype(ImmutableSet.of(declaredType, input), EMFPatternLanguageHelper.getEPackageImportsIterable(patternModel));
                }
                
            }), TYPENAME);
            if (!Iterables.isEmpty(inconsistentTypes)) {
                error("Variable types [" + Joiner.on(", ").join(inconsistentTypes)
                        + "] do not conform to declared type " + typeSystem.typeString(declaredType),
                        variable.getReferences().get(0), null, EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
            }

        }
    }
    
    private void checkParameterTypes(final Variable variable) {
        Iterable<Variable> parameterReferences = Iterables.filter(
                CorePatternLanguageHelper.getLocalReferencesOfParameter(variable), Predicates.notNull());
        
        final IInputKey inferredType = typeInferrer.getType(variable);
        if (variable.getType() == null) {
            // Missing type validation
            Set<IInputKey> possibleTypes = Sets.newHashSet();

            for (Variable bodyVar : parameterReferences) {
                possibleTypes.addAll(typeInferrer.getAllPossibleTypes(bodyVar));
            }
            reportMissingParameterTypeDeclaration(variable, possibleTypes, typeInferrer.getInferredType(variable));
            
            if (possibleTypes.isEmpty()) {
                return;
            } else if (possibleTypes.size() > 1 && Iterables.all(possibleTypes, Predicates.instanceOf(EClassTransitiveInstancesKey.class))) {
                Set<IInputKey> types = typeSystem.getCompatibleSupertypes(possibleTypes);
                Iterable<EClass> eClasses = Iterables.filter(Iterables.transform(
                        Iterables.filter(types, EClassTransitiveInstancesKey.class),
                        new Function<EClassTransitiveInstancesKey, EClass>() {
                            
                            @Override
                            public EClass apply(EClassTransitiveInstancesKey input) {
                                return input.getEmfKey();
                                
                            }
                            
                        }), NOTNULL_NOTPROXY);
                Iterable<String> typeNames = Iterables.concat(Iterables.transform(eClasses, new Function<EClass, String>() {

                    @Override
                    public String apply(EClass input) {
                        return input.getName();
                    }
                }), ImmutableSet.of("EObject"));
                String[] issueData = Iterables.toArray(typeNames, String.class);
                EClass reduced = IterableExtensions.fold(eClasses, EcorePackage.Literals.EOBJECT, new Function2<EClass, EClass, EClass>() {

                    @Override
                    public EClass apply(EClass t1, EClass t2) {
                        if (t1 == EcorePackage.Literals.EOBJECT) {
                            return t2;
                        } else if (t1 == null || t2 == null) {
                            return null;
                        } else {
                            return (EClass) EcoreUtil2.getCompatibleType(t1, t2, null);
                        }
                    }
                    
                });
                if (reduced == null) {
                    error("Variable type cannot be calculated unambiguously, the types [" 
                            + Joiner.on(", ").join(Iterables.transform(possibleTypes, TYPENAME))
                            + "] have no _unique_ common supertype. The list of possible supertypes found are [" 
                            + Joiner.on(", ").join(typeNames)
                            + "], specify one as the intended supertype.", variable, null,
                            EMFIssueCodes.PARAMETER_TYPE_AMBIGUOUS, issueData);
                }
            }
        } else {
            // Check for more specific type inferrable for bodies
            Iterable<IInputKey> referenceTypes = Iterables.filter(Iterables.transform(parameterReferences, new Function<Variable, IInputKey>() {

                @Override
                public IInputKey apply(Variable input) {
                    return typeInferrer.getInferredType(input);
                }
            }), Predicates.notNull());
            boolean allTypesMoreSpecific = Iterables.all(referenceTypes, new Predicate<IInputKey>() {

                @Override
                public boolean apply(IInputKey aggregatedType) {
                    return !Objects.equals(inferredType, aggregatedType) 
                            && Objects.equals(inferredType.getClass(), aggregatedType.getClass())
                            && typeSystem.isConformant(inferredType, aggregatedType);
                }
                
            });
            Iterator<IInputKey> it = referenceTypes.iterator();
            if (it.hasNext() && allTypesMoreSpecific) {
                Set<IInputKey> aggregatedTypes = typeSystem.minimizeTypeInformation(Sets.newHashSet(referenceTypes), true);
                if (aggregatedTypes.size() == 1 && inferredType != null) {
                    IInputKey aggregatedType = aggregatedTypes.iterator().next();
                    if (!Objects.equals(inferredType, aggregatedType) && Objects.equals(inferredType.getClass(), aggregatedType.getClass()) && typeSystem.isConformant(inferredType, aggregatedType)) {
                        warning("Declared type " + typeSystem.typeString(inferredType) + " is less specific then the type " + typeSystem.typeString(aggregatedType) + " inferred from bodies", variable, null,
                                EMFIssueCodes.PARAMETER_TYPE_INVALID);                
                    }
                }
            }
        }
    }

    private void reportMissingParameterTypeDeclaration(Variable parameter, Set<IInputKey> possibleTypes, IInputKey inferredType) {
        if (possibleTypes.isEmpty()) {
            return;
        } else if (possibleTypes.size() == 1 && !(possibleTypes.iterator().next() instanceof BottomTypeKey)) {
            String[] issueData = new String[]{new InputKeyToData(typeSystem).apply(inferredType)};
            info("Type not defined for variable " + parameter.getName() + ", inferred type " + typeSystem.typeString(inferredType) + " is used instead.",
                    PatternLanguagePackage.Literals.VARIABLE__NAME, EMFIssueCodes.MISSING_PARAMETER_TYPE,
                    issueData);
        } else {
            Set<IInputKey> orderedTypes = ImmutableSortedSet.orderedBy(new Comparator<IInputKey>() {
    
                @Override
                public int compare(IInputKey o1, IInputKey o2) {
                    if (o1 instanceof EClassTransitiveInstancesKey && !(o2 instanceof EClassTransitiveInstancesKey)) {
                        return +1;
                    } else if (o2 instanceof EClassTransitiveInstancesKey && !(o1 instanceof EClassTransitiveInstancesKey)) {
                        return -1;
                    } else if (o1 instanceof EDataTypeInSlotsKey && !(o2 instanceof EDataTypeInSlotsKey)) {
                        return +1;
                    } else if (o2 instanceof EDataTypeInSlotsKey && !(o1 instanceof EDataTypeInSlotsKey)) {
                        return +1;
                    } else if (typeSystem.isConformant(o1, o2)){ //Common type group
                        return +1;
                    } else if (typeSystem.isConformant(o2, o1)) {
                        return -1;
                    }
                    return 0;
                }}).addAll(possibleTypes).build();
            Set<String> superClasses = (Iterables.any(possibleTypes, new EClassType())) 
                    ? ImmutableSet.of("EObject")
                    : ImmutableSet.of(EMFIssueCodes.JAVA_TYPE_PREFIX + "java.lang.Object");
            Iterable<String> typeNames = Iterables.concat(Iterables.filter(Iterables.transform(orderedTypes, new InputKeyToData(typeSystem)), Predicates.notNull()), superClasses);
            String[] issueData = Iterables.toArray(typeNames, String.class);
            if (issueData.length > 0) {
                info("Type not defined for variable " + parameter.getName() + ", inferred type " + typeSystem.typeString(inferredType) + " is used instead.",
                        PatternLanguagePackage.Literals.VARIABLE__NAME, EMFIssueCodes.MISSING_PARAMETER_TYPE,
                        issueData);
            }
        }
    }
    
    /**
     * A validator for cartesian products (isolated constraints) in pattern bodies. There are two types of warnings:
     * strict and soft. Strict warning means that there are constraints in the body which has no connection at all, in
     * soft cases they connected at least with a count find. The validator's result always just a warning, however a
     * strict warning usually a modeling design flaw which should be corrected.
     * 
     * @param patternBody
     */
    @Check(CheckType.NORMAL)
    public void checkForCartesianProduct(PatternBody patternBody) {
        List<Variable> variables = Lists.newArrayList(patternBody.getVariables());
        List<Variable> unnamedRunningVariables = CorePatternLanguageHelper.getUnnamedRunningVariables(patternBody);
        variables.removeAll(unnamedRunningVariables);
        UnionFind<Variable> justPositiveUnionFindForVariables = new UnionFind<>(variables);
        UnionFind<Variable> generalUnionFindForVariables = new UnionFind<>(variables);
        boolean isSecondRunNeeded = false;

        // First run
        // Just put together the real positive connections, and all of the general connections first
        for (Constraint constraint : patternBody.getConstraints()) {
            Set<Variable> positiveVariables = new HashSet<>();
            Set<Variable> generalVariables = new HashSet<>();
            if (constraint instanceof CompareConstraint) {
                // Equality and inequality (==, !=)
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                ValueReference leftValueReference = compareConstraint.getLeftOperand();
                ValueReference rightValueReference = compareConstraint.getRightOperand();
                Set<Variable> leftVariables = CorePatternLanguageHelper
                        .getVariablesFromValueReference(leftValueReference);
                Set<Variable> rightVariables = CorePatternLanguageHelper
                        .getVariablesFromValueReference(rightValueReference);
                if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                    // Equality ==
                    if (!isValueReferenceComputed(leftValueReference)
                            && !isValueReferenceComputed(rightValueReference)) {
                        positiveVariables.addAll(leftVariables);
                        positiveVariables.addAll(rightVariables);
                        generalVariables.addAll(leftVariables);
                        generalVariables.addAll(rightVariables);
                    } else {
                        isSecondRunNeeded = true;
                        generalVariables.addAll(leftVariables);
                        generalVariables.addAll(rightVariables);
                    }
                } else if (CompareFeature.INEQUALITY.equals(compareConstraint.getFeature())) {
                    // Inequality !=
                    generalVariables.addAll(leftVariables);
                    generalVariables.addAll(rightVariables);
                }
            } else if (constraint instanceof PatternCompositionConstraint) {
                // Find and neg-find constructs
                PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                if (!patternCompositionConstraint.isNegative()) {
                    // Positive composition (find)
                    for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                        if (!isValueReferenceComputed(valueReference)) {
                            positiveVariables
                                    .addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                            generalVariables
                                    .addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                        } else {
                            isSecondRunNeeded = true;
                            generalVariables
                                    .addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                        }
                    }
                } else {
                    // Negative composition (neg find)
                    for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                        generalVariables
                                .addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    }
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                // Normal attribute-reference constraint
                PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                ValueReference valueReference = pathExpressionHead.getDst();
                Variable pathExpressionHeadSourceVariable = null;
                if (pathExpressionHead.getSrc() != null) {
                    pathExpressionHeadSourceVariable = pathExpressionHead.getSrc().getVariable();
                }
                if (!isValueReferenceComputed(valueReference)) {
                    positiveVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    positiveVariables.add(pathExpressionHeadSourceVariable);
                    generalVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    generalVariables.add(pathExpressionHeadSourceVariable);
                } else {
                    isSecondRunNeeded = true;
                    generalVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    generalVariables.add(pathExpressionHeadSourceVariable);
                }
            } else if (constraint instanceof CheckConstraint) {
                // Variables used together in check expression, always negative
                CheckConstraint checkConstraint = (CheckConstraint) constraint;
                generalVariables.addAll(CorePatternLanguageHelper
                        .getReferencedPatternVariablesOfXExpression(checkConstraint.getExpression(), associations));
            }
            justPositiveUnionFindForVariables.unite(positiveVariables);
            generalUnionFindForVariables.unite(generalVariables);
        }

        // Second run
        // If variables in a computation formula (e.g.: count find Pattern(X,Y)) are in the same union in the positive
        // case then they are considered to be in a positive relation with the respective target as well
        // M == count find Pattern(X,Y), so M with X and Y is positive if X and Y is positive
        // If the aggregated contains unnamed/running vars it should be omitted during the positive relation checking
        if (isSecondRunNeeded) {
            for (Constraint constraint : patternBody.getConstraints()) {
                Set<Variable> positiveVariables = new HashSet<>();
                if (constraint instanceof CompareConstraint) {
                    CompareConstraint compareConstraint = (CompareConstraint) constraint;
                    if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                        // Equality (==), with aggregates in it
                        ValueReference leftValueReference = compareConstraint.getLeftOperand();
                        ValueReference rightValueReference = compareConstraint.getRightOperand();
                        if (isValueReferenceComputed(leftValueReference)
                                || isValueReferenceComputed(rightValueReference)) {
                            addPositiveVariablesFromValueReference(unnamedRunningVariables,
                                    justPositiveUnionFindForVariables, positiveVariables, leftValueReference);
                            addPositiveVariablesFromValueReference(unnamedRunningVariables,
                                    justPositiveUnionFindForVariables, positiveVariables, rightValueReference);
                        }
                    }
                } else if (constraint instanceof PatternCompositionConstraint) {
                    PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                    if (!patternCompositionConstraint.isNegative()) {
                        // Positive composition (find), with aggregates in it
                        for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                            addPositiveVariablesFromValueReference(unnamedRunningVariables,
                                    justPositiveUnionFindForVariables, positiveVariables, valueReference);
                        }
                    }
                } else if (constraint instanceof PathExpressionConstraint) {
                    // Normal attribute-reference constraint, with aggregates in it
                    PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                    PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                    Variable pathExpressionHeadSourceVariable = null;
                    if (pathExpressionHead.getSrc() != null) {
                        pathExpressionHeadSourceVariable = pathExpressionHead.getSrc().getVariable();
                    }
                    positiveVariables.add(pathExpressionHeadSourceVariable);
                    ValueReference valueReference = pathExpressionHead.getDst();
                    addPositiveVariablesFromValueReference(unnamedRunningVariables, justPositiveUnionFindForVariables,
                            positiveVariables, valueReference);
                }
                justPositiveUnionFindForVariables.unite(positiveVariables);
            }
        }

        // Remove variables which are equated to constants
        for (Constraint constraint : patternBody.getConstraints()) {
            if (constraint instanceof CompareConstraint) {
                // Just equality
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                    ValueReference leftValueReference = compareConstraint.getLeftOperand();
                    ValueReference rightValueReference = compareConstraint.getRightOperand();
                    if (isConstantExpression(patternBody, leftValueReference)
                            && rightValueReference instanceof VariableValue) {
                        VariableValue variableValue = (VariableValue) rightValueReference;
                        Variable variableToRemove = variableValue.getValue().getVariable();
                        generalUnionFindForVariables = copyAndRemove(generalUnionFindForVariables, variableToRemove);
                        justPositiveUnionFindForVariables = copyAndRemove(justPositiveUnionFindForVariables,
                                variableToRemove);
                    } else if (leftValueReference instanceof VariableValue
                            && isConstantExpression(patternBody, rightValueReference)) {
                        VariableValue variableValue = (VariableValue) leftValueReference;
                        Variable variableToRemove = variableValue.getValue().getVariable();
                        generalUnionFindForVariables = copyAndRemove(generalUnionFindForVariables, variableToRemove);
                        justPositiveUnionFindForVariables = copyAndRemove(justPositiveUnionFindForVariables,
                                variableToRemove);
                    }
                }
            }
        }

        if (generalUnionFindForVariables.getPartitions().size() > 1) {
            // Giving strict warning in this case
            warning("The pattern body contains isolated constraints (\"cartesian products\") that can lead to severe performance and memory footprint issues. The independent partitions are: "
                    + prettyPrintPartitions(generalUnionFindForVariables) + ".", patternBody, null,
                    EMFIssueCodes.CARTESIAN_STRICT_WARNING);
        } else if (justPositiveUnionFindForVariables.getPartitions().size() > 1) {
            // Giving soft warning in this case
            warning("The pattern body contains constraints which are only loosely connected. This may negatively impact performance. The weakly dependent partitions are: "
                    + prettyPrintPartitions(justPositiveUnionFindForVariables), patternBody, null,
                    EMFIssueCodes.CARTESIAN_SOFT_WARNING);
        }
    }

    /**
     * A value reference is constant, if it represents either (1) a literal value, (2) an enum or (3) a eval with no
     * incoming parameters.
     */
    private boolean isConstantExpression(PatternBody body, ValueReference reference) {
        return (reference instanceof LiteralValueReference)
            || (reference instanceof EnumValue)
            || (reference instanceof FunctionEvaluationValue 
               && CorePatternLanguageHelper.getUsedVariables(((FunctionEvaluationValue)reference).getExpression(), body.getVariables()).isEmpty());
    }
    
    private void addPositiveVariablesFromValueReference(List<Variable> unnamedRunningVariables,
            UnionFind<Variable> justPositiveUnionFindForVariables, Set<Variable> positiveVariables,
            ValueReference valueReference) {
        Set<Variable> leftVariables = CorePatternLanguageHelper.getVariablesFromValueReference(valueReference);
        leftVariables.removeAll(unnamedRunningVariables);
        if (justPositiveUnionFindForVariables.isSameUnion(leftVariables)) {
            positiveVariables.addAll(leftVariables);
        }
    }

    /**
     * Returns a copy of this with the given value removed. The given value does not have to be a set's root node.
     */
    private static <V> UnionFind<V> copyAndRemove(UnionFind<V> unionFind, V element) {
        UnionFind<V> result = new UnionFind<>();
        for (Set<V> partition : unionFind.getPartitions()) {
            Set<V> filteredPartition = new HashSet<>(partition);
            filteredPartition.remove(element);
            result.makeSet(filteredPartition);
        }
        return result;
    }

    private static String prettyPrintPartitions(UnionFind<Variable> unionFind) {
        StringBuilder result = new StringBuilder();
        for (Set<Variable> partition : unionFind.getPartitions()) {
            result.append("[");
            Iterable<String> variableNames = Iterables.transform(partition, new Function<Variable, String>() {
                @Override
                public String apply(Variable variable) {
                    return variable.getName();
                }
            });
            result.append(Joiner.on(", ").join(variableNames));
            result.append("]");
        }
        return result.toString();
    }

    private static boolean isValueReferenceComputed(ValueReference valueReference) {
        return valueReference instanceof ComputationValue;
    }

    /**
     * This validator checks if the literal or computational values match the other side's type in a compare constraint
     * (equality/inequality). Both sides can be literal, we will do the check if at least on side is that.
     * 
     * @param compareConstraint
     */
    @Check
    public void checkForWrongLiteralAndComputationValuesInCompareConstraints(CompareConstraint compareConstraint) {
        // Equality and inequality (==, !=)
        ValueReference leftValueReference = compareConstraint.getLeftOperand();
        ValueReference rightValueReference = compareConstraint.getRightOperand();
        if ((leftValueReference instanceof LiteralValueReference || leftValueReference instanceof ComputationValue
                || rightValueReference instanceof LiteralValueReference
                || rightValueReference instanceof ComputationValue) && !(leftValueReference instanceof VariableValue)
                && !(rightValueReference instanceof VariableValue)) {
            IInputKey leftClassifier = typeInferrer.getType(leftValueReference);
            IInputKey rightClassifier = typeInferrer.getType(rightValueReference);
            if (!typeSystem.isConformant(leftClassifier, rightClassifier)) {
                final String leftName = leftClassifier == null ? "null" : leftClassifier.getPrettyPrintableName();
                final String rightName = rightClassifier == null ? "null" : rightClassifier.getPrettyPrintableName();
                error("The types of the literal/computational values are different: " + leftName + ", " + rightName
                        + ".", compareConstraint, null, EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE);
            }
        }
    }

    /**
     * This validator checks if the literal or computational values match the path expression's type.
     * 
     * @param pathExpressionConstraint
     */
    @Check
    public void checkForWrongLiteralAndComputationValuesInPathExpressionConstraints(
            PathExpressionConstraint pathExpressionConstraint) {
        // Normal attribute-reference constraint
        PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
        ValueReference valueReference = pathExpressionHead.getDst();
        if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
            IInputKey actualType = typeInferrer.getType(valueReference);
            IInputKey expectedType = typeSystem
                    .extractTypeDescriptor(getTypeFromPathExpressionTail(pathExpressionHead.getTail()));
            if (!typeSystem.isConformant(expectedType, actualType)) {
                String name = expectedType == null ? "<unknown>" : typeSystem.typeString(expectedType);
                error("The type inferred from the path expression (" + name
                        + ") is different from the input literal/computational value ("
                        + typeSystem.typeString(actualType) + ").", pathExpressionConstraint, null,
                        EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION);
            }
        }
    }

    /**
     * This validator checks if the literal or computational values match the pattern call's type.
     * 
     * @param patternCall
     */
    @Check
    public void checkForWrongLiteralAndComputationValuesInPatternCalls(PatternCall patternCall) {
        if (patternCall.getParameters().size() != patternCall.getPatternRef().getParameters().size()) {
            // This kind of error is detected in another place, however it throws an exception during literal checks
            return;
        }
        // Find and neg find (including count find as well)
        for (ValueReference valueReference : patternCall.getParameters()) {
            if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
                Pattern pattern = patternCall.getPatternRef();
                Variable variable = pattern.getParameters().get(patternCall.getParameters().indexOf(valueReference));
                IInputKey parameterType = typeInferrer.getType(variable);
                IInputKey inputType = typeInferrer.getType(valueReference);
                if (!typeSystem.isConformant(parameterType, inputType)) {
                    final String typeClassifierName = parameterType == null ? "(unknown)"
                            : typeSystem.typeString(parameterType);
                    error("The type inferred from the called pattern (" + typeClassifierName
                            + ") is different from the input literal/computational value ("
                            + typeSystem.typeString(inputType) + ").", patternCall, null,
                            EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL);
                }
            }
        }
    }

    /**
     * This validator looks up all variables in the {@link CheckConstraint} and reports an error if one them is not an
     * {@link EDataType} instance. We do not allow arbitrary EMF elements in, so the checks are less likely to have
     * side-effects.
     */
    @Check
    public void checkForWrongVariablesInXExpressions(CheckConstraint checkConstraint) {
        checkForWrongVariablesInXExpressionsInternal(checkConstraint.getExpression());
    }

    /**
     * This validator looks up all variables in the {@link FunctionEvaluationValue} and reports an error if one them is
     * not an {@link EDataType} instance. We do not allow arbitrary EMF elements in, so the checks are less likely to
     * have side-effects.
     */
    @Check
    public void checkForWrongVariablesInXExpressions(FunctionEvaluationValue eval) {
        checkForWrongVariablesInXExpressionsInternal(eval.getExpression());
    }

    private void checkForWrongVariablesInXExpressionsInternal(final XExpression expression) {
        for (Variable variable : CorePatternLanguageHelper.getReferencedPatternVariablesOfXExpression(expression,
                associations)) {
            IInputKey classifier = typeInferrer.getType(variable);
            if (classifier instanceof BottomTypeKey) {
                error("Only simple EDataTypes are allowed in check() and eval() expressions. The variable "
                        + variable.getName() + " has an unknown type.",
                        expression.eContainer(), null, EMFIssueCodes.CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR);
            } else if (classifier != null && !(classifier instanceof EDataTypeInSlotsKey)
                    && !(classifier instanceof JavaTransitiveInstancesKey)) {// null-check needed, otherwise code throws
                                                                             // NPE for classifier.getName()
                error("Only simple EDataTypes are allowed in check() and eval() expressions. The variable "
                        + variable.getName() + " has a type of " + classifier.getPrettyPrintableName() + ".",
                        expression.eContainer(), null, EMFIssueCodes.CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR);
            }
        }
    }

    /**
     * This validator looks up all {@link EStructuralFeature} used in a {@link PathExpressionConstraint} and reports a
     * warning on each that is not representable by VIATRA Query. This is a warning, since we only see well-behaving
     * extensions in the host.
     * 
     * @param pathExpressionConstraint
     */
    @Check
    public void checkForNotWellbehavingDerivedFeatureInPathExpressions(
            PathExpressionConstraint pathExpressionConstraint) {
        PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
        Map<PathExpressionTail, EStructuralFeature> tailFeatureMap = getAllFeaturesFromPathExpressionTail(
                pathExpressionHead.getTail());
        for (Entry<PathExpressionTail, EStructuralFeature> tail : tailFeatureMap.entrySet()) {
            EStructuralFeature feature = tail.getValue();
            EMFModelComprehension comprehension = new EMFModelComprehension(new BaseIndexOptions());
            if (!comprehension.representable(feature)) {
                final EStructuralFeatureInstancesKey featureInputKey = new EStructuralFeatureInstancesKey(feature);
                if (SurrogateQueryRegistry.instance().hasSurrogateQueryFQN(featureInputKey)) {
                    final PQuery surrogateQuery = SurrogateQueryRegistry.instance().getSurrogateQuery(featureInputKey);
                    String surrogateQueryFQN = surrogateQuery == null ? "(null)"
                            : surrogateQuery.getFullyQualifiedName();
                    info("The derived/volatile feature " + feature.getName() + " of class "
                            + feature.getEContainingClass().getName()
                            + " used in the path expression has a surrogate query " + surrogateQueryFQN
                            + " which will be used by VIATRA Query.", tail.getKey().getType(), null,
                            EMFIssueCodes.SURROGATE_QUERY_EXISTS);
                } else {
                    warning("The derived/volatile feature " + feature.getName() + " of class "
                            + feature.getEContainingClass().getName()
                            + " used in the path expression is not representable in VIATRA Query."
                            + " For details, consult the documentation on well-behaving features.",
                            tail.getKey().getType(), null, EMFIssueCodes.FEATURE_NOT_REPRESENTABLE);
                }
            }
        }
    }

    @Check
    public void checkPatternName(Pattern pattern) {
        if (pattern.getName() != null && !SourceVersion.isName(pattern.getName())) {
            JvmType inferredSpecification = inferrerUtil.findInferredSpecification(pattern);
            if (inferredSpecification != null && !inferredSpecification.eIsProxy() && !SourceVersion.isName(inferredSpecification.getQualifiedName())) {
                error(String.format("The pattern name %s is not a valid Java classname", pattern.getName()), PatternLanguagePackage.Literals.PATTERN__NAME, EMFIssueCodes.OTHER_ISSUE);
            }
        }
    }
    
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

    @Check
    public void checkReferredPackages(ReferenceType type) {
        if (type.getRefname() == null || type.getRefname().eIsProxy()) {
            // If reference name is not set, do not check package
            return;
        }
        final EClass referredType = type.getRefname().getEContainingClass();
        final EPackage referredPackage = referredType.getEPackage();
        final String nsUri = Strings.emptyIfNull(referredPackage.getNsURI());
        final EObject rootContainer = EcoreUtil2.getRootContainer(type);
        if (rootContainer instanceof PatternModel) {
            PatternModel model = (PatternModel) rootContainer;
            if (model.getImportPackages() != null
                    && !Iterables.any(model.getImportPackages().getPackageImport(), new SamePackageUri(nsUri))) {
                error(String.format("Reference to an EClass %s that is not imported from EPackage %s.",
                        referredType.getName(), nsUri), type,
                        EMFPatternLanguagePackage.Literals.REFERENCE_TYPE__REFNAME,
                        EMFIssueCodes.MISSING_PACKAGE_IMPORT, nsUri);
            }

        }
    }

    @SuppressWarnings("restriction")
    @Check
    public void checkPatternImports(VQLImportSection section) {
        if (!isIgnored(IMPORT_UNUSED)) {
            final Set<Pattern> usedPatterns = Sets.newHashSet();
            final UnmodifiableIterator<PatternCall> it = Iterators.filter(section.eResource().getAllContents(),
                    PatternCall.class);
            while (it.hasNext()) {
                PatternCall call = it.next();
                usedPatterns.add(call.getPatternRef());
            }
            for (PatternImport decl : section.getPatternImport()) {
                if (!usedPatterns.contains(decl.getPattern())) {
                    warning("The import '" + CorePatternLanguageHelper.getFullyQualifiedName(decl.getPattern())
                            + "' is never used.", decl, null, IMPORT_UNUSED);
                }
            }

        }
    }

    @Check
    public void checkClassPath(PatternModel modelFile) {
        final JvmGenericType listType = (JvmGenericType) typeReferences.findDeclaredType(List.class, modelFile);
        if (listType == null || listType.getTypeParameters().isEmpty()) {
            error("Couldn't find a JDK 1.5 or higher on the project's classpath.", modelFile,
                    PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME, EMFIssueCodes.JDK_NOT_ON_CLASSPATH);
        } else if (typeReferences.findDeclaredType(ViatraQueryEngine.class, modelFile) == null) {
            error("Couldn't find the mandatory library 'org.eclipse.viatra.query.runtime' on the project's classpath.",
                    modelFile, PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
                    EMFIssueCodes.IQR_NOT_ON_CLASSPATH, "org.eclipse.viatra.query.runtime");
        }
    }
    
    @Check
    public void checkClassPath(Modifiers modifier) {
        if (modifier.getExecution() == ExecutionType.SEARCH) {
            final JvmEnumerationType lsBackendType = (JvmEnumerationType) typeReferences.findDeclaredType(LocalSearchBackendFactory.class, modifier);
            if (lsBackendType == null || lsBackendType.eIsProxy()) {
                error("Couldn't find the mandatory library 'org.eclipse.viatra.query.runtime.localsearch' on the project's classpath.",
                        modifier, PatternLanguagePackage.Literals.MODIFIERS__EXECUTION,
                        EMFIssueCodes.IQR_NOT_ON_CLASSPATH, "org.eclipse.viatra.query.runtime.localsearch");
            }
        }
    }
}
