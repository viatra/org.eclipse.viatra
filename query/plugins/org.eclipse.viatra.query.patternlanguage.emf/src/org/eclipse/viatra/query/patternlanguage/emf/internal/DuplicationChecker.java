/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.util.IProjectHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.LiveShadowedResourceDescriptions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @since 2.0
 *
 */
public class DuplicationChecker {

    @Inject
    private LiveShadowedResourceDescriptions resourceDescriptions;
    @Inject
    private IQualifiedNameProvider nameProvider;
    @Inject
    private IContainer.Manager containerManager;
    @Inject
    private IQualifiedNameConverter nameConverter;
    @Inject
    private IProjectHelper projectHelper; 

    private static Predicate<IContainer> contains(final IResourceDescription resourceDescription) {
        return container -> Iterables.contains(container.getResourceDescriptions(), resourceDescription);
    }

    public Set<IEObjectDescription> findDuplicates(Pattern pattern) {
        QualifiedName fullyQualifiedName = nameProvider.getFullyQualifiedName(pattern);
        return findShadowingClasses(pattern, fullyQualifiedName, PatternLanguagePackage.Literals.PATTERN);
    }
    
    /**
     * @since 1.7
     */
    public Set<IEObjectDescription> findShadowingClasses(Pattern pattern, String fullyQualifiedName, EClass sourceType) {
        return findShadowingClasses(pattern, nameConverter.toQualifiedName(fullyQualifiedName), sourceType);
    }
    /**
     * @since 1.7
     */
    public Set<IEObjectDescription> findShadowingClasses(Pattern pattern, QualifiedName fullyQualifiedName, EClass sourceType) {
        resourceDescriptions.setContext(pattern.eContainer());
        
        Iterable<IEObjectDescription> shadowingPatternDescriptions = null;
        final URI uri = pattern.eResource().getURI();
        if (projectHelper.isStandaloneFileURI(pattern, uri)) {
            // If pattern is not in a source folder, duplicate analysis is only meaningful inside the file
            final IResourceDescription resourceDescription = resourceDescriptions.getLocalDescriptions().getResourceDescription(uri);
            shadowingPatternDescriptions = resourceDescription == null ? new HashSet<>(): 
                    resourceDescription.getExportedObjects(sourceType, fullyQualifiedName, true);
            // Visibility can be ignored in case of local descriptions
            return processDuplicateCandidates(pattern, false, shadowingPatternDescriptions);
        } else {
            // Otherwise collect all visible duplicates
            shadowingPatternDescriptions = resourceDescriptions
                    .getExportedObjects(sourceType, fullyQualifiedName, true);
            return processDuplicateCandidates(pattern, true, shadowingPatternDescriptions);
        }
    }
    
    private Set<IEObjectDescription> processDuplicateCandidates(Pattern pattern, boolean calculateVisibility,
            final Iterable<IEObjectDescription> shadowingPatternDescriptions) {
        Set<IEObjectDescription> duplicates = Sets.newHashSet();
        for (IEObjectDescription shadowingPatternDescription : shadowingPatternDescriptions) {
            EObject shadowingPattern = shadowingPatternDescription.getEObjectOrProxy();
            if (!Objects.equals(shadowingPattern, pattern)) {
                URI resourceUri = pattern.eResource().getURI();
                // not using shadowingPattern because it might be proxy
                URI otherResourceUri = shadowingPatternDescription.getEObjectURI().trimFragment(); 
                if (!Objects.equals(resourceUri, otherResourceUri) && projectHelper.isStandaloneFileURI(shadowingPattern, otherResourceUri)) {
                    // If shadowing pattern is not in another source file in a source folder, it does not matter
                    continue;
                }
                
                if (calculateVisibility) {
                    IResourceDescription resourceDescription = resourceDescriptions.getResourceDescription(resourceUri);
                    IResourceDescription otherResourceDescription = resourceDescriptions
                            .getResourceDescription(otherResourceUri);
                    List<IContainer> visible = containerManager.getVisibleContainers(resourceDescription,
                            resourceDescriptions);
                    List<IContainer> visibleFromOther = containerManager.getVisibleContainers(otherResourceDescription,
                            resourceDescriptions);
                    if (Iterables.any(visible, contains(otherResourceDescription))
                            || Iterables.any(visibleFromOther, contains(resourceDescription))) {
                        duplicates.add(shadowingPatternDescription);
                        
                    }
                } else {
                    duplicates.add(shadowingPatternDescription);
                }
            }
        }
        return duplicates;
    }
}
