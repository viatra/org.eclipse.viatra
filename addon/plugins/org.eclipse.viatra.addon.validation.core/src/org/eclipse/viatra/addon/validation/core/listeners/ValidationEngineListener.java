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

package org.eclipse.viatra.addon.validation.core.listeners;

import org.eclipse.viatra.addon.validation.core.api.IConstraint;

/**
 * Interface for listening for notifications on specific events regarding a validation engine.
 * 
 * @author Balint Lorand
 *
 */
public interface ValidationEngineListener {

    /**
     * Called if a new constraint has been registered on the validation engine on which the listener is registered.
     * 
     * @param violation
     *            The constraint which has been registered.
     */
    public void constraintRegistered(IConstraint constraint);

    /**
     * Called if a constraint has been deregistered on the validation engine on which the listener is registered.
     * 
     * @param violation
     *            The constraint which has been deregistered.
     */
    public void constraintDeregistered(IConstraint constraint);

}
