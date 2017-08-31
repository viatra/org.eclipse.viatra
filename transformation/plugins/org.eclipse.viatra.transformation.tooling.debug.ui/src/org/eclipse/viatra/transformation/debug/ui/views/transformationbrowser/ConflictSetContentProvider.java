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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.views.model.CompositeItem;

public class ConflictSetContentProvider implements ITreeContentProvider {
    private static final String NEXT_NAME = "Conflicting Activations";
    private static final String CONFLICTING_NAME = "Executable Activations";
    
    protected TransformationBrowserView view;
    
    public ConflictSetContentProvider(TransformationBrowserView view){
        this.view = view;
    }
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        //do nothing
    }

    @Override
    public void dispose() {
      //do nothing
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof TransformationThread) {
            TransformationState transformationState = ((TransformationThread) inputElement).getTransformationState();
            if(transformationState !=null){
                return new TransformationState[]{((TransformationThread) inputElement).getTransformationState()};
            }
        }
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if(parentElement instanceof TransformationState){
                List<RuleActivation> conflictingActivations = ((TransformationState) parentElement).getConflictingActivations();
                List<RuleActivation> nextActivations = ((TransformationState) parentElement).getNextActivations();
                CompositeItem nextContainer = new CompositeItem(NEXT_NAME, nextActivations.toArray());
                CompositeItem conflictingNode = new CompositeItem(CONFLICTING_NAME, conflictingActivations.toArray());
                CompositeItem[] retVal = {nextContainer, conflictingNode};
                return retVal;

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
        if(element instanceof TransformationState){
                List<RuleActivation> conflictingActivations = ((TransformationState) element).getConflictingActivations();
                List<RuleActivation> nextActivations = ((TransformationState) element).getNextActivations();
                return nextActivations.size()>0 || conflictingActivations.size()>0;
        } else if(element instanceof CompositeItem){
            Object[] children = ((CompositeItem) element).getChildren();
            return children.length>0;
        }
        return false;
    }
}
