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
package org.eclipse.viatra.query.patternlanguage.util;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.LiveShadowedResourceDescriptions;
import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @since 1.6
 *
 */
@SuppressWarnings("restriction")
public class DuplicationChecker {

    @Inject
    private LiveShadowedResourceDescriptions resourceDescriptions;
    @Inject
    private IQualifiedNameProvider nameProvider;
    @Inject
    private IContainer.Manager containerManager;
    @Inject
    IProjectConfigProvider projectConfigurationProvider;

    private static Predicate<IContainer> contains(final IResourceDescription resourceDescription) {
        return new Predicate<IContainer>() {
            @Override
            public boolean apply(IContainer container) {
                return Iterables.contains(container.getResourceDescriptions(), resourceDescription);
            }
        };
    }

    public Set<IEObjectDescription> findDuplicates(Pattern pattern) {
        
        resourceDescriptions.setContext(pattern.eContainer());
        QualifiedName fullyQualifiedName = nameProvider.getFullyQualifiedName(pattern);
        
        Iterable<IEObjectDescription> shadowingPatternDescriptions = null;
        if (isStandaloneFileURI(pattern, pattern.eResource().getURI())) {
            // If pattern is not in a source folder, duplicate analysis is only meaningful inside the file
            shadowingPatternDescriptions = resourceDescriptions.getLocalDescriptions().getExportedObjects(PatternLanguagePackage.Literals.PATTERN, fullyQualifiedName, true);
            // Visibility can be ignored in case of local descriptions
            return processDuplicateCandidates(pattern, false, shadowingPatternDescriptions);
        } else {
            // Otherwise collect all visible duplicates
            shadowingPatternDescriptions = resourceDescriptions
                    .getExportedObjects(PatternLanguagePackage.Literals.PATTERN, fullyQualifiedName, true);
            return processDuplicateCandidates(pattern, true, shadowingPatternDescriptions);
        }
        
    }

    private Set<IEObjectDescription> processDuplicateCandidates(Pattern pattern, boolean calculateVisibility,
            final Iterable<IEObjectDescription> shadowingPatternDescriptions) {
        Set<IEObjectDescription> duplicates = Sets.newHashSet();
        for (IEObjectDescription shadowingPatternDescription : shadowingPatternDescriptions) {
            EObject shadowingPattern = shadowingPatternDescription.getEObjectOrProxy();
            if (Objects.equals(shadowingPattern, pattern)) {
                URI resourceUri = pattern.eResource().getURI();
                // not using shadowingPattern because it might be proxy
                URI otherResourceUri = shadowingPatternDescription.getEObjectURI().trimFragment(); 
                if (!Objects.equals(resourceUri, otherResourceUri) && isStandaloneFileURI(shadowingPattern, otherResourceUri)) {
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
                    if ((Iterables.any(visible, contains(otherResourceDescription))
                            || Iterables.any(visibleFromOther, contains(resourceDescription))) && !Objects.equals(resourceUri, otherResourceUri)) {
                        duplicates.add(shadowingPatternDescription);
                        
                    }
                } else {
                    duplicates.add(shadowingPatternDescription);
                }
            }
        }
        return duplicates;
    }
    
    /**
     * Only returns true for uris that are (a) workspace file uris, but (b) are not in source folders 
     * @param uri
     * @return
     */
    private boolean isStandaloneFileURI(EObject context, URI uri) {
        if (uri.isPlatformResource() && context.eResource() != null) {
            IProjectConfig project = projectConfigurationProvider.getProjectConfig(context.eResource().getResourceSet());
            if (project != null) {
                return project.findSourceFolderContaining(uri) == null;
            }
        }
        return true;
    }
}
