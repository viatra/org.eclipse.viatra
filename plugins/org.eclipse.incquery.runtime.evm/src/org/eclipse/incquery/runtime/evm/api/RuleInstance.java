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
package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;

import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.notification.ActivationNotificationProvider;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationProvider;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * The rule instance is created in the EVM for a rule specification. 
 * The instance manages the set of activations and processes events 
 * that affect the instance and its activations. It uses the life-cycle
 * defined in its specification for updating the state of activations and
 * the jobs to execute them, when requested.
 * 
 * The instance also provides change notification to the agenda about
 * activation state changes.
 * 
 * @author Abel Hegedus
 * 
 */
public class RuleInstance<EventAtom> implements IActivationNotificationProvider{

    /**
     * A default implementation for providing activation state change 
     * notifications to listeners.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultActivationNotificationProvider extends ActivationNotificationProvider {
        @Override
        protected void listenerAdded(final IActivationNotificationListener listener, final boolean fireNow) {
            if (fireNow) {
                ActivationState inactiveState = getActivationInactiveState();
                for (Activation<EventAtom> activation : getAllActivations()) {
                    listener.activationCreated(activation, inactiveState);
                }
            }
        }
    }

    private final RuleSpecification<EventAtom> specification;
    private final Table<ActivationState, EventAtom, Activation<EventAtom>> activations;
    private final ActivationNotificationProvider activationNotificationProvider;
    private EventHandler<EventAtom> handler;
   
    /**
     * Creates an instance using a RuleSpecification.
     * 
     * @param specification
     * @param engine
     * @throws IllegalArgumentException if filter is mutable
     */
    protected RuleInstance(final RuleSpecification<EventAtom> specification) {
        this.specification = checkNotNull(specification, "Cannot create rule instance for null specification!");
        this.activations = HashBasedTable.create();
        
        this.activationNotificationProvider = new DefaultActivationNotificationProvider();
    }
    
    public void setHandler(EventHandler<EventAtom> handler) {
        checkArgument(handler != null, "Handler cannot be null!");
        checkState(this.handler == null || handler.equals(this.handler), "Handler already set!");
        this.handler = handler;
    }
    
    public Activation<EventAtom> createActivation(EventAtom atom) {
        return new Activation<EventAtom>(this, atom, getActivationInactiveState());
    }

    private ActivationState getActivationInactiveState() {
        return specification.getLifeCycle().getInactiveState();
    }

    /**
     * Fires the given activation using the supplied context.
     * Delegates to the doFire method
     * 
     * @param activation
     * @param context
     */
    public void fire(final Activation<EventAtom> activation, final Context context) {
        checkNotNull(activation, "Cannot fire null activation!");
        checkNotNull(context,"Cannot fire activation with null context");
        ActivationState activationState = activation.getState();
        EventAtom atom = activation.getAtom();

        doFire(activation, activationState, atom, context);
    }

    /**
     * Checks whether the activation is part of the activation set of
     * the instance, then updates the state by calling activationStateTransition().
     * Finally, it executes each job that corresponds to the 
     * activation state using the supplied context.
     * 
     * @param activation
     * @param incQueryActivationStateEnum
     * @param atom
     * @param context
     */
    protected void doFire(final Activation<EventAtom> activation, final ActivationState activationState, final EventAtom atom, final Context context) {
        if (activations.contains(activationState, atom)) {
            Collection<Job<EventAtom>> jobs = specification.getJobs(activationState);
            activationStateTransition(activation, EventType.RuleEngineEventType.FIRE);
            for (Job<? super EventAtom> job : jobs) {
                try {
                    job.execute(activation, context);
                } catch(Exception e) {
                    job.handleError(activation, e, context);
                }
            }
        }
    }

    /**
     * Performs the state transition on the given activation in response to the specified event
     * using the life-cycle defined in the rule specification. If there is a transition defined for the 
     * current state and the event, the activation state is updated. Finally, an activation change
     * notification is sent to listeners and the new state is returned.
     * 
     * @param activation
     * @param event
     * @return the state of the activation after the transition
     */
    public ActivationState activationStateTransition(final Activation<EventAtom> activation, final EventType event) {
        checkNotNull(activation, "Cannot perform state transition on null activation!");
        checkNotNull(event, "Cannot perform state transition with null event!");
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        EventAtom atom = activation.getAtom();
        if (nextActivationState != null) {
            Activation<EventAtom> removed = activations.remove(activationState, atom);
            activation.setState(nextActivationState);
            if (!nextActivationState.isInactive()) {
                activations.put(nextActivationState, atom, activation);
                if(removed == null) { // activation did not exist
                    activationNotificationProvider.notifyActivationCreated(activation, activationState);
                } else { // still exists, only changed
                    activationNotificationProvider.notifyActivationChanged(activation, activationState, event);
                }
            } else {
                // inactive state, remove
                activationNotificationProvider.notifyActivationRemoved(activation, activationState);
            }
        } else {
            nextActivationState = activationState;
            // no effect, but event occured
            activationNotificationProvider.notifyActivationChanged(activation, activationState, event);
        }
        return nextActivationState;
    }
    
    

    /**
     * Delegate method for {@link ActivationNotificationProvider#addActivationNotificationListener}.
     * 
     * @param listener
     * @param fireNow
     * @return
     */
    @Override
    public boolean addActivationNotificationListener(final IActivationNotificationListener listener, final boolean fireNow) {
        return activationNotificationProvider.addActivationNotificationListener(listener, fireNow);
    }
    
    /**
     * Delegate method for {@link ActivationNotificationProvider#removeActivationNotificationListener}.
     * 
     * @param listener
     * @return
     */
    @Override
    public boolean removeActivationNotificationListener(final IActivationNotificationListener listener) {
        return activationNotificationProvider.removeActivationNotificationListener(listener);
    }

    /**
     * @return the specification
     */
    public RuleSpecification<EventAtom> getSpecification() {
        return specification;
    }

    public EventFilter<? super EventAtom> getFilter(){
        checkState(handler != null, "Cannot get filter, bacause handler is null!");
        return handler.getEventFilter();
    }

    public ActivationLifeCycle getLifeCycle() {
        return specification.getLifeCycle();
    }

    /**
     * 
     * @return the live table of activations
     */
    public Table<ActivationState, EventAtom, Activation<EventAtom>> getActivations() {
        return activations;
    }
    
    
    /**
     * 
     * @return the live set of activations
     */
    public Collection<Activation<EventAtom>> getAllActivations() {
        return activations.values();
    }

    /**
     * 
     * @param state
     * @return the live set of activations in the given state
     */
    public Collection<Activation<EventAtom>> getActivations(final ActivationState state) {
        checkNotNull(state, "Cannot return activations for null state");
        return activations.row(state).values();
    }

    /**
     * Disposes the rule instance by inactivating all activations and disposing of its
     * activation notification provider and attribute monitor.
     * 
     * Rule instances are managed by their RuleBase, they should be disposed through that!
     * 
     */
    protected void dispose() {
        this.handler.dispose();
        for (Cell<ActivationState, EventAtom, Activation<EventAtom>> cell : activations.cellSet()) {
            Activation<EventAtom> activation = cell.getValue();
            ActivationState activationState = activation.getState();
            activation.setState(specification.getLifeCycle().getInactiveState());
            activationNotificationProvider.notifyActivationRemoved(activation, activationState);
        } 
        this.activationNotificationProvider.dispose();
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("spec",specification).add("activations",activations).toString();
    }
}
