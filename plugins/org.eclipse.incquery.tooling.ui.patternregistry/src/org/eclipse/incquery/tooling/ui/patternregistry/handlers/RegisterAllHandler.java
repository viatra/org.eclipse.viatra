/*******************************************************************************
 * Copyright (c) 2010-2013, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class RegisterAllHandler extends AbstractHandler {

    @Inject
    private IResourceSetProvider resourceSetProvider;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (selection != null && selection instanceof IStructuredSelection) {
                IStructuredSelection structured = (IStructuredSelection) selection;
                IJavaProject javaProject = (IJavaProject) structured.getFirstElement();
                for (IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
                    for (Object nonJavaObject : packageFragmentRoot.getNonJavaResources()) {
                        handleNonJavaObject(nonJavaObject);
                    }
                    for (IJavaElement javaElement : packageFragmentRoot.getChildren()) {
                        if (javaElement instanceof IPackageFragment) {
                            IPackageFragment packageFragment = (IPackageFragment) javaElement;
                            for (Object nonJavaObject : packageFragment.getNonJavaResources()) {
                                handleNonJavaObject(nonJavaObject);
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            throw new ExecutionException("Error loading eiq file.", exception);
        }
        return null;
    }

    private void handleNonJavaObject(Object nonJavaObject) throws IncQueryException {
        if (nonJavaObject instanceof IFile) {
            IFile file = (IFile) nonJavaObject;
            RegisterHandlersUtil.registerSingleFile(file, resourceSetProvider);
        }
    }

}
