/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core.api;

import java.util.Collection;

import org.eclipse.viatra.addon.validation.core.listeners.ValidationEngineListener;

/**
 * A validation engine is responsible for managing the constraints existing in the scope of an VIATRA Query Engine (e.g.
 * resource set) for a set of constraint specifications added to the validation engine.
 * <p>
 * The validation engine provides capabilities for:
 * <ul>
 * <li>adding and removing constraint specifications
 * <li>listing the set of constraints
 * <li>registering listeners for notifications on the changes in the constraint set and other events related to the life
 * cycle of the validation engine.
 * </ul>
 * 
 * @author Balint Lorand
 *
 */
public interface IValidationEngine {

    /**
     * Initializes the validation engine.
     */
    public void initialize();

    /**
     * Disposes the validation engine.
     */
    public void dispose();

    /**
     * Returns the constraints to the registered constraint specifications.
     * 
     * @return The Collection of constraints in the validation engine.
     */
    public Collection<IConstraint> getConstraints();

    /**
     * Adds the given constraint specification to the validation engine and returns the created constraint.
     * 
     * @param constraintSpecification
     *            The constraint specification instance to be registered.
     * @return The created corresponding constraint instance.
     */
    public IConstraint addConstraintSpecification(IConstraintSpecification constraintSpecification);

    /**
     * Removes the given constraint specification from the validation engine and returns the corresponding constraint.
     * 
     * @param constraintSpecification
     *            The constraint specification to be deregistered.
     * @return The removed corresponding constraint instance.
     */
    public IConstraint removeConstraintSpecification(IConstraintSpecification constraintSpecification);

    /**
     * Returns the listeners registered for the validation engine.
     * 
     * @return The Collection of listeners registered for the validation engine.
     */
    public Collection<ValidationEngineListener> getListeners();

    /**
     * Adds the given listener to the list of listeners to be notified on specific events regarding the validation
     * engine.
     * 
     * @param listener
     *            The listener to be registered.
     * @return <code>true</code> if the listener was not registered before.
     */
    public boolean addListener(ValidationEngineListener listener);

    /**
     * Removes the given listener from the list of listeners to be notified on specific events regarding the validation
     * engine.
     * 
     * @param listener
     *            The listener to be deregistered.
     * @return <code>true</code> if the listener was in fact registered.
     */
    public boolean removeListener(ValidationEngineListener listener);

}
