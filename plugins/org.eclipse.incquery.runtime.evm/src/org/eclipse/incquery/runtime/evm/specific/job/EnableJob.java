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

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.Context;

/**
 * @author Abel Hegedus
 *
 * @param <Match>
 */
public class EnableJob<Match extends IPatternMatch> extends StatelessJob<Match> {
    private boolean enabled = false;

    /**
     * @param activationState
     * @param matchProcessor
     */
    public EnableJob(ActivationState activationState, IMatchProcessor<Match> matchProcessor) {
        super(activationState, matchProcessor);
    }

    @Override
    protected void execute(Activation activation, Context context) {
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