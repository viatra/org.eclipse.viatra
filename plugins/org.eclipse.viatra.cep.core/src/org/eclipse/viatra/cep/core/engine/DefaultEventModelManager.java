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
import java.util.List;
import java.util.Map;

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
import org.eclipse.viatra.cep.core.engine.runtime.RuntimeRules;
import org.eclipse.viatra.cep.core.eventprocessingstrategy.EventProcessingStrategyFactory;
import org.eclipse.viatra.cep.core.eventprocessingstrategy.IEventProcessingStrategy;
import org.eclipse.viatra.cep.core.evm.CepRealm;
import org.eclipse.viatra.cep.core.logging.LoggerUtils;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.streams.EventStream;

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
        new RuntimeRules(this).registerRulesWithCustomPriorities();
    }

    public Automaton getAutomaton(EventPattern eventPattern) {
        Compiler compiler = new Compiler(model);
        Automaton automaton = compiler.compile(eventPattern);

        model.getEnabledForTheLatestEvent().add(automaton);

        return automaton;
    }

    private void refreshModel(Event event) {
        model.setLatestEvent(null);
        for (EventToken eventToken : model.getEventTokensInModel()) {
            eventToken.setLastProcessed(null);
        }

        model.getEnabledForTheLatestEvent().clear();
        strategy.handleInitTokenCreation(model, AutomatonFactory.eINSTANCE);
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
    public List<Automaton> getEnabledAutomataForTheLatestEvent() {
        return model.getEnabledForTheLatestEvent();
    }

    @Override
    public void registerNewEventStream(EventStream newEventStream) {
        newEventStream.eAdapters().add(eventAdapter);
    }

    @Override
    public void handleEvent(TypedTransition transition, EventToken token) {
        strategy.handleEvent(transition, token);
    }

    @Override
    public void fireTransition(TypedTransition transition, EventToken token) {
        strategy.fireTransition(transition, token);
    }

    @Override
    public void callbackOnFiredToken(Transition transition, EventToken eventTokenToMove) {
        EObject automaton = eventTokenToMove.eContainer();
        if (!(automaton instanceof Automaton)) {
            return;
        }

        model.getEnabledForTheLatestEvent().add(((Automaton) automaton));
    }

    @Override
    public void callbackOnPatternRecognition(IObservableComplexEventPattern observedPattern) {
        // NOP
    }
}