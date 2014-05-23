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

public class DefaultCepRule implements ICepRule {
    private static final Logger logger = LoggerUtils.getInstance().getLogger();

    private List<EventPattern> eventPatterns = Lists.newArrayList();
    private Job<IObservableComplexEventPattern> job;

    public DefaultCepRule(List<EventPattern> eventPatterns, Job<IObservableComplexEventPattern> job) {
        this.eventPatterns = eventPatterns;
        this.job = job;
    }

    public DefaultCepRule(List<EventPattern> eventPatterns) {
        this(eventPatterns, getDefaultJob());
    }

    public void addEventPattern(EventPattern eventPattern) {
        eventPatterns.add(eventPattern);
    }

    public void addEventPatterns(List<EventPattern> eventPatterns) {
        this.eventPatterns.addAll(eventPatterns);
    }

    @Override
    public List<EventPattern> getEventPatterns() {
        return eventPatterns;
    }

    @Override
    public Job<IObservableComplexEventPattern> getJob() {
        return job;
    }

    public static Job<IObservableComplexEventPattern> getDefaultJob() {
        return new Job<IObservableComplexEventPattern>(CepActivationStates.ACTIVE) {

            protected void execute(Activation<? extends IObservableComplexEventPattern> activation, Context context) {
                logger.debug("CepJobs#DefaultJob: Complex event pattern appeared: "
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
