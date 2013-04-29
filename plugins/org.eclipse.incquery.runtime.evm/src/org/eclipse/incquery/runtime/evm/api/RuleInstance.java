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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.evm.api.event.Atom;
import org.eclipse.incquery.runtime.evm.notification.ActivationNotificationProvider;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationProvider;
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;

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
public abstract class RuleInstance implements IActivationNotificationProvider{

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
                for (Activation activation : getAllActivations()) {
                    listener.activationChanged(activation, ActivationState.INACTIVE, ActivationLifeCycleEvent.MATCH_APPEARS);
                }
            }
        }
    }

    

    private final RuleSpecification specification;
    private Table<ActivationState, Atom, Activation> activations;
    private ActivationNotificationProvider activationNotificationProvider;
    private IAttributeMonitorListener attributeMonitorListener;
    private AttributeMonitor attributeMonitor;
    private final Atom filter;
    
    /**
     * @return the filter
     */
    public Atom getFilter() {
        return filter;
    }

    protected Activation createActivation(Atom atom) {
        return new Activation(this, atom);
    }
    
    /**
     * Creates an instance using a RuleSpecification.
     * 
     * @param specification
     * @param engine
     * @throws IllegalArgumentException if filter is mutable
     */
    protected RuleInstance(final RuleSpecification specification, Atom filter) {
        this.specification = checkNotNull(specification, "Cannot create rule instance for null specification!");
        this.filter = checkNotNull(filter, "Cannot create rule instance with null filter! Use EmptyAtom.INSTANCE instead.");
        checkArgument(!filter.isMutable(),String.format("Mutable filter %s is used in rule instance!",filter));
        this.activations = HashBasedTable.create();
        
        this.activationNotificationProvider = new DefaultActivationNotificationProvider();
    }

    /**
     * Prepares the attribute monitor
     */
    protected void prepareAttributeMonitorAndListener() {
        this.attributeMonitorListener = checkNotNull(prepareAttributeMonitorListener(), "Prepared attribute monitor listener is null!");
        this.attributeMonitor = checkNotNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        this.attributeMonitor.addAttributeMonitorListener(attributeMonitorListener);
    }
    
    /**
     * @return
     */
    protected abstract AttributeMonitor prepareAttributeMonitor();

    /**
     * @return the attributeMonitor
     */
    protected AttributeMonitor getAttributeMonitor() {
        return attributeMonitor;
    }
    
    /**
     * @return
     */
    protected abstract IAttributeMonitorListener prepareAttributeMonitorListener();

    /**
     * Fires the given activation using the supplied context.
     * Delegates to the doFire method
     * 
     * @param activation
     * @param context
     */
    public void fire(final Activation activation, final Context context) {
        checkNotNull(activation, "Cannot fire null activation!");
        checkNotNull(context,"Cannot fire activation with null context");
        ActivationState activationState = activation.getState();
        Atom atom = activation.getAtom();

        doFire(activation, activationState, atom, context);
    }

    /**
     * Checks whether the activation is part of the activation set of
     * the instance, then updates the state by calling activationStateTransition().
     * Finally, it executes each job that corresponds to the 
     * activation state using the supplied context.
     * 
     * @param activation
     * @param activationState
     * @param patternMatch
     * @param context
     */
    protected void doFire(final Activation activation, final ActivationState activationState, final Atom patternMatch, final Context context) {
        if (activations.contains(activationState, patternMatch)) {
            Collection<Job> jobs = specification.getJobs(activationState);
            activationStateTransition(activation, ActivationLifeCycleEvent.ACTIVATION_FIRES);
            for (Job job : jobs) {
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
    protected ActivationState activationStateTransition(final Activation activation, final ActivationLifeCycleEvent event) {
        checkNotNull(activation, "Cannot perform state transition on null activation!");
        checkNotNull(event, "Cannot perform state transition with null event!");
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        Atom atom = activation.getAtom();
        if (nextActivationState != null) {
            activations.remove(activationState, atom);
            activation.setState(nextActivationState);
            if (!nextActivationState.equals(ActivationState.INACTIVE)) {
                activations.put(nextActivationState, atom, activation);
            } else {
                attributeMonitor.unregisterFor(atom);
            }
        } else {
            nextActivationState = activationState;
        }
        activationNotificationProvider.notifyActivationChanged(activation, activationState, event);
        return nextActivationState;
    }
    
    

    /**
     * @return the specification
     */
    public RuleSpecification getSpecification() {
        return specification;
    }
    
    /**
     * @return the logger
     */
    protected abstract Logger getLogger();

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
     * 
     * @return the live table of activations
     */
    public Table<ActivationState, Atom, Activation> getActivations() {
        return activations;
    }
    
    
    /**
     * 
     * @return the live set of activations
     */
    public Collection<Activation> getAllActivations() {
        return activations.values();
    }

    /**
     * 
     * @param state
     * @return the live set of activations in the given state
     */
    public Collection<Activation> getActivations(final ActivationState state) {
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
        for (Cell<ActivationState, Atom, Activation> cell : activations.cellSet()) {
            Activation activation = cell.getValue();
            ActivationState activationState = activation.getState();
            activation.setState(ActivationState.INACTIVE);
            activationNotificationProvider.notifyActivationChanged(activation, activationState, ActivationLifeCycleEvent.MATCH_DISAPPEARS);
        } 
        this.activationNotificationProvider.dispose();
        this.attributeMonitor.removeAttributeMonitorListener(attributeMonitorListener);
        this.attributeMonitor.dispose();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("spec",specification).add("activations",activations).toString();
    }
}
