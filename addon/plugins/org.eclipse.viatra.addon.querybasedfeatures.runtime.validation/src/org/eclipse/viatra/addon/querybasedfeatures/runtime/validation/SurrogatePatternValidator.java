/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime.validation;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * @author Abel Hegedus
 * 
 */
public class SurrogatePatternValidator extends PatternAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String FEATURE_PARAMETER_NAME = "feature";
    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.query.patternlanguage.surrogate.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String METAMODEL_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_metamodel";
    public static final String PATTERN_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_pattern";
    public static final String ANNOTATION_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_annotation";

    @Inject
    private EMFTypeInferrer typeInferrer;
    @Inject
    private EMFTypeSystem typeSystem;

   private static final PatternAnnotationParameter FEATURE_PARAMETER = new PatternAnnotationParameter(FEATURE_PARAMETER_NAME, 
           PatternAnnotationParameter.STRING,
           "The name of the EStructuralFeature that the query will serve (default: pattern name).",
           /*multiple*/ false,
           /*mandatory*/ false);
               
    public SurrogatePatternValidator() {
        super("Surrogate", "This annotation is used to mark a pattern as a surrogate query definition for a feature.", FEATURE_PARAMETER);
    }
    
    @Override
    public Optional<IPatternAnnotationAdditionalValidator> getAdditionalValidator() {
        return Optional.of(this);
    }
    
    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        boolean foundErrors = false;

        Pattern pattern = (Pattern) annotation.eContainer();

        foundErrors = checkFeatureUniquenessOnSurrogateAnnotations(annotation, validator, pattern);
        if(foundErrors) {
            return;
        }
        
        // 1. at least two parameters
        if (pattern.getParameters().size() != 2) {
            validator.error("Surrogate pattern must have exactly 2 parameters.", pattern,
                    PatternLanguagePackage.Literals.PATTERN__PARAMETERS, PATTERN_ISSUE_CODE);
            return;
        }
        // 2. first parameter is EClassifier -> Source
        Variable source = pattern.getParameters().get(0);
        IInputKey sourceTypeKey = null;
        EClass sourceClass = null;
        if (source != null) {
            sourceTypeKey = typeInferrer.getType(source);
            sourceClass = (EClass) typeSystem.inputKeyToClassifier(sourceTypeKey).filter(input -> input instanceof EClass).orElse(null);
        }
        if (sourceClass == null) {
            validator.error("The 'source' parameter must be EClass.", source,
                    PatternLanguagePackage.Literals.VARIABLE__TYPE, PATTERN_ISSUE_CODE);
            return;
        }

        // 3. pattern name or "feature" is a feature of Source
        String featureName = null;
        EObject contextForFeature = null;
        EStructuralFeature contextESFForFeature = null;
        ValueReference ref = PatternLanguageHelper.getFirstAnnotationParameter(annotation, FEATURE_PARAMETER_NAME);
        if (ref == null) {
            featureName = pattern.getName();
            contextForFeature = pattern;
            contextESFForFeature = PatternLanguagePackage.Literals.PATTERN__NAME;
        } else if (ref instanceof StringValue) {
            featureName = ((StringValue) ref).getValue();
            contextForFeature = ref;
            contextESFForFeature = PatternLanguagePackage.Literals.STRING_VALUE__VALUE;
        }
        if (featureName == null || featureName.isEmpty()) {
            validator.error("The 'feature' parameter must not be empty.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, ANNOTATION_ISSUE_CODE);
            return;
        }
        EStructuralFeature feature = null;
        for (EStructuralFeature f : sourceClass.getEStructuralFeatures()) {
            if (featureName.equals(f.getName())) {
                feature = f;
                break;
            }
        }
        if (feature == null) {
            validator.error(String.format("Cannot find feature %s of EClass %s.", featureName, sourceClass.getName()),
                    contextForFeature, contextESFForFeature, ANNOTATION_ISSUE_CODE);
            return;
        } else {
            if (feature instanceof EReference) {
                boolean featureError = false;
                if(!feature.isDerived()) {
                    validator.error(String.format("Feature %s is not derived.",featureName),
                            contextForFeature, contextESFForFeature, METAMODEL_ISSUE_CODE);
                    featureError = true;
                }
                if(!feature.isTransient()) {
                    validator.error(String.format("Feature %s is not transient.",featureName),
                            contextForFeature, contextESFForFeature, METAMODEL_ISSUE_CODE);
                    featureError = true;
                }
                if(!feature.isVolatile()) {
                    validator.error(String.format("Feature %s is not volatile.",featureName),
                            contextForFeature, contextESFForFeature, METAMODEL_ISSUE_CODE);
                    featureError = true;
                }
                if(featureError) {
                    return;
                }
            }
        }
        EClassifier classifier = feature.getEGenericType().getEClassifier();
        if (classifier == null) {
            validator.error(String.format("Feature %s has no type information set in the metamodel", featureName),
                    contextForFeature, contextESFForFeature, METAMODEL_ISSUE_CODE);
            return;
        }
        
        
        
        // 4. second parameter is compatible(?) with feature type -> Target
        Variable target = pattern.getParameters().get(1);
        final IInputKey parameterType = typeInferrer.getType(target);
        
        if (!typeSystem.isConformant(typeSystem.classifierToInputKey(classifier), parameterType)) {
            validator.warning(String.format("The 'target' parameter type %s is not equal to actual feature type %s.",
                    featureName, sourceClass.getName()), target, PatternLanguagePackage.Literals.VARIABLE__TYPE,
                    PATTERN_ISSUE_CODE);
        }
        
    }

    private boolean checkFeatureUniquenessOnSurrogateAnnotations(Annotation annotation, IIssueCallback validator, Pattern pattern) {
        Collection<Annotation> qbfAnnotations = PatternLanguageHelper.getAnnotationsByName(pattern, "Surrogate");
        if(qbfAnnotations.size() > 1) {
            ValueReference feature = PatternLanguageHelper.getFirstAnnotationParameter(annotation, FEATURE_PARAMETER_NAME);
            if(feature == null) {
                validator.error("Feature must be specified when multiple Surrogate annotations are used on a single pattern.", annotation,
                        PatternLanguagePackage.Literals.ANNOTATION__NAME, ANNOTATION_ISSUE_CODE);
                return true;
            } else {
                String featureName = ((StringValue) feature).getValue();
                for (Annotation antn : qbfAnnotations) {
                    ValueReference otherFeature = PatternLanguageHelper.getFirstAnnotationParameter(antn, FEATURE_PARAMETER_NAME);
                    if(otherFeature != null) {
                        String otherFeatureName = ((StringValue) otherFeature).getValue();
                        if(featureName.equals(otherFeatureName)) {
                            validator.error("Feature must be unique among multiple Surrogate annotations used on a single pattern.", annotation,
                                    PatternLanguagePackage.Literals.ANNOTATION__NAME, ANNOTATION_ISSUE_CODE);
                            return true;
                        }
                    }
                }
            }
            
        }
        return false;
    }
    

}
