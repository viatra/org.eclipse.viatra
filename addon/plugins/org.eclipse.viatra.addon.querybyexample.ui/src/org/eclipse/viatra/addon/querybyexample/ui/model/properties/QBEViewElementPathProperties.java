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
package org.eclipse.viatra.addon.querybyexample.ui.model.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElement;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPath;
import org.eclipse.viatra.addon.querybyexample.ui.ui.CheckBoxPropertyDescriptor;

public class QBEViewElementPathProperties implements IPropertySource {

    private static final String PROPERTY_CHECKBOX = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementPathProperties:checkbox";
    private static final String IS_VISIBLE_PROPERTIES_LBL = "Included";

    private QBEViewElementPath element;

    public QBEViewElementPathProperties(QBEViewElementPath element) {
        this.element = element;
    }

    @Override
    public Object getEditableValue() {
        return true;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[1];
        PropertyDescriptor checkBoxDescriptor = new CheckBoxPropertyDescriptor(PROPERTY_CHECKBOX,
                IS_VISIBLE_PROPERTIES_LBL);
        checkBoxDescriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        ret[0] = (IPropertyDescriptor) checkBoxDescriptor;
        return ret;
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_CHECKBOX.equals(id))
            return this.element.getPath().isVisible();
        return null;
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_CHECKBOX.equals(id))
            this.element.getContainer().getService().setPathVisibility(this.element.getPath(), (Boolean) value);
        this.element.getContainer().updateViewer();
    }

}
