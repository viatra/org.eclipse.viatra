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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElement;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementVariable;
import org.eclipse.viatra.addon.querybyexample.ui.ui.CheckBoxPropertyDescriptor;

public class QBEViewElementVariableProperties implements IPropertySource {

    private QBEViewElementVariable element;

    private boolean isAnchor;

    private static final String PROPERTY_TEXT = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementVariableProperties:text";
    private static final String PROPERTY_CHECKBOX = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementVariableProperties:checkbox";
    private static final String PROPERTY_CHECKBOX_INPUTVAR = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementVariableProperties:checkboxinputvar";
    private static final String PROPERTY_COMBO = "org.eclipse.viatra.addon.querybyexample.view.model.properties.QBEViewElementVariableProperties:combo";
    private static final String VARIABLE_NAME_PROPERTIES_LBL = "Variable Name";
    private static final String IS_VISIBLE_PROPERTIES_LBL = "Visible as local variable constraint";
    private static final String TYPE_PROPERTIES_LBL = "Type";
    private static final String INPUTVAR_PROPERTIES_LBL = "Local variable as input parameter";
    private static final String ERROR_DIALOG_TITLE = "Validation error";
    private static final String ERROR_DIALOG_MAIN_TEXT = "Invalid variable name";

    private Map<Integer, EClass> typesMap;

    public QBEViewElementVariableProperties(QBEViewElementVariable element) {
        this.element = element;
        this.isAnchor = element.isAnchor();
    }

    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        // initializing supertypes' structures for type selector combo
        this.typesMap = new HashMap<Integer, EClass>();
        Set<EClass> types = this.element.getContainer().getService().getSuperTypeList(element.getEo());
        List<String> typeNames = new ArrayList<String>();
        int i = 0;
        for (EClass ec : types) {
            this.typesMap.put(i++, ec);
            typeNames.add(ec.getName());
        }

        List<IPropertyDescriptor> ret = new ArrayList<IPropertyDescriptor>();
        PropertyDescriptor descriptor = new TextPropertyDescriptor(PROPERTY_TEXT, VARIABLE_NAME_PROPERTIES_LBL);
        descriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);

        PropertyDescriptor descriptorCombo = new ComboBoxPropertyDescriptor(PROPERTY_COMBO, TYPE_PROPERTIES_LBL,
                typeNames.toArray(new String[typeNames.size()]));
        descriptorCombo.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
        descriptorCombo.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object el) {
                if (el instanceof Integer) {
                    final int integerElement = (Integer) el;
                    return typesMap.get(integerElement).getName();
                }
                return null;
            }
        });

        ret.add((IPropertyDescriptor) descriptor);
        ret.add((IPropertyDescriptor) descriptorCombo);

        if (!this.isAnchor) {
            PropertyDescriptor checkBoxDescriptorInputVar = new CheckBoxPropertyDescriptor(PROPERTY_CHECKBOX_INPUTVAR,
                    INPUTVAR_PROPERTIES_LBL);
            checkBoxDescriptorInputVar.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
            ret.add((IPropertyDescriptor) checkBoxDescriptorInputVar);

            if (!this.element.getVariableSetting().isInputVariable()) {
                PropertyDescriptor checkBoxDescriptor = new CheckBoxPropertyDescriptor(PROPERTY_CHECKBOX,
                        IS_VISIBLE_PROPERTIES_LBL);
                checkBoxDescriptor.setCategory(QBEViewElement.COMMON_CATEGORY_BASIC);
                ret.add((IPropertyDescriptor) checkBoxDescriptor);
            }
        }

        return ret.toArray(new IPropertyDescriptor[ret.size()]);
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_CHECKBOX.equals(id))
            return this.element.getVariableSetting().isVisible();
        if (PROPERTY_CHECKBOX_INPUTVAR.equals(id))
            return this.element.getVariableSetting().isInputVariable();
        if (PROPERTY_TEXT.equals(id))
            return this.element.getVariableSetting().getVariableName();
        if (PROPERTY_COMBO.equals(id)) {
            for (Entry<Integer, EClass> typesEntry : this.typesMap.entrySet()) {
                if (this.element.getVariableSetting().getType().equals(typesEntry.getValue()))
                    return typesEntry.getKey();
            }
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
        if (PROPERTY_CHECKBOX.equals(id))
            this.element.getVariableSetting().setVisible((Boolean) value);
        if (PROPERTY_CHECKBOX_INPUTVAR.equals(id)) {
            boolean b = (Boolean) value;
            this.element.getVariableSetting().setInputVariable(b);
            if (b)
                this.element.getVariableSetting().setVisible(b);
        }
        if (PROPERTY_TEXT.equals(id)) {
            String strValue = (String) value;
            if (QBEViewUtils.checkVariableNameIsReserved(strValue)) {
                strValue = QBEViewUtils.RESERVED_WORD_VAR_PREFIX + strValue;
            } else if (!QBEViewUtils.validatePropertyName(strValue)) {
                MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        ERROR_DIALOG_TITLE, ERROR_DIALOG_MAIN_TEXT);
                return;
            }
            this.element.getVariableSetting().setVariableName(strValue);
        }
        if (PROPERTY_COMBO.equals(id) && value instanceof Integer) {
            this.element.getVariableSetting().setType(this.typesMap.get((Integer) value));
        }
        this.element.getContainer().updateViewer();
    }
}
