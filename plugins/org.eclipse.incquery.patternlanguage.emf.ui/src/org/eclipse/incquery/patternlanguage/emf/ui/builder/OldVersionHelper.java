/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.ui.builder;

import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class OldVersionHelper {

    Map<URI, Resource> resourceMap = Maps.newHashMap();
    Map<IPath, URI> copiedURIMap = Maps.newHashMap();
    Map<IProject, ResourceSet> resourceSetMap = Maps.newHashMap();

    /**
     * Predicate to decide whether a classpath entry is the corresponding source folder for a path
     *
     */
    public final class SourceFolderFinder implements Predicate<IClasspathEntry> {
        private final IPath relativePath;

        private SourceFolderFinder(IPath relativePath) {
            this.relativePath = relativePath;
        }

        @Override
        public boolean apply(IClasspathEntry entry) {
            boolean isSourceEntry = entry.getContentKind() == IPackageFragmentRoot.K_SOURCE;
            if (isSourceEntry) {
                return entry.getPath().isPrefixOf(relativePath);
            } else {
                return false;
            }
        }
    }

    @Inject
    private IWorkspaceRoot workspaceRoot;
    @Inject
    private IResourceSetProvider provider;

    private ResourceSet getResourceSet(IProject project) {
        if (!resourceSetMap.containsKey(project)) {
            ResourceSet set = provider.get(project);
            resourceSetMap.put(project, set);
            return set;
        }
        return resourceSetMap.get(project);
    }

    private URI getCopiedURI(IProject project, IPath relativePath) throws JavaModelException {
        if (!copiedURIMap.containsKey(relativePath)) {
            IJavaProject javaProject = JavaCore.create(project);
            IClasspathEntry sourceEntry = Iterators.find(Iterators.forArray(javaProject.getResolvedClasspath(true)),
                    new SourceFolderFinder(relativePath));
            IPath outputLocation = sourceEntry.getOutputLocation();
            if (outputLocation == null) {
                outputLocation = javaProject.getOutputLocation();
            }
            IPath path = outputLocation.append(relativePath.makeRelativeTo(sourceEntry.getPath()));
            URI copiedURI = URI.createPlatformResourceURI(path.toString(), true);
            copiedURIMap.put(relativePath, copiedURI);
            return copiedURI;
        }
        return copiedURIMap.get(relativePath);
    }

    public Pattern findPattern(URI proxyURI) throws JavaModelException {
        final IPath relativePath = new Path(proxyURI.toPlatformString(true));
        IResource file = workspaceRoot.findMember(relativePath);
        IProject project = file.getProject();
        ResourceSet set = getResourceSet(project);

        URI copiedURI = getCopiedURI(project, relativePath);
        Resource res = set.getResource(copiedURI, true);
        String fragment = proxyURI.fragment();
        return (Pattern) res.getEObject(fragment);
    }
}
