/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.base.api.filters;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * 
 * Defines if an {@link EStructuralFeature} should not be indexed by VIATRA Base. This filtering 
 * method should only be used if the input metamodel has certain features, that the base indexer 
 * cannot handle. If the filtered feature is a containment feature, the whole sub-tree accessible 
 * through the said feature will be filtered.
 *
 * Note: This API feature is for advanced users only. Usage of this feature is not encouraged, 
 * unless the filtering task is impossible via using the more straightforward 
 * {@link IBaseIndexResourceFilter} or {@link IBaseIndexObjectFilter}.   
 * 
 * @author Peter Lunk
 * 
 */
public interface IBaseIndexFeatureFilter {

    /**
     * Decides whether the selected {@link EStructuralFeature} is filtered.
     * 
     * @param feature
     * @return true, if the feature should not be indexed
     */
    boolean isFiltered(EStructuralFeature feature);

}