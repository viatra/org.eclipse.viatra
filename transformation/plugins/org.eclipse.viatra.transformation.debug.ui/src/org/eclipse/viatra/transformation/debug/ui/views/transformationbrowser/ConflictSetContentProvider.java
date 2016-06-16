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

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.views.model.CompositeItem;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

public class ConflictSetContentProvider implements ITreeContentProvider {
    private static final String NEXT_NAME = "Conflicting Activations";
    private static final String CONFLICTING_NAME = "Executable Activations";
    
    protected AdaptableTransformationBrowser view;
    
    public ConflictSetContentProvider(AdaptableTransformationBrowser view){
        this.view = view;
    }
    
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
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if(parentElement instanceof AdaptableEVM){
            TransformationState transformationState = view.getTransformationStateMap().get(parentElement);
            if(transformationState == null){
                return new Object[0];
            }else{
                List<Activation<?>> conflictingActivations = transformationState.getNotExecutableActivations();
                List<Activation<?>> nextActivations = transformationState.getNextActivations();
                CompositeItem nextContainer = new CompositeItem(NEXT_NAME, nextActivations.toArray());
                CompositeItem conflictingNode = new CompositeItem(CONFLICTING_NAME, conflictingActivations.toArray());
                CompositeItem[] retVal = {nextContainer, conflictingNode};
                return retVal;
                
            }
        }else if(parentElement instanceof CompositeItem){
            Object[] children = ((CompositeItem) parentElement).getChildren();
            return children;
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if(element instanceof AdaptableEVM){
            TransformationState transformationState = view.getTransformationStateMap().get(element);
            if(transformationState == null){
                return false;
            }else{
                List<Activation<?>> nextActivations = transformationState.getNextActivations();
                List<Activation<?>> conflictingActivations = transformationState.getConflictingActivations();
                return nextActivations.size()>0 | conflictingActivations.size()>0;
            }
        } else if(element instanceof CompositeItem){
            Object[] children = ((CompositeItem) element).getChildren();
            return children.length>0;
        }
        return false;
    }
}
