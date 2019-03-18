/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class PatternSetValidator {

    @Inject
    private IResourceValidator validator;

    public PatternSetValidationDiagnostics validate(Resource resource) {
        PatternSetValidationDiagnostics collectedIssues = new PatternSetValidationDiagnostics();
        validator.validate(resource, CheckMode.ALL, null).stream().forEach(collectedIssues::accept);
        return collectedIssues;
    }

    /**
     * Returns the validation results of a single pattern
     * 
     * @param pattern
     * @since 2.0
     */
    public PatternSetValidationDiagnostics validate(Pattern pattern) {
        return validate(ImmutableList.of(pattern));
    }

    /**
     * Returns the validation results of a single pattern and all its (transitively )referenced patterns.
     * 
     * @param pattern
     * @since 2.0
     */
    public PatternSetValidationDiagnostics validateTransitively(Pattern pattern) {
        Set<Pattern> patternsToValidate = PatternLanguageHelper.getReferencedPatternsTransitive(pattern);
        return validate(patternsToValidate);
    }

    /**
     * Returns the validation results of a collection of patterns
     * 
     * @param patternSet
     */
    public PatternSetValidationDiagnostics validate(Collection<Pattern> patternSet) {
        PatternSetValidationDiagnostics collectedIssues = new PatternSetValidationDiagnostics();
        Set<Resource> containerResources = patternSet.stream().map(EObject::eResource).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        for (Resource resource : containerResources) {
            validator.validate(resource, CheckMode.ALL, null).stream().filter(
                    issue -> {
                        URI uri = issue.getUriToProblem();
                        return Objects.equals(resource.getURI(), uri.trimFragment())
                                && EcoreUtil.isAncestor(patternSet, resource.getEObject(uri.fragment()));
                    })
                    .forEach(collectedIssues::accept);
        }
        return collectedIssues;
    }

}
