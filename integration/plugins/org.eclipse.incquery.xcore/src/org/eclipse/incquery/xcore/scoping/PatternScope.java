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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractScope;

import com.google.common.base.Predicate;

public class PatternScope extends AbstractScope {

    private EObject context;
    private EReference reference;
    private IGlobalScopeProvider scopeProvider;

    private PatternScope(EObject context, EReference reference, IGlobalScopeProvider scopeProvider) {
        super(IScope.NULLSCOPE, true);
        this.context = context;
        this.reference = reference;
        this.scopeProvider = scopeProvider;
    }

    public static PatternScope createFor(EObject context, EReference reference, IGlobalScopeProvider scopeProvider) {
        return new PatternScope(context, reference, scopeProvider);
    }

    @Override
    protected Iterable<IEObjectDescription> getAllLocalElements() {
        IScope scope = scopeProvider.getScope(context.eResource(), reference, new Predicate<IEObjectDescription>() {

            @Override
            public boolean apply(IEObjectDescription input) {
                return true;
            }
        });

        List<IEObjectDescription> result = new ArrayList<IEObjectDescription>();
        
        for (IEObjectDescription description : scope.getAllElements()) {
            result.add(new AliasedEObjectDescription(QualifiedName.create(description.getName().getLastSegment()), description));
        }

        return result;
    }
}
