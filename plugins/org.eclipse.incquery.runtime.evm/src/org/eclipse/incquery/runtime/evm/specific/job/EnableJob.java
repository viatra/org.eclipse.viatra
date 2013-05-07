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
package org.eclipse.incquery.runtime.evm.specific.job;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.CompositeJob;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;

/**
 * @author Abel Hegedus
 *
 * @param <Match>
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