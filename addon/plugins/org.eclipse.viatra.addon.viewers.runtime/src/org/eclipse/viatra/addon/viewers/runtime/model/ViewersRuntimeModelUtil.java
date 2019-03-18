/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import org.eclipse.viatra.addon.viewers.runtime.specifications.ContainmentQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.EdgeQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ItemQuerySpecificationDescriptor;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

/**
 * @author istvanrath
 *
 */
public class ViewersRuntimeModelUtil {

    private ViewersRuntimeModelUtil() {}
    
    public static boolean isItemQuerySpecification(IQuerySpecification<?> querySpecification) {
        return querySpecification.getFirstAnnotationByName(ItemQuerySpecificationDescriptor.ANNOTATION_ID).isPresent();
    }
    
    public static boolean isEdgeQuerySpecification(IQuerySpecification<?> querySpecification) {
        return querySpecification.getFirstAnnotationByName(EdgeQuerySpecificationDescriptor.ANNOTATION_ID).isPresent();
    }
    
    public static boolean isContainmentQuerySpecification(IQuerySpecification<?> querySpecification) {
        return querySpecification.getFirstAnnotationByName(ContainmentQuerySpecificationDescriptor.ANNOTATION_ID).isPresent();
    }
    
}
