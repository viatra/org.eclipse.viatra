/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.launch;

import java.util.List;

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
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugTarget;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVMFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;

import com.google.common.collect.Lists;

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
            TransformationDebugger debugger = null;

            List<IEVMAdapter> adapters = vm.getAdapters();
            for (IEVMAdapter ievmAdapter : adapters) {
                if (ievmAdapter instanceof TransformationDebugger) {
                    debugger = (TransformationDebugger) ievmAdapter;
                }
            }

            if (mode.equals(ILaunchManager.DEBUG_MODE) && debugger != null) {
                TransformationDebugTarget target = new TransformationDebugTarget(launch, Lists.newArrayList(debugger), transformationType,
                        vm.getIdentifier());
                launch.addDebugTarget(target);
            }
        }

    }

}
