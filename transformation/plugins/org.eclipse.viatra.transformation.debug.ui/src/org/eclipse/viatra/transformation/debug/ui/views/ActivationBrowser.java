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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.debug.model.ITransformationStateListener;
import org.eclipse.viatra.transformation.debug.model.TransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.wb.swt.ResourceManager;

import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public class ActivationBrowser extends ViewPart implements ITransformationStateListener {

    public static final String ID = "org.eclipse.viatra.transformation.ui.debug.DisplayActivations";
    private TreeViewer treeViewer;
    private Activation<?> selection;
    private Activation<?> nextActivation;
    private List<TransformationBreakpoint> breakpoints = Lists.newArrayList();


    public Activation<?> getSelection() {
        return selection;
    }

    @Override
    public void createPartControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        treeViewer = new TreeViewer(composite, SWT.BORDER);

        treeViewer.setContentProvider(new ActivationContentProvider());
        treeViewer.setLabelProvider(new ActivationLabelProvider());
        
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object firstElement = ((StructuredSelection) treeViewer.getSelection()).getFirstElement();
                if(firstElement instanceof Activation){
                    selection = (Activation) firstElement;
                }
                
            }
        });
        
        TransformationThreadFactory.INSTANCE.addListener(this);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        TransformationThreadFactory.INSTANCE.unRegisterListener(this);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    private class ActivationLabelProvider extends LabelProvider {
        
        @Override
        public String getText(Object element) {
            if (element instanceof Activation) {
                Activation activation = (Activation) element;
                return activation.getInstance().getSpecification().getName() + " Activation, State: " + activation.getState().toString();
            }

            return element.getClass().getName()+" Hash: "+element.hashCode();
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof Activation) {
                if(nextActivation!=null && element.equals(nextActivation)){
                    return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation_stopped.gif");
                }
                for (TransformationBreakpoint breakpoint : breakpoints) {
                    if(breakpoint.getActivation().equals(element)){
                        try {
                            if(breakpoint.isEnabled()){
                                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation_brkp.gif");
                            }else{
                                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation_brkpd.gif");
                            }
                        } catch (CoreException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation.gif");
            } 
            return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/atom.gif");
        }
    }

    private class ActivationContentProvider implements ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List<?>) {
                return ((List<?>) inputElement).toArray();
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Activation<?>){
                ArrayList<Object> matches = Lists.newArrayList(((Activation) parentElement).getAtom());
                return matches.toArray();
            }
            if (parentElement instanceof IPatternMatch){
                IPatternMatch match = ((IPatternMatch) parentElement);
                List<Object> parameters = Lists.newArrayList();
                for(String name : match.parameterNames()){
                    parameters.add(match.get(name));
                }                
                return Lists.newArrayList(parameters).toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof Activation<?>){
                return ((Activation) element).getAtom()!= null;
            }
            if (element instanceof IPatternMatch){
                IPatternMatch match = ((IPatternMatch) element);
                List<Object> parameters = Lists.newArrayList();
                for(String name : match.parameterNames()){
                    parameters.add(match.get(name));
                }                
                return parameters.size()>0;
            }
            return false;
        }

    }

    @Override
    public void transformationStateChanged(final TransformationState state) {
        this.nextActivation = state.getNextActivation();
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {
        
            @Override
            public void run() {
                treeViewer.setInput(state.getActivations());
                for(TreeItem item : treeViewer.getTree().getItems()){
                    if(item.getData().equals(state.getNextActivation())){
                        item.setFont(setBold(item.getFont()));
                    }
                }
            }
        });
    }
    
    public Font setBold(Font font) {
        Display display = treeViewer.getControl().getDisplay();
        FontData[] fD = font.getFontData();
        fD[0].setStyle(SWT.BOLD); 
        return new Font(display,fD[0]);
    }
    
    @Override
    public void transformationStateDisposed(TransformationState state) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                treeViewer.setInput(Lists.newArrayList());
            }
        });
        nextActivation = null;
    }
}
