/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core.listeners;

import org.eclipse.viatra.addon.validation.core.api.IEntry;
import org.eclipse.viatra.addon.validation.core.api.IViolation;

/**
 * Interface for listening for notifications on specific events regarding a violation.
 * 
 * @author Balint Lorand
 *
 */
public interface ViolationListener {

    /**
     * Called if a new entry has appeared for the violation on which the listener is registered.
     * 
     * @param violation
     *            The violation for which the new entry appeared.
     * @param entry
     *            The new entry which appeared.
     */
    public void violationEntryAppeared(IViolation violation, IEntry entry);

    /**
     * Called if the message has been updated for the violation on which the listener is registered.
     * 
     * @param violation
     *            The violation for which the messaged has been updated.
     */
    public void violationMessageUpdated(IViolation violation);

    /**
     * Called if an entry has disappeared for the violation on which the listener is registered.
     * 
     * @param violation
     *            The violation for which the entry disappeared.
     * @param entry
     *            The entry which disappeared.
     */
    public void violationEntryDisappeared(IViolation violation, IEntry entry);

}
