/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.validators;

import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationValidator;

/**
 * @since 2.0
 *
 */
public class ContainsItemValidator extends PatternAnnotationValidator {

    private static final PatternAnnotationParameter CONTAINER_PARAMETER = new PatternAnnotationParameter("container",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The pattern parameter representing the container. Must refer an EObject.", 
            /*multiple*/ false,
            /*mandatory*/ true);
    private static final PatternAnnotationParameter ITEM_PARAMETER = new PatternAnnotationParameter("item",
            PatternAnnotationParameter.VARIABLEREFERENCE,
            "The pattern parameter referring to a contained element. Must refer to an EObject.",
            /*multiple*/ false,
            /*mandatory*/ true);
    
    public ContainsItemValidator() {
        super("ContainsItem", "An annotation describing a containment relation between two items.", CONTAINER_PARAMETER, ITEM_PARAMETER);
    }

}
