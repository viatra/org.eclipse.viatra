/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal.operations;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;

public class EnsureProjectDependencies extends WorkspaceModifyOperation {
    private final IProject project;
    private final List<String> dependencies;

    public EnsureProjectDependencies(IProject project, List<String> dependencies) {
        this.project = project;
        this.dependencies = dependencies;
    }

    protected void execute(IProgressMonitor monitor) throws CoreException {
        ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
    }
}