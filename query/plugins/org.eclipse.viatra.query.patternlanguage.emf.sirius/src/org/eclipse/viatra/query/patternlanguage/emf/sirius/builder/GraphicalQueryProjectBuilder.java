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
package org.eclipse.viatra.query.patternlanguage.emf.sirius.builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.util.FileSystemHelper;
import org.eclipse.viatra.query.patternlanguage.metamodel.code.generator.VqlCodeGenerator;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternPackage;

/**
 * An incremental project builder for VIATRA Query projects.
 *
 * @author Zoltan Ujhelyi
 *
 */
public class GraphicalQueryProjectBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "org.eclipse.viatra.query.patternlanguage.emf.sirius.vgqlbuilder";
    private static final String OUTPUT_FOLDER_NAME = "vgql-gen";
    private static final String SOURCE_FOLDER_NAME = "src";

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        final ResourceSet set = new ResourceSetImpl();
        switch (kind) {
        case CLEAN_BUILD:
            clean(monitor);
            break;
        case FULL_BUILD:
            getProject().getFolder(SOURCE_FOLDER_NAME).accept(resource -> {
                if (resource instanceof IFile && Objects.equals("vgql", resource.getFileExtension())) {
                    buildFile(set, (IFile) resource);
                }
                return true;
            });
            break;
        case AUTO_BUILD:
        case INCREMENTAL_BUILD:
        default:
            final IPath sourceFolderPath = getProject().getFolder(SOURCE_FOLDER_NAME).getFullPath();
            getDelta(getProject()).accept(delta -> {
                if (sourceFolderPath.isPrefixOf(delta.getFullPath())) {
                    if (delta.getResource() instanceof IFile
                            && Objects.equals("vgql", delta.getResource().getFileExtension())) {
                        switch (delta.getKind()) {
                        case IResourceDelta.REMOVED:
                        case IResourceDelta.REMOVED_PHANTOM:
                            final IResource outputFile = getProject().findMember(getOutputPath(delta.getFullPath()));
                            outputFile.delete(true, monitor);
                            break;
                        default:
                            buildFile(set, (IFile) delta.getResource());
                        }
                    }
                }
                return true;
            });
        }
        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        getProject().getFolder(OUTPUT_FOLDER_NAME).accept(resource -> {
            if (Objects.equals("vql", resource.getFileExtension())) {
                resource.delete(true, monitor);
            }
            return true;
        });
    }

    private void buildFile(ResourceSet set, IFile sourceFile) throws CoreException {
        final IPath sourcePath = sourceFile.getFullPath();
        final Resource resource = set.getResource(URI.createPlatformResourceURI(sourcePath.toString(), true), true);
        final TreeIterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            final EObject next = it.next();
            if (next instanceof PatternPackage) {
                final PatternPackage pkg = (PatternPackage) next;

                IFile outputFile = getProject().getFile(getOutputPath(sourcePath));
                VqlCodeGenerator generator = new VqlCodeGenerator();
                InputStream contentStream = new ByteArrayInputStream(
                        generator.generate(pkg).getBytes(StandardCharsets.UTF_8));
                if (outputFile.exists()) {
                    outputFile.setContents(contentStream, IFile.KEEP_HISTORY, new NullProgressMonitor());
                } else {
                    // Given all paths begin with "vgql-gen", the container must always be a folder
                    FileSystemHelper.ensureParentsExists((IFolder) outputFile.getParent());
                    outputFile.create(contentStream, true, new NullProgressMonitor());
                }
                outputFile.setDerived(true, new NullProgressMonitor());
                it.prune();
            }
        }
    }

    private IPath getOutputPath(IPath sourcePath) {
        final IPath sourceFolderRelativePath = sourcePath
                .makeRelativeTo(getProject().getFolder(SOURCE_FOLDER_NAME).getFullPath());
        final IPath outputFileName = sourceFolderRelativePath.removeFileExtension().addFileExtension("vql");
        final IPath outputPath = getProject().getFolder(OUTPUT_FOLDER_NAME).getFullPath().append(outputFileName);
        return outputPath.makeRelativeTo(getProject().getFullPath());
    }


}
