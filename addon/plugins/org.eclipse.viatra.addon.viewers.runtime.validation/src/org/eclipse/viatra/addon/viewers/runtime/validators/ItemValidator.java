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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.viatra.addon.viewers.runtime.notation.HierarchyPolicy;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;


/**
 * A validator for Item objects
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ItemValidator extends AbstractAnnotationValidator {

    public static final String HIERARCHY_LITERAL_ISSUE = VALIDATOR_BASE_CODE + "hierarchyliteral";

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        // Label validation is handled in parent class
        super.executeAdditionalValidation(annotation, validator);
        
        ValueReference hierarchyRef = PatternLanguageHelper.getFirstAnnotationParameter(annotation, "hierarchy");
        if (hierarchyRef instanceof StringValue) {
            String value = ((StringValue) hierarchyRef).getValue();
            
            final List<String> valueList = Arrays.stream(HierarchyPolicy.values())
                    .map(policy -> policy.name().toLowerCase()).collect(Collectors.toList());
                    
            if (!valueList.contains(value.toLowerCase())) {
                validator.error(
                        String.format("Invalid hierarchy literal %s. Possible values are %s.", value,
                                valueList.stream().collect(Collectors.joining(", "))), hierarchyRef,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, HIERARCHY_LITERAL_ISSUE);
            }
        }
    }

}
