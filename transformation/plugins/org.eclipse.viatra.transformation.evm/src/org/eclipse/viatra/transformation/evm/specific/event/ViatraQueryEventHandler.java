/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.api.event.adapter.EventHandlerAdapter;
import org.eclipse.viatra.transformation.evm.api.event.adapter.EventProcessorAdapter;
import org.eclipse.viatra.transformation.evm.notification.AttributeMonitor;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

public class ViatraQueryEventHandler<Match extends IPatternMatch> extends EventHandlerAdapter<Match> {

    private AttributeMonitor<Match> attributeMonitor;
    private UnregisterMonitorActivationNotificationListener unregisterListener;
    
    protected ViatraQueryEventHandler(ViatraQueryEventSource<Match> source, EventFilter<? super Match> filter, RuleInstance<Match> instance) {
        super(source, filter, instance);
    }

    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        //return new DefaultAttributeMonitor<Match>();
        LightweightAttributeMonitor<Match> monitor = null;
        ViatraQueryEventSource<Match> eventSource = (ViatraQueryEventSource<Match>) getSource();
        try {
            monitor = new LightweightAttributeMonitor<Match>(eventSource.getMatcher().getEngine().getBaseIndex());
        } catch (ViatraQueryException e) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error("Error happened while accessing base index", e);
        }
        return monitor;
    }
    
    @Override
    protected void prepareEventHandler() {
        super.prepareEventHandler();
        
        attributeMonitor = Objects.requireNonNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        ViatraQueryEventSource<Match> eventSource = (ViatraQueryEventSource<Match>) getSource();
        eventSource.addHandler(this);
        attributeMonitor.addAttributeMonitorListener(eventSource.getAttributeMonitorListener());
        unregisterListener = Objects.requireNonNull(prepareActivationNotificationListener(), "Prepared activation notification listener is null!");
        getInstance().addActivationNotificationListener(unregisterListener, false);
    }

    protected UnregisterMonitorActivationNotificationListener prepareActivationNotificationListener() {
        return new UnregisterMonitorActivationNotificationListener();
    }

    @Override
    protected Map<EventType, EventProcessorAdapter<Match>> prepareEventProcessors() {
    
        Map<EventType,EventProcessorAdapter<Match>> processors = new HashMap<>();
        processors.put(CRUDEventTypeEnum.CREATED, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, CRUDEventTypeEnum.CREATED);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                Match eventAtom = event.getEventAtom();
                Activation<Match> activation = getInstance().createActivation(eventAtom);
                if(getInstance().getLifeCycle().containsTo(CRUDActivationStateEnum.UPDATED)) {
                    attributeMonitor.registerFor(eventAtom);
                }
                getInstance().activationStateTransition(activation, CRUDEventTypeEnum.CREATED);
            }
        });
        processors.put(CRUDEventTypeEnum.UPDATED, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, CRUDEventTypeEnum.UPDATED);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                Preconditions.checkState(false, "Atom %s updated without existing activation in rule instance %s!", event.getEventAtom(), this);
            }
        });
        processors.put(CRUDEventTypeEnum.DELETED, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, CRUDEventTypeEnum.DELETED);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                Preconditions.checkState(false, "Match %s disappeared without existing activation in rule instance %s!", event.getEventAtom(), this);
            }
        });
        return processors;
    }
    

    
    private final class UnregisterMonitorActivationNotificationListener implements IActivationNotificationListener {
        @SuppressWarnings("unchecked")
        @Override
        public void activationRemoved(Activation<?> activation, ActivationState oldState) {
            attributeMonitor.unregisterFor((Match) activation.getAtom());
        }

        @Override
        public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        }

        @Override
        public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        }
    }

    @Override
    public void dispose() {
        getInstance().removeActivationNotificationListener(unregisterListener);
        ((ViatraQueryEventSource<Match>) getSource()).removeHandler(this);
        attributeMonitor.dispose();
    }

}
