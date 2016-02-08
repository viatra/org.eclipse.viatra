/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.validators;

import org.eclipse.viatra.query.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.validation.IIssueCallback;

import com.google.inject.Inject;

/**
 * A validator for viewer annotations
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    protected static final String VALIDATOR_BASE_CODE = "org.eclipse.incquery.viewers.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String EXPRESSION_MISMATCH_ISSUE_CODE = VALIDATOR_BASE_CODE + "expressionmismatch";
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    public AbstractAnnotationValidator() {
        super();
    }

    

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference labelRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "label");
        if (labelRef instanceof StringValue) {
            String value = ((StringValue) labelRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, labelRef, validator);
        }
    }

}