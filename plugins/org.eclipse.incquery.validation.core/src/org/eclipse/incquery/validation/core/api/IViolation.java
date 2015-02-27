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
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.validation.core.listeners.ViolationListener;

/**
 * A violation is set of model elements in an instance model that satisfy the specification of a constraint. E.g. for
 * the above constraint, a violation is a port P which is terminated and a port connection PC with "PC.end = P".
 * <p>
 * Each violation has:
 * <ul>
 * <li>a corresponding constraint
 * <li>a key (one or more model elements that are relevant for the violation (e.g. the port and the port connection in
 * the example)
 * <li>a formatted message.
 * </ul>
 * <p>
 * The violation should provide capabilities for
 * <ul>
 * <li>registering listeners for notifications on life cycle events, e.g. a change in the message.
 * </ul>
 * For violation of constraints from EMF-IncQuery patterns, the match is also stored.
 * 
 * @author Balint Lorand
 *
 */
public interface IViolation {

    /**
     * Returns the constraint for which the violation appeared.
     * 
     * @return The constraint.
     */
    public IConstraint getConstraint();

    /**
     * Returns the message of the violation generated from the format message of the constraint specification and the
     * key objects.
     * 
     * @return The message.
     */
    public String getMessage();

    /**
     * Returns the key objects Map of the violation, which serves as a unique key of the violation.
     * 
     * @return The key objects Map, containing the parameters names and values as key value pairs.
     */
    public Map<String, Object> getKeyObjects();

    /**
     * Returns the entries for each pattern match witch corresponds to the violation.
     * 
     * @return The Set of entries, each representing a pattern match for the violation.
     */
    public Set<IEntry> getEntries();

    /**
     * Returns all the values from the violation's entries for the given property.
     * 
     * @param propertyName
     *            The property's name for which the values should be returned.
     * @return The Set of values for the requested property.
     */
    public Set<Object> getValuesOfProperty(String propertyName);

    /**
     * Returns the listeners registered for the violation.
     * 
     * @return The Collection of listeners registered for the violation.
     */
    public Collection<ViolationListener> getListeners();

    /**
     * Adds the given listener to the list of listeners to be notified on specific events regarding the violation.
     * 
     * @param listener
     *            The listener to be registered.
     */
    public boolean addListener(ViolationListener listener);

    /**
     * Removes the given listener from the list of listeners to be notified on specific events regarding the violation.
     * 
     * @param listener
     *            The listener to be deregistered.
     * @return <code>true</code> if the listener was in fact registered.
     */
    public boolean removeListener(ViolationListener listener);

}
