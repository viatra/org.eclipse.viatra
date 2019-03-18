/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core.api;

/**
 * Interface for filtering violations when retrieving them from a constraint or registering for event notifications.
 * 
 * @author Balint Lorand
 *
 */
public interface IViolationFilter {

    /**
     * Checks the given violation object whether it passes through the filter.
     * 
     * @param violation
     *            The violation to be checked.
     * @return <code>true</code> if the violation passes the filter.
     */
    boolean apply(IViolation violation);

}
