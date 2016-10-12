/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.properties;

import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.TransformationModelElementLabelProvider;

import com.google.common.collect.Lists;

public class TransformationModelElementPropertySource implements IPropertySource{
    
    private static final String IDENTIFIER_ID = "ID";
    private static final String ATTRIBUTE_ID = "Attributes";
    private static final String REFERENCE_ID = "References";
    private final TransformationModelElement element;
    
    
    public TransformationModelElementPropertySource(TransformationModelElement element){
        this.element = element;
    }
   
    @Override
    public Object getEditableValue() {
        return element;
    }
    
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> descriptors = Lists.newArrayList();
        //ID
        PropertyDescriptor idDescriptor = new PropertyDescriptor(IDENTIFIER_ID, IDENTIFIER_ID);
        idDescriptor.setCategory(ATTRIBUTE_ID);
        descriptors.add(idDescriptor);
        
        
        //Attributes
        Map<String, String> attributes = element.getAttributes();
        for (String attrName : attributes.keySet()) {
            if(!attrName.equals(TransformationModelElement.TYPE_ATTR)){
                PropertyDescriptor descriptor = new PropertyDescriptor(attrName, attrName);
                descriptor.setCategory(ATTRIBUTE_ID);
                descriptors.add(descriptor); 
            }
        }
        
        //Cross References
        Map<String, List<TransformationModelElement>> crossReferences = element.getCrossReferences();
        for(String crossRefName : crossReferences.keySet()){
            PropertyDescriptor descriptor = new PropertyDescriptor(crossRefName, crossRefName);
            descriptor.setLabelProvider(new TransformationModelElementLabelProvider());
            descriptor.setCategory(REFERENCE_ID);
            descriptors.add(descriptor); 
        }
        
        
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }
    
    @Override
    public Object getPropertyValue(Object id) {
        //ID
        if(id.equals(IDENTIFIER_ID)){
            return element.getId();
        }
        //Attributes
        Map<String, String> attributes = element.getAttributes();
        for(String attrName : attributes.keySet()){
            if(attrName.equals(id)){
                return attributes.get(attrName);
            }
        }
        //Cross References
        Map<String, List<TransformationModelElement>> crossReferences = element.getCrossReferences();
        for(String crossrefName : crossReferences.keySet()){
            if(crossrefName.equals(id)){
                List<TransformationModelElement> list = crossReferences.get(crossrefName);
                if(list.size() == 1){
                    return new CrossReferenceElementPropertySource(list.get(0));
                }else{
                    return new CrossReferencePropertySource(list);
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isPropertySet(Object id) {
        return true;
    }
    
    @Override
    public void resetPropertyValue(Object id) {

    }
    
    @Override
    public void setPropertyValue(Object id, Object value) {
        
    }
}
