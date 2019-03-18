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

/**
 * @author Abel Hegedus
 *
 */
public class CompositeJob<EventAtom> extends Job<EventAtom> {

    private Job<EventAtom> containedJob;
    
    protected CompositeJob(Job<EventAtom> job) {
        super(Objects.requireNonNull(job, "Cannot compose null job!").getActivationState());
        this.containedJob = job;
    }
    
    @Override
    protected void execute(Activation<? extends EventAtom> activation, Context context) {
        containedJob.execute(activation, context);
    }

    @Override
    protected void handleError(Activation<? extends EventAtom> activation, Exception exception, Context context) {
        containedJob.handleError(activation, exception, context);
    }

}
