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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.specific.DefaultAttributeMonitor;

import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventHandler<Match extends IPatternMatch> implements EventHandler<Match> {

    private final IncQueryEventSource<Match> source;
    private final EventFilter<Match> filter;
    private final RuleInstance<Match> instance;
    private AttributeMonitor<Match> attributeMonitor;
    private final Map<IncQueryEventTypeEnum,IMatchProcessor<Match>> processors;
    
    protected IncQueryEventHandler(IncQueryEventSource<Match> source, EventFilter<Match> filter, RuleInstance<Match> instance) {
        checkArgument(source != null, "Cannot create handler with null source");
        checkArgument(filter != null, "Cannot create handler with null filter");
        this.instance = instance;
        this.source = source;
        this.filter = filter;
        this.processors = Maps.newEnumMap(IncQueryEventTypeEnum.class);
    }

    @Override
    public void handleEvent(Event<Match> event) {
        Match eventAtom = event.getEventAtom();
        if(filter.isProcessable(eventAtom)) {
            EventType eventType = event.getEventType();
            if(processors.containsKey(eventType)) {
                IMatchProcessor<Match> processor = processors.get(eventType);
                processor.process(event.getEventAtom());
            }
        }
    }

    @Override
    public EventSource<Match> getSource() {
        return source;
    }

    @Override
    public EventFilter<Match> getEventFilter() {
        return filter;
    }
    
        
    /**
     * @return a new attribute monitor
     */
    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        return new DefaultAttributeMonitor<Match>();
    }
    
    protected void setInstance(RuleInstance<Match> instance) {
        checkArgument(instance != null, "Instance cannot be null!");
        this.instance.setHandler(this);
        prepareEventProcessors(processors);
        source.resendEventsForExistingMatches(this);
        attributeMonitor = checkNotNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        attributeMonitor.addAttributeMonitorListener(source.getAttributeMonitorListener());
    }

    protected void activationStateTransition(Activation<Match> activation, IncQueryEventTypeEnum eventType) {
        ActivationState nextActivationState = instance.activationStateTransition(activation, eventType);
        if(IncQueryActivationStateEnum.INACTIVE.equals(nextActivationState)){
            attributeMonitor.unregisterFor(activation.getAtom());
        }
    }

    protected void prepareEventProcessors(Map<IncQueryEventTypeEnum,IMatchProcessor<Match>> processors) {
        
        processors.put(IncQueryEventTypeEnum.MATCH_APPEARS, new DefaultMatchEventProcessor() {
            @Override
            protected void activationExists(Activation<Match> activation) {
                activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_APPEARS);
            }

            @Override
            protected void activationMissing(Match atom) {
                Activation<Match> activation = instance.createActivation(atom);
                if(instance.getLifeCycle().containsTo(IncQueryActivationStateEnum.UPDATED)) {
                    attributeMonitor.registerFor(atom);
                }
                activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_APPEARS);
            }
        });
        processors.put(IncQueryEventTypeEnum.MATCH_UPDATES, new DefaultMatchEventProcessor() {
            @Override
            protected void activationExists(Activation<Match> activation) {
                activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_UPDATES);
            }

            @Override
            protected void activationMissing(Match atom) {
                checkState(false, String.format("Atom %s updated without existing activation in rule instance %s!", atom, this));
            }
        });
        processors.put(IncQueryEventTypeEnum.MATCH_DISAPPEARS, new DefaultMatchEventProcessor() {
            @Override
            protected void activationExists(Activation<Match> activation) {
                activationStateTransition(activation, IncQueryEventTypeEnum.MATCH_DISAPPEARS);
            }

            @Override
            protected void activationMissing(Match atom) {
                checkState(false, String.format("Match %s disappeared without existing activation in rule instance %s!",atom,this));
            }
        });
        
    }
    

    
    /**
     * This class is the common supertype for default event processors
     *  in the rule instance.
     *  
     * @author Abel Hegedus
     *
     */
    private abstract class DefaultMatchEventProcessor implements IMatchProcessor<Match> {
        
        /**
         * This method is called with the match corresponding to the
         * activation that is affected by the event.
         * 
         * @param atom
         */
        @Override
        public void process(Match atom) {
            checkNotNull(atom,"Cannot process null match!");
            
            Map<ActivationState, Activation<Match>> column = instance.getActivations().column(atom);
            if(column.size() > 0) {
                checkArgument(column.size() == 1, String.format("%s activations in the same rule for the same match",column.size() == 0 ? "No" : "Multiple"));
                Activation<Match> act = column.values().iterator().next();
                activationExists(act);
            } else {
                activationMissing(atom);
            }
        }
        
        /**
         * This method is called by processMatchEvent if the activation
         * already exists for the given match.
         * 
         * @param activation
         */
        protected abstract void activationExists(Activation<Match> activation);
        
        /**
         * This method is called by processMatchEvent if the activation
         * does not exists for the given match.
         * 
         * @param match
         */
        protected abstract void activationMissing(Match atom);
    }



    @Override
    public void dispose() {
        source.removeHandler(this);
        attributeMonitor.dispose();
    }

}
