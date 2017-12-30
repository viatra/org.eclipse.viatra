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
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.xtext.resource.DefaultFragmentProvider;
import org.eclipse.xtext.resource.IFragmentProvider;

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
        Optional<EObject> candidatePattern = 
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(resource.getAllContents(), Spliterator.ORDERED), false).
                filter(Pattern.class::isInstance).
                map(Pattern.class::cast).
                filter(input -> Objects.equals(input.getName(), fragment)).
                map(EObject.class::cast).
                findAny();
        
        return candidatePattern.orElse(fallback.getEObject(fragment));
    }
    
}
