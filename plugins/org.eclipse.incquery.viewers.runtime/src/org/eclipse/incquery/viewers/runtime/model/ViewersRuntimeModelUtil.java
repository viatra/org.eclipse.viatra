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
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.viewers.runtime.specifications.ContainmentQuerySpecificationDescriptor;
import org.eclipse.incquery.viewers.runtime.specifications.EdgeQuerySpecificationDescriptor;
import org.eclipse.incquery.viewers.runtime.specifications.ItemQuerySpecificationDescriptor;

/**
 * @author istvanrath
 *
 */
public class ViewersRuntimeModelUtil {

	public static boolean isItemQuerySpecification(IQuerySpecification<?> querySpecification) {
		return querySpecification.getFirstAnnotationByName(ItemQuerySpecificationDescriptor.ANNOTATION_ID) != null;
	}
	
	public static boolean isEdgeQuerySpecification(IQuerySpecification<?> querySpecification) {
	    return querySpecification.getFirstAnnotationByName(EdgeQuerySpecificationDescriptor.ANNOTATION_ID) != null;
	}
	
	public static boolean isContainmentQuerySpecification(IQuerySpecification<?> querySpecification) {
	    return querySpecification.getFirstAnnotationByName(ContainmentQuerySpecificationDescriptor.ANNOTATION_ID) != null;
	}
	
}
