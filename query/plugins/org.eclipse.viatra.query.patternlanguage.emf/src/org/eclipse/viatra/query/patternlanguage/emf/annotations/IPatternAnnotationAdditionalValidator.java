/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.annotations;

import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;

/**
 * Interface for providing annotation-specific validators
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public interface IPatternAnnotationAdditionalValidator {

    /**
     * Executes additional, annotation-specific validation on a pattern.
     * 
     * @param annotation
     *            the pattern to validate
     * @param validator
     *            a callback validator to report errors and warnings
     */
    void executeAdditionalValidation(Annotation annotation, IIssueCallback validator);
}
