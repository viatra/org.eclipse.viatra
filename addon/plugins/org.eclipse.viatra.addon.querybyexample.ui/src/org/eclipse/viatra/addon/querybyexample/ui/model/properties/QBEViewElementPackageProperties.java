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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElement;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPackage;

public class QBEViewElementPackageProperties implements IPropertySource {

    private QBEViewElementPackage element;

    private static final String PROPERTY_TEXT = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementPackageProperties:text";
    private static final String PCKG_NAME_PROPERTIES_LBL = "Package Name";
    private static final String ERROR_DIALOG_TITLE = "Validation error";
    private static final String ERROR_DIALOG_MAIN_TEXT = "Invalid package name";

    public QBEViewElementPackageProperties(QBEViewElementPackage element) {
        this.element = element;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[1];
        PropertyDescriptor descriptor = new TextPropertyDescriptor(PROPERTY_TEXT, PCKG_NAME_PROPERTIES_LBL);
        descriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        ret[0] = (IPropertyDescriptor) descriptor;
        return ret;
    }

    @Override
    public Object getPropertyValue(Object id) {
        return this.element.getPackageName();
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
        String strValue = (String) value;
        if (!QBEViewUtils.validatePropertyName(strValue)) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ERROR_DIALOG_TITLE,
                    ERROR_DIALOG_MAIN_TEXT);
            return;
        }
        this.element.getContainer().getService().setPackageName(strValue);
        this.element.getContainer().updateViewer();
    }
}
