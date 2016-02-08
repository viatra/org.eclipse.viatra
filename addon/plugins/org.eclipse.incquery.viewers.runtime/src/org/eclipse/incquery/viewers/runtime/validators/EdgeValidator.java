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

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;


/**
 * A validator for Item objects
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.incquery.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class EdgeValidator extends AbstractAnnotationValidator {

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        // Label validation is handled in parent class
        super.executeAdditionalValidation(annotation, validator);
    }

}
