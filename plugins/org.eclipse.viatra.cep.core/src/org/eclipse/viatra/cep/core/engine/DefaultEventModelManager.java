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

package org.eclipse.viatra.cep.core.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.incquery.runtime.evm.api.EventDrivenVM;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.update.UpdateCompleteProvider;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.engine.compiler.Compiler;
import org.eclipse.viatra.cep.core.engine.runtime.ModelHandlingRules;
import org.eclipse.viatra.cep.core.eventprocessingstrategy.EventProcessingStrategyFactory;
import org.eclipse.viatra.cep.core.eventprocessingstrategy.IEventProcessingStrategy;
import org.eclipse.viatra.cep.core.evm.CepRealm;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.streams.EventStream;

import com.google.common.collect.Lists;

public class DefaultEventModelManager implements IEventModelManager {
    private final Logger logger = LoggerUtils.getInstance().getLogger();

    private InternalModel model;
    private ResourceSet resourceSet;
    private Adapter eventAdapter;

    private IEventProcessingStrategy strategy;

    private CEPUpdateCompleteProvider cepUpdateCompleteProvider = new CEPUpdateCompleteProvider();
    private CepRealm cepRealm = new CepRealm();
    private UpdateCompleteBasedSchedulerFactory schedulerFactory = new UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory(
            cepUpdateCompleteProvider);

    // cache
    private Map<Automaton, FinalState> finalStatesForAutomata = new LinkedHashMap<Automaton, FinalState>();
    private Map<Automaton, InitState> initStatesForAutomata = new LinkedHashMap<Automaton, InitState>();
    private Map<Automaton, Boolean> wasEnabledForTheLatestEvent = new LinkedHashMap<Automaton, Boolean>();
    private List<EventToken> eventTokensToClear = Lists.newArrayList();
    private List<State> statesToClear = Lists.newArrayList();

    private final static class CEPUpdateCompleteProvider extends UpdateCompleteProvider {
        protected void latestEventHandled() {
            updateCompleted();
        }
    }

    public DefaultEventModelManager() {
        this(EventContext.CHRONICLE);
    }

    public DefaultEventModelManager(EventContext context) {
        prepareModel();

        eventAdapter = new AdapterImpl() {
            @Override
            public void notifyChanged(Notification notification) {
                if (notification.getEventType() != Notification.ADD) {
                    return;
                }
                Object newValue = notification.getNewValue();
                if (newValue instanceof Event) {
                    Event event = (Event) newValue;
                    logger.debug("EventModelManager: Event " + event.getClass().getName() + " captured...");
                    refreshModel(event);
                }
            }
        };

        this.strategy = EventProcessingStrategyFactory.getStrategy(context, this);

        initializeLowLevelModelHandling();
    }

    private void prepareModel() {
        model = AutomatonFactory.eINSTANCE.createInternalModel();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("cep", new XMIResourceFactoryImpl());
        resourceSet = new ResourceSetImpl();
        Resource smModelResource = resourceSet.createResource(URI.createURI("cep/sm.cep"));
        smModelResource.getContents().add(model);
    }

    private void initializeLowLevelModelHandling() {
        new ModelHandlingRules(this).registerRulesWithCustomPriorities();
    }

    public void initializeAutomatons() {
        for (Entry<Automaton, InitState> entry : initStatesForAutomata.entrySet()) {
            if (entry.getValue().getEventTokens().isEmpty()) {
                EventToken token = AutomatonFactory.eINSTANCE.createEventToken();
                token.setCurrentState(entry.getValue());
                entry.getKey().getEventTokens().add(token);
            }
        }
    }

    public Automaton getAutomaton(EventPattern eventPattern) {
        Compiler compiler = new Compiler(model);
        Automaton automaton = compiler.compile(eventPattern);
        finalStatesForAutomata.put(automaton, compiler.getFinalState());
        initStatesForAutomata.put(automaton, compiler.getInitState());

        wasEnabledForTheLatestEvent.put(automaton, true);

        return automaton;
    }

    private void refreshModel(Event event) {
        model.setLatestEvent(null);
        for (EventToken eventToken : eventTokensToClear) {
            eventToken.setLastProcessed(null);
        }
        for (State state : statesToClear) {
            state.setLastProcessedEvent(null);
        }
        
        wasEnabledForTheLatestEvent.clear();
        strategy.handleInitTokenCreation(model, AutomatonFactory.eINSTANCE, null);
        model.setLatestEvent(event);
        cepUpdateCompleteProvider.latestEventHandled();
        strategy.handleAutomatonResets(model, AutomatonFactory.eINSTANCE);
    }

    public ExecutionSchema createExecutionSchema() {
        return EventDrivenVM.createExecutionSchema(cepRealm, schedulerFactory,
                Collections.<RuleSpecification<?>> emptySet());
    }

    public UpdateCompleteBasedSchedulerFactory getSchedulerFactory() {
        return schedulerFactory;
    }

    @Override
    public InternalModel getModel() {
        return model;
    }

    @Override
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    @Override
    public CepRealm getCepRealm() {
        return cepRealm;
    }

    @Override
    public Map<Automaton, InitState> getInitStatesForAutomata() {
        return initStatesForAutomata;
    }

    @Override
    public Map<Automaton, FinalState> getFinalStatesForAutomata() {
        return finalStatesForAutomata;
    }

    @Override
    public Map<Automaton, Boolean> getWasEnabledForTheLatestEvent() {
        return wasEnabledForTheLatestEvent;
    }

    @Override
    public void registerNewEventStream(EventStream newEventStream) {
        newEventStream.eAdapters().add(eventAdapter);
    }

    @Override
    public void fireTransition(TypedTransition transition, EventToken token) {
        strategy.fireTransition(transition, token);
    }

    @Override
    public void callbackOnFiredToken(Transition transition, EventToken eventTokenToMove) {
        EObject state = transition.eContainer();
        if (!(state instanceof State)) {
            return;
        }

        EObject container = ((State) state).eContainer();
        if (!(container instanceof Automaton)) {
            return;
        }

        wasEnabledForTheLatestEvent.put(((Automaton) container), true);
        eventTokensToClear.add(eventTokenToMove);
        statesToClear.add(transition.getPreState());
    }

    @Override
    public void callbackOnPatternRecognition(IObservableComplexEventPattern observedPattern) {
        strategy.handleInitTokenCreation(model, AutomatonFactory.eINSTANCE, observedPattern);
    }

}