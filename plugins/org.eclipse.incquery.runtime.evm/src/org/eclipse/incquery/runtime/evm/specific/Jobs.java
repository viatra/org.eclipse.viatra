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
package org.eclipse.incquery.runtime.evm.specific;

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.specific.job.EnableJob;
import org.eclipse.incquery.runtime.evm.specific.job.RecordingJob;
import org.eclipse.incquery.runtime.evm.specific.job.StatelessJob;

/**
 * Provides static methods acting on or generating a {@link Job}. 
 * 
 * @author Abel Hegedus
 *
 */
public final class Jobs {

    /**
     * 
     */
    private Jobs() {
    }
    
    /**
     * Creates a {@link StatelessJob} for the given state with the given processor.
     * A stateless job simply processes the match in the activation during execution.
     * 
     * @param activationState
     * @param processor
     * @return
     */
    public static <Match extends IPatternMatch> Job newStatelessJob(ActivationState activationState, IMatchProcessor<Match> processor){
        return new StatelessJob<Match>(activationState, processor);
    }
    
    /**
     * Creates a {@link RecordingJob} for the given state with the given processor.
     * A recording job attempts to find the transactional editing domain for the EVM
     * and wraps the execution inside a command, that is accessible from the context afterwards.
     * 
     * @param activationState
     * @param processor
     * @return
     */
    public static <Match extends IPatternMatch> Job newRecordingJob(ActivationState activationState, IMatchProcessor<Match> processor){
        return new RecordingJob<Match>(activationState, processor);
    }
    
    /**
     * Creates a {@link Job} that does not have any effect. Useful when you don't want to do anything 
     * for a given activation state but you want to fire.
     * 
     * Consider using your own LifeCycle instead of Nop jobs!
     * 
     * @param activationState
     * @return
     */
    public static final <Match extends IPatternMatch> Job newNopJob(ActivationState activationState) {
        return new Job(activationState) {
            @Override
            protected void execute(Activation activation, Context context) {
                // do nothing
            }
            @Override
            protected void handleError(Activation activation, Exception exception, Context context) {
                // never happens!
                checkState(false, "NopJob should never cause errors!");
            }
        };
    }
    
    public static <Match extends IPatternMatch> Job newEnableJob(ActivationState activationState, IMatchProcessor<Match> processor) {
        return new EnableJob<Match>(activationState, processor);
    }
    
}
