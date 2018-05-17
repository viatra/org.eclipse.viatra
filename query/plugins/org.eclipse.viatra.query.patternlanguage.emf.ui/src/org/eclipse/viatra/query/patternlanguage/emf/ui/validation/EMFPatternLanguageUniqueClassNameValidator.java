/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.validation;

import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.util.IProjectHelper;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.xbase.ui.validation.ProjectAwareUniqueClassNameValidator;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * @since 2.0
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageUniqueClassNameValidator extends ProjectAwareUniqueClassNameValidator {

    @Inject
    private IProjectHelper projectHelper;
    
    @Override
    protected boolean checkUniqueInIndex(JvmDeclaredType type, Iterable<IEObjectDescription> descriptions) {
        URI objectURI = EcoreUtil.getURI(type);
        if (objectURI.isPlatformResource()) {
            String project = objectURI.segment(1);
            return super.checkUniqueInIndex(type, Iterables.filter(descriptions, it -> { 
                URI candidate = it.getEObjectURI();
                return candidate.isPlatformResource() && !!projectHelper.isStandaloneFileURI(type, candidate) && Objects.equals(candidate.segment(1), project);
            }));
        }
        return true;
    }
    
}
