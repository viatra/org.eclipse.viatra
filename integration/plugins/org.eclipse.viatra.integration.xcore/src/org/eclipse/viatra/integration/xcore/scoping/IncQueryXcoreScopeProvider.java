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
package org.eclipse.viatra.integration.xcore.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.xcore.scoping.XcoreScopeProvider;
import org.eclipse.viatra.integration.xcore.mappings.IncQueryXcoreMapper;
import org.eclipse.viatra.integration.xcore.model.XcorePackage;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This scope provider reuses all the functionality of {@link XcoreScopeProvider}, but the mapper is initialized to
 * {@link IncQueryXcoreMapper}.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@Singleton
public class IncQueryXcoreScopeProvider extends XcoreScopeProvider {

    @Inject
    private IncQueryXcoreMapper mapper;

    @Inject
    private IGlobalScopeProvider globalScopeProvider;

    public IncQueryXcoreScopeProvider() {
        super();
        super.mapper = this.mapper;
    }

    @Override
    public IScope getScope(EObject context, EReference reference) {
        if (reference == XcorePackage.Literals.XINC_QUERY_DERIVED_FEATURE__PATTERN) {
            return PatternScope.createFor(context, reference, globalScopeProvider);
        } else {
            return super.getScope(context, reference);
        }
    }
}