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

/**
 * The engine for processing complex events.
 * 
 * <p>
 * The engine hosts the following components:
 * <ul>
 * <li>a {@link DefaultEventModelManager}, dealing with the internal representation of the (partially) matched event
 * patterns;
 * <li>a {@link DefaultStreamManager}, managing the streams of events;
 * <li>an EVM {@link ExecutionSchema}, serving as the rule engine.
 * </ul>
 * 
 * <p>
 * An instance of the engine can be obtained either by using the default event processing context ({@link #newEngine()}
 * ), or by specifying one explicitly ({@link #newEngine(EventContext)}).
 * 
 * @author Istvan David
 * 
 */
public class CEPEngine {
    private static final EventContext DEFAULT_EVENT_CONTEXT = EventContext.CHRONICLE;

    private DefaultEventModelManager eventModelManager;
    private ExecutionSchema ruleEngine;
    private IStreamManager streamManager;

    /**
     * Obtain an instance of the {@link CEPEngine} with the default {@link EventContext}.
     * 
     * @return a {@link CEPEngine} instance
     */
    public static CEPEngine newEngine() {
        return new CEPEngine(DEFAULT_EVENT_CONTEXT);
    }

    /**
     * Obtain an instance of the {@link CEPEngine} with the specified {@link EventContext}.
     * 
     * @param context
     *            the {@link EventContext} to be used whilst processing events
     * @return a {@link CEPEngine} instance
     */
    public static CEPEngine newEngine(EventContext context) {
        return new CEPEngine(context);
    }

    private CEPEngine(EventContext context) {
        eventModelManager = new DefaultEventModelManager(context);

        ruleEngine = eventModelManager.createExecutionSchema();

        streamManager = DefaultStreamManager.getInstance(eventModelManager);
    }

    /**
     * Assign a list of {@link ICepRule}s to the rule engine.
     * 
     * @param rules
     *            {@link ICepRule}s to be assigned
     */
    public void addRules(List<ICepRule> rules) {
        Preconditions.checkArgument(!rules.isEmpty());
        for (ICepRule rule : rules) {
            addSingleRule(rule);
        }
        eventModelManager.initializeAutomatons();
    }

    /**
     * Assign a single {@link ICepRule} to the rule engine.
     * 
     * @param rule
     *            {@link ICepRule} to be assigned
     */
    public void addRule(ICepRule rule) {
        addSingleRule(rule);
        eventModelManager.initializeAutomatons();
    }

    /**
     * Creates a {@link RuleSpecification} from a single {@link ICepRule}
     * 
     * @param rule
     *            {@link ICepRule} to push into the rule engine
     */
    private void addSingleRule(ICepRule rule) {
        Preconditions.checkArgument(!rule.getEventPatterns().isEmpty());
        for (EventPattern eventPattern : rule.getEventPatterns()) {
            Automaton automaton = eventModelManager.getAutomaton(eventPattern);

            CepEventSourceSpecification sourceSpec = new CepEventSourceSpecification(automaton);

            Job<IObservableComplexEventPattern> job = rule.getJob();

            @SuppressWarnings("unchecked")
            RuleSpecification<IObservableComplexEventPattern> ruleSpec = new RuleSpecification<IObservableComplexEventPattern>(
                    sourceSpec, getDefaultLifeCycle(), Sets.newHashSet(job));
            ruleEngine.addRule(ruleSpec);
        }
    }

    /**
     * @return default {@link ActivationLifeCycle} for the {@link RuleSpecification}s created from {@link ICepRule}s.
     */
    private ActivationLifeCycle getDefaultLifeCycle() {
        ActivationLifeCycle lifeCycle = ActivationLifeCycle.create(CepActivationStates.INACTIVE);
        lifeCycle.addStateTransition(CepActivationStates.INACTIVE, CepEventType.APPEARED, CepActivationStates.ACTIVE);
        lifeCycle
                .addStateTransition(CepActivationStates.ACTIVE, RuleEngineEventType.FIRE, CepActivationStates.INACTIVE);
        return lifeCycle;
    }

    /**
     * @return the stream manager dealing with data streams in the current {@link CEPEngine} instance.
     */
    public IStreamManager getStreamManager() {
        return streamManager;
    }

    /**
     * Set the debug {@link Level} of the {@link Logger} instance associated with the rule engine (
     * {@link ExecutionSchema}).
     * 
     * @param level
     *            the {@link Level} to be set
     */
    public void setRuleEngineDebuggingLevel(Level level) {
        ruleEngine.getLogger().setLevel(level);
    }

    /**
     * Set the debug {@link Level} of the {@link Logger} instance associated with the current {@link CEPEngine}
     * instance.
     * 
     * @param level
     *            the {@link Level} to be set
     */
    public void setCepEngineDebugLevel(Level level) {
        LoggerUtils.getInstance().getLogger().setLevel(level);
    }

    /**
     * @return the {@link Logger} instance associated with the current {@link CEPEngine} instance.
     */
    public Logger getLogger() {
        return LoggerUtils.getInstance().getLogger();
    }
}
