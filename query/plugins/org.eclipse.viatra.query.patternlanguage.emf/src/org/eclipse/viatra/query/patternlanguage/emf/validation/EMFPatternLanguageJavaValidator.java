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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra.query.patternlanguage.emf.ResolutionException;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.XImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ComputationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.LiteralValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.UnionFind;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
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
 */
public class EMFPatternLanguageJavaValidator extends AbstractEMFPatternLanguageJavaValidator {

    private final static class SamePackageUri implements Predicate<PackageImport> {
        private final String nsUri;

        private SamePackageUri(String nsUri) {
            this.nsUri = nsUri;
        }

        @Override
        public boolean apply(PackageImport importDecl) {
            return nsUri.equals(importDecl.getEPackage().getNsURI());
        }
    }

    @Inject
    private IMetamodelProvider metamodelProvider;

    @Inject
    private IEMFTypeProvider emfTypeProvider;
    
    @Inject
    private IJvmModelAssociations associations;
    
    @Inject
    private TypeReferences typeReferences;

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
        if (packageImport.getEPackage() != null
                && packageImport.getEPackage().getNsURI() != null
                && !metamodelProvider.isGeneratedCodeAvailable(packageImport.getEPackage(), packageImport.eResource()
                        .getResourceSet())) {
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
                error(String.format("Invalid enumeration constant %s", enumType.getName()), value,
                        EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION, EMFIssueCodes.INVALID_ENUM_LITERAL);
            }
        }
    }

    /**
     * The parameter's type must be the same or more specific than the type inferred from the pattern's body. This
     * warning usually arises when we have more pattern bodies, which contains different type definitions for the same
     * parameter. In a case like this the common parameter's type is the most specific common supertype of the
     * respective calculated types in the bodies.
     * 
     * @param pattern
     */
    @Check(/*CheckType.NORMAL*/)
    public void checkPatternParametersType(Pattern pattern) {
        for (Variable variable : pattern.getParameters()) {
            EClassifier classifierCorrect = emfTypeProvider.getClassifierForVariable(variable);
            EClassifier classifierDefined = emfTypeProvider.getClassifierForType(variable.getType());
            if (classifierCorrect == null || classifierDefined == null || classifierDefined.equals(classifierCorrect)) {
                // Either correct - they are the same, or other validator returns the type error
                return;
            } else {
                if (classifierCorrect instanceof EClass && classifierDefined instanceof EClass) {
                    if (((EClass) classifierDefined).getEAllSuperTypes().contains(classifierCorrect)) {
                        // Correct the defined is more specific than what the pattern needs
                        return;
                    }
                }
                // OK, issue warning now
                warning(String.format(
                        "Inconsistent parameter type definition, should be %s based on the pattern definition",
                        classifierCorrect.getName()), variable, null, EMFIssueCodes.PARAMETER_TYPE_INVALID);
            }
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
    @Check(/*CheckType.NORMAL*/)
    public void checkPatternVariablesType(Pattern pattern) {
        for (PatternBody patternBody : pattern.getBodies()) {
            for (Variable variable : patternBody.getVariables()) {
                Set<EClassifier> possibleClassifiers = emfTypeProvider.getIrreducibleClassifiersForVariableInBody(
                        patternBody, variable);
                // We only need to give warnings/errors if there is more possible classifiers
                if (possibleClassifiers.size() > 1) {
                    Set<String> classifierNamesSet = new HashSet<String>();
                    Set<String> classifierPackagesSet = new HashSet<String>();
                    for (EClassifier classifier : possibleClassifiers) {
                        classifierNamesSet.add(classifier.getName());
                        if (classifier.getEPackage() != null) {
                            classifierPackagesSet.add(classifier.getEPackage().getName());
                        }
                    }
                    // If the String sets contains only 1 elements than it is an error
                    // There is some element which is defined multiple types within the ecores
                    if (classifierNamesSet.size() == 1 && classifierPackagesSet.size() <= 1) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Variable ");
                        sb.append(variable.getName());
                        sb.append(" has a type "); 
                        sb.append(classifierNamesSet.iterator().next());
                        sb.append(" which has multiple definitions: ");
                        for (EClassifier cls : possibleClassifiers) {
                            sb.append(" '");
                            if (cls.eIsProxy()) {
                                sb.append(((BasicEObjectImpl)cls).eProxyURI());
                            } else {
                                sb.append(cls.eResource().getURI());
                            }
                            sb.append("' -- ");
                        }
                        error(sb.toString(), variable
                                .getReferences().get(0), null, EMFIssueCodes.VARIABLE_TYPE_MULTIPLE_DECLARATION);
                    } else {
                        EClassifier explicitType = emfTypeProvider.getExplicitClassifierForPatternParameterVariable(variable);
                        PatternModel patternModel = (PatternModel) patternBody.eContainer().eContainer();
                        if (explicitType != null && possibleClassifiers.contains(explicitType)
                                && hasCommonSubType(patternModel, possibleClassifiers)) {
                            warning("Ambiguous variable type defintions: " + classifierNamesSet
                                    + ", the parameter type (" + explicitType.getName() + ") is used now.", variable
                                    .getReferences().get(0), null, EMFIssueCodes.VARIABLE_TYPE_INVALID_WARNING);
                        } else {
                            boolean isParameter = false;
                            for (Variable parameter : pattern.getParameters()) {
                                if (parameter.getName().equals(variable.getName())) {
                                    isParameter = true;
                                }
                            }
                            if (isParameter) {
                                error("Ambiguous variable type defintions: "
                                        + classifierNamesSet
                                        + ", type cannot be selected. Please specify the one to be used as the parameter type"
                                        + " by adding it to the parameter definition.",
                                        variable.getReferences().get(0), null,
                                        EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
                            } else {
                                error("Inconsistent variable type defintions: " + classifierNamesSet
                                        + ", type cannot be selected.", variable.getReferences().get(0), null,
                                        EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param patternModel
     * @param classifiers
     * @return True if the given classifiers has a common subtype. The {@link PatternModel} is needed for focusing the
     *         search, all ecore packages referenced from the patternmodel's head, and it's subpackages will be searched
     *         for common subtype elements.
     */
    private boolean hasCommonSubType(PatternModel patternModel, Set<EClassifier> classifiers) {
        Set<EClass> realSubTypes = new HashSet<EClass>();
        Set<EClassifier> probableSubTypes = new HashSet<EClassifier>();
        for (PackageImport packageImport : EMFPatternLanguageHelper.getPackageImportsIterable(patternModel)) {
            probableSubTypes.addAll(getAllEClassifiers(packageImport.getEPackage()));
        }
        for (EClassifier classifier : probableSubTypes) {
            if (classifier instanceof EClass) {
                EClass eClass = (EClass) classifier;
                if (eClass.getEAllSuperTypes().containsAll(classifiers)) {
                    realSubTypes.add(eClass);
                }
            }
        }
        return !realSubTypes.isEmpty();
    }

    /**
     * @param ePackage
     * @return all EClassifiers contained in the ePackage, and in the subpackages as well
     */
    private static Set<EClassifier> getAllEClassifiers(EPackage ePackage) {
        Set<EClassifier> resultSet = new HashSet<EClassifier>();
        resultSet.addAll(ePackage.getEClassifiers());
        for (EPackage subEPackage : ePackage.getESubpackages()) {
            resultSet.addAll(subEPackage.getEClassifiers());
        }
        return resultSet;
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
        List<Variable> variables = patternBody.getVariables();
        List<Variable> unnamedRunningVariables = CorePatternLanguageHelper.getUnnamedRunningVariables(patternBody);
        variables.removeAll(unnamedRunningVariables);
        UnionFind<Variable> justPositiveUnionFindForVariables = new UnionFind<Variable>(variables);
        UnionFind<Variable> generalUnionFindForVariables = new UnionFind<Variable>(variables);
        boolean isSecondRunNeeded = false;

        // First run
        // Just put together the real positive connections, and all of the general connections first
        for (Constraint constraint : patternBody.getConstraints()) {
            Set<Variable> positiveVariables = new HashSet<Variable>();
            Set<Variable> generalVariables = new HashSet<Variable>();
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
                            positiveVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                            generalVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                        } else {
                            isSecondRunNeeded = true;
                            generalVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                        }
                    }
                } else {
                    // Negative composition (neg find)
                    for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                        generalVariables.addAll(CorePatternLanguageHelper
                                .getVariablesFromValueReference(valueReference));
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
                Set<Variable> positiveVariables = new HashSet<Variable>();
                if (constraint instanceof CompareConstraint) {
                    CompareConstraint compareConstraint = (CompareConstraint) constraint;
                    if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                        // Equality (==), with aggregates in it
                        ValueReference leftValueReference = compareConstraint.getLeftOperand();
                        ValueReference rightValueReference = compareConstraint.getRightOperand();
                        if (isValueReferenceComputed(leftValueReference)
                                || isValueReferenceComputed(rightValueReference)) {
                            addPositiveVariablesFromValueReference(unnamedRunningVariables, justPositiveUnionFindForVariables, positiveVariables,
                                    leftValueReference);
                            addPositiveVariablesFromValueReference(unnamedRunningVariables, justPositiveUnionFindForVariables, positiveVariables,
                                    rightValueReference);
                        }
                    }
                } else if (constraint instanceof PatternCompositionConstraint) {
                    PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                    if (!patternCompositionConstraint.isNegative()) {
                        // Positive composition (find), with aggregates in it
                        for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                            addPositiveVariablesFromValueReference(unnamedRunningVariables, justPositiveUnionFindForVariables, positiveVariables,
                                    valueReference);
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
                    addPositiveVariablesFromValueReference(unnamedRunningVariables, justPositiveUnionFindForVariables, positiveVariables,
                            valueReference);
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
                    if ((leftValueReference instanceof LiteralValueReference || leftValueReference instanceof EnumValue)
                            && rightValueReference instanceof VariableValue) {
                        VariableValue variableValue = (VariableValue) rightValueReference;
                        Variable variableToRemove = variableValue.getValue().getVariable();
                        generalUnionFindForVariables = copyAndRemove(generalUnionFindForVariables, variableToRemove);
                        justPositiveUnionFindForVariables = copyAndRemove(justPositiveUnionFindForVariables, variableToRemove);
                    } else if (leftValueReference instanceof VariableValue
                            && (rightValueReference instanceof LiteralValueReference || rightValueReference instanceof EnumValue)) {
                        VariableValue variableValue = (VariableValue) leftValueReference;
                        Variable variableToRemove = variableValue.getValue().getVariable();
                        generalUnionFindForVariables = copyAndRemove(generalUnionFindForVariables, variableToRemove);
                        justPositiveUnionFindForVariables = copyAndRemove(justPositiveUnionFindForVariables, variableToRemove);
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
     * Returns a copy of this with the given value removed.
     * The given value does not have to be a set's root node.
     */
    private static <V> UnionFind<V> copyAndRemove(UnionFind<V> unionFind, V element) {
        UnionFind<V> result = new UnionFind<V>();
        for (Set<V> partition : unionFind.getPartitions()) {
            Set<V> filteredPartition = new HashSet<V>(partition);
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
                || rightValueReference instanceof LiteralValueReference || rightValueReference instanceof ComputationValue)
                && !(leftValueReference instanceof VariableValue) && !(rightValueReference instanceof VariableValue)) {
            EClassifier leftClassifier = emfTypeProvider
                    .getClassifierForLiteralComputationEnumValueReference(leftValueReference);
            EClassifier rightClassifier = emfTypeProvider
                    .getClassifierForLiteralComputationEnumValueReference(rightValueReference);
            if (!isCompatibleClassifiers(leftClassifier, rightClassifier)) {
                final String leftName = leftClassifier == null ? "null" : leftClassifier.getInstanceClassName();
                final String rightName = rightClassifier == null ? "null" : rightClassifier.getInstanceClassName();
                error("The types of the literal/computational values are different: "
                        + leftName + ", " + rightName + ".",
                        compareConstraint, null, EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE);
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
            EClassifier inputClassifier = emfTypeProvider
                    .getClassifierForLiteralComputationEnumValueReference(valueReference);
            EClassifier typeClassifier = emfTypeProvider.getClassifierForType(emfTypeProvider
                    .getTypeFromPathExpressionTail(pathExpressionHead.getTail()));
            if (!isCompatibleClassifiers(typeClassifier, inputClassifier)) {
                String name = typeClassifier == null ? "<unknown>" : typeClassifier.getInstanceClassName();
                error("The type inferred from the path expression (" + name
                        + ") is different from the input literal/computational value ("
                        + inputClassifier.getInstanceClassName() + ").", pathExpressionConstraint, null,
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
    		//This kind of error is detected in another place, however it throws an exception during literal checks
    		return;
    	}
    	// Find and neg find (including count find as well)
        for (ValueReference valueReference : patternCall.getParameters()) {
            if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
                Pattern pattern = patternCall.getPatternRef();
                Variable variable = pattern.getParameters().get(patternCall.getParameters().indexOf(valueReference));
                EClassifier typeClassifier = emfTypeProvider.getClassifierForVariable(variable);
                EClassifier inputClassifier = emfTypeProvider
                        .getClassifierForLiteralComputationEnumValueReference(valueReference);
                if (!isCompatibleClassifiers(typeClassifier, inputClassifier)) {
                    final String typeClassifierName = typeClassifier == null ? "(unknown)" : typeClassifier.getInstanceClassName();
                    error("The type inferred from the called pattern (" + typeClassifierName
                            + ") is different from the input literal/computational value ("
                            + inputClassifier.getInstanceClassName() + ").", patternCall, null,
                            EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL);
                }
            }
        }
    }

    private static boolean isCompatibleClassifiers(EClassifier classifierFirst, EClassifier classifierSecond) {
        if (classifierFirst != null && classifierSecond != null) {
            Class<?> firstInstanceClass = classifierFirst.getInstanceClass();
            Class<?> secondInstanceClass = classifierSecond.getInstanceClass();
            if (firstInstanceClass.equals(secondInstanceClass)) {
                return true;
            } else if (firstInstanceClass.isPrimitive() || secondInstanceClass.isPrimitive()) {
                Class<?> firstWrapperClass = getWrapperClassForType(firstInstanceClass);
                Class<?> secondWrapperClass = getWrapperClassForType(secondInstanceClass);
                if (firstWrapperClass.equals(secondWrapperClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param typeClass
     * @return The wrapper class if the input is primitive. If it is not, it returns with the input unchanged.
     */
    private static Class<?> getWrapperClassForType(Class<?> typeClass) {
        if (typeClass != null && typeClass.isPrimitive()) {
            if (typeClass == boolean.class) {
                return java.lang.Boolean.class;
            } else if (typeClass == byte.class) {
                return java.lang.Byte.class;
            } else if (typeClass == char.class) {
                return java.lang.Character.class;
            } else if (typeClass == double.class) {
                return java.lang.Double.class;
            } else if (typeClass == float.class) {
                return java.lang.Float.class;
            } else if (typeClass == int.class) {
                return java.lang.Integer.class;
            } else if (typeClass == long.class) {
                return java.lang.Long.class;
            } else if (typeClass == short.class) {
                return java.lang.Short.class;
            }
        }
        return typeClass;
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
     * This validator looks up all variables in the {@link FunctionEvaluationValue} and reports an error if one them is not an
     * {@link EDataType} instance. We do not allow arbitrary EMF elements in, so the checks are less likely to have
     * side-effects.
     */
    @Check
    public void checkForWrongVariablesInXExpressions(FunctionEvaluationValue eval) {
		checkForWrongVariablesInXExpressionsInternal(eval.getExpression());
    }
    
	private void checkForWrongVariablesInXExpressionsInternal(final XExpression expression) {
		for (Variable variable : CorePatternLanguageHelper.getReferencedPatternVariablesOfXExpression(expression, associations)) {
            EClassifier classifier = emfTypeProvider.getClassifierForVariable(variable);
            if (classifier != null && !(classifier instanceof EDataType)) {// null-check needed, otherwise code throws
                                                                           // NPE for classifier.getName()
                error("Only simple EDataTypes are allowed in check() and eval() expressions. The variable " + variable.getName()
                        + " has a type of " + classifier.getName() + ".", expression.eContainer(), null,
                        EMFIssueCodes.CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR);
            }
        }
	}

    /**
     * This validator looks up all {@link EStructuralFeature} used in a {@link PathExpressionConstraint} and reports a
     * warning on each that is not representable by EMF-IncQuery. This is a warning, since we only see well-behaving
     * extensions in the host.
     * 
     * @param pathExpressionConstraint
     */
    @Check
    public void checkForNotWellbehavingDerivedFeatureInPathExpressions(PathExpressionConstraint pathExpressionConstraint) {
        PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
        Map<PathExpressionTail, EStructuralFeature> tailFeatureMap = emfTypeProvider
                .getAllFeaturesFromPathExpressionTail(pathExpressionHead.getTail());
        for (Entry<PathExpressionTail, EStructuralFeature> tail : tailFeatureMap.entrySet()) {
            EStructuralFeature feature = tail.getValue();
            EMFModelComprehension comprehension = new EMFModelComprehension(new BaseIndexOptions());
            if (!comprehension.representable(feature)) {
            	final EStructuralFeatureInstancesKey featureInputKey = new EStructuralFeatureInstancesKey(feature);
				if(SurrogateQueryRegistry.instance().hasSurrogateQueryFQN(featureInputKey)) {
            		final PQuery surrogateQuery = SurrogateQueryRegistry.instance().getSurrogateQuery(featureInputKey);
                    String surrogateQueryFQN = surrogateQuery == null ? "(null)" : surrogateQuery.getFullyQualifiedName();
            		info("The derived/volatile feature " + feature.getName() + " of class "
                        + feature.getEContainingClass().getName()
                        + " used in the path expression has a surrogate query " + surrogateQueryFQN + " which will be used by EMF-IncQuery.",
                        tail.getKey().getType(), null, EMFIssueCodes.SURROGATE_QUERY_EXISTS);
            	} else {
                    warning("The derived/volatile feature " + feature.getName() + " of class "
                            + feature.getEContainingClass().getName()
                            + " used in the path expression is not representable in EMF-IncQuery."
                            + " For details, consult the documentation on well-behaving features.",
                            tail.getKey().getType(), null, EMFIssueCodes.FEATURE_NOT_REPRESENTABLE);
            	}
            }
        }
    }

    @Check
    public void checkReferredPackages(ReferenceType type) {
        if (type.getRefname() == null || type.getRefname().eIsProxy()) {
            //If reference name is not set, do not check package
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
    public void checkPatternImports(XImportSection section) {
        if (!isIgnored(IMPORT_UNUSED)) {
            final Set<Pattern> usedPatterns = Sets.newHashSet();
            final UnmodifiableIterator<PatternCall> it = Iterators.filter(section.eResource().getAllContents(), PatternCall.class);
            while (it.hasNext()) {
                PatternCall call = it.next();
                usedPatterns.add(call.getPatternRef());
            }
            for (PatternImport decl : section.getPatternImport()) {
                if (!usedPatterns.contains(decl.getPattern())) {
                    warning("The import '" + CorePatternLanguageHelper.getFullyQualifiedName(decl.getPattern())
                            + "' is never used.", decl, null,
                            IMPORT_UNUSED);
                }
            }

        }
    }

    @Check
    public void checkMissingParameterTypes(Variable variable) {
        if (variable.eContainer() instanceof Pattern && variable.getType() == null) {
            Pattern pattern = (Pattern) variable.eContainer();
            Set<EClassifier> possibleTypes = Sets.newHashSet();
            for (PatternBody body : pattern.getBodies()) {
                possibleTypes.addAll(emfTypeProvider.getIrreducibleClassifiersForVariableInBody(body, variable));
            }
            Iterable<String> typeNames = Iterables.transform(Iterables.filter(possibleTypes, EClass.class),
                    new Function<EClassifier, String>() {

                        @Override
                        public String apply(EClassifier input) {
                            final String name = input.getName();
                            return (Strings.isEmpty(name)) ? "" : name;
                        }

                    });

            String[] issueData = Iterables.toArray(typeNames, String.class);
            if (issueData.length > 0) {
                warning("Type not defined for variable " + variable.getName(),
                        PatternLanguagePackage.Literals.VARIABLE__NAME, EMFIssueCodes.MISSING_PARAMETER_TYPE, issueData);
            }
        }
    }
    

    @Check
    public void checkClassPath(PatternModel modelFile) {
        final JvmGenericType listType = (JvmGenericType) typeReferences.findDeclaredType(List.class, modelFile);
        if (listType == null || listType.getTypeParameters().isEmpty()) {
            error("Couldn't find a JDK 1.5 or higher on the project's classpath.", modelFile, PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
                EMFIssueCodes.JDK_NOT_ON_CLASSPATH);
        } else if (typeReferences.findDeclaredType(IncQueryEngine.class, modelFile) == null) {
            error("Couldn't find the mandatory library 'org.eclipse.viatra.query.runtime' on the project's classpath.",
                modelFile, PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME, EMFIssueCodes.IQR_NOT_ON_CLASSPATH);
        }
    }
}
