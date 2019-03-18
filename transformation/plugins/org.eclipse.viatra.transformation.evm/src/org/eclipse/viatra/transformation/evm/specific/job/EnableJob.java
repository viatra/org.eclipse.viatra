/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.job;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.CompositeJob;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;

/**
 * @author Abel Hegedus
 *
 * @param <EventAtom>
 */
public class EnableJob<EventAtom> extends CompositeJob<EventAtom> {
    /**
     * @param job
     */
    public EnableJob(Job<EventAtom> job) {
        super(job);
    }

    private boolean enabled = false;

    @Override
    protected void execute(Activation<? extends EventAtom> activation, Context context) {
        if(enabled) {
            super.execute(activation, context);
        }
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}