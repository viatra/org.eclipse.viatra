/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime.util.validation;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.validation.IIssueCallback;

import com.google.inject.Inject;

/**
 * @author Abel Hegedus
 * 
 */
public class SurrogatePatternValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.query.patternlanguage.surrogate.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String METAMODEL_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_metamodel";
    public static final String PATTERN_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_pattern";
    public static final String ANNOTATION_ISSUE_CODE = VALIDATOR_BASE_CODE + "faulty_annotation";

    @Inject
    private IEMFTypeProvider typeProvider;

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
        EClassifier sourceClassifier = null;
        if (source != null) {
            sourceClassifier = typeProvider.getClassifierForVariable(source);
        }
        if (sourceClassifier == null || !(sourceClassifier instanceof EClass)) {
            validator.error("The 'source' parameter must be EClass.", source,
                    PatternLanguagePackage.Literals.VARIABLE__TYPE, PATTERN_ISSUE_CODE);
            return;
        }
        EClass sourceClass = (EClass) sourceClassifier;

        // 3. pattern name or "feature" is a feature of Source
        String featureName = null;
        EObject contextForFeature = null;
        EStructuralFeature contextESFForFeature = null;
        ValueReference ref = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "feature");
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
        EClassifier targetClassifier = typeProvider.getClassifierForVariable(target);
        if (targetClassifier == null) {
            validator.warning("Cannot find target EClassifier", target, PatternLanguagePackage.Literals.VARIABLE__TYPE,
                    PATTERN_ISSUE_CODE);
        }
        
        if (!classifier.equals(targetClassifier)) {
            validator.warning(String.format("The 'target' parameter type %s is not equal to actual feature type %s.",
                    featureName, sourceClass.getName()), target, PatternLanguagePackage.Literals.VARIABLE__TYPE,
                    PATTERN_ISSUE_CODE);
        }
        
    }

    private boolean checkFeatureUniquenessOnSurrogateAnnotations(Annotation annotation, IIssueCallback validator, Pattern pattern) {
        Collection<Annotation> qbfAnnotations = CorePatternLanguageHelper.getAnnotationsByName(pattern, "Surrogate");
        if(qbfAnnotations.size() > 1) {
            ValueReference feature = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "feature");
            if(feature == null) {
                validator.error("Feature must be specified when multiple Surrogate annotations are used on a single pattern.", annotation,
                        PatternLanguagePackage.Literals.ANNOTATION__NAME, ANNOTATION_ISSUE_CODE);
                return true;
            } else {
                String featureName = ((StringValue) feature).getValue();
                for (Annotation antn : qbfAnnotations) {
                    ValueReference otherFeature = CorePatternLanguageHelper.getFirstAnnotationParameter(antn, "feature");
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
