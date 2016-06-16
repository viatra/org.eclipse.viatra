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

import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;
import org.eclipse.viatra.transformation.debug.ui.views.model.CompositeItem;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class ConflictSetLabelProvider extends RuleBrowserLabelProvider {

    public ConflictSetLabelProvider(AdaptableTransformationBrowser view) {
        super(view);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Activation) {
            Activation<?> activation = (Activation<?>) element;
            TransformationState state = view.getStateForActivation(activation);
            if(state.getNewActivations().contains(activation)){
                return "<<NEW>> "+ activation.getInstance().getSpecification().getName()+" Activation, State: " + activation.getState().toString()+ activation.getAtom().toString();
            }else{
                return activation.getInstance().getSpecification().getName()+" Activation, State: " + activation.getState().toString()+ activation.getAtom().toString();
            }
        }else if (element instanceof CompositeItem) {
            return ((CompositeItem) element).getName();
        }else{
            return super.getText(element);
        }
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof CompositeItem) {
            return TransformationDebugUIActivator.getDefault().getImageRegistry().get(TransformationDebugUIActivator.ICON_VIATRA_ATOM);
        }else {
            return super.getImage(element);
        }
    }
}
