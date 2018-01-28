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
package org.eclipse.viatra.addon.validation.runtime.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableValue;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Class for validating a Constraint annotation on a pattern destined to be a constraint specification.
 * 
 * <p/>
 * Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Abel Hegedus
 */
public class ConstraintAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.query.livevalidation.";
    public static final String SEVERITY_ISSUE_CODE = VALIDATOR_BASE_CODE + "severity";
    public static final String INVALID_SYMMETRIC_PARAMETERS = VALIDATOR_BASE_CODE + "symmetric";
    public static final String INVALID_KEY_PARAMETERS = VALIDATOR_BASE_CODE + "key";
    
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    @Inject
    private ITypeInferrer typeInferrer;
    
    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        validateMessage(annotation, validator, pattern);
        validateSeverity(annotation, validator);
        List<Variable> keyList = validateKeys(annotation, validator, pattern);
        validateSymmetry(annotation, validator, pattern, keyList);
    }

    private void validateSymmetry(Annotation annotation, IIssueCallback validator, Pattern pattern, List<Variable> keyList) {
        Collection<ValueReference> symmetricLists = PatternLanguageHelper.getAnnotationParameters(annotation,
                "symmetric");
        for (ValueReference symmetry : symmetricLists) {
            List<Variable> symmetryList = Lists.newArrayList();
            if (symmetry instanceof ListValue) {
                symmetryList = computeVariableListFromListValue(validator, pattern, symmetry, INVALID_SYMMETRIC_PARAMETERS);
            }
            List<String> symmetricParameters = Lists.newArrayList();
            List<String> symmetricKeys = Lists.newArrayList();
            List<String> symmetricProperties = Lists.newArrayList();
            for (Variable symmetricVariable : symmetryList) {
                String variableName = symmetricVariable.getName();
                symmetricParameters.add(variableName);
                if (keyList.contains(symmetricVariable)) {
                    symmetricKeys.add(variableName);
                } else {
                    symmetricProperties.add(variableName);
                }
            }
            if (!symmetricKeys.isEmpty() && !symmetricProperties.isEmpty()) {
                validator.error("Symmetric parameters " + symmetricParameters.toString()
                        + " contains both key and non-key parameters!", symmetry, null, INVALID_SYMMETRIC_PARAMETERS);
            }
            if(symmetricParameters.size() < 2){
                validator.error("Symmetric parameters must have at least two values!", symmetry, null, INVALID_SYMMETRIC_PARAMETERS);
            }
        }
    }

    private List<Variable> validateKeys(Annotation annotation, IIssueCallback validator, final Pattern pattern) {
        List<Variable> keyList = Lists.newArrayList();
        ValueReference keyRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "key");
        if (keyRef instanceof ListValue) {
            keyList = computeVariableListFromListValue(validator, pattern, keyRef, INVALID_KEY_PARAMETERS);
        }
        if (keyList.isEmpty()) {
            validator.error("No key defined!", keyRef, null, INVALID_KEY_PARAMETERS);
        } else {
            boolean atLeastOneEClassKey = Iterables.any(keyList, key -> (typeInferrer.getType(key) instanceof EClassTransitiveInstancesKey));
            if (!atLeastOneEClassKey) {
                validator.warning("At least one key should be EClass to make location possible!", keyRef, null,
                        INVALID_KEY_PARAMETERS);
            }
        }
        return keyList;
    }

    private List<Variable> computeVariableListFromListValue(IIssueCallback validator, final Pattern pattern, ValueReference listValue, String issueCode) {
        List<Variable> variables = Lists.newArrayList();
        Iterable<VariableReference> variableReferenceList = transformVariableReferenceList(listValue);
        Iterable<StringValue> stringValueList = transformStringList(listValue);
        
        if(!Iterables.isEmpty(variableReferenceList) && !Iterables.isEmpty(stringValueList)){
            validator.error("Must not mix string and variable values!", listValue, null, issueCode);
        }
        
        for (StringValue key : stringValueList) {
            Optional<Variable> parameterByName = PatternLanguageHelper.getParameterByName(pattern, key.getValue());
            if (parameterByName.isPresent()) {
                variables.add(parameterByName.get());
                validator.warning("Deprecated: remove quotes to use variable reference instead!", key, null, issueCode);
            } else {
                validator.error(key.getValue() + " is not a pattern parameter!", key, null, issueCode);
            }
        }
        for (VariableReference key : variableReferenceList) {
            if(key.getVariable() != null){
                variables.add(key.getVariable());
            }
        }
        return variables;
    }

    private Iterable<StringValue> transformStringList(ValueReference listParameter) {
        EList<ValueReference> listValues = ((ListValue) listParameter).getValues();
        Iterable<StringValue> keyStringValues = Iterables.filter(listValues, StringValue.class);
        return keyStringValues;
    }

    private Iterable<VariableReference> transformVariableReferenceList(ValueReference listParameter) {
        EList<ValueReference> listValues = ((ListValue) listParameter).getValues();
        Iterable<VariableValue> keyStringValues = Iterables.filter(listValues, VariableValue.class);
        Iterable<VariableReference> keyParamList = Iterables.transform(keyStringValues, VariableValue::getValue);
        return keyParamList;
    }

    private void validateSeverity(Annotation annotation, IIssueCallback validator) {
        ValueReference severityRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "severity");

        if (severityRef instanceof StringValue) {
            String value = ((StringValue) severityRef).getValue();
            if (!(value.equals("error") || value.equals("warning") || value.equals("info"))) {
                validator.error("Severity must be either 'error','warning' or 'info'.", severityRef, null,
                        SEVERITY_ISSUE_CODE);
            }
        }
    }

    private void validateMessage(Annotation annotation, IIssueCallback validator, Pattern pattern) {
        ValueReference messageRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "message");
        if (messageRef instanceof StringValue) {
            String value = ((StringValue) messageRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, messageRef, validator);
        }
    }

}
