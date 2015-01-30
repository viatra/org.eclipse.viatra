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
package org.eclipse.viatra.emf.runtime.modelmanipulation;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public interface IModelManipulations {

	/**
	 * Creates an EObject and puts it as the root of the selected resources.
	 */
	EObject create(Resource res, EClass clazz)
			throws ModelManipulationException;

	/**
	 * Creates an EObject and puts it into the selected reference of a
	 * container. The container reference must be a containment reference.
	 */
	EObject createChild(EObject container, EReference reference, EClass clazz)
			throws ModelManipulationException;

	/**
	 * Adds an existing model element to a selected EReference. The feature is
	 * an EReference, it must <em>not</em> be a containment reference.
	 */
	void addTo(EObject container, EStructuralFeature feature, Object element)
			throws ModelManipulationException;

	void addTo(EObject container, EStructuralFeature reference,
			Collection<? extends Object> element)
			throws ModelManipulationException;

	void set(EObject container, EStructuralFeature feature, Object value)
			throws ModelManipulationException;

	/**
	 * Removes an object from the model
	 */
	void remove(EObject object) throws ModelManipulationException;

	/**
	 * Removes an element from the 'many'-valued reference; if the reference is
	 * a containment reference, the element is removed from the model as well.
	 */
	void remove(EObject container, EReference reference, EObject element)
			throws ModelManipulationException;

	/**
	 * Removes all elements from the 'many'-valued reference; if the reference
	 * is a containment reference, the element is removed from the model as
	 * well.
	 */
	void remove(EObject container, EStructuralFeature reference)
			throws ModelManipulationException;

	void moveTo(EObject what, Resource newContainer) throws ModelManipulationException;
	
	void moveTo(EObject what, EObject newContainer, EReference reference)
			throws ModelManipulationException;

	void moveTo(Collection<EObject> what, EObject newContainer, EReference reference)
			throws ModelManipulationException;	
}