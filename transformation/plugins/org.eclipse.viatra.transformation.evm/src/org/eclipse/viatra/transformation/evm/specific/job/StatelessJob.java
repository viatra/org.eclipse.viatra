/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.job;

import java.util.Objects;
import java.util.function.Consumer;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

/**
 * This class represents a {@link Job} that uses an {@link Consumer} 
 * on the match of the activation when executed.
 * 
 * @author Abel Hegedus
 */
public class StatelessJob<Match extends IPatternMatch> extends Job<Match> {

    private Consumer<Match> matchProcessor;
    
    /**
     * @return the matchProcessor executed by the job
     * @since 2.0
     */
    public Consumer<Match> getMatchProcessor() {
        return matchProcessor;
    }

    /**
     * Creates a stateless job for the given state and processor.
     * @since 2.0
     */
    public StatelessJob(final CRUDActivationStateEnum activationStateEnum, final Consumer<Match> matchProcessor) {
        super(activationStateEnum);
        this.matchProcessor = Objects.requireNonNull(matchProcessor,
                "StatelessJob cannot be instantiated with null match processor");
    }

    @Override
    protected void execute(final Activation<? extends Match> activation, final Context context) {
        matchProcessor.accept(activation.getAtom());
    }

    @Override
    protected void handleError(final Activation<? extends Match> activation, final Exception exception, final Context context) {
        throw new IllegalStateException("Exception " + exception.getMessage() + " was thrown when executing " + activation
                + "! Stateless job doesn't handle errors!", exception);
    }
}
