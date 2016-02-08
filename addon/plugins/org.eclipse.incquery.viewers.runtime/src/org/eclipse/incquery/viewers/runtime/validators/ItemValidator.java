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
package org.eclipse.incquery.viewers.runtime.validators;

import java.util.Arrays;
import java.util.List;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;
import org.eclipse.incquery.viewers.runtime.model.HierarchyPolicy;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * A validator for Item objects
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.incquery.patternlanguage.emf!
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
        
        ValueReference hierarchyRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "hierarchy");
        if (hierarchyRef instanceof StringValue) {
            String value = ((StringValue) hierarchyRef).getValue();
            
            
            final List<String> valueList = Lists.transform(Arrays.asList(HierarchyPolicy.values()),  new Function<HierarchyPolicy, String>() {
                
                @Override
                public String apply(HierarchyPolicy policy) {
                    return policy.name().toLowerCase();
                }
            });
                    
            if (!valueList.contains(value.toLowerCase())) {
                validator.error(
                        String.format("Invalid hierarchy literal %s. Possible values are %s.", value,
                                Iterables.toString(valueList)), hierarchyRef,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, HIERARCHY_LITERAL_ISSUE);
            }
        }
    }

}
