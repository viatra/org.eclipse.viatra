/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Objects;

import org.eclipse.viatra.transformation.evm.api.event.ActivationState;

/**
 * A job represents an action that can be executed on an activation
 * if it is in the state defined by the job.
 * 
 * @author Abel Hegedus
 *
 */
public abstract class Job<EventAtom> {

    private ActivationState activationState;
    
    /**
     * @return the activationState
     */
    public ActivationState getActivationState() {
        return activationState;
    }
    
    /**
     * Creates a new job corresponding to the given state.
     */
    protected Job(final ActivationState activationState) {
        this.activationState = Objects.requireNonNull(activationState, "Cannot create job with null activation state!");
    }
    
    /**
     * Executes the action on the activation using the supplied context.
     * 
     * @param activation
     * @param context
     */
    protected abstract void execute(final Activation<? extends EventAtom> activation, final Context context);
    
    /**
     * Called if the {@link #execute} method has thrown an exception to allow jobs to handle their own errors.
     *  
     * @param activation
     * @param exception
     * @param context
     */
    protected abstract void handleError(final Activation<? extends EventAtom> activation, final Exception exception, final Context context);
    
}
