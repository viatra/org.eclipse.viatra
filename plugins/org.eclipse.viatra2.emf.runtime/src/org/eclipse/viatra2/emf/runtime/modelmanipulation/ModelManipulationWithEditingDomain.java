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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class ModelManipulationWithEditingDomain extends AbstractModelManipulations {

	EditingDomain domain;
	IncQueryEngine engine;


	public ModelManipulationWithEditingDomain(EditingDomain domain,
			IncQueryEngine engine) {
		super();
		this.domain = domain;
		this.engine = engine;
	}

	@Override
	protected EObject doCreate(Resource res, EClass clazz)
			throws ModelManipulationException {
		EObject obj = EcoreUtil.create(clazz);
		Command createCommand = AddCommand.create(domain, res, null,
				res.getContents());
		executeCommand(createCommand);
		return obj;
	}

	@Override
	protected EObject doCreate(EObject container, EReference reference,
			EClass clazz) {
		EObject obj = EcoreUtil.create(clazz);
		Command createCommand = AddCommand.create(domain, container, reference,
				obj);
		createCommand.execute();
		return obj;
	}

	@Override
	protected void doAdd(EObject container, EReference reference, Collection<? extends EObject> elements)
			throws ModelManipulationException {
		Command createCommand = AddCommand.create(domain, container, reference,
				elements);
		executeCommand(createCommand);
	}

	@Override
	protected void doAdd(EObject container, EAttribute attribute, Object value)
			throws ModelManipulationException {
		Command createCommand = AddCommand.create(domain, container, attribute,
				value);
		executeCommand(createCommand);
	}

	@Override
	protected void doSet(EObject container, EStructuralFeature feature,
			Object value) throws ModelManipulationException {
		Command setCommand = SetCommand.create(domain, container, feature, value);
		executeCommand(setCommand);
	}

	@Override
	protected void doRemove(EObject object) throws ModelManipulationException {
		Command removeCommand = DeleteCommand.create(domain, object);
		executeCommand(removeCommand);
	}

	@Override
	protected void doRemove(EObject container, EReference reference,
			EObject element) throws ModelManipulationException {
		Command removeCommand = RemoveCommand.create(domain, container,
				reference, element);
		executeCommand(removeCommand);
	}

	@Override
	protected void doRemove(EObject container, EStructuralFeature reference)
			throws ModelManipulationException {
		Collection<?> list = (Collection<?>) container.eGet(reference);
		Command removeCommand = RemoveCommand.create(domain, container, reference, list);
		executeCommand(removeCommand);
	}

	@Override
	protected <Type extends EObject> void doMoveTo(Type what, EList<Type> where)
			throws ModelManipulationException {
		try {
			//TODO command!
			engine.getBaseIndex().cheapMoveTo(what, where);
		} catch (IncQueryException e) {
			throw new ModelManipulationException(e);
		}
	}

	private void executeCommand(Command command)
			throws ModelManipulationException {
		if (command.canExecute()) {
			command.execute();
		}
		throw new ModelManipulationException("Cannot execute command");
	}
}
