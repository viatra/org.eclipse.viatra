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
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Abstract base class for model manipulation implementation. It checks for the
 * preconditions of the operations, and the subclasses should override the do*
 * methods.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public abstract class AbstractModelManipulations implements IModelManipulations {

	protected final IncQueryEngine engine;
	private NavigationHelper baseEMFIndex;


	public AbstractModelManipulations(IncQueryEngine engine) {
		super();
		this.engine = engine;
	}
	
	protected NavigationHelper getBaseEMFIndex() throws IncQueryException {
		if (baseEMFIndex == null) {
			baseEMFIndex = EMFScope.extractUnderlyingEMFIndex(engine);
		}
		return baseEMFIndex;
	}

	protected void doMoveTo(Collection<EObject> what, EObject newContainer, EReference reference) throws ModelManipulationException {
		for (EObject obj : what) {
			doMoveTo(obj, newContainer, reference);
		}
	}

	protected abstract void doMoveTo(EObject what, Resource newContainer) throws ModelManipulationException;
	protected abstract void doMoveTo(EObject what, EObject newContainer, EReference reference) throws ModelManipulationException;

	/**
	 * Remove a non-containment reference value
	 */
	protected abstract void doRemove(EObject container, EReference reference,
			EObject element) throws ModelManipulationException;

	/**
	 * Removes an element from the containment hierarchy 
	 */
	protected abstract void doRemove(EObject object)
			throws ModelManipulationException;
	
	protected abstract void doRemove(EObject container, EStructuralFeature reference) throws ModelManipulationException;

	protected abstract void doAdd(EObject container, EStructuralFeature reference,
			Collection<? extends Object> element) throws ModelManipulationException;

	protected abstract void doSet(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;
	
	protected abstract EObject doCreate(EObject container,
			EReference reference, EClass clazz) throws ModelManipulationException;

	protected abstract EObject doCreate(Resource res, EClass clazz)
			throws ModelManipulationException;

	@Override
	public EObject create(Resource res, EClass clazz)
			throws ModelManipulationException {
		return doCreate(res, clazz);
	}
	
	@Override
	public EObject createChild(EObject container, EReference reference, EClass clazz) throws ModelManipulationException {
		EClass containerClass = container.eClass();
		Preconditions
		.checkArgument(
				!(containerClass.getEAllReferences().contains(container)),
				"The container of EClass %s does neither define or inherit an EReference %s.",
				containerClass.getName(), reference.getName());
		Preconditions
		.checkArgument(reference.isContainment(),
				"Created elements must be inserted directly into the containment hierarchy.");
		Preconditions.checkArgument(!clazz.isAbstract(), "Cannot instantiate abstract EClass %s.", clazz.getName());
		
		return doCreate(container, reference, clazz);
	}

	@Override
	public void addTo(EObject container, EStructuralFeature feature, Object element)
			throws ModelManipulationException {
		
		addTo(container, feature, ImmutableList.of(element));
	}

	@Override
	public void addTo(EObject container, EStructuralFeature feature, Collection<? extends Object> elements)
			throws ModelManipulationException {
		EClass containerClass = container.eClass();
		Preconditions
				.checkArgument(
						!(containerClass.getEAllStructuralFeatures().contains(container)),
						"The container of EClass %s does neither define or inherit an EReference or EAttribute named %s.",
						containerClass.getName(), feature.getName());
		Preconditions.checkArgument(feature.isMany(),
				"The EAttribute %s must have an upper bound larger than 1.",
				feature.getName());
		Preconditions
				.checkArgument(
						!(feature instanceof EReference && ((EReference)feature).isContainment()),
						"Adding existing elements into the containment reference %s is not supported.",
						feature.getName());
		doAdd(container, feature, elements);	
	}
	
	@Override
	public void set(EObject container, EStructuralFeature feature, Object value)
			throws ModelManipulationException {
		EClass containerClass = container.eClass();
		Preconditions
				.checkArgument(
						containerClass.getEAllStructuralFeatures().contains(feature),
						"The container of EClass %s does neither define or inherit an EAttribute or EReference %s.",
						containerClass.getName(), feature.getName());
		Preconditions.checkArgument(!feature.isMany(), "The feature %s must have an upper bound of 1.", feature.getName());
		doSet(container, feature, value);
	}

	@Override
	public void remove(EObject object) throws ModelManipulationException {
		doRemove(object);
	}

	@Override
	public void remove(EObject container, EReference reference, EObject element)
			throws ModelManipulationException {
		Preconditions.checkArgument(reference.isMany(), "Remove only works on references with 'many' multiplicity.");
		if (reference.isContainment()) {
			doRemove(element);
		} else {
			doRemove(container, reference, element);
		}
	}

	@Override
	public void remove(EObject container, EStructuralFeature reference)
			throws ModelManipulationException {
		Preconditions.checkArgument(reference.isMany(), "Remove only works on references with 'many' multiplicity.");
		doRemove(container, reference);
	}

	@Override
	public void moveTo(EObject what, Resource newContainer) throws ModelManipulationException {
		doMoveTo(what, newContainer);
	}
	
	@Override
	public void moveTo(EObject what, EObject newContainer, EReference reference)
			throws ModelManipulationException {
		doMoveTo(what, newContainer, reference);
	}

	@Override
	public void moveTo(Collection<EObject> what, EObject newContainer, EReference reference) throws ModelManipulationException {
		doMoveTo(what, newContainer, reference);
	}

}