/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.api.rules;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

import com.google.common.collect.Lists;

/**
 * Default implementation of the {@link ICepRule} interface.
 * 
 * @author Istvan David
 * 
 */
public class DefaultCepRule implements ICepRule {
    private static final Logger LOGGER = LoggerUtils.getInstance().getLogger();

    private List<EventPattern> eventPatterns = Lists.newArrayList();
    private Job<IObservableComplexEventPattern> job;

    /**
     * Creates a new rule with the specified {@link EventPattern}s and {@link Job}.
     * 
     * @param eventPatterns
     *            the {@link EventPattern}s the rule should be activated upon
     * @param job
     *            the {@link Job} to be executed when patterns get matched
     */
    public DefaultCepRule(List<EventPattern> eventPatterns, Job<IObservableComplexEventPattern> job) {
        this.eventPatterns = eventPatterns;
        this.job = job;
    }

    /**
     * Creates a new rule with the specified {@link EventPattern}s and a default {@link Job}.
     * 
     * @param eventPatterns
     *            the {@link EventPattern}s the rule should be activated upon
     */
    public DefaultCepRule(List<EventPattern> eventPatterns) {
        this(eventPatterns, getDefaultJob());
    }

    /**
     * Associates a list of {@link EventPattern}s with the rule.
     * 
     * @param eventPatterns
     *            the patterns to be included into the rule
     */
    public void addEventPatterns(List<EventPattern> eventPatterns) {
        this.eventPatterns.addAll(eventPatterns);
    }

    /**
     * Associates a single {@link EventPattern} with the rule.
     * 
     * @param eventPattern
     *            the pattern to be included into the rule
     */
    public void addEventPattern(EventPattern eventPattern) {
        eventPatterns.add(eventPattern);
    }

    @Override
    public List<EventPattern> getEventPatterns() {
        return eventPatterns;
    }

    @Override
    public Job<IObservableComplexEventPattern> getJob() {
        return job;
    }

    /**
     * @return default {@link Job} that logs diagnostic information on the console.
     */
    private static Job<IObservableComplexEventPattern> getDefaultJob() {
        return new Job<IObservableComplexEventPattern>(CepActivationStates.ACTIVE) {

            protected void execute(Activation<? extends IObservableComplexEventPattern> activation, Context context) {
                LOGGER.debug("CepJobs#DefaultJob: Complex event pattern appeared: "
                        + activation.getAtom().getObservableEventPattern().getId());
            }

            @Override
            protected void handleError(Activation<? extends IObservableComplexEventPattern> activation,
                    Exception exception, Context context) {
                // not gonna happen
            }
        };
    }
}
