/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/
package org.eclipse.incquery.validation.runtime.annotation;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.incquery.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.ListValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Class for validating a Constraint annotation on a pattern destined to be a constraint specification.
 * 
 * <p/>
 * Note that this class uses the optional dependency org.eclipse.incquery.patternlanguage.emf!
 * 
 * @author Abel Hegedus
 */
public class ConstraintAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.incquery.livevalidation.";
    public static final String SEVERITY_ISSUE_CODE = VALIDATOR_BASE_CODE + "severity";
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        validateMessage(annotation, validator, pattern);
        validateSeverity(annotation, validator);
        List<String> keyList = validateKeys(annotation, validator, pattern);
        validateSymmetry(annotation, validator, pattern, keyList);
    }

    private void validateSymmetry(Annotation annotation, IIssueCallback validator, Pattern pattern, List<String> keyList) {
        Collection<ValueReference> symmetricLists = CorePatternLanguageHelper.getAnnotationParameters(annotation,
                "symmetric");
        for (ValueReference symmetry : symmetricLists) {
            Iterable<String> symmetryParameters = transformStringList(symmetry);
            List<String> symmetricKeys = Lists.newArrayList();
            List<String> symmetricProperties = Lists.newArrayList();
            List<String> invalidSymmetries = Lists.newArrayList();
            for (String key : symmetryParameters) {
                Variable parameterByName = CorePatternLanguageHelper.getParameterByName(pattern, key);
                if (keyList.contains(key)) {
                    symmetricKeys.add(key);
                } else if (parameterByName != null) {
                    symmetricProperties.add(key);
                } else {
                    invalidSymmetries.add(key);
                }
            }
            if (!invalidSymmetries.isEmpty()) {
                validator.error(
                        "Symmetric parameters " + invalidSymmetries.toString() + " are not pattern parameters!",
                        symmetry, null, SEVERITY_ISSUE_CODE);
            }
            if (!symmetricKeys.isEmpty() && !symmetricProperties.isEmpty()) {
                validator.error("Symmetric parameters " + symmetryParameters.toString()
                        + " contains both key and non-key parameters!", symmetry, null, SEVERITY_ISSUE_CODE);
            }
        }
    }

    private List<String> validateKeys(Annotation annotation, IIssueCallback validator, final Pattern pattern) {
        List<String> keyList = Lists.newArrayList();
        ValueReference locationRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "location");
        ValueReference keyRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "key");
        if (locationRef != null && keyRef != null) {
            validator.error("Cannot use both location and key!", keyRef, null, SEVERITY_ISSUE_CODE);
        }
        if (locationRef instanceof VariableValue) {
            String locationVarName = ((VariableValue) locationRef).getValue().getVariable().getName();
            keyList.add(locationVarName);
        }
        if (keyRef instanceof ListValue) {
            Iterable<String> keyParamList = transformStringList(keyRef);

            List<String> invalidKeys = Lists.newArrayList();
            for (String key : keyParamList) {
                Variable parameterByName = CorePatternLanguageHelper.getParameterByName(pattern, key);
                if (parameterByName == null) {
                    invalidKeys.add(key);
                } else {
                    keyList.add(key);
                }
            }
            if (!invalidKeys.isEmpty()) {
                validator.error("Keys " + invalidKeys.toString() + " are not pattern parameters!", keyRef, null,
                        SEVERITY_ISSUE_CODE);
            }
        }
        if (keyList.isEmpty()) {
            validator.error("No key defined!", keyRef, null, SEVERITY_ISSUE_CODE);
        } else {
            boolean atLeastOneEClassKey = Iterables.any(keyList, new Predicate<String>() {
                @Override
                public boolean apply(String key) {
                    Variable firstKeyParameter = CorePatternLanguageHelper.getParameterByName(pattern, key);
                    Type sourceType = firstKeyParameter.getType();
                    if (!(sourceType instanceof ClassType)
                            || !(((ClassType) sourceType).getClassname() instanceof EClass)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            if (!atLeastOneEClassKey) {
                validator.warning("At least one key should be EClass to make location possible!", keyRef, null,
                        SEVERITY_ISSUE_CODE);
            }
        }
        return keyList;
    }

    private Iterable<String> transformStringList(ValueReference listParameter) {
        EList<ValueReference> listValues = ((ListValue) listParameter).getValues();
        Iterable<StringValue> keyStringValues = Iterables.filter(listValues, StringValue.class);
        Iterable<String> keyParamList = Iterables.transform(keyStringValues, new Function<StringValue, String>() {
            @Override
            public String apply(StringValue ref) {
                return ((StringValue) ref).getValue();
            }
        });
        return keyParamList;
    }

    private void validateSeverity(Annotation annotation, IIssueCallback validator) {
        ValueReference severityRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "severity");

        if (severityRef instanceof StringValue) {
            String value = ((StringValue) severityRef).getValue();
            if (!(value.equals("error") || value.equals("warning") || value.equals("info"))) {
                validator.error("Severity must be either 'error','warning' or 'info'.", severityRef, null,
                        SEVERITY_ISSUE_CODE);
            }
        }
    }

    private void validateMessage(Annotation annotation, IIssueCallback validator, Pattern pattern) {
        ValueReference messageRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "message");
        if (messageRef instanceof StringValue) {
            String value = ((StringValue) messageRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, messageRef, validator);
        }
    }

}
