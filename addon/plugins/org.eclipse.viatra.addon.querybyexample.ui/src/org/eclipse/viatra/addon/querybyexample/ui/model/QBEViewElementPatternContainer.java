/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.ui.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class QBEViewElementPatternContainer extends QBEViewElement {

    private String displayValue;

    public QBEViewElementPatternContainer(String displayName) {
        this.displayValue = displayName;
    }

    public void addNewElement(QBEViewElement newElement) {
        newElement.setParent(this);
        this.children.add(newElement);
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {

        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
    }

    @Override
    public String getLabel(Object o) {
        return this.displayValue;
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class)
            return this;
        return null;
    }
}
