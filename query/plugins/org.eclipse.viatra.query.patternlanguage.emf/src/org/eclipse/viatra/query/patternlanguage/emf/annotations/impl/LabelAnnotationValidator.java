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
public class LabelAnnotationValidator extends PatternAnnotationValidator {

    private static final String ANNOTATION_NAME = "Label";
    private static final String ANNOTATION_DESCRIPTION = "The annotation can be used to define the way to present the pattern and its match set in the Query Explorer.";
    
    private static final PatternAnnotationParameter LABEL_PARAMETER = new PatternAnnotationParameter("label",
            PatternAnnotationParameter.STRING,
            "This string defines how to represent matches of this pattern for end-users. The string may refer the parameter variables between $ symbols, or their EMF features, such as in $Param1.name$.",
            /* multiple */false,
            /* mandatory */true); 
    
    public LabelAnnotationValidator() {
        super(ANNOTATION_NAME, ANNOTATION_DESCRIPTION, LABEL_PARAMETER);
    }

}
