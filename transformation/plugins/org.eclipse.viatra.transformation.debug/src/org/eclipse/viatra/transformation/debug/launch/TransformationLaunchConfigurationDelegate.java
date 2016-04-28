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
import org.eclipse.viatra.transformation.debug.model.TransformationDebugTarget;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVMFactory;

public class TransformationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
    public static final String ADAPTABLE_EVM_ATTR = "org.eclipse.viatra.transformation.debug.launch.AdaptableEVMAttribute";
    public static final String TRANSFORMATION_ATTR = "org.eclipse.viatra.transformation.debug.launch.TransformationClass";
    public static final String PROJECT_NAME = "org.eclipse.viatra.transformation.debug.launch.TransformationProject";

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        String evmAttr = configuration.getAttribute(ADAPTABLE_EVM_ATTR, "");
        String transformationClassName = configuration.getAttribute(TRANSFORMATION_ATTR, "");
        String projectName = configuration.getAttribute(PROJECT_NAME, "");

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.hasNature(JavaCore.NATURE_ID)) {
            IJavaProject javaProject = JavaCore.create(project);
            IType transformationType = javaProject.findType(transformationClassName);

            AdaptableEVM vm = AdaptableEVMFactory.INSTANCE.getAdaptableEVMInstance(evmAttr);

            if (mode.equals(ILaunchManager.DEBUG_MODE) && vm != null) {
                TransformationDebugTarget target = new TransformationDebugTarget(launch, vm, transformationType,
                        vm.getIdentifier());
                launch.addDebugTarget(target);
            }
        }

    }

}
