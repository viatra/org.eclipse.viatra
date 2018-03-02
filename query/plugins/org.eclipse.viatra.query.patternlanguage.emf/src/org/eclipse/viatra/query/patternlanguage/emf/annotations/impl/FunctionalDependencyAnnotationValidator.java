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
package org.eclipse.viatra.query.patternlanguage.emf.annotations.impl;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;

/**
 * Annotation information for the <pre>FunctionalDependency</pre> annotation for VQL.
 * @since 2.0
 *
 */
public class FunctionalDependencyAnnotationValidator extends PatternAnnotationValidator {

    private static final String ANNOTATION_NAME = "FunctionalDependency";
    private static final String ANNOTATION_DESCRIPTION = "This annotation is used to record domain-specific knowledge about a functional dependency among matches of this pattern. For a given value combination of the 'forEach' parameters, at most one value of each 'unique' parameter may occur in the match set.";
    
    private static final PatternAnnotationParameter FOREACH_PARAMETER = new PatternAnnotationParameter("forEach",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The name of a query parameter on the left-hand side of the dependency.",
            /* multiple */true,
            /* mandatory */false); 
    private static final PatternAnnotationParameter UNIQUE_PARAMETER = new PatternAnnotationParameter("unique",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The name of a query parameter on the right-hand side of the dependency.",
            /* multiple */true,
            /* mandatory */false);
    
    public FunctionalDependencyAnnotationValidator() {
        super(ANNOTATION_NAME, ANNOTATION_DESCRIPTION, FOREACH_PARAMETER, UNIQUE_PARAMETER);
    }

}
