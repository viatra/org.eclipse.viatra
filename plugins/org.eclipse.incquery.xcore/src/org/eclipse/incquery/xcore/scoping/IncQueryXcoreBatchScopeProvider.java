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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.xcore.XcorePackage;
import org.eclipse.emf.ecore.xcore.scoping.XcoreBatchScopeProvider;
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;

public class IncQueryXcoreBatchScopeProvider extends XcoreBatchScopeProvider {
    
    @Inject
    protected IncQueryXcoreMapper mapper;

    @Override
    public IScope getScope(final EObject context, EReference reference) {
        if (reference == XcorePackage.Literals.XREFERENCE__OPPOSITE) {
            return new IncQueryXcoreScopeProvider.OppositeScope(qualifiedNameConverter, IScope.NULLSCOPE, false, context);
        } else if (reference == XcorePackage.Literals.XREFERENCE__KEYS) {
            return new IncQueryXcoreScopeProvider.KeyScope(mapper, qualifiedNameConverter, IScope.NULLSCOPE, false, context);
        } else {
            IScope scope = super.getScope(context, reference);
            return reference == XcorePackage.Literals.XGENERIC_TYPE__TYPE ? new IncQueryXcoreScopeProvider.TypeParameterScope(
                    mapper, qualifiedNameConverter, scope, false, context) : scope;
        }
    }
}
