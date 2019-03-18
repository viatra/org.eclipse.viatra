/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.validation;

import java.util.Optional;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;

import com.google.inject.Inject;

/**
 * A validator for observable value annotations
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ObservableValuePatternValidator extends PatternAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra.addon.databinding.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String EXPRESSION_MISMATCH_ISSUE_CODE = VALIDATOR_BASE_CODE + "expressionmismatch";

    private static final PatternAnnotationParameter NAME_PARAMETER = new PatternAnnotationParameter("name",
            PatternAnnotationParameter.STRING,
            "The name of the observable value.", 
            /* multiple */false,
            /* mandatory */false);
    private static final PatternAnnotationParameter EXPRESSION_PARAMETER = new PatternAnnotationParameter("expression",
            PatternAnnotationParameter.STRING,
            "This expression defines the attribute of a pattern parameter for which the IObservableValue will be created.  Only one of the expression and labelExpression properties must be set.", 
            /*multiple*/ false, 
            /*mandatory*/ false);
    private static final PatternAnnotationParameter LABEL_EXPRESSION_PARAMETER = new PatternAnnotationParameter("labelExpression",
            PatternAnnotationParameter.STRING,
            "A label expression definition that can contain references to match parameters inside $ symbols. Only one of the expression and labelExpression properties must be set.",
            /*multiple*/ false,
            /*mandatory*/ false);
    
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    public ObservableValuePatternValidator() {
        super("ObservableValue",
                "Defines observable values for the pattern's parameters; the code generator will create accessors for such values to use in databinding contexts.",
                NAME_PARAMETER, EXPRESSION_PARAMETER, LABEL_EXPRESSION_PARAMETER);
    }
    
    @Override
    public Optional<IPatternAnnotationAdditionalValidator> getAdditionalValidator() {
        return Optional.of(this);
    }

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        if (annotation.getParameters().isEmpty())
            return;
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference ref = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "expression");
        ValueReference labelRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "labelExpression");

        if (ref == null && labelRef == null) {
            validator.error("Specify either the parameter 'expression' or 'labelExpression'", annotation,
                    PatternLanguagePackage.Literals.ANNOTATION__PARAMETERS, EXPRESSION_MISMATCH_ISSUE_CODE);
        }
        if (ref != null && labelRef != null) {
            validator.error("Specify only one of the parameter 'expression' or 'labelExpression'", annotation,
                    PatternLanguagePackage.Literals.ANNOTATION__PARAMETERS, EXPRESSION_MISMATCH_ISSUE_CODE);
        }

        if (ref instanceof StringValue) {
            String value = ((StringValue) ref).getValue();
            if (value.contains("$")) {
                validator.warning("The expressions are not required to be escaped using $ characters.", ref,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
            }

            expressionValidator.validateModelExpression(value, pattern, ref, validator);
        }
        if (labelRef instanceof StringValue) {
            String value = ((StringValue) labelRef).getValue();
            if (!value.contains("$")) {
                validator.warning("The label expressions should contain escaped references using $ characters.", ref,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
            }

            expressionValidator.validateStringExpression(value, pattern, labelRef, validator);
        }

    }

}
