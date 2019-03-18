/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.tooling;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;

/**
 * @author Abel Hegedus
 * 
 */
public final class ProjectLocator {

    public static IJavaProject locateProject(String path, Logger logger) {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path);
        if (project.exists()) {
            ArrayList<String> dependencies = new ArrayList<String>();
            dependencies.add("org.eclipse.viatra.query.runtime");
            dependencies.add("org.eclipse.viatra.addon.querybasedfeatures.runtime");
            try {
                ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
            } catch (CoreException e) {
                logger.error("Could not add required dependencies to model project.", e);
            }
            return JavaCore.create(project);
        } else {
            return null;
        }
    }

    private ProjectLocator() {
    }

}
