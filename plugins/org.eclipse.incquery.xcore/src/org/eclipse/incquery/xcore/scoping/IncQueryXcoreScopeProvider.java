/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.scoping;

import org.eclipse.emf.ecore.xcore.scoping.XcoreScopeProvider;
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper;

import com.google.inject.Inject;

/**
 * This scope provider reuses all the functionality of {@link XcoreScopeProvider}, but the mapper is initialized to
 * {@link IncQueryXcoreMapper}.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class IncQueryXcoreScopeProvider extends XcoreScopeProvider {
    @Inject
    private IncQueryXcoreMapper mapper;

    public IncQueryXcoreScopeProvider() {
        super();
        super.mapper = this.mapper;
    }

}