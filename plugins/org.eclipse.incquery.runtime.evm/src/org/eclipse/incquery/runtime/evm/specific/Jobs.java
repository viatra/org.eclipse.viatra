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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.job.EnableJob;
import org.eclipse.incquery.runtime.evm.specific.job.ErrorLoggingJob;
import org.eclipse.incquery.runtime.evm.specific.job.EventAtomDomainObjectProvider;
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
     * @param incQueryActivationStateEnum
     * @param processor
     * @return
     */
    public static <Match extends IPatternMatch> Job<Match> newStatelessJob(IncQueryActivationStateEnum incQueryActivationStateEnum, IMatchProcessor<Match> processor){
        return new StatelessJob<Match>(incQueryActivationStateEnum, processor);
    }
    
    /**
     * Creates a {@link RecordingJob} for the given state with the given processor.
     * A recording job attempts to find the transactional editing domain for the EVM
     * and wraps the execution inside a command, that is accessible from the context afterwards.
     * 
     * @param incQueryActivationStateEnum
     * @param processor
     * @return
     * @deprecated Use newStatelessJob and call newRecordingJob(Job) with the result!
     */
    @Deprecated
	public static <Match extends IPatternMatch> Job<Match> newRecordingJob(IncQueryActivationStateEnum incQueryActivationStateEnum, IMatchProcessor<Match> processor){
        return new RecordingJob<Match>(new StatelessJob<Match>(incQueryActivationStateEnum, processor), new EventAtomDomainObjectProvider<Match>() {
            @Override
            public Object findDomainObject(Activation<? extends Match> activation, Context context) {
              Match match = activation.getAtom();
              final int arity = match.parameterNames().size();
              if(arity > 0) {
                  for (int i = 0; i < arity; i++) {
                      if(match.get(i) instanceof EObject) {
                          return match.get(i);
                      }
                  }
              }
              return null;
            }
        });
    }
    
    public static <EventAtom> Job<EventAtom> newRecordingJob(Job<EventAtom> job){
        return new RecordingJob<EventAtom>(job);
    }
    
    /**
     * Creates a {@link Job} that does not have any effect. Useful when you don't want to do anything 
     * for a given activation state but you want to fire.
     * 
     * Consider using your own LifeCycle instead of Nop jobs!
     * 
     * @param incQueryActivationStateEnum
     * @return
     */
    public static final <EventAtom> Job<EventAtom> newNopJob(ActivationState activationState) {
        return new Job<EventAtom>(activationState) {
            @Override
            protected void execute(Activation<? extends EventAtom> activation, Context context) {
                // do nothing
            }
            @Override
            protected void handleError(Activation<? extends EventAtom> activation, Exception exception, Context context) {
                // never happens!
                checkState(false, "NopJob should never cause errors!");
            }
        };
    }
    
    /**
     * 
     * @param incQueryActivationStateEnum
     * @param processor
     * @return
     * @deprecated Use newStatelessJob and call newRecordingJob(Job) with the result!
     */
    @Deprecated
	public static <Match extends IPatternMatch> Job<Match> newEnableJob(IncQueryActivationStateEnum incQueryActivationStateEnum, IMatchProcessor<Match> processor) {
        return new EnableJob<Match>(new StatelessJob<Match>(incQueryActivationStateEnum, processor));
    }

    public static <EventAtom> Job<EventAtom> newEnableJob(Job<EventAtom> job) {
        return new EnableJob<EventAtom>(job);
    }
    
    public static <EventAtom> Job<EventAtom> newErrorLoggingJob(Job<EventAtom> job) {
        return new ErrorLoggingJob<EventAtom>(job);
    }
    
}
