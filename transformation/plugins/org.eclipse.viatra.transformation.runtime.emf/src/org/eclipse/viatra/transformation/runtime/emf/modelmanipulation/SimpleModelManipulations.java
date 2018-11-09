/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public class SimpleModelManipulations extends AbstractModelManipulations {

    public SimpleModelManipulations(ViatraQueryEngine engine) {
        super(engine);
    }

    
    @Override
    protected int doCount(EObject container, EStructuralFeature feature) {
        return getSlotValuesInternal(container, feature).size();
    }
    
    @Override
    protected Stream<? extends Object> doStream(EObject container, EStructuralFeature feature) {
        return getSlotValuesInternal(container, feature).stream();
    }

    
    @Override
    protected boolean doIsSetTo(EObject container, EStructuralFeature feature, Object value) {
        return getSlotValuesInternal(container, feature).contains(value);
    }
    
    @Override
    protected EObject doCreate(Resource res, EClass clazz) throws ModelManipulationException {
        EObject obj = EcoreUtil.create(clazz);
        res.getContents().add(obj);
        return obj;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected EObject doCreate(EObject container, EReference reference, EClass clazz) {
        EObject obj = EcoreUtil.create(clazz);
        if (reference.isMany()) {
            ((EList) container.eGet(reference)).add(obj);
        } else {
            container.eSet(reference, obj);
        }
        return obj;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void doAdd(EObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException {
        ((EList) container.eGet(reference)).addAll(elements);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void doAdd(EObject container, EStructuralFeature reference, Object what, int index)
            throws ModelManipulationException {
        ((EList) container.eGet(reference)).add(index, what);
    }

    @Override
    protected void doSet(EObject container, EStructuralFeature feature, Object value) {
        container.eSet(feature, value);
    }

    @Override
    protected void doRemove(EObject object) throws ModelManipulationException {
        EcoreUtil.remove(object);
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature reference) throws ModelManipulationException {
        List<?> list = (List<?>) container.eGet(reference);
        list.clear();
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException {
        ((EList<?>) container.eGet(feature)).remove(element);
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature feature, int index)
            throws ModelManipulationException {
        ((EList<?>)container.eGet(feature)).remove(index);
    }

    @Override
    protected void doMoveTo(EObject what, EObject newContainer, EReference reference)
            throws ModelManipulationException {
        try {
            getBaseEMFIndex().cheapMoveTo(what, newContainer, reference);
        } catch (ViatraQueryException e) {
            throw new ModelManipulationException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void doMoveTo(EObject what, EObject newContainer, EReference reference, int index)
            throws ModelManipulationException {
        if (reference.isMany()) {
            ((EList)newContainer.eGet(reference)).add(index, what);
        } else{
            newContainer.eSet(reference, what);
        }
    }

    @Override
    protected void doMoveTo(EObject what, Resource newContainer) throws ModelManipulationException {
        try {
            getBaseEMFIndex().cheapMoveTo(what, newContainer.getContents());
        } catch (ViatraQueryException e) {
            throw new ModelManipulationException(e);
        }
    }

    @Override
    protected void doMoveTo(EObject what, Resource newContainer, int index) throws ModelManipulationException {
        newContainer.getContents().add(index, what);
    }

    @Override
    protected void doChangeIndex(EObject container, EStructuralFeature feature, int oldIndex, int newIndex) {
        EList featureValue = (EList)container.eGet(feature);
        featureValue.move(newIndex, oldIndex);
    }
}
