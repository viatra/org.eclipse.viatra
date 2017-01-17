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
package org.eclipse.viatra.query.patternlanguage.emf.ui.builder;

import java.util.Objects;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.IShouldGenerate;
import org.eclipse.xtext.ui.generator.EclipseBasedShouldGenerate;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.UriUtil;
import org.eclipse.xtext.xbase.lib.Exceptions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.ISourceFolder;
import org.eclipse.xtext.workspace.ProjectConfigAdapter;

/**
 * Based on the implementation from {@link EclipseBasedShouldGenerate} but extended with source folder checking.
 * @author Zoltan Ujhelyi
 * @since 1.6
 *
 */
@SuppressWarnings("restriction")
public class PatternLanguageShouldGenerate implements IShouldGenerate {

    @Override
    public boolean shouldGenerate(Resource resource, CancelIndicator cancelIndicator) {
        URI uri = resource.getURI();
        if (uri == null || !uri.isPlatformResource()) {
            return false;
        }
        
        try {
            return calculateShouldGenerate(resource, uri);
        } catch (CoreException e) {
            //XXX Wrapping with sneaky throw from Xtend (same behaviour as EclipseBasedShouldGenerate)
            throw Exceptions.sneakyThrow(e);
        }
    }

    private boolean calculateShouldGenerate(Resource resource, URI uri) throws CoreException {
        IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(uri.toPlatformString(true)));
        if (member != null && member.getType() == IResource.FILE) {
            ProjectConfigAdapter adapter = ProjectConfigAdapter.findInEmfObject(resource.getResourceSet());
            if (adapter != null) {
                IProjectConfig projectConfig = adapter.getProjectConfig();
                if (Objects.equals(member.getProject().getName(), projectConfig.getName())) {
                    return isInSourceFolder(projectConfig, uri) &&
                            member.findMaxProblemSeverity(null, true, IResource.DEPTH_INFINITE) != IMarker.SEVERITY_ERROR;
                }
            }
        }
        return false;
    }
    
    private boolean isInSourceFolder(IProjectConfig config, final URI uri) {
        if (config.findSourceFolderContaining(uri) == null) {
            // XXX: If classpath entry has a trailing slash, an empty segment is added to the URI
            return Iterables.any(config.getSourceFolders(), new Predicate<ISourceFolder>() {

                @Override
                public boolean apply(ISourceFolder folder) {
                    URI folderUri = folder.getPath();
                    if (folderUri.segment(folderUri.segmentCount() - 1).isEmpty()) {
                        return UriUtil.isPrefixOf(folderUri.trimSegments(1), uri);
                    }
                    return false;
                }
            });
        } else {
            return true;
        }
    }

}
