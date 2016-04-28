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
package org.eclipse.viatra.transformation.debug.ui.views;

import java.util.List;
import java.util.Map;

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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.transformation.debug.launch.TransformationLaunchConfigurationDelegate;
import org.eclipse.viatra.transformation.debug.model.ITransformationStateListener;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVMFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.IAdaptableEVMFactoryListener;
import org.eclipse.wb.swt.ResourceManager;

import com.google.common.collect.Maps;

@SuppressWarnings("restriction")
public class AdaptableTransformationBrowser extends ViewPart implements IAdaptableEVMFactoryListener, ITransformationStateListener{

    public static final String ID = "org.eclipse.viatra.transformation.ui.debug.DisplayActivations";
    
    private Map<AdaptableEVM, TransformationState> transformationStateMap = Maps.newHashMap();
    private TreeViewer treeViewer;

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        treeViewer = new TreeViewer(composite, SWT.BORDER);
        
        treeViewer.setContentProvider(new AdaptableEVMContentProvider());
        treeViewer.setLabelProvider(new AdaptableEVMLabelProvider());

        treeViewer.addDoubleClickListener(new AdaptableEVMDoubleClickListener(this));

        AdaptableEVMFactory.INSTANCE.registerListener(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        AdaptableEVMFactory.INSTANCE.unRegisterListener(this);
        TransformationThreadFactory.INSTANCE.unRegisterListener(this);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }
    private class AdaptableEVMDoubleClickListener implements IDoubleClickListener {
        AdaptableTransformationBrowser view;
        
        public AdaptableEVMDoubleClickListener(AdaptableTransformationBrowser view){
            this.view = view;
        }
        
        @Override
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof IStructuredSelection) {
                Object firstElement = ((IStructuredSelection) selection).getFirstElement();
                if (firstElement instanceof AdaptableEVM && transformationStateMap.get(firstElement) == null) {
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
                        
                        TransformationThreadFactory.INSTANCE.registerListener(view, vm.getIdentifier());
                        
                        ModelInstanceViewer view = (ModelInstanceViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ModelInstanceViewer.ID);
                        view.registerToId(vm.getIdentifier());
                        
                        
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        
    }

    private class AdaptableEVMLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof AdaptableEVM) {
                AdaptableEVM vm = (AdaptableEVM) element;
                return vm.getIdentifier();
            }else if (element instanceof RuleSpecification) {
                RuleSpecification<?> spec = (RuleSpecification<?>) element;
                return spec.getName();
            }else if (element instanceof Activation) {
                Activation<?> activation = (Activation<?>) element;
                return "Activation, State: " + activation.getState().toString();
            }
            //TODO More info about activation

            return element.getClass().getName() + " Hash: " + element.hashCode();
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof AdaptableEVM) {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui",
                        "icons/rsz_viatra_logo.png");
                //TODO different icon for EVM instances with debugger running
            }else if (element instanceof RuleSpecification) {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.debug.ui",
                        "icons/atom.gif");
            }else if (element instanceof Activation) {
                TransformationState state = getStateForActivation((Activation<?>) element);
                
                if(element.equals(state.getNextActivation())){
                    return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation_stopped.gif");
                }else {
                    return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation.gif");
                }
            } 
            return null;
        }
    }

    private class AdaptableEVMContentProvider implements ITreeContentProvider {
                
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Map) {
                return ((Map<?,?>) inputElement).keySet().toArray();
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if(parentElement instanceof AdaptableEVM){
                TransformationState transformationState = transformationStateMap.get(parentElement);
                if(transformationState == null){
                    return null;
                }else{
                    List<RuleSpecification<?>> rules = transformationState.getRules();
                    return rules.toArray(new RuleSpecification<?>[rules.size()]);
                }
            } else if (parentElement instanceof RuleSpecification){
                TransformationState state = getStateForRuleSpecification((RuleSpecification<?>) parentElement);
                if(state != null){
                    List<Activation<?>> activations = state.getActivations((RuleSpecification<?>) parentElement);
                    return activations.toArray(new Activation<?>[activations.size()]);
                }else{
                    return null;
                }
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if(element instanceof AdaptableEVM){
                TransformationState transformationState = transformationStateMap.get(element);
                if(transformationState == null){
                    return false;
                }else{
                    List<RuleSpecification<?>> rules = transformationState.getRules();
                    return rules.size()>0;
                }
            } else if (element instanceof RuleSpecification){
                TransformationState state = getStateForRuleSpecification((RuleSpecification<?>) element);
                if(state != null){
                    return state.getActivations((RuleSpecification<?>) element).size()>0;
                }else{
                    return false;
                }
            }
            return false;
        }

    }

    @Override
    public void adaptableEVMPoolChanged(final List<AdaptableEVM> adaptableEVMs) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                for (AdaptableEVM evm : transformationStateMap.keySet()) {
                    if(!adaptableEVMs.contains(evm)){
                        transformationStateMap.remove(evm);
                    }
                }
                for (AdaptableEVM evm : adaptableEVMs) {
                    if(!transformationStateMap.containsKey(evm)){
                        transformationStateMap.put(evm, null);
                    }
                }
                treeViewer.setInput(transformationStateMap);
            }
        });

    }

    @Override
    public void transformationStateChanged(final TransformationState state, final String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                AdaptableEVM evmInstance = AdaptableEVMFactory.INSTANCE.getAdaptableEVMInstance(id);
                
                transformationStateMap.put(evmInstance, state);
                treeViewer.setInput(transformationStateMap);
            }
        });       
    }

    @Override
    public void transformationStateDisposed(final TransformationState state, final String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                AdaptableEVM evmInstance = AdaptableEVMFactory.INSTANCE.getAdaptableEVMInstance(id);
                
                transformationStateMap.remove(evmInstance);
                treeViewer.setInput(transformationStateMap);
            }
        }); 
        
    }
    
    private TransformationState getStateForRuleSpecification(RuleSpecification<?> spec){
        for (AdaptableEVM evm : transformationStateMap.keySet()) {
            TransformationState transformationState = transformationStateMap.get(evm);
            if(transformationState.getRules().contains(spec)){
                return transformationState;
            }
        }
        return null;
    }
    
    private TransformationState getStateForActivation(Activation<?> act){
        for (AdaptableEVM evm : transformationStateMap.keySet()) {
            TransformationState transformationState = transformationStateMap.get(evm);
            if(transformationState.getActivations().contains(act)){
                return transformationState;
            }
        }
        return null;
    }
}
