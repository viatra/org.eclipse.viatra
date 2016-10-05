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
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TransformationModelElement implements Serializable{
    private static final long serialVersionUID = -8165991633354685442L;
    public static final String TYPE_ATTR = "EObjectType";
    
    private UUID id = UUID.randomUUID(); 
    private boolean loaded = false; 
    
    private Map<String, String> attributes = Maps.newHashMap();
    
    private Map<String, List<TransformationModelElement>> crossReferences = Maps.newHashMap();
    
    private Map<String, List<TransformationModelElement>> containedElements = Maps.newHashMap();
    
    public UUID getId() {
        return id;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public String getAttribute(String name) {
        return attributes.get(name);
    }
    
    public Map<String, String> getAttributes() {
        return Maps.newHashMap(attributes);
    }
    
    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }
    
    
    public Collection<TransformationModelElement> getCrossReference(String name) {
        return crossReferences.get(name);
    }
    
    public void addCrossReference(String name, TransformationModelElement value) {
        List<TransformationModelElement> list = crossReferences.get(name);
        if(list==null){
            crossReferences.put(name, Lists.newArrayList(value));
        }else{
            list.add(value);
        }
    }
    
    public Map<String, List<TransformationModelElement>> getCrossReferences() {
        return Maps.newHashMap((crossReferences));
    }
    
    public Collection<TransformationModelElement> getContainedElement(String name) {
        return containedElements.get(name);
    }
    
    public void addContainedElement(String name, TransformationModelElement value) {
        List<TransformationModelElement> list = containedElements.get(name);
        if(list==null){
            containedElements.put(name, Lists.newArrayList(value));
        }else{
            list.add(value);
        }
    }
    
    public Map<String, List<TransformationModelElement>> getContainments() {
        return Maps.newHashMap((containedElements));
    }
    
    public List<TransformationModelElement> getChildren() {
        List<TransformationModelElement> list = Lists.newArrayList();
        for(String label : containedElements.keySet()){
            list.addAll(containedElements.get(label));
        } 
        return list;
    }
    
    public void setCrossReferences(Map<String, List<TransformationModelElement>> crossReferences) {
        this.crossReferences = crossReferences;
        loaded = true;
    }

    public void setContainedElements(Map<String, List<TransformationModelElement>> containedElements) {
        this.containedElements = containedElements;
        loaded = true;
    }
    
    public String getNameAttribute(){
        for (String attributeKey : attributes.keySet()) {
            if(attributeKey.matches("(.*ID.*|.*identifier.*|.*name.*)")){
                return attributes.get(attributeKey);
            }
        }
        return "";
    }
    
    public String getTypeAttribute(){
        return getAttribute(TYPE_ATTR);
    }
}
