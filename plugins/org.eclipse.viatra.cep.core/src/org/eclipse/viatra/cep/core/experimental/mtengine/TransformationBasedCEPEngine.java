/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.experimental.mtengine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventType.RuleEngineEventType;
import org.eclipse.viatra.cep.core.api.engine.CEPEngine;
import org.eclipse.viatra.cep.core.api.engine.ResetTransformations;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.evm.CepEventSourceSpecification;
import org.eclipse.viatra.cep.core.evm.CepEventType;
import org.eclipse.viatra.cep.core.experimental.mtcompiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.events.EventModel;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.Trace;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel;
import org.eclipse.viatra.cep.core.streams.DefaultStreamManager;
import org.eclipse.viatra.cep.core.streams.EventStream;
import org.eclipse.viatra.cep.core.streams.IStreamManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Experimental alternative of the {@link CEPEngine} for the {@link TransformationBasedCompiler}. Will replace the
 * {@link CEPEngine}.
 * 
 * @author Istvan David
 *
 */
public class TransformationBasedCEPEngine {
    private static final EventContext DEFAULT_EVENT_CONTEXT = EventContext.CHRONICLE;

    private MTBasedEventModelManager eventModelManager;
    private ExecutionSchema ruleEngine;
    private IStreamManager streamManager;

    private ResourceSet resourceSet;
    private InternalModel internalModel;
    private EventModel eventModel;
    private TraceModel traceModel;

    public static TransformationBasedCEPEngine newEngine() {
        return new TransformationBasedCEPEngine(DEFAULT_EVENT_CONTEXT);
    }

    public static TransformationBasedCEPEngine newEngine(EventContext context) {
        return new TransformationBasedCEPEngine(context);
    }

    private TransformationBasedCEPEngine(EventContext context) {
        setUpResourceSet();
        eventModelManager = new MTBasedEventModelManager(context, resourceSet);
        ruleEngine = eventModelManager.createExecutionSchema();
        streamManager = new DefaultStreamManager(eventModelManager);
    }

    private void setUpResourceSet() {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("cep", new XMIResourceFactoryImpl());

        resourceSet = new ResourceSetImpl();

        internalModel = AutomatonFactory.eINSTANCE.createInternalModel();
        Resource internalModelResource = resourceSet.createResource(TransformationBasedCompiler.AUTOMATON_MODEL_URI);
        internalModelResource.getContents().add(internalModel);

        eventModel = EventsFactory.eINSTANCE.createEventModel();
        Resource eventModelResource = resourceSet.createResource(TransformationBasedCompiler.EVENT_MODEL_URI);
        eventModelResource.getContents().add(eventModel);

        traceModel = TraceFactory.eINSTANCE.createTraceModel();
        Resource traceModelResource = resourceSet.createResource(TransformationBasedCompiler.TRACE_MODEL_URI);
        traceModelResource.getContents().add(traceModel);
    }

    public void addRules(List<ICepRule> rules) {
        Preconditions.checkArgument(!rules.isEmpty());
        for (ICepRule rule : rules) {
            addSingleRule(rule);
        }
    }

    public void addRule(ICepRule rule) {
        addSingleRule(rule);
    }

    private Multimap<EventPattern, ICepRule> mappings = ArrayListMultimap.create();

    private void addSingleRule(ICepRule rule) {
        Preconditions.checkArgument(!rule.getEventPatterns().isEmpty());
        for (EventPattern eventPattern : rule.getEventPatterns()) {
            eventModel.getEventPatterns().add(eventPattern);
            mappings.put(eventPattern, rule);
        }
    }

    public void start() {
        new TransformationBasedCompiler().compile(resourceSet);

        // XXX this lookup is ugly, should be replaced by a more efficient technique
        for (Trace trace : traceModel.getTraces()) {
            Automaton automaton = trace.getAutomaton();
            EventPattern eventPattern = trace.getEventPattern();
            for (Entry<EventPattern, ICepRule> entry : mappings.entries()) {
                if (entry.getKey().equals(eventPattern)) {
                    CepEventSourceSpecification sourceSpec = new CepEventSourceSpecification(automaton);
                    Job<IObservableComplexEventPattern> job = entry.getValue().getJob();

                    @SuppressWarnings("unchecked")
                    RuleSpecification<IObservableComplexEventPattern> ruleSpec = new RuleSpecification<IObservableComplexEventPattern>(
                            sourceSpec, getDefaultLifeCycle(), Sets.newHashSet(job));
                    ruleEngine.addRule(ruleSpec);
                }
            }
        }
    }

    private ActivationLifeCycle getDefaultLifeCycle() {
        ActivationLifeCycle lifeCycle = ActivationLifeCycle.create(CepActivationStates.INACTIVE);
        lifeCycle.addStateTransition(CepActivationStates.INACTIVE, CepEventType.APPEARED, CepActivationStates.ACTIVE);
        lifeCycle
                .addStateTransition(CepActivationStates.ACTIVE, RuleEngineEventType.FIRE, CepActivationStates.INACTIVE);
        return lifeCycle;
    }

    /**
     * Clears the event processing state, including the partial event pattern matches and the event stream
     */
    public void reset() {
        new ResetTransformations(eventModelManager.getModel()).resetAll();

        for (EventStream eventStream : streamManager.getEventStreams()) {
            eventStream.getQueue().clear();
        }
    }

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
