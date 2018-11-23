/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Interface for commands manipulating some representation of an instance of an Ecore metamodel. 
 * {@link IModelManipulations} is provided as the default case where the instance model is simply an EMF model. 
 * 
 * <p> Note that not all representations may support / preserve ordered collections.
 * 
 * @param <RootContainer> the type of root containers in which model elements may reside (e.g. a {@link Resource})
 * @param <ModelObject> the type representing a model element; can be simply an {@link EObject} or a surrogate key 
 *  
 * @noextend This interface is not intended to be implemented by clients. 
 *  
 * @author Gabor Bergmann
 * @since 2.1
 */
public interface IEcoreManipulations<RootContainer, ModelObject> {
    


    // ************************************* CREATE **************************************
    /**
     * Creates a model object and puts it as the root of the selected root container.
     */
    ModelObject create(RootContainer res, EClass clazz) throws ModelManipulationException;

    /**
     * Creates a model object and puts it into the selected reference of a
     * container. The container reference must be a containment reference.
     */
    ModelObject createChild(ModelObject container, EReference reference, EClass clazz) throws ModelManipulationException;

    // *************************************** ADD ***************************************
    /**
     * Adds an existing element to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * 
     * <p> Same as {@link #addTo(Object, EStructuralFeature, Object)}
     */
    default void add(ModelObject container, EStructuralFeature feature, Object element) throws ModelManipulationException {
        addTo(container, feature, element);
    }

    /**
     * Adds an existing element to a selected EStructuralFeature at the specified index.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * @since 1.2
     * 
     * <p> Same as {@link #addTo(Object, EStructuralFeature, Object, int)}
     */
    default void add(ModelObject container, EStructuralFeature feature, Object element, int index) 
            throws ModelManipulationException {
        addTo(container, feature, element, index);
    }

    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * 
     * <p> Same as {@link #addAllTo(Object, EStructuralFeature, Collection)}
     */
    default void addAll(ModelObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException {
        addAllTo(container, reference, elements);
    }

    /**
     * Adds an existing element to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void addTo(ModelObject container, EStructuralFeature feature, Object element) throws ModelManipulationException;

    /**
     * Adds an existing element to a selected EStructuralFeature at the specified index.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     * @since 1.2
     */
    void addTo(ModelObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException;

    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void addAllTo(ModelObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException;

    // *************************************** SET ***************************************
    /**
     * Sets an element to a selected 'single'-valued EStructuralFeature.
     * TODO restrict to non-containment?
     */
    void set(ModelObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;

    // ************************************* REMOVE **************************************

    /**
     * Removes an element from the 'many'-valued reference; if the reference is a containment reference, the element is
     * removed from the model as well (it is assumed that no dangling cross-references point to it).
     */
    void remove(ModelObject container, EStructuralFeature feature, Object element) throws ModelManipulationException;

    /**
     * Removes the element at the specified index from the 'many'-valued reference;
     * if the reference is a containment reference, the element is removed from the model as well 
     * (it is assumed that no dangling cross-references point to it).
     * @since 1.2
     */
    void remove(ModelObject container, EStructuralFeature feature, int index) throws ModelManipulationException;

    /**
     * Removes all elements from the 'many'-valued reference; if the reference is a containment reference, the element
     * is removed from the model as well (it is assumed that no dangling cross-references point to it).
     */
    void remove(ModelObject container, EStructuralFeature feature) throws ModelManipulationException;
    
    /**
     * Removes an object from the model, along with all contained objects, and any incoming or outgoing references.
     */
    void remove(ModelObject object) throws ModelManipulationException;

    // ************************************* MOVE TO *************************************
    /**
     * Moves an existing element into the root of the selected root container.
     */
    void moveTo(ModelObject what, RootContainer newContainer) throws ModelManipulationException;

    /**
     * Moves an existing element into the root of the selected root container at the specified index.
     * @since 1.2
     */
    void moveTo(ModelObject what, RootContainer newContainer, int index) throws ModelManipulationException;

    /**
     * Moves an existing element into the selected containment reference of the selected model object.
     */
    void moveTo(ModelObject what, ModelObject newContainer, EReference reference) throws ModelManipulationException;

    /**
     * Moves an existing element into the selected containment reference of the selected model object at the specified index.
     * @since 1.2
     */
    void moveTo(ModelObject what, ModelObject newContainer, EReference reference, int index) throws ModelManipulationException;

    /**
     * Moves a collection of existing elements into the selected containment reference of the selected model object.
     */
    void moveAllTo(Collection<ModelObject> what, ModelObject newContainer, EReference reference) throws ModelManipulationException;

    /**
     * Changes the position of an existing element in the selected feature of the selected model object.
     * @since 1.2
     */
    void changeIndex(ModelObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException;
    
}
