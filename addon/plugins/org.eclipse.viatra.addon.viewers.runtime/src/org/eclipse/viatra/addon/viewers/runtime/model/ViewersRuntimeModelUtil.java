/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
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
