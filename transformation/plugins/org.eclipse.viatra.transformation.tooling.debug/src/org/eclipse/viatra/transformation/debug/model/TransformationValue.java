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
package org.eclipse.viatra.transformation.debug.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelProvider;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class TransformationValue extends TransformationDebugElement implements IValue {
    private List<TransformationVariable> transformationVariables = Lists.newArrayList();
    private Object value;
    private boolean initialized = false;
    private final TransformationModelProvider modelProvider;
    
    public Object getValue() {
        return value;
    }

    public TransformationValue(TransformationDebugTarget target, Object value, TransformationModelProvider modelProvider) {
        super(target);
        this.value = value;
        this.modelProvider = modelProvider;
    }

    public void addTransformationVariable(TransformationVariable variable) {
        transformationVariables.add(variable);
    }

    @Override
    public String getReferenceTypeName() throws DebugException {
        if(value instanceof TransformationModelElement){
            return ((TransformationModelElement)value).getTypeAttribute();
        }else{
            return value.getClass().getSimpleName();
        }
    }

    @Override
    public String getValueString() throws DebugException {
        AdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
        AdapterFactoryLabelProvider adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
        
        if (value == null) {
            return "NULL";
        } else if (value instanceof TransformationModelElement){
            String nameAttribute = ((TransformationModelElement) value).getNameAttribute();
            return ((TransformationModelElement)value).getTypeAttribute()+((nameAttribute.isEmpty() )? " " : (" \""+nameAttribute+"\" "))+"ID="+((TransformationModelElement)value).getId();
        } else if (value instanceof List<?>){
            Type[] arguments = ((ParameterizedType) value.getClass().getGenericSuperclass()).getActualTypeArguments();
            return value.getClass().getSimpleName()+"<"+Joiner.on(",").join(arguments)+">";
        } 
        return adapterFactoryLabelProvider.getText(value);
    }
    

    @Override
    public boolean isAllocated() throws DebugException {
        return true;
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
        if (!initialized) {
            initialize();
            initialized = true;
        }
        
        return transformationVariables.toArray(new IVariable[transformationVariables.size()]);
    }

    private void initialize() {
        if (value instanceof EObject) {
            EObject eObject = (EObject) value;
            EList<EStructuralFeature> eStructuralFeatures = eObject.eClass().getEStructuralFeatures();
            for (EStructuralFeature eSFeature : eStructuralFeatures) {
                Object eValue = eObject.eGet(eSFeature);
                String label = eSFeature.getName();
                transformationVariables.add(createTransformationVariable(eValue, label));
            }
        } else if (value instanceof TransformationModelElement) {
            TransformationModelElement element = (TransformationModelElement) value;
            modelProvider.loadElementContent(element);
            
            //Attributes
            Map<String, String> attributes = element.getAttributes();
            for (String attrLabel : attributes.keySet()) {
                if(!attrLabel.equals(TransformationModelElement.TYPE_ATTR)){
                    transformationVariables.add(createTransformationVariable("\""+attributes.get(attrLabel)+"\"", attrLabel));
                }
            }
            //CrossReferences
            Map<String, List<TransformationModelElement>> crossReferences = element.getCrossReferences();
            for (String referenceLabel : crossReferences.keySet()) {
                Collection<TransformationModelElement> collection = crossReferences.get(referenceLabel);
                if(collection.size()==1){
                    transformationVariables.add(createTransformationVariable(crossReferences.get(referenceLabel).iterator().next(), referenceLabel));
                }else{
                    transformationVariables.add(createTransformationVariable(crossReferences.get(referenceLabel), referenceLabel));
                }
            }
            //Children
            Map<String, List<TransformationModelElement>> children = element.getContainments();
            for (String containmentLabel : children.keySet()) {
                transformationVariables.add(createTransformationVariable(children.get(containmentLabel), containmentLabel));
            }
            
        } else if (value instanceof List<?>) {
            List<?> eList = (List<?>) value;
            for (Object object : eList) {
                String label = "["+Integer.toString(eList.indexOf(object))+"]";
                transformationVariables.add(createTransformationVariable(object, label));
            }
        }
    }

    protected TransformationVariable createTransformationVariable(Object value, String parameterName) {
        if (value == null) {
            TransformationValue eTValue = new TransformationValue(
                    (TransformationDebugTarget) getDebugTarget(), "NULL", modelProvider);
            return new TransformationVariable(
                    (TransformationDebugTarget) getDebugTarget(), parameterName, eTValue);
        } else {
            TransformationValue eTValue = new TransformationValue(
                    (TransformationDebugTarget) getDebugTarget(), value, modelProvider);
            return new TransformationVariable(
                    (TransformationDebugTarget) getDebugTarget(), parameterName, eTValue);
        }
    }

    @Override
    public boolean hasVariables() throws DebugException {
        if(!initialized){
            initialize();
            initialized = true;
        }
        return !transformationVariables.isEmpty();
    }

}
