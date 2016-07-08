/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.internal;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.xtext.resource.DefaultFragmentProvider;
import org.eclipse.xtext.resource.IFragmentProvider;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class PatternLanguageFragmentProvider extends DefaultFragmentProvider {

    @Override
    public String getFragment(EObject obj, IFragmentProvider.Fallback fallback) {
        if (obj instanceof Pattern) {
            return ((Pattern) obj).getName();
        }
        return fallback.getFragment(obj);
    }

    @Override
    public EObject getEObject(Resource resource, final String fragment, IFragmentProvider.Fallback fallback) {
        Optional<EObject> candidatePattern = Iterators.tryFind(resource.getAllContents(), new Predicate<EObject>() {

            @Override
            public boolean apply(EObject input) {
                return (input instanceof Pattern) && Objects.equals(((Pattern)input).getName(), fragment);
            }
        });
        
        return candidatePattern.or(fallback.getEObject(fragment));
    }
    
}
