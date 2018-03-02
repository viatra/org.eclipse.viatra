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
package org.eclipse.viatra.addon.viewers.runtime.validators;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;

/**
 * @since 2.0
 *
 */
public class FormatValidator extends PatternAnnotationValidator {

    private static final PatternAnnotationParameter COLOR_PARAMETER = new PatternAnnotationParameter("color",
            PatternAnnotationParameter.STRING,
            "The main color definition in CSS format (#RRGGBB)", 
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter LINE_COLOR_PARAMETER = new PatternAnnotationParameter("lineColor",
            PatternAnnotationParameter.STRING,
            "The line color setting in CSS format (#RRGGBB).", 
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter TEXT_COLOR_PARAMETER = new PatternAnnotationParameter("textColor",
            PatternAnnotationParameter.STRING,
            "The text color setting in CSS format (#RRGGBB).",
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter LINE_WIDTH_PARAMETER = new PatternAnnotationParameter("lineWidth",
            PatternAnnotationParameter.INT,
            "The width of the line in pixels. Only available for Edges.",
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter LINE_STYLE_PARAMETER = new PatternAnnotationParameter("lineStyle",
            PatternAnnotationParameter.STRING,
            "The style of the line (solid, dashed, dotted, dashdot).",
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter ARROW_SOURCE_PARAMETER = new PatternAnnotationParameter("arrowSourceEnd",
            PatternAnnotationParameter.STRING,
            "The style of the line's arrow at the target end (none, standard, diamond, triangle). Only available for Edges.",
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter ARROW_TARGET_PARAMETER = new PatternAnnotationParameter("arrowTargetEnd",
            PatternAnnotationParameter.STRING,
            "The style of the line's arrow at the target end (none, standard, diamond, triangle). Only available for Edges.",
            /*multiple*/ false,
            /*mandatory*/ false);
    
    public FormatValidator() {
        super("Format", "Formatting specification for Items and Edges", COLOR_PARAMETER, LINE_COLOR_PARAMETER,
                TEXT_COLOR_PARAMETER, LINE_WIDTH_PARAMETER, LINE_STYLE_PARAMETER, ARROW_SOURCE_PARAMETER,
                ARROW_TARGET_PARAMETER);
    }

}
