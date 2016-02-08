/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Istvan David - updated for VIATRA-CEP
 *******************************************************************************/

package org.eclipse.viatra.cep.tooling.ui.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ViatraCepProjectBuilder extends IncrementalProjectBuilder {

    private static final class ChangeDetector implements IResourceDeltaVisitor {
        public ChangeDetector(IPath path) {
            super();
            this.path = path;
        }

        private final IPath path;
        private boolean changeFound = false;

        public boolean visit(IResourceDelta delta) throws CoreException {
            if (path.equals(delta.getFullPath())) {
                changeFound = true;
            }
            return !changeFound;
        }

        public boolean isChangeFound() {
            return changeFound;
        }
    }

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        if (kind == AUTO_BUILD || kind == INCREMENTAL_BUILD) {
            ChangeDetector visitor = new ChangeDetector(getProject().getFullPath());
            getDelta(getProject()).accept(visitor);
            if (visitor.isChangeFound()) {
                getProject().accept(new IResourceVisitor() {
                    @Override
                    public boolean visit(IResource resource) throws CoreException {
                        final String extension = resource.getFileExtension();
                        if ("vepl".equals(extension)) {
                            resource.touch(new NullProgressMonitor());
                        }
                        return false;
                    }
                });
            }
        }
        return new IProject[0];
    }

}
