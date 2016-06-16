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
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("unchecked")
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
                List<Pair<RuleSpecification<?>, EventFilter<?>>> rules = transformationState.getRules();
                return rules.toArray(new Pair<?,?>[rules.size()]);
            }
        } else if (parentElement instanceof Pair<?,?>){
            Object key = ((Pair<?,?>) parentElement).getKey();
            Object value = ((Pair<?,?>) parentElement).getValue();
            if(key instanceof RuleSpecification<?> && value instanceof EventFilter<?>){
                TransformationState state = view.getStateForRuleSpecification((Pair<RuleSpecification<?>, EventFilter<?>>) parentElement);
                if(state != null){
                    List<Activation<?>> activations = state.getConflictingActivations((Pair<RuleSpecification<?>, EventFilter<?>>) parentElement);
                    return activations.toArray(new Activation<?>[activations.size()]);
                }else{
                    return new Object[0];
                }
            }
            
            
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
                List<Pair<RuleSpecification<?>, EventFilter<?>>> rules = transformationState.getRules();
                return rules.size()>0;
            }
        } else if (element instanceof Pair<?,?>){
            Object key = ((Pair<?,?>) element).getKey();
            Object value = ((Pair<?,?>) element).getValue();
            if(key instanceof RuleSpecification<?> && value instanceof EventFilter<?>){
                TransformationState state = view.getStateForRuleSpecification((Pair<RuleSpecification<?>, EventFilter<?>>) element);
                if(state != null){
                    List<Activation<?>> activations = state.getConflictingActivations((Pair<RuleSpecification<?>, EventFilter<?>>) element);
                    return activations.size()>0;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

}
