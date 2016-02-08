/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * @author Abel Hegedus
 *
 */
public class CompositeJob<EventAtom> extends Job<EventAtom> {

    private Job<EventAtom> containedJob;
    
    /**
     * 
     */
    protected CompositeJob(Job<EventAtom> job) {
        super(checkNotNull(job, "Cannot compose null job!").getActivationState());
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
