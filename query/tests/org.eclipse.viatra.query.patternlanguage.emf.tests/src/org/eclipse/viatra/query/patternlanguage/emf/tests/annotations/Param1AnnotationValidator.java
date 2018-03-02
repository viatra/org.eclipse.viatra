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
package org.eclipse.viatra.query.patternlanguage.emf.tests.annotations;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;

public class Param1AnnotationValidator extends PatternAnnotationValidator {
    
    private final static PatternAnnotationParameter P1_PARAM = new PatternAnnotationParameter("p1",
            PatternAnnotationParameter.STRING,
            null,
            /*multiple*/ false,
            /*mandatory*/ false);
    
    public Param1AnnotationValidator() {
        super("Param1", "", P1_PARAM);
    }

}
