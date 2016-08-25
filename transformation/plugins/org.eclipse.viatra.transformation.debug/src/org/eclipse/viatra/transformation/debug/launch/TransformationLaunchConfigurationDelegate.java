/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra.transformation.debug.communication.DebuggerHostEndpoint;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugTarget;

public class TransformationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
    public static final String SELECTED_TARGET = "org.eclipse.viatra.transformation.debug.launch.SelectedTarget";
    public static final String TRANSFORMATION_ATTR = "org.eclipse.viatra.transformation.debug.launch.TransformationClass";
    public static final String PROJECT_NAME = "org.eclipse.viatra.transformation.debug.launch.TransformationProject";

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        String endpointID = configuration.getAttribute(SELECTED_TARGET, "");
        String transformationClassName = configuration.getAttribute(TRANSFORMATION_ATTR, "");
        String projectName = configuration.getAttribute(PROJECT_NAME, "");

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.hasNature(JavaCore.NATURE_ID)) {
            IJavaProject javaProject = JavaCore.create(project);
            IType transformationType = javaProject.findType(transformationClassName);
            
            //Create new debugger host AGENT
            //connect to the target agent via using the specified parameter
            DebuggerHostEndpoint agent = new DebuggerHostEndpoint(endpointID);
            if (mode.equals(ILaunchManager.DEBUG_MODE)) {
                
                TransformationDebugTarget target = new TransformationDebugTarget(launch, agent, transformationType, endpointID);
                launch.addDebugTarget(target);
            }
        }

    }

}
