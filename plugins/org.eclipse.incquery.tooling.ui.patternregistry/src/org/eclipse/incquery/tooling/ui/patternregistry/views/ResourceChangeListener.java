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
package org.eclipse.incquery.tooling.ui.patternregistry.views;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.incquery.tooling.ui.patternregistry.handlers.RegisterHandlersUtil;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

public class ResourceChangeListener implements IResourceChangeListener {

    private final IResourceSetProvider resourceSetProvider;

    public ResourceChangeListener(IResourceSetProvider resourceSetProvider) {
        super();
        this.resourceSetProvider = resourceSetProvider;
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {
                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    IResource resource = delta.getResource();
                    if (resource instanceof IFile) {
                        IFile file = (IFile) resource;
                        List<IPatternInfo> relatedIPatternInfoList = PatternRegistry.INSTANCE
                                .getFileToPatternInfoList(file);
                        if (!relatedIPatternInfoList.isEmpty()) {
                            if (delta.getKind() == IResourceDelta.CHANGED) {
                                for (IPatternInfo patternInfo : relatedIPatternInfoList) {
                                    PatternRegistry.INSTANCE.removePatternFromRegistry(patternInfo);
                                }
                                RegisterHandlersUtil.registerSingleFile(file, resourceSetProvider);
                            } else if (delta.getKind() == IResourceDelta.REMOVED) {
                                for (IPatternInfo patternInfo : relatedIPatternInfoList) {
                                    PatternRegistry.INSTANCE.removePatternFromRegistry(patternInfo);
                                }
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
        } catch (CoreException e) {
            // FIXME do it
            e.printStackTrace();
        }
    }

}
