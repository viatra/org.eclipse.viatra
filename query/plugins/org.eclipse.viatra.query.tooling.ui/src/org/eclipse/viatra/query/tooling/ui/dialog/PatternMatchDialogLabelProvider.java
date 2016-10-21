/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.dialog;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DisplayUtil;

/**
 * @author Mark Czotter
 * 
 */
public class PatternMatchDialogLabelProvider extends LabelProvider {

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public Image getImage(Object element) {
        ImageRegistry imageRegistry = ViatraQueryGUIPlugin.getDefault().getImageRegistry();
        if (element instanceof IPatternMatch) {
            return imageRegistry.get(ViatraQueryGUIPlugin.ICON_MATCH);
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IPatternMatch) {
            String message = DisplayUtil.getMessage((IPatternMatch) element);//, true);
            if (message != null) {
                return ViatraQueryRuntimeHelper.getMessage((IPatternMatch) element, message);
            } else {
                return element.toString();
            }
        }
        return null;
    }

}
