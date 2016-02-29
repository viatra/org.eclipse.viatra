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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementVariableProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementVariable extends QBEViewElement {

    private EObject eo;
    private VQLVariableSetting variableSetting;
    private QBEView container;
    private boolean anchor = false;

    private static final ImageDescriptor anchorImg = ImageDescriptor.createFromFile(QBEViewElementVariable.class,
            "/icons/toc_anchor_obj.png");
    private static final ImageDescriptor varImg = ImageDescriptor.createFromFile(QBEViewElementVariable.class,
            "/icons/qbe_var.gif");
    private static final ImageDescriptor varImgNeg = ImageDescriptor.createFromFile(QBEViewElementVariable.class,
            "/icons/qbe_var_disabled.gif");

    public QBEViewElementVariable(QBEView container, EObject eo, VQLVariableSetting variableSetting, boolean isAnchor) {
        this.container = container;
        this.eo = eo;
        this.variableSetting = variableSetting;
        this.anchor = isAnchor;
    }

    public EObject getEo() {
        return eo;
    }

    public void setEo(EObject eo) {
        this.eo = eo;
    }

    public VQLVariableSetting getVariableSetting() {
        return variableSetting;
    }

    public void setVariableSetting(VQLVariableSetting variableSetting) {
        this.variableSetting = variableSetting;
    }

    public QBEView getContainer() {
        return container;
    }

    public void setContainer(QBEView container) {
        this.container = container;
    }

    public boolean isAnchor() {
        return anchor;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        if (this.anchor)
            return anchorImg;
        return this.variableSetting.isVisible() ? varImg : varImgNeg;
    }

    @Override
    public String getLabel(Object o) {
        String eClassName = variableSetting.getType().getName();
        String varName = variableSetting.getVariableName();
        return variableSetting.isInputVariable() == true ? varName + " : " + eClassName
                : eClassName + "(" + varName + ")";
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class)
            return new QBEViewElementVariableProperties(this);
        if (adapter == IWorkbenchAdapter.class)
            return this;
        return null;
    }
}
