/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Csaba Debreceni - minor modifications
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.function.Predicate;

import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ContainmentQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.EdgeQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ItemQuerySpecificationDescriptor;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;

/**
 * Helper predicate to decide whether a pattern is annotated with one of the Viewers framework annotations (
 * {@value Item#ANNOTATION_ID}, {@value Edge#ANNOTATION_ID}, {@value Containment#ANNOTATION_ID}.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ViewersAnnotatedPatternTester implements Predicate<PAnnotation> {

    @Override
    public boolean test(PAnnotation annotation) {
        String name = annotation.getName();
        if (name == null) {
            return false;
        }
        return (name.equals(ItemQuerySpecificationDescriptor.ANNOTATION_ID) || name.equals(EdgeQuerySpecificationDescriptor.ANNOTATION_ID)
                || name.equals(ContainmentQuerySpecificationDescriptor.ANNOTATION_ID));// || name.equals(FormatSpecification.FORMAT_ANNOTATION));
    }
}