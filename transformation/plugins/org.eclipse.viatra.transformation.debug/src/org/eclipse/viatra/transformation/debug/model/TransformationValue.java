/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.model;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import com.google.common.collect.Lists;

public class TransformationValue extends TransformationDebugElement implements IValue {
    private List<TransformationVariable> transformationVariables = Lists.newArrayList();
    private Object value;
    private boolean initialized = false;

    public Object getValue() {
        return value;
    }

    public TransformationValue(TransformationDebugTarget target, Object value) {
        super(target);
        this.value = value;

    }

    public void addTransformationVariable(TransformationVariable variable) {
        transformationVariables.add(variable);
    }

    @Override
    public String getReferenceTypeName() throws DebugException {
        return value.getClass().getSimpleName();
    }

    @Override
    public String getValueString() throws DebugException {
//        Descriptor descriptor = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry().getDescriptor(Lists.newArrayList(value));
//        AdapterFactory adapterFactory = descriptor.createAdapterFactory();
        AdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
        AdapterFactoryLabelProvider adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
        
        
        if (value == null) {
            return "NULL";
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
            if (value instanceof EObject) {
                EObject eObject = (EObject) value;
                EList<EStructuralFeature> eStructuralFeatures = eObject.eClass().getEStructuralFeatures();
                for (EStructuralFeature eSFeature : eStructuralFeatures) {
                    Object eValue = eObject.eGet(eSFeature);
                    String label = eSFeature.getName();
                    transformationVariables.add(createTransformationVariable(eValue, label));
                }
            } else if (value instanceof EList<?>) {
                EList<?> eList = (EList<?>) value;
                for (Object object : eList) {
                    String label = Integer.toString(eList.indexOf(object));
                    transformationVariables.add(createTransformationVariable(object, label));
                }
            }
            initialized = true;
        }
        
        return transformationVariables.toArray(new IVariable[transformationVariables.size()]);
    }

    protected TransformationVariable createTransformationVariable(Object value, String parameterName) {
        if (value == null) {
            TransformationValue eTValue = new TransformationValue(
                    (TransformationDebugTarget) getDebugTarget(), "NULL");
            return new TransformationVariable(
                    (TransformationDebugTarget) getDebugTarget(), parameterName, eTValue);
        } else {
            TransformationValue eTValue = new TransformationValue(
                    (TransformationDebugTarget) getDebugTarget(), value);
            return new TransformationVariable(
                    (TransformationDebugTarget) getDebugTarget(), parameterName, eTValue);
        }
    }

    @Override
    public boolean hasVariables() throws DebugException {
        return !initialized || !transformationVariables.isEmpty();
    }

}
