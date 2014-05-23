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

package org.eclipse.viatra.cep.core.api.engine;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventType.RuleEngineEventType;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.DefaultCepRule;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.engine.DefaultEventModelManager;
import org.eclipse.viatra.cep.core.evm.CepEventSourceSpecification;
import org.eclipse.viatra.cep.core.evm.CepEventType;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.streams.DefaultStreamManager;
import org.eclipse.viatra.cep.core.streams.IStreamManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class CEPEngine {
    private static final EventContext DEFAULT_EVENT_CONTEXT = EventContext.CHRONICLE;

    private DefaultEventModelManager eventModelManager;
    private ExecutionSchema ruleEngine;
    private IStreamManager streamManager;

    public static CEPEngine newEngine() {
        return new CEPEngine(DEFAULT_EVENT_CONTEXT);
    }

    public static CEPEngine newEngine(EventContext context) {
        return new CEPEngine(context);
    }

    private CEPEngine(EventContext context) {
        eventModelManager = new DefaultEventModelManager(context);

        ruleEngine = eventModelManager.createExecutionSchema();

        streamManager = DefaultStreamManager.getInstance(eventModelManager);
    }

    public void addRules(List<ICepRule> rules) {
        Preconditions.checkArgument(!rules.isEmpty());
        for (ICepRule rule : rules) {
            addSingleRule(rule);
        }
        eventModelManager.initializeAutomatons();
    }

    public void addRule(ICepRule rule) {
        addSingleRule(rule);
        eventModelManager.initializeAutomatons();
    }

    public void addSingleRule(ICepRule rule) {
        Preconditions.checkArgument(!rule.getEventPatterns().isEmpty());
        for (EventPattern eventPattern : rule.getEventPatterns()) {
            Automaton automaton = eventModelManager.getAutomaton(eventPattern);

            CepEventSourceSpecification sourceSpec = new CepEventSourceSpecification(automaton);

            Job<IObservableComplexEventPattern> job = rule.getJob();
            if (job == null) {
                job = DefaultCepRule.getDefaultJob();
            }
            @SuppressWarnings("unchecked")
            RuleSpecification<IObservableComplexEventPattern> ruleSpec = new RuleSpecification<IObservableComplexEventPattern>(
                    sourceSpec, getDefaultLifeCycle(), Sets.newHashSet(job));
            ruleEngine.addRule(ruleSpec);
        }
    }

    private ActivationLifeCycle getDefaultLifeCycle() {
        ActivationLifeCycle lifeCycle = ActivationLifeCycle.create(CepActivationStates.INACTIVE);
        lifeCycle.addStateTransition(CepActivationStates.INACTIVE, CepEventType.APPEARED, CepActivationStates.ACTIVE);
        lifeCycle
                .addStateTransition(CepActivationStates.ACTIVE, RuleEngineEventType.FIRE, CepActivationStates.INACTIVE);
        return lifeCycle;
    }

    public IStreamManager getStreamManager() {
        return streamManager;
    }

    public void setRuleEngineDebuggingLevel(Level level) {
        ruleEngine.getLogger().setLevel(level);
    }

    public void setCepEngineDebugLevel(Level level) {
        LoggerUtils.getInstance().getLogger().setLevel(level);
    }

    public Logger getLogger() {
        return LoggerUtils.getInstance().getLogger();
    }
}
