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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransformationModelElement implements Serializable{
    private static final long serialVersionUID = -8165991633354685442L;
    public static final String TYPE_ATTR = "EObjectType";
    
    private UUID id = UUID.randomUUID(); 
    private boolean loaded = false; 
    
    private Map<String, String> attributes = new HashMap<>();
    
    private Map<String, List<TransformationModelElement>> crossReferences = new HashMap<>();
    
    private Map<String, List<TransformationModelElement>> containedElements = new HashMap<>();
    
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
        return new HashMap<>(attributes);
    }
    
    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }
    
    
    public Collection<TransformationModelElement> getCrossReference(String name) {
        return crossReferences.get(name);
    }
    
    public void addEmptyCrossReference(String name) {
        crossReferences.computeIfAbsent(name, n -> new ArrayList<>());
    }
    
    public void addCrossReference(String name, TransformationModelElement value) {
        crossReferences.computeIfAbsent(name, n -> new ArrayList<>()).add(value);
    }
    
    public Map<String, List<TransformationModelElement>> getCrossReferences() {
        return new HashMap<>(crossReferences);
    }
    
    public Collection<TransformationModelElement> getContainedElement(String name) {
        return containedElements.get(name);
    }
    
    public void addEmptyContainment(String name) {
        containedElements.computeIfAbsent(name, n -> new ArrayList<>());
    }
    
    public void addContainedElement(String name, TransformationModelElement value) {
        containedElements.computeIfAbsent(name, n -> new ArrayList<>()).add(value);
    }
    
    public Map<String, List<TransformationModelElement>> getContainments() {
        return new HashMap<>(containedElements);
    }
    
    public List<TransformationModelElement> getChildren() {
        return containedElements.values().stream().flatMap(i -> i.stream()).collect(Collectors.toList());
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
        for (Entry<String, String> attr : attributes.entrySet()) {
            if(attr.getKey().matches("(.*ID.*|.*identifier.*|.*name.*)")){
                return attr.getValue();
            }
        }
        return "";
    }
    
    public String getTypeAttribute(){   
        return getAttribute(TYPE_ATTR);
    }
    
    @Override
    public String toString() {
        return getTypeAttribute() + ((getNameAttribute().isEmpty()) ? " " : (" \"" + getNameAttribute() + "\" "));
    }
}
