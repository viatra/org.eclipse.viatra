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
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.api.event.adapter.EventHandlerAdapter;
import org.eclipse.incquery.runtime.evm.api.event.adapter.EventProcessorAdapter;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventHandler<Match extends IPatternMatch> extends EventHandlerAdapter<Match> {

    private AttributeMonitor<Match> attributeMonitor;
    private UnregisterMonitorActivationNotificationListener unregisterListener;
    
    protected IncQueryEventHandler(IncQueryEventSource<Match> source, EventFilter<? super Match> filter, RuleInstance<Match> instance) {
        super(source, filter, instance);
    }

    /**
     * @return a new attribute monitor
     */
    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        //return new DefaultAttributeMonitor<Match>();
        LightweightAttributeMonitor<Match> monitor = null;
        IncQueryEventSource<Match> eventSource = (IncQueryEventSource<Match>) getSource();
        try {
            monitor = new LightweightAttributeMonitor<Match>(eventSource.getMatcher().getEngine().getBaseIndex());
        } catch (IncQueryException e) {
            IncQueryLoggingUtil.getDefaultLogger().error("Error happened while accessing base index", e);
        }
        return monitor;
    }
    
    @Override
    protected void prepareEventHandler() {
        super.prepareEventHandler();
        
        attributeMonitor = checkNotNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        IncQueryEventSource<Match> eventSource = (IncQueryEventSource<Match>) getSource();
        eventSource.addHandler(this);
        attributeMonitor.addAttributeMonitorListener(eventSource.getAttributeMonitorListener());
        unregisterListener = checkNotNull(prepareActivationNotificationListener(), "Prepared activation notification listener is null!");
        getInstance().addActivationNotificationListener(unregisterListener, false);
    }

    protected UnregisterMonitorActivationNotificationListener prepareActivationNotificationListener() {
        return new UnregisterMonitorActivationNotificationListener();
    }

    @Override
    protected Map<EventType, EventProcessorAdapter<Match>> prepareEventProcessors() {
    
        Map<EventType,EventProcessorAdapter<Match>> processors = Maps.newHashMap();
        processors.put(IncQueryEventTypeEnum.MATCH_APPEARS, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_APPEARS);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                Match eventAtom = event.getEventAtom();
                Activation<Match> activation = getInstance().createActivation(eventAtom);
                if(getInstance().getLifeCycle().containsTo(IncQueryActivationStateEnum.UPDATED)) {
                    attributeMonitor.registerFor(eventAtom);
                }
                getInstance().activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_APPEARS);
            }
        });
        processors.put(IncQueryEventTypeEnum.MATCH_UPDATES, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_UPDATES);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                checkState(false, String.format("Atom %s updated without existing activation in rule instance %s!", event.getEventAtom(), this));
            }
        });
        processors.put(IncQueryEventTypeEnum.MATCH_DISAPPEARS, new EventProcessorAdapter<Match>(getInstance()) {
            @Override
            protected void activationExists(Event<Match> event, Activation<Match> activation) {
                getInstance().activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_DISAPPEARS);
            }

            @Override
            protected void activationMissing(Event<Match> event) {
                checkState(false, String.format("Match %s disappeared without existing activation in rule instance %s!",event.getEventAtom(),this));
            }
        });
        return processors;
    }
    

    
    /**
     * @author Abel Hegedus
     *
     */
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
        ((IncQueryEventSource<Match>) getSource()).removeHandler(this);
        attributeMonitor.dispose();
    }

}
