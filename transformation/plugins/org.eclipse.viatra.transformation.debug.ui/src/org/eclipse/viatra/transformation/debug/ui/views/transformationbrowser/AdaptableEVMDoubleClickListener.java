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
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.viatra.transformation.debug.launch.TransformationLaunchConfigurationDelegate;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

@SuppressWarnings("restriction")
public class AdaptableEVMDoubleClickListener implements IDoubleClickListener {
    AdaptableTransformationBrowser view;
    
    public AdaptableEVMDoubleClickListener(AdaptableTransformationBrowser view){
        this.view = view;
    }
    
    @Override
    public void doubleClick(DoubleClickEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            if (firstElement instanceof AdaptableEVM && view.getTransformationStateMap().get(firstElement) == null) {
                AdaptableEVM vm = (AdaptableEVM) firstElement;
                String fullyQualifiedName = "";
                String projectName = "";

                ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
                ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType("org.eclipse.viatra.transformation.debug.launchViatraTransformation");
                try {
                    SelectionDialog dialog = JavaUI.createTypeDialog(JDIDebugUIPlugin.getActiveWorkbenchShell(),
                            JDIDebugUIPlugin.getActiveWorkbenchWindow(), SearchEngine.createWorkspaceScope(),
                            IJavaElementSearchConstants.CONSIDER_CLASSES, false);
                    dialog.setTitle("Select Transformation Class");
                    dialog.create();
                    if (dialog.open() == Window.CANCEL) {
                        return;
                        
                    }
                    Object[] results = dialog.getResult();
                    IType type = (IType) results[0];
                    if (type != null) {
                        fullyQualifiedName = type.getFullyQualifiedName();
                        projectName = type.getJavaProject().getElementName();
                    }
                    ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, vm.getIdentifier());
                    
                    workingCopy.setAttribute(TransformationLaunchConfigurationDelegate.ADAPTABLE_EVM_ATTR,
                            vm.getIdentifier());
                    workingCopy.setAttribute(TransformationLaunchConfigurationDelegate.TRANSFORMATION_ATTR,
                            fullyQualifiedName);
                    workingCopy.setAttribute(TransformationLaunchConfigurationDelegate.PROJECT_NAME,
                            projectName);
                    
                    
                    DebugUITools.launch(workingCopy, "debug");
                    
                    TransformationThreadFactory.getInstance().registerListener(view, vm.getIdentifier());
                                            
                    
                } catch (CoreException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    
}
