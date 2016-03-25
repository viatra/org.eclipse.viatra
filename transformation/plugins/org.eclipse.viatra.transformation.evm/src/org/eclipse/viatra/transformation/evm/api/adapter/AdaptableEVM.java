/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.Iterator;
import java.util.List;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Agenda;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Executor;
import org.eclipse.viatra.transformation.evm.api.IExecutor;
import org.eclipse.viatra.transformation.evm.api.RuleBase;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.ScheduledExecution;
import org.eclipse.viatra.transformation.evm.api.Scheduler;
import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryEventRealm;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;

import com.google.common.collect.Lists;

/**
 * Class that is responsible for containing {@link IEVMAdapter} and {@link IEVMListener} objects, as well as dispatching
 * the events they are subscribed to. It also contains static methods for creating an adaptable {@link RuleEngine} and
 * {@link ExecutionSchema} objects as well.
 * 
 * @author Peter Lunk
 *
 */
public class AdaptableEVM {
    private List<IEVMAdapter> adapters = Lists.newArrayList();
    private List<IEVMListener> listeners = Lists.newArrayList();

    public List<IEVMAdapter> getAdapters() {
        return adapters;
    }

    public List<IEVMListener> getListeners() {
        return listeners;
    }

    public void addAdapter(IEVMAdapter adapter) {
        this.adapters.add(adapter);
    }

    public void addAdapters(List<IEVMAdapter> adapters) {
        this.adapters.addAll(adapters);
    }

    public void addListener(IEVMListener adapter) {
        this.listeners.add(adapter);
    }

    public void addListeners(List<IEVMListener> adapters) {
        this.listeners.addAll(adapters);
    }

    public void addConfiguration(IAdapterConfiguration adapterConfiguration) {
        this.adapters.addAll(adapterConfiguration.getAdapters());
        this.listeners.addAll(adapterConfiguration.getListeners());
    }

    public void initializeListener() {
        for (IEVMListener listener : listeners) {
            listener.initializeListener();
        }
    }

    public void beforeFiring(Activation<?> activation) {
        for (IEVMListener listener : listeners) {
            listener.beforeFiring(activation);
        }
    }

    public void afterFiring(Activation<?> activation) {
        for (IEVMListener listener : listeners) {
            listener.afterFiring(activation);
        }
    }

    public void startTransaction(String transactionID) {
        for (IEVMListener listener : listeners) {
            listener.startTransaction(transactionID);
        }
    }

    public void endTransaction(String transactionID) {
        for (IEVMListener listener : listeners) {
            listener.endTransaction(transactionID);
        }
    }

    public void addedRule(RuleSpecification<?> specification) {
        for (IEVMListener listener : listeners) {
            listener.addedRule(specification);
        }
    }

    public void removedRule(RuleSpecification<?> specification) {
        for (IEVMListener listener : listeners) {
            listener.addedRule(specification);
        }
    }

    public void disposeListener() {
        for (IEVMListener listener : listeners) {
            listener.disposeListener();
        }
    }

    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        for (IEVMListener listener : listeners) {
            listener.activationChanged(activation, oldState, event);
        }
    }

    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        for (IEVMListener listener : listeners) {
            listener.activationCreated(activation, inactiveState);
        }
    }

    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
        for (IEVMListener listener : listeners) {
            listener.activationRemoved(activation, oldState);
        }
    }

    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        Iterator<Activation<?>> returnIterator = iterator;
        for (IEVMAdapter ievmAdapter : adapters) {
            returnIterator = ievmAdapter.getExecutableActivations(iterator);
        }
        return returnIterator;
    }

    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
        ChangeableConflictSet returnConflictSet = set;
        for (IEVMAdapter ievmAdapter : adapters) {
            returnConflictSet = ievmAdapter.getConflictSet(set);
        }
        return returnConflictSet;
    }
    
    /**
     * Creates a new adaptable {@link ExecutionSchema} based on the input parameters.
     * @param queryEngine
     * @param schedulerFactory
     * @param conflictResolver
     * @return
     */
    public ExecutionSchema createAdaptableExecutionSchema(ViatraQueryEngine queryEngine,
            ISchedulerFactory schedulerFactory, ConflictResolver conflictResolver) {

        IExecutor executor = new AdaptableExecutor(new Executor(), this);
        ConflictResolver adaptableConflictResolver = new AdaptableConflictResolver(conflictResolver, this);

        Agenda debugAgenda = new Agenda(adaptableConflictResolver);
        debugAgenda.setActivationListener(
                new AdaptableActivationNotificationListener(debugAgenda.getActivationListener(), this));
        RuleBase debugRulebase = new AdaptableRuleBase(ViatraQueryEventRealm.create(queryEngine), debugAgenda, this);
        ScheduledExecution execution = new ScheduledExecution(debugRulebase, executor);
        Scheduler scheduler = schedulerFactory.prepareScheduler(execution);
        final ExecutionSchema schema = ExecutionSchema.create(scheduler);
        schema.setConflictResolver(adaptableConflictResolver);
        return schema;
    }
    
    /**
     * Creates a new adaptable {@link RuleEngine} based on the input {@link ViatraQueryEngine}
     * @param queryEngine
     * @return
     */
    public RuleEngine createAdaptableRuleEngine(ViatraQueryEngine queryEngine) {
        AdaptableConflictResolver conflictResolver = new AdaptableConflictResolver(new ArbitraryOrderConflictResolver(),
                this);
        Agenda debugAgenda = new Agenda(conflictResolver);
        debugAgenda.setActivationListener(
                new AdaptableActivationNotificationListener(debugAgenda.getActivationListener(), this));

        RuleBase debugRulebase = new AdaptableRuleBase(ViatraQueryEventRealm.create(queryEngine), debugAgenda, this);
        return RuleEngine.create(debugRulebase);
    }

}
