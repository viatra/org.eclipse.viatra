/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.properties;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.TransformationModelElementLabelProvider;

import com.google.common.collect.Lists;

public class CrossReferencePropertySource implements IPropertySource{
    
    private final List<TransformationModelElement> elements;
    
    
    public CrossReferencePropertySource(List<TransformationModelElement> elements){
        this.elements = elements;
    }
   
    @Override
    public Object getEditableValue() {
        return null;
    }
    
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> descriptors = Lists.newArrayList();
        
        for (TransformationModelElement element : elements) {
            Integer indexOf = elements.indexOf(element);
            PropertyDescriptor descriptor = new PropertyDescriptor(indexOf, "["+indexOf+"]");
            descriptor.setLabelProvider(new TransformationModelElementLabelProvider());
            descriptors.add(descriptor); 
        }

        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }
    
    @Override
    public Object getPropertyValue(Object id) {
        if(id instanceof Integer){
            return new CrossReferenceElementPropertySource(elements.get((int) id));
        }
        return null;
    }
    
    @Override
    public boolean isPropertySet(Object id) {
        return true;
    }
    
    @Override
    public void resetPropertyValue(Object id) {
        //do nothing
    }
    
    @Override
    public void setPropertyValue(Object id, Object value) {
        //do nothing
    }
}
