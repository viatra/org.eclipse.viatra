/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.annotations.impl;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;

/**
 * Support for the deprecated Query Explorer annotation.
 * 
 * @since 2.0
 *
 */
public class QueryExplorerAnnotationValidator extends PatternAnnotationValidator {

    public static final String ANNOTATION_ID = "QueryExplorer";
    
    private static final PatternAnnotationParameter CHECKED_PARAMETER = new PatternAnnotationParameter("checked",
            PatternAnnotationParameter.BOOLEAN,
            "Possible values: 'true' to be checked by default (triggering result set display) and 'false' to not.", 
            /*multiple*/ false,
            /*mandatory*/ false,
            /*deprecated*/ true);
    private static final PatternAnnotationParameter MESSAGE_PARAMETER = new PatternAnnotationParameter("message",
            PatternAnnotationParameter.STRING,
            "This message will appear for each match of the pattern. The message may refer the parameter variables between $ symbols, or their EMF features, such as in $Param1.name$.&quot;",
            /*multiple*/ false,
            /*mandatory*/ false,
            /*deprecated*/ true);
    
    public QueryExplorerAnnotationValidator() {
        super(ANNOTATION_ID,
                "This annotation was used by the now deprecated Query Explorer. Use the @Label annotation instead.",
                true, Arrays.asList(CHECKED_PARAMETER, MESSAGE_PARAMETER), Optional.empty());
    }

}
