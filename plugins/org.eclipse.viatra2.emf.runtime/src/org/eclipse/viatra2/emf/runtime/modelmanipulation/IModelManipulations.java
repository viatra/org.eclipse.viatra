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
package org.eclipse.viatra2.emf.runtime.modelmanipulation;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public interface IModelManipulations {

	/**
	 * Creates an EObject and puts it as the root of the selected resources.
	 */
	EObject create(Resource res, EClass clazz) throws ModelManipulationException;
	
	/**
	 * Creates an EObject and puts it into the selected reference of a container. The container reference must be a containment reference.
	 */
	EObject create(EObject container, EReference reference) throws ModelManipulationException;
	
	EObject create(EObject container, EReference reference, EClass clazz) throws ModelManipulationException;

	/**
	 * Adds an existing model element to a selected EReference. The reference must <em>not</em> be a containment reference.
	 */
	void add(EObject container, EReference reference,
			EObject element) throws ModelManipulationException;
	
	void add(EObject container, EReference reference,
			Collection<? extends EObject> element) throws ModelManipulationException;
	/**
	 * Adds a selected value to the list of attributes 
	 */
	void add(EObject container, EAttribute attribute,
			Object value) throws ModelManipulationException;

	void set(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;
	/**
	 * Removes an object from the model
	 */
	void remove(EObject object)	throws ModelManipulationException;

	/**
	 * Removes an element from the 'many'-valued reference; if the reference is a containment reference, the element is removed from the model as well.
	 */
	void remove(EObject container, EReference reference,
			EObject element) throws ModelManipulationException;
	
	/**
	 * Removes all elements from the 'many'-valued reference; if the reference is a containment reference, the element is removed from the model as well.
	 */
	void remove(EObject container, EStructuralFeature reference) throws ModelManipulationException;

	<Type extends EObject> void moveTo(Type what,
			EList<Type> where) throws ModelManipulationException;

	<Type extends EObject> void moveTo(Collection<Type> what,
			EList<Type> where) throws ModelManipulationException;

}