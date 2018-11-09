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
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public class ModelManipulationWithEditingDomain extends AbstractModelManipulations {

    EditingDomain domain;
    
    private abstract class ReadCommand extends AbstractCommand {
        Collection<Object> result = Collections.emptySet();

        @Override
        public void redo() {
            execute();
        }
        
        @Override
        public void undo() {
            // NO-OP
        }
        
        @Override
        public Collection<?> getResult() {
            return result;
        }

    }
    private class ReadSlotCommand extends ReadCommand {
        EObject container; 
        EStructuralFeature feature;
        Function<Collection<Object>, Object> extractResult;
        
        
        protected ReadSlotCommand(EObject container, EStructuralFeature feature, Function<Collection<Object>, Object> extractResult) {
            super();
            this.container = container;
            this.feature = feature;
            this.extractResult = extractResult;
        }
        
        @Override
        public Collection<?> getAffectedObjects() {
            return Collections.singleton(container);
        }

        @Override
        public void execute() {
            Collection<Object> slotValues = getSlotValuesInternal(container, feature);
            Object resultValue = extractResult.apply(slotValues);
            result = Collections.singleton(resultValue);
        }
        
    }

    private class MoveEObjectCommand extends AddCommand {

        public MoveEObjectCommand(EditingDomain domain, EList<?> list, Object value) {
            super(domain, list, value);
        }

        public MoveEObjectCommand(EditingDomain domain, EList<?> list, Object value, int index) {
            super(domain, list, value, index);
        }

        public MoveEObjectCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Object value) {
            super(domain, owner, feature, value);
        }

        public MoveEObjectCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Object value,
                int index) {
            super(domain, owner, feature, value, index);
        }

        @Override
        public void doExecute() {
            if (this.index == CommandParameter.NO_INDEX) {
                try {
                    for (Object obj : collection) {
                        getBaseEMFIndex().cheapMoveTo((EObject) obj, owner, (EReference) feature);
                    }
                } catch (ViatraQueryException e) {
                    throw new WrappedException(new ModelManipulationException(e));
                }
            } else {
                ((EList) owner.eGet(feature)).addAll(index, collection);
            }
        }

        @Override
        public void doUndo() {
            throw new UnsupportedOperationException("Undoing VIATRA move is not supported.");
        }

    }

    private class RemoveFromIndexCommand extends RemoveCommand {
        int index = CommandParameter.NO_INDEX;

        public RemoveFromIndexCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, int index) {
            super(domain, owner, feature, ((EList)owner.eGet(feature)).get(index));
            this.index = index;
        }

        @Override
        public void doExecute() {
            if (this.index == CommandParameter.NO_INDEX) {
                throw new IllegalArgumentException("Index is not specified");
            } else {
                ((EList) owner.eGet(feature)).remove(index);
            }
        }

        @Override
        public void doUndo() {
            throw new UnsupportedOperationException("Undoing remove index is not supported.");
        }
    }

    private class ChangeIndexCommand extends AbstractOverrideableCommand {
        EObject owner;
        EStructuralFeature feature;
        int oldIndex = CommandParameter.NO_INDEX;
        int newIndex = CommandParameter.NO_INDEX;
        
        public ChangeIndexCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, int oldIndex, int newIndex) {
            super(domain);
            this.owner = owner;
            this.feature = feature;
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
        }
        
        @Override
        public void doExecute() {
            EList featureValue = (EList)owner.eGet(feature);
            featureValue.move(newIndex, oldIndex);
        }
        
        @Override
        public boolean doCanExecute() {
            if (this.oldIndex == CommandParameter.NO_INDEX || 
                    this.newIndex == CommandParameter.NO_INDEX ||
                    this.feature == null ||
                    this.owner == null) {
                return false;
            }
            return true;
        }
        
        @Override
        public void doUndo() {
            throw new UnsupportedOperationException("Undoing index change is not supported.");
        }

        @Override
        public void doRedo() {
            throw new UnsupportedOperationException("Redoing index change is not supported.");
        }
    }
    
    public ModelManipulationWithEditingDomain(ViatraQueryEngine engine, EditingDomain domain) {
        super(engine);
        this.domain = domain;
    }
    
    @Override
    protected int doCount(EObject container, EStructuralFeature feature) throws ModelManipulationException {
        Command command = new ReadSlotCommand(container, feature, (slotValues) -> slotValues.size());
        executeCommand(command);
        return (Integer) command.getResult().iterator().next();
    }
    @Override
    protected Stream<? extends Object> doStream(EObject container, EStructuralFeature feature) throws ModelManipulationException {
        Command command = new ReadSlotCommand(container, feature, (slotValues) -> slotValues.stream());
        executeCommand(command);
        return (Stream<? extends Object>) command.getResult().iterator().next();
    }
    @Override
    protected boolean doIsSetTo(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException {
        Command command = new ReadSlotCommand(container, feature, (slotValues) -> slotValues.contains(value));
        executeCommand(command);
        return (Boolean) command.getResult().iterator().next();
    }
    
    @Override
    protected EObject doCreate(Resource res, EClass clazz) throws ModelManipulationException {
        EObject obj = EcoreUtil.create(clazz);
        Command createCommand = new AddCommand(domain, res.getContents(), obj);
        executeCommand(createCommand);
        return obj;
    }

    @Override
    protected EObject doCreate(EObject container, EReference reference, EClass clazz)
            throws ModelManipulationException {
        EObject obj = EcoreUtil.create(clazz);
        Command command;
        if(reference.isMany()) {
            command = AddCommand.create(domain, container, reference, obj);
        } else {
            command = SetCommand.create(domain, container, reference, obj);
        }
        executeCommand(command);
        return obj;
    }

    @Override
    protected void doAdd(EObject container, EStructuralFeature feature, Collection<? extends Object> elements)
            throws ModelManipulationException {
        Command addCommand = AddCommand.create(domain, container, feature, elements);
        executeCommand(addCommand);
    }

    @Override
    protected void doAdd(EObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException {
        Command addCommand = AddCommand.create(domain, container, feature, element, index);
        executeCommand(addCommand);
    }

    @Override
    protected void doSet(EObject container, EStructuralFeature feature, Object value)
            throws ModelManipulationException {
        Command setCommand = SetCommand.create(domain, container, feature, value);
        executeCommand(setCommand);
    }

    @Override
    protected void doRemove(EObject object) throws ModelManipulationException {
        Command removeCommand = null;
        if (object.eContainer() != null) {
            /*
             * We must explicitly use container and feature to make this work in non-OSGi environment, otherwise the
             * command creation tries to find the parent using item providers that are registered by extension points
             * (or manually be the user)
             */
            removeCommand = RemoveCommand.create(domain, object.eContainer(), object.eContainingFeature(), object);
        } else {
            // DeleteCommand cannot deal with root objects
            removeCommand = new RemoveCommand(domain, object.eResource().getContents(), object);
        }
        executeCommand(removeCommand);
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException {
        Command removeCommand = RemoveCommand.create(domain, container, feature, element);
        // Do not throw exception for incorrect remove to ensure identical behavior to simple implementation
        if (removeCommand.canExecute()) {
            executeCommand(removeCommand);
        }
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature feature, int index)
            throws ModelManipulationException {
        Command removeCommand = new RemoveFromIndexCommand(domain, container, feature, index);
        executeCommand(removeCommand);
    }

    @Override
    protected void doRemove(EObject container, EStructuralFeature reference) throws ModelManipulationException {
        Collection<?> list = (Collection<?>) container.eGet(reference);
        Command removeCommand = RemoveCommand.create(domain, container, reference, list);
        executeCommand(removeCommand);
    }

    @Override
    protected void doMoveTo(EObject what, Resource newContainer) throws ModelManipulationException {
        MoveEObjectCommand moveCommand = new MoveEObjectCommand(domain, newContainer.getContents(), what);
        executeCommand(moveCommand);
    }

    @Override
    protected void doMoveTo(EObject what, EObject newContainer, EReference reference, int index)
            throws ModelManipulationException {
        MoveEObjectCommand moveCommand = new MoveEObjectCommand(domain, newContainer, reference, what, index);
        executeCommand(moveCommand);
    }

    @Override
    protected void doMoveTo(EObject what, EObject newContainer, EReference reference)
            throws ModelManipulationException {
        MoveEObjectCommand moveCommand = new MoveEObjectCommand(domain, newContainer, reference, what);
        executeCommand(moveCommand);
    }

    @Override
    protected void doMoveTo(EObject what, Resource newContainer, int index) throws ModelManipulationException {
        MoveEObjectCommand moveCommand = new MoveEObjectCommand(domain, newContainer.getContents(), what, index);
        executeCommand(moveCommand);
    }

    protected void executeCommand(Command command) throws ModelManipulationException {
        if (command.canExecute()) {
            domain.getCommandStack().execute(command);
        } else {
            throw new ModelManipulationException("Cannot execute command");
        }
    }

    @Override
    protected void doChangeIndex(EObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException {
        Command changeIndexCommand = new ChangeIndexCommand(domain, container, feature, oldIndex, newIndex);
        executeCommand(changeIndexCommand);
    }
}
