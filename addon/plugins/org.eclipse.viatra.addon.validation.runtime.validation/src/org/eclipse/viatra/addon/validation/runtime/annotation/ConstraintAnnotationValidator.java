/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.runtime.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;
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
public class ConstraintAnnotationValidator extends PatternAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.query.livevalidation.";
    public static final String SEVERITY_ISSUE_CODE = VALIDATOR_BASE_CODE + "severity";
    public static final String INVALID_SYMMETRIC_PARAMETERS = VALIDATOR_BASE_CODE + "symmetric";
    public static final String INVALID_KEY_PARAMETERS = VALIDATOR_BASE_CODE + "key";
    
    private static final PatternAnnotationParameter KEY_PARAMETER = new PatternAnnotationParameter("key", 
            PatternAnnotationParameter.LIST,
            "The keys of a constraint represents the pattern parameter objects the constraint violation needs to be attached to. Keys are defined as a list of parameter names (e.g. keys = {param1, param2}",
            /*multiple*/ false,
            /*mandatory*/ true);
    private static final PatternAnnotationParameter MESSAGE_PARAMETER = new PatternAnnotationParameter("message",
            PatternAnnotationParameter.STRING,
            "The message to display when the constraint violation is found. The message may refer the parameter variables between $ symbols, or their EMF features, such as in $Param1.name$.",
            /*multiple*/ false, 
            /*mandatory*/ true);
    private static final PatternAnnotationParameter SEVERITY_PARAMETER = new PatternAnnotationParameter("severity", 
            PatternAnnotationParameter.STRING,
            "Possible values: &quot;error&quot;, &quot;warning&quot; and &quot;info&quot;.",
            /*multiple*/ false,
            /*mandatory*/ true);
    private static final PatternAnnotationParameter EDITOR_PARAMETER = new PatternAnnotationParameter("targetEditorId",
            PatternAnnotationParameter.STRING,
            "An Eclipse editor ID where the validation framework should register itself to the context menu. Use &quot;*&quot; as a wildcard if the constraint should be used always when validation is started.",
            /*multiple*/ true,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter SYMMETRIC_PARAMETER = new PatternAnnotationParameter("symmetric", 
            PatternAnnotationParameter.LIST,
            "Provide parameter a list, where permutations of the same values register as one match violation. Symmetries are defined as a list of parameter names (e.g. symmetric = {param1, param2}",
            /*multiple*/ true, 
            /*mandatory*/ false);
    
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    @Inject
    private ITypeInferrer typeInferrer;
    
    
    public ConstraintAnnotationValidator() {
        super("Constraint",
                "This annotation is used to mark a pattern for use in the VIATRA Query validation framework.",
                KEY_PARAMETER, MESSAGE_PARAMETER, SEVERITY_PARAMETER, EDITOR_PARAMETER, SYMMETRIC_PARAMETER);
    }
    
    @Override
    public Optional<IPatternAnnotationAdditionalValidator> getAdditionalValidator() {
        return Optional.of(this);
    }
    
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
                SYMMETRIC_PARAMETER.getName());
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
        ValueReference keyRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, KEY_PARAMETER.getName());
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
        return Iterables.filter(listValues, VariableReference.class);
    }

    private void validateSeverity(Annotation annotation, IIssueCallback validator) {
        ValueReference severityRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, SEVERITY_PARAMETER.getName());

        if (severityRef instanceof StringValue) {
            String value = ((StringValue) severityRef).getValue();
            if (!(value.equals("error") || value.equals("warning") || value.equals("info"))) {
                validator.error("Severity must be either 'error','warning' or 'info'.", severityRef, null,
                        SEVERITY_ISSUE_CODE);
            }
        }
    }

    private void validateMessage(Annotation annotation, IIssueCallback validator, Pattern pattern) {
        ValueReference messageRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, MESSAGE_PARAMETER.getName());
        if (messageRef instanceof StringValue) {
            String value = ((StringValue) messageRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, messageRef, validator);
        }
    }

}
