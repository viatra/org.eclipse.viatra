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

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @since 1.6
 *
 */
public class DuplicationChecker {

    @Inject
    private LiveShadowedResourceDescriptions resourceDescriptions;
    @Inject
    private IQualifiedNameProvider nameProvider;
    @Inject
    private IContainer.Manager containerManager;

    private static Predicate<IContainer> contains(final IResourceDescription resourceDescription) {
        return new Predicate<IContainer>() {
            @Override
            public boolean apply(IContainer container) {
                return Iterables.contains(container.getResourceDescriptions(), resourceDescription);
            }
        };
    }

    public Set<IEObjectDescription> findDuplicates(Pattern pattern) {
        Set<IEObjectDescription> duplicates = Sets.newHashSet();
        resourceDescriptions.setContext(pattern.eContainer());
        QualifiedName fullyQualifiedName = nameProvider.getFullyQualifiedName(pattern);
        final Iterable<IEObjectDescription> shadowingPatternDescriptions = resourceDescriptions
                .getExportedObjects(PatternLanguagePackage.Literals.PATTERN, fullyQualifiedName, true);
        for (IEObjectDescription shadowingPatternDescription : shadowingPatternDescriptions) {
            EObject shadowingPattern = shadowingPatternDescription.getEObjectOrProxy();
            if (shadowingPattern != pattern) {
                URI resourceUri = pattern.eResource().getURI();
                URI otherResourceUri = shadowingPatternDescription.getEObjectURI().trimFragment(); // not using
                                                                                                   // shadowingPattern
                                                                                                   // because it
                                                                                                   // might be
                                                                                                   // proxy
                IResourceDescription resourceDescription = resourceDescriptions.getResourceDescription(resourceUri);
                IResourceDescription otherResourceDescription = resourceDescriptions
                        .getResourceDescription(otherResourceUri);
                List<IContainer> visible = containerManager.getVisibleContainers(resourceDescription,
                        resourceDescriptions);
                List<IContainer> visibleFromOther = containerManager.getVisibleContainers(otherResourceDescription,
                        resourceDescriptions);
                if (Iterables.any(visible, contains(otherResourceDescription))
                        || Iterables.any(visibleFromOther, contains(resourceDescription))) {
                    if (!Objects.equal(resourceUri, otherResourceUri)) {
                        duplicates.add(shadowingPatternDescription);

                    }
                }
            }
        }
        return duplicates;
    }
}
