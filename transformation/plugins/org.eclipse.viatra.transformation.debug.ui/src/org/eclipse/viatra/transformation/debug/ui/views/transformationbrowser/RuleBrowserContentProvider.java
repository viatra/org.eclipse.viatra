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
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationRule;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public class RuleBrowserContentProvider implements ITreeContentProvider {
    protected AdaptableTransformationBrowser view;
    
    public RuleBrowserContentProvider(AdaptableTransformationBrowser view){
        this.view = view;
    }
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    @Override
    public void dispose() {
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
            List<TransformationRule> rules = ((TransformationState) parentElement).getRules();
            return rules.toArray(new TransformationRule[rules.size()]);
        } else if (parentElement instanceof TransformationRule) {
            List<RuleActivation> activations = ((TransformationRule) parentElement).getActivations();
            return activations.toArray(new RuleActivation[activations.size()]);
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
            List<TransformationRule> rules = ((TransformationState) element).getRules();
            return rules.size()>0;
        } else if (element instanceof TransformationRule) {
            List<RuleActivation> activations = ((TransformationRule) element).getActivations();
            return activations.size()>0;
        }
        return false;
    }

}
