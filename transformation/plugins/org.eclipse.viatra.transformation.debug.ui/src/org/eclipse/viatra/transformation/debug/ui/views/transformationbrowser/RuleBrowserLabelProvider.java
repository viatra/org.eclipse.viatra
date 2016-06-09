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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.xtext.xbase.lib.Pair;

public class RuleBrowserLabelProvider extends LabelProvider {
    protected AdaptableTransformationBrowser view;

    public RuleBrowserLabelProvider(AdaptableTransformationBrowser view) {
        this.view = view;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof AdaptableEVM) {
            AdaptableEVM vm = (AdaptableEVM) element;
            return vm.getIdentifier();
        } else if (element instanceof Pair<?, ?>) {
            Object key = ((Pair<?, ?>) element).getKey();
            Object value = ((Pair<?, ?>) element).getValue();
            if (key instanceof RuleSpecification<?> && value instanceof EventFilter<?>) {
                if (value.equals(((RuleSpecification<?>) key).createEmptyFilter())) {
                    return ((RuleSpecification<?>) key).getName();
                } else {
                    return ((RuleSpecification<?>) key).getName() + " FILTERED";
                }
            }
        } else if (element instanceof RuleSpecification) {
            RuleSpecification<?> spec = (RuleSpecification<?>) element;
            return spec.getName();
        } else if (element instanceof Activation) {
            Activation<?> activation = (Activation<?>) element;
            TransformationState state = view.getStateForActivation(activation);
            if (state.getNewActivations().contains(activation)) {
                return "<<NEW>> Activation, State: " + activation.getState().toString()+ activation.getAtom().toString();
            } else {
                return "Activation, State: " + activation.getState().toString()+ activation.getAtom().toString();
            }

        }
        return element.getClass().getName() + " Hash: " + element.hashCode();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof AdaptableEVM) {
            if (view.isUnderDebugging((AdaptableEVM) element)) {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.debug.ui",
                        "icons/viatra_debug.gif");
            } else {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.debug.ui",
                        "icons/rsz_viatra_logo.png");
            }
        } else if (element instanceof Pair<?, ?>) {
            Object key = ((Pair<?, ?>) element).getKey();
            Object value = ((Pair<?, ?>) element).getValue();
            if (key instanceof RuleSpecification<?> && value instanceof EventFilter<?>) {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.debug.ui", "icons/atom.gif");
            }
        } else if (element instanceof Activation) {
            TransformationState state = view.getStateForActivation((Activation<?>) element);

            if (element.equals(state.getNextActivation())) {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui",
                        "icons/activation_stopped.gif");
            } else {
                return ResourceManager.getPluginImage("org.eclipse.viatra.transformation.ui", "icons/activation.gif");
            }
        }
        return null;
    }
}
