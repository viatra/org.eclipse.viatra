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
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElement;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementConstraint;
import org.eclipse.viatra.addon.querybyexample.ui.ui.CheckBoxPropertyDescriptor;

public class QBEViewElementNegativeConstraintProperties implements IPropertySource {

    private QBEViewElementConstraint element;

    private static final String PROPERTY_CONSTRAINT_NAME_ID = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementNegativeConstraintProperties:constraintName";
    private static final String PROPERTY_IS_VISIBLE_ID = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementNegativeConstraintProperties:isVisible";
    private static final String PROPERTY_IS_CHECKED_ID = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementNegativeConstraintProperties:checked";
    private static final String CONSTRAINT_NAME_PROPERTIES_LBL = "Constraint Name";
    private static final String IS_VISIBLE_PROPERTIES_LBL = "Visible";
    private static final String IS_CHECKED_PROPERTIES_LBL = "Checked in Query Explorer";

    public QBEViewElementNegativeConstraintProperties(QBEViewElementConstraint constraintElement) {
        this.element = constraintElement;
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[3];
        PropertyDescriptor descriptor = new TextPropertyDescriptor(PROPERTY_CONSTRAINT_NAME_ID,
                CONSTRAINT_NAME_PROPERTIES_LBL);
        descriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        ret[0] = (IPropertyDescriptor) descriptor;

        PropertyDescriptor checkBoxDescriptor = new CheckBoxPropertyDescriptor(PROPERTY_IS_VISIBLE_ID,
                IS_VISIBLE_PROPERTIES_LBL);
        checkBoxDescriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        ret[1] = (IPropertyDescriptor) checkBoxDescriptor;

        PropertyDescriptor checkBoxDescriptorTwo = new CheckBoxPropertyDescriptor(PROPERTY_IS_CHECKED_ID,
                IS_CHECKED_PROPERTIES_LBL);
        checkBoxDescriptorTwo.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        ret[2] = (IPropertyDescriptor) checkBoxDescriptorTwo;

        return ret;
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (this.element.getConstraint() instanceof VQLNegConstraint) {
            if (PROPERTY_CONSTRAINT_NAME_ID.equals(id))
                return ((VQLNegConstraint) this.element.getConstraint()).getHelperPatternName();
            if (PROPERTY_IS_VISIBLE_ID.equals(id))
                return ((VQLNegConstraint) this.element.getConstraint()).isVisible();
            if (PROPERTY_IS_CHECKED_ID.equals(id))
                return ((VQLNegConstraint) this.element.getConstraint()).isQueryExplorerChecked();
        }
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
        if (this.element.getConstraint() instanceof VQLNegConstraint) {
            if (PROPERTY_CONSTRAINT_NAME_ID.equals(id))
                ((VQLNegConstraint) this.element.getConstraint()).setHelperPatternName(value.toString());
            if (PROPERTY_IS_VISIBLE_ID.equals(id))
                ((VQLNegConstraint) this.element.getConstraint()).setVisible((Boolean) value);
            if (PROPERTY_IS_CHECKED_ID.equals(id))
                ((VQLNegConstraint) this.element.getConstraint()).setQueryExplorerChecked((Boolean) value);
            this.element.getContainer().updateViewer();
        }
    }

}
