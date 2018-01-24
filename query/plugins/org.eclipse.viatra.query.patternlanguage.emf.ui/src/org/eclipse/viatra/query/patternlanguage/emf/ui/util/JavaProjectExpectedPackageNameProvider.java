/*******************************************************************************
 * Copyright (c) 2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.util;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.viatra.query.patternlanguage.emf.util.IExpectedPackageNameProvider;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.util.Pair;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class JavaProjectExpectedPackageNameProvider implements IExpectedPackageNameProvider {

    @Inject
    private IStorage2UriMapper storage2UriMapper;
    @Inject
    private Logger logger;
    
    /**
     * Based on org.eclipse.xtend.ide.validator.XtendUIValidator.java
     */
    @Override
    public String getExpectedPackageName(PatternModel model) {
        URI fileURI = model.eResource().getURI();
        for (Pair<IStorage, IProject> storage : storage2UriMapper.getStorages(fileURI)) {
            if (storage.getFirst() instanceof IFile) {
                IPath fileWorkspacePath = storage.getFirst().getFullPath();
                IJavaProject javaProject = JavaCore.create(storage.getSecond());
                if (javaProject != null && javaProject.exists() && javaProject.isOpen()) {
                    try {
                        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                            if (!root.isArchive() && !root.isExternal()) {
                                IResource resource = root.getResource();
                                if (resource != null) {
                                    IPath sourceFolderPath = resource.getFullPath();
                                    if (sourceFolderPath.isPrefixOf(fileWorkspacePath)) {
                                        IPath classpathRelativePath = fileWorkspacePath
                                                .makeRelativeTo(sourceFolderPath);
                                        return classpathRelativePath.removeLastSegments(1).toString().replace("/", ".");
                                    }
                                }
                            }
                        }
                    } catch (JavaModelException e) {
                        logger.error("Error resolving package declaration for Pattern Model", e);
                    }
                }
            }
        }
        return null;
    }

}
