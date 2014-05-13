/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.api.filters;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Defines a filter for indexing resources
 * @author Zoltan Ujhelyi
 *
 */
public interface IBaseIndexResourceFilter {

    /**
     * Decides whether a selected resource needs to be indexed
     * @param resource
     * @return true, if the selected resource is filtered
     */
    boolean isResourceFiltered(Resource resource);

}