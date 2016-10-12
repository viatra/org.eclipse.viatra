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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationRule;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;

public class RuleBrowserLabelProvider extends LabelProvider {
    protected TransformationBrowserView view;
    protected ImageRegistry imageRegistry;

    public RuleBrowserLabelProvider(TransformationBrowserView view) {
        this.view = view;
        this.imageRegistry = TransformationDebugUIActivator.getDefault().getImageRegistry();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TransformationState) {
            return ((TransformationState) element).getID();
        } else if (element instanceof TransformationRule) {
            if (((TransformationRule) element).isFiltered()) {
                return ((TransformationRule) element).getRuleName() + " FILTERED";
            } else {
                return ((TransformationRule) element).getRuleName();
            }
        } else if (element instanceof RuleActivation) {
            return element.toString();
        }
        return element.getClass().getName() + " Hash: " + element.hashCode();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof TransformationState) {
            return imageRegistry.get(TransformationDebugUIActivator.ICON_VIATRA_DEBUG_LOGO);
        } else if (element instanceof TransformationRule) {
            return imageRegistry.get(TransformationDebugUIActivator.ICON_VIATRA_ATOM);
        } else if (element instanceof RuleActivation) {
            if (((RuleActivation) element).isNextActivation()) {
                return imageRegistry.get(TransformationDebugUIActivator.ICON_VIATRA_ACT_STOPPED);
            } else {
                return imageRegistry.get(TransformationDebugUIActivator.ICON_VIATRA_ACTIVATION);
            }
        }
        return null;
    }
}
