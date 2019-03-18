/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.validators;

import java.util.Optional;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;

import com.google.inject.Inject;

/**
 * A validator for viewer annotations
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractAnnotationValidator extends PatternAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    protected static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.query.viewers.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String EXPRESSION_MISMATCH_ISSUE_CODE = VALIDATOR_BASE_CODE + "expressionmismatch";
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    public AbstractAnnotationValidator(String name, String description,
            PatternAnnotationParameter... parameters) {
        super(name, description, parameters);
    }

    @Override
    public Optional<IPatternAnnotationAdditionalValidator> getAdditionalValidator() {
        return Optional.of(this);
    }

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference labelRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "label");
        if (labelRef instanceof StringValue) {
            String value = ((StringValue) labelRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, labelRef, validator);
        }
    }

}