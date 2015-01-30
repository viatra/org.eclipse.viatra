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
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class SimpleModelManipulations extends AbstractModelManipulations{

	public SimpleModelManipulations(IncQueryEngine engine) {
		super(engine);
	}

	@Override
	protected EObject doCreate(Resource res, EClass clazz)
			throws ModelManipulationException {
		EObject obj = EcoreUtil.create(clazz);
		res.getContents().add(obj);
		return obj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected EObject doCreate(EObject container, EReference reference,
			EClass clazz) {
		EObject obj = EcoreUtil.create(clazz);
		if (reference.isMany()) {
			((EList)container.eGet(reference)).add(obj);
		} else {
			container.eSet(reference, obj);
		}
		return obj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doAdd(EObject container, EStructuralFeature reference,
			Collection<? extends Object> elements) throws ModelManipulationException {
		((EList)container.eGet(reference)).addAll(elements);
	}
	
	@Override
	protected void doSet(EObject container, EStructuralFeature feature,
			Object value) {
		container.eSet(feature, value);
	}

	@Override
	protected void doRemove(EObject object) throws ModelManipulationException {
		EcoreUtil.remove(object);
	}

	@Override
	protected void doRemove(EObject container, EStructuralFeature reference)
			throws ModelManipulationException {
		List<?> list = (List<?>) container.eGet(reference);
		list.clear();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void doRemove(EObject container, EReference reference,
			EObject element) throws ModelManipulationException {
		((EList)container.eGet(reference)).remove(element);
		
	}

	@Override
	protected void doMoveTo(EObject what, EObject newContainer, EReference reference)
			throws ModelManipulationException {
		try {
			getBaseEMFIndex().cheapMoveTo(what, newContainer, reference);
		} catch (IncQueryException e) {
			throw new ModelManipulationException(e);
		}
	}

	@Override
	protected void doMoveTo(EObject what, Resource newContainer) throws ModelManipulationException {
		try {
			getBaseEMFIndex().cheapMoveTo(what, newContainer.getContents());
		} catch (IncQueryException e) {
			throw new ModelManipulationException(e);
		}
	}
}
