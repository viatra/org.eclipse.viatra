/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * An {@link ILabelProvider} implementation used in the {@link ElementListSelectionDialog} when choosing the type of a
 * pattern parameter.
 * 
 * @author Tamas Szabo
 * 
 */
public class ObjectParameterConfigurationLabelProvider extends LabelProvider {

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof EClassifier) {
            return ((EClassifier) element).getName();
        } else {
            return element.toString();
        }
    }

}
