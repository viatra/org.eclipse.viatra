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

import java.util.List;

/**
 * This interfaces represents a tuple of non-key parameters in a violation of a constraint.
 * It allows access to the list of values and the value of each parameter individually.
 * 
 * @author Balint Lorand
 *
 */
public interface IEntry {

    /**
     * Returns the value of a property with the given name.
     * 
     * @param propertyName
     *            The requested property's name.
     * @return The property value or <code>null</code> if there is no such property.
     */
    public Object getValue(String propertyName);

    /**
     * Returns a List of all property values of the entry.
     * 
     * @return The List of the property values.
     */
    public List<Object> getValues();
}
