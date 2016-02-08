/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;

public class PatternsViewerFlatLabelProvider implements ILabelProvider {

    protected PatternsViewerInput input;

    public PatternsViewerFlatLabelProvider(PatternsViewerInput input) {
        this.input = input;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {

    }

    @Override
    public Image getImage(Object element) {
        ImageRegistry imageRegistry = ViatraQueryGUIPlugin.getDefault().getImageRegistry();

        if (element instanceof PatternLeaf) {
            return imageRegistry.get(ViatraQueryGUIPlugin.ICON_EIQ);
        } else if (element instanceof PatternComposite) {
            if (!element.equals(input.getGeneratedPatternsRoot()) && !element.equals(input.getGenericPatternsRoot())) {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_EPACKAGE);
            } else {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_ROOT);
            }
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof PatternComposite) {
            PatternComposite composite = (PatternComposite) element;

            if (composite.equals(input.getGeneratedPatternsRoot()) || composite.equals(input.getGenericPatternsRoot())) {
                return composite.getPatternNameFragment();
            } else {
                return composite.getFullPatternNamePrefix();
            }
        } else if (element instanceof PatternLeaf) {
            return ((PatternComponent) element).getPatternNameFragment();
        }

        return null;
    }

}
