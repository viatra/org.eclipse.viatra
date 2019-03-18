/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;
import org.eclipse.viatra.transformation.debug.ui.views.model.CompositeItem;

public class ConflictSetLabelProvider extends RuleBrowserLabelProvider {

    public ConflictSetLabelProvider(TransformationBrowserView view) {
        super(view);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof RuleActivation) {
            return "Rule: "+((RuleActivation) element).getRuleName()+" - "+element.toString();
        } else if (element instanceof CompositeItem) {
            return ((CompositeItem) element).getName();
        } else {
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
