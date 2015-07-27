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

package org.eclipse.viatra.cep.core.api.engine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.engine.DefaultEventModelManager;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.evm.CepEventSourceSpecification;
import org.eclipse.viatra.cep.core.evm.CepEventType;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
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
 * 
 * @since 0.8
 * 
 * @author Istvan David
 *
 */
public class CEPEngine {
    private static final EventContext DEFAULT_EVENT_CONTEXT = EventContext.CHRONICLE;
    private Logger logger = LoggerUtils.getInstance().getLogger();

    private String engineId;

    private DefaultEventModelManager eventModelManager;
    private ExecutionSchema ruleEngine;
    private IStreamManager streamManager;

    private ResourceSet resourceSet;
    private InternalModel internalModel;
    private EventModel eventModel;
    private TraceModel traceModel;
    private Multimap<EventPattern, ICepRule> patternToRuleMappings;

    /**
     * Builder class for the {@link CEPEngine}.
     * 
     * <p>
     * Use a dedicated {@link CEPEngineBuilder} for every {@link CEPEngine} to be created, or make sure the rules
     * assigned to the builder do not interfere among multiple engines.
     * </p>
     * 
     * @since 0.8
     * 
     * @author Istvan David
     *
     */
    public static class CEPEngineBuilder {
        private String engineId;
        private EventContext eventContext = DEFAULT_EVENT_CONTEXT;
        private List<Class<? extends ICepRule>> ruleSpecifications = Lists.newArrayList();

        public CEPEngineBuilder id(String engineId) {
            this.engineId = engineId;
            return this;
        }

        public CEPEngineBuilder eventContext(EventContext eventContext) {
            this.eventContext = eventContext;
            return this;
        }

        public CEPEngineBuilder rules(List<Class<? extends ICepRule>> rules) {
            for (Class<? extends ICepRule> rule : rules) {
                this.rule(rule);
            }
            return this;
        }

        public CEPEngineBuilder rule(Class<? extends ICepRule> rule) {
            this.ruleSpecifications.add(rule);
            return this;
        }

        public CEPEngine prepare() {
            if (engineId == null) {
                engineId = UUID.randomUUID().toString();
            }
            Preconditions.checkArgument(engineId != null);
            Preconditions.checkArgument(eventContext != null);
            Preconditions.checkArgument(
                    !ruleSpecifications.isEmpty(),
                    String.format("No rule specifications added to the CEP engine \"%s\" (%s).", engineId,
                            this.toString()));

            List<ICepRule> ruleInstances = Lists.newArrayList();
            for (Class<? extends ICepRule> ruleSpecification : ruleSpecifications) {
                try {
                    ruleInstances.add(ruleSpecification.newInstance());
                } catch (Exception e) {
                    LoggerUtils.getInstance().getLogger().error(e.getMessage());
                }
            }

            Preconditions.checkArgument(!ruleInstances.isEmpty(), String.format(
                    "No rule instances were created from the rule specifications \"%s\" (%s).", engineId,
                    this.toString()));
            CEPEngine engine = new CEPEngine(engineId, eventContext, ruleInstances);
            engine.prepare();
            return engine;
        }
    }

    public static CEPEngineBuilder newEngine() {
        return new CEPEngineBuilder();
    }

    /**
     * Hidden default constructor.
     */
    private CEPEngine() {
    }

    private CEPEngine(String engineId, EventContext eventContext, List<ICepRule> rules) {
        this.engineId = engineId;

        setUpResourceSet();
        this.eventModelManager = new DefaultEventModelManager(eventContext, resourceSet);
        this.ruleEngine = eventModelManager.createExecutionSchema();
        this.streamManager = new DefaultStreamManager(eventModelManager);

        addRules(rules);
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

    private void addRules(List<ICepRule> rules) {
        patternToRuleMappings = ArrayListMultimap.create();
        Preconditions.checkArgument(!rules.isEmpty());
        for (ICepRule rule : rules) {
            addSingleRule(rule);
        }
    }

    private void addSingleRule(ICepRule rule) {
        Preconditions.checkArgument(!rule.getEventPatterns().isEmpty());
        for (EventPattern eventPattern : rule.getEventPatterns()) {
            eventModel.getEventPatterns().add(eventPattern);
            patternToRuleMappings.put(eventPattern, rule);
        }
    }

    private void prepare() {
        Preconditions.checkArgument(resourceSet != null);
        new TransformationBasedCompiler().compile(resourceSet);

        // XXX this lookup is ugly, should be replaced by a more efficient technique
        for (Trace trace : traceModel.getTraces()) {
            Automaton automaton = trace.getAutomaton();
            EventPattern eventPattern = trace.getEventPattern();
            for (Entry<EventPattern, ICepRule> entry : patternToRuleMappings.entries()) {
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

    /**
     * Default activation lifecycle for the CEP rules.
     * 
     * TODO should be moved to a more common place
     */
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
        logger.debug(String.format("Resetting engine \"%s\" (%s).", engineId, this.toString()));
        new ResetTransformations(eventModelManager.getModel()).resetAll();

        for (EventStream eventStream : streamManager.getEventStreams()) {
            eventStream.getQueue().clear();
        }
    }

    public String getEngineId() {
        return engineId;
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
