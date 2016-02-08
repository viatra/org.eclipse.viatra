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
package org.eclipse.viatra.transformation.evm.specific.job;

import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.CompositeJob;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;

/**
 * Composite job for logging any errors that happen in the execution of the composed job.
 * 
 * @author Abel Hegedus
 *
 */
public class ErrorLoggingJob<EventAtom> extends CompositeJob<EventAtom> {
    
    /**
     * Composes the given job into an error logging job.
     * 
     * @param job the composed job
     */
    public ErrorLoggingJob(Job<EventAtom> job) {
        super(job);
    }

    @Override
    protected void handleError(Activation<? extends EventAtom> activation, Exception exception, Context context) {
        IncQueryLoggingUtil.getLogger(getClass()).error("Exception occurred while executing job on activation " + activation +"!",
                exception);
    }

}
