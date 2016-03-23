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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public interface IModelManipulations {

    // ************************************* CREATE **************************************
    /**
     * Creates an EObject and puts it as the root of the selected resource.
     */
    EObject create(Resource res, EClass clazz) throws ModelManipulationException;

    /**
     * Creates an EObject and puts it into the selected reference of a
     * container. The container reference must be a containment reference.
     */
    EObject createChild(EObject container, EReference reference, EClass clazz) throws ModelManipulationException;

    // *************************************** ADD ***************************************
    /**
     * Adds an existing element to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void add(EObject container, EStructuralFeature feature, Object element) throws ModelManipulationException;

    /**
     * Adds an existing element to a selected EStructuralFeature at the specified index.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * @since 1.2
     */
    void add(EObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException;

    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void add(EObject container, EStructuralFeature reference, Collection<? extends Object> element)
            throws ModelManipulationException;

    /**
     * Adds an existing element to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void addTo(EObject container, EStructuralFeature feature, Object element) throws ModelManipulationException;

    /**
     * Adds an existing element to a selected EStructuralFeature at the specified index.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * @since 1.2
     */
    void addTo(EObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException;

    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void addTo(EObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException;

    // *************************************** SET ***************************************
    /**
     * Sets an element to a selected 'single'-valued EStructuralFeature.
     */
    void set(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;

    // ************************************* REMOVE **************************************
    /**
     * Removes an object from the model.
     */
    void remove(EObject object) throws ModelManipulationException;

    /**
     * Removes an element from the 'many'-valued reference; if the reference is a containment reference, the element is
     * removed from the model as well.
     */
    void remove(EObject container, EStructuralFeature feature, Object element) throws ModelManipulationException;

    /**
     * Removes the element at the specified index from the 'many'-valued reference;
     * if the reference is a containment reference, the element is removed from the model as well.
     * @since 1.2
     */
    void remove(EObject container, EStructuralFeature feature, int index) throws ModelManipulationException;

    /**
     * Removes all elements from the 'many'-valued reference; if the reference is a containment reference, the element
     * is removed from the model as well.
     */
    void remove(EObject container, EStructuralFeature feature) throws ModelManipulationException;

    // ************************************* MOVE TO *************************************
    /**
     * Moves an existing element into the root of the selected resource.
     */
    void moveTo(EObject what, Resource newContainer) throws ModelManipulationException;

    /**
     * Moves an existing element into the root of the selected resource at the specified index.
     * @since 1.2
     */
    void moveTo(EObject what, Resource newContainer, int index) throws ModelManipulationException;

    /**
     * Moves an existing element into the selected containment reference of the selected EObject.
     */
    void moveTo(EObject what, EObject newContainer, EReference reference) throws ModelManipulationException;

    /**
     * Moves an existing element into the selected containment reference of the selected EObject at the specified index.
     * @since 1.2
     */
    void moveTo(EObject what, EObject newContainer, EReference reference, int index) throws ModelManipulationException;

    /**
     * Moves a collection of existing elements into the selected containment reference of the selected EObject.
     */
    void moveTo(Collection<EObject> what, EObject newContainer, EReference reference) throws ModelManipulationException;

    /**
     * Changes the index of an existing element in the selected feature of the selected EObject.
     * @since 1.2
     */
    void changeIndex(EObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException;
}