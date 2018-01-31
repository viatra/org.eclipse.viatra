/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.xtext.validation.Issue;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class ValidationHelper {

    private ValidationHelper() {/* Utility class constructor */}

    public static PProblem toPProblem(Issue issue) {
        return new PProblem(issue.getMessage(), issue.getLineNumber(), issue.getColumn());
    }
}
