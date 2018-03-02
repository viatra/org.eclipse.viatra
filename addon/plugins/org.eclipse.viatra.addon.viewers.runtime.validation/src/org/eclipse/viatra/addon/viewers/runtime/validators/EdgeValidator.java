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

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;

/**
 * A validator for Edge objects
 * 
 * <p/>Note that this class uses the optional dependency org.eclipse.viatra.query.patternlanguage.emf!
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class EdgeValidator extends AbstractAnnotationValidator {

    private static final PatternAnnotationParameter LABEL_PARAMETER = new PatternAnnotationParameter("label", 
            PatternAnnotationParameter.STRING,
            "A label for the edge.",
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter SOURCE_PARAMETER = new PatternAnnotationParameter("source",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The pattern parameter representing the source item of the edge. Must refer to an EObject.", 
            /*multiple*/ false,
            /*mandatory*/ false);
    private static final PatternAnnotationParameter TARGET_PARAMETER = new PatternAnnotationParameter("target",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The pattern parameter representing the target item of the edge. Must refer to an EObject.", 
            /*multiple*/ false,
            /*mandatory*/ false);
    
    public EdgeValidator() {
        super("Edge", "Represents a custom, directed edge between GuiItems. Not supported by all viewers.", LABEL_PARAMETER, SOURCE_PARAMETER, TARGET_PARAMETER);
    }
}
