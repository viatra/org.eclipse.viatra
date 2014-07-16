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

package org.eclipse.incquery.validation.core.api;

import java.util.Collection;

import org.eclipse.incquery.validation.core.listeners.ConstraintListener;

/**
 * Interface for a constraint.
 * <p>
 * We differentiate between Constraint Specification that represents the validation rule and Constraint that represents
 * the instantiation of a constraint specification on a validation engine.
 * <p>
 * Each constraint stores:
 * <ul>
 * <li>its specification
 * <li>validation engine
 * </ul>
 * <p>
 * It provides capabilities for:
 * <ul>
 * <li>listing the set of violations
 * <li>registering listeners for notifications on the changes in the violation set and other
 * </ul>
 * events related to the life cycle of the constraint.
 * <p>
 * For constraints specified by EMF-IncQuery patterns, the matcher is stored.
 * 
 * @author Balint Lorand
 *
 */
public interface IConstraint {

    /**
     * Returns the violations retrieved from the validation engine on demand.
     * 
     * @return The Collection of violations.
     */
    public Collection<IViolation> listViolations();

    /**
     * Returns the violations retrieved from the validation engine on demand and filtered by the provided violation
     * filter.
     * 
     * @param filter
     *            The violation filter instance to be used to filter the retrieved violations.
     * @return The Collection of violations after applying the filter.
     */
    public Collection<IViolation> listViolations(IViolationFilter filter);

    /**
     * Returns the violations stored by the constraint.
     * <p>
     * Violations are stored if they have at least one listener registered.
     * 
     * @return The Collection of stored Violations.
     */
    public Collection<IViolation> getStoredViolations();

    /**
     * Returns the constraint specification from which the constraint was instantiated on the validation engine.
     * 
     * @return The constraint specification.
     */
    public IConstraintSpecification getSpecification();

    /**
     * Returns the listeners registered for the constraint.
     * 
     * @return The Collection of listeners registered for the constraint.
     */
    public Collection<ConstraintListener> getListeners();

    /**
     * Adds the given listener to the list of listeners to be notified on specific events regarding the constraint.
     * 
     * @param listener
     *            The listener to be registered.
     */
    public boolean addListener(ConstraintListener listener);

    /**
     * Adds the given listener to the list of listeners to be notified on specific events regarding the constraint.
     * 
     * @param listener
     *            The listener to be registered.
     * @return <code>true</code> if the listener was not registered before.
     * @param filter
     * 
     */
    public boolean addListener(ConstraintListener listener, IViolationFilter filter);

    /**
     * Removes the given listener from the list of listeners to be notified on specific events regarding the constraint.
     * 
     * @param listener
     *            The listener to be deregistered.
     * @return <code>true</code> if the listener was in fact registered.
     */
    public boolean removeListener(ConstraintListener listener);

}
