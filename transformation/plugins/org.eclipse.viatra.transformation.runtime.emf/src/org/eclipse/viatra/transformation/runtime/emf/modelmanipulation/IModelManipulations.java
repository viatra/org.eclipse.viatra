/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Model manipulation interface for conventional EMF instance models.
 *
 * @noimplement API may be extended in the future.
 */
public interface IModelManipulations extends IEcoreManipulations<Resource, EObject> {

    // *************************************** ADD ***************************************
    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    default void add(EObject container, EStructuralFeature reference, Collection<? extends Object> elements) 
            throws ModelManipulationException {
        addTo(container, reference, elements);
    }
    
    /**
     * Adds a collection of existing elements to a selected EStructuralFeature.
     * If the feature is an EReference, it must <em>not</em> be a containment reference.
     */
    void addTo(EObject container, EStructuralFeature reference, Collection<? extends Object> elements) throws ModelManipulationException;
    
    /**
     * @since 2.1
     */
    @Override
    default void addAllTo(EObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException {
        addTo(container, reference, elements);
    }
    
    
    // ************************************* MOVE TO *************************************
    
    /**
     * Moves a collection of existing elements into the selected containment reference of the selected model object.
     * @since 2.1
     */
    void moveTo(Collection<EObject> what, EObject newContainer, EReference reference) throws ModelManipulationException;
    
    /**
     * @since 2.1
     */
    @Override
    default void moveAllTo(Collection<EObject> what, EObject newContainer, EReference reference)
            throws ModelManipulationException {
        moveTo(what, newContainer, reference);
    }

}