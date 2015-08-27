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

package org.eclipse.viatra.cep.core.engine;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.evm.api.EventDrivenVM;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.update.UpdateCompleteProvider;
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
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
import org.eclipse.viatra.cep.core.streams.EventStream;

/**
 * 
 * @since 0.8
 * 
 * @author Istvan David
 *
 */
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

    public DefaultEventModelManager(ResourceSet resourceSet) {
        this(EventContext.CHRONICLE, resourceSet);
    }

    public DefaultEventModelManager(EventContext context, ResourceSet resourceSet) {
        this.resourceSet = resourceSet;
        this.model = (InternalModel) resourceSet.getResource(TransformationBasedCompiler.AUTOMATON_MODEL_URI, false)
                .getContents().get(0);

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
                    if (event instanceof ParameterizableEventInstance) {
                        boolean checkExpressionTrue = ((ParameterizableEventInstance) event).evaluateCheckExpression();
                        if (checkExpressionTrue) {
                            refreshModel(event);
                        }
                    }
                }
            }
        };

        this.strategy = EventProcessingStrategyFactory.getStrategy(context, this);

        initializeLowLevelModelHandling();
    }

    private void initializeLowLevelModelHandling() {
        new RuntimeRules(this).registerRulesWithCustomPriorities();
    }

    private void refreshModel(Event event) {
        model.setLatestEvent(null);
        for (EventToken eventToken : model.getEventTokensInModel()) {
            eventToken.setLastProcessed(null);
        }

        model.getEnabledForTheLatestEvent().clear();
        strategy.handleInitTokenCreation(model, AutomatonFactory.eINSTANCE);
        model.setLatestEvent(event);
        strategy.handleAutomatonResets(model, AutomatonFactory.eINSTANCE);
        cepUpdateCompleteProvider.latestEventHandled();
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

    @Deprecated
    @Override
    public void callbackOnPatternRecognition(IObservableComplexEventPattern observedPattern) {
        // NOP
    }
}