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
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;

import com.google.common.base.Predicate;

/**
 * Helper predicate to decide whether a pattern is annotated with one of the Viewers framework annotations (
 * {@value Item#ANNOTATION_ID}, {@value Edge#ANNOTATION_ID}, {@value Containment#ANNOTATION_ID}.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ViewersAnnotatedPatternTester implements Predicate<Annotation> {

    @Override
    public boolean apply(Annotation annotation) {
        String name = annotation.getName();
        if (name == null) {
            return false;
        }
        return (name.equals(Item.ANNOTATION_ID) || name.equals(Edge.ANNOTATION_ID)
                || name.equals(Containment.ANNOTATION_ID));// || name.equals(FormatSpecification.FORMAT_ANNOTATION));
    }
}