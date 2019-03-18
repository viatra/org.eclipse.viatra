/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class TransformationModelBuilder {
    private Map<TransformationModelElement, EObject> elementMap = new HashMap<>();

    public synchronized void reset() {
        elementMap.clear();
    }

    public synchronized TransformationModelElement getTransformationElement(EObject eobject) {
        for (Entry<TransformationModelElement, EObject> elementEntry : elementMap.entrySet()) {
            if (Objects.equals(elementEntry.getValue(), eobject)) {
                return elementEntry.getKey();
            }
        }
        
        return createTransformationElement(eobject);
    }

    private TransformationModelElement createTransformationElement(EObject eobject) {
        TransformationModelElement element = new TransformationModelElement();
        for (EAttribute attribute : eobject.eClass().getEAllAttributes()) {
            Object eGet = eobject.eGet(attribute);
            if (eGet != null && !attribute.getName().equals(TransformationModelElement.TYPE_ATTR)) {
                element.addAttribute(attribute.getName(), eobject.eGet(attribute).toString());
            }
        }
        element.addAttribute(TransformationModelElement.TYPE_ATTR, eobject.eClass().getName());
        elementMap.put(element, eobject);
        return element;
    }

    public synchronized Map<String, List<TransformationModelElement>> createChildElements(TransformationModelElement element) {
        EObject eobject = getEObject(element);
        if (eobject != null) {
            for (EReference reference : eobject.eClass().getEReferences()) {
                if (reference.isContainment()) {
                    Object eGet = eobject.eGet(reference);
                    if (eGet instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<EObject> referenceList = new ArrayList<>(((List<EObject>) eGet));
                        for (EObject object : referenceList) {
                            element.addContainedElement(reference.getName(), getTransformationElement(object));
                        }
                        if(referenceList.isEmpty()){
                            element.addEmptyContainment(reference.getName());
                        }
                    } else if (eGet instanceof EObject) {
                        element.addContainedElement(reference.getName(), getTransformationElement((EObject) eGet));
                    } else {
                        element.addEmptyContainment(reference.getName());
                    }
                }
            }
        }
        return element.getContainments();
    }

    public synchronized Map<String, List<TransformationModelElement>> createCrossReferenceElements(
            TransformationModelElement element) {
        EObject eobject = getEObject(element);
        if (eobject != null) {
            for (EReference reference : eobject.eClass().getEReferences()) {
                if (!reference.isContainment()) {
                    Object eGet = eobject.eGet(reference);
                    if (eGet instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<EObject> referenceList = new ArrayList<>(((List<EObject>) eGet));
                        for (EObject object : referenceList) {
                            element.addCrossReference(reference.getName(), getTransformationElement(object));
                        }
                        if(referenceList.isEmpty()){
                            element.addEmptyContainment(reference.getName());
                        }
                    } else if (eGet instanceof EObject) {
                        element.addCrossReference(reference.getName(), getTransformationElement((EObject) eGet));
                    } else {
                        element.addEmptyCrossReference(reference.getName());
                    }
                }
            }
        }
        return element.getCrossReferences();
    }

    private EObject getEObject(TransformationModelElement element) {
        for (Entry<TransformationModelElement, EObject> elementEntry : elementMap.entrySet()) {
            if (Objects.equals(elementEntry.getKey().getId(), element.getId())) {
                return elementEntry.getValue();
            }
        }
        return null;
    }

}
