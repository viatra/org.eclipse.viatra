/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.notification.ActivationNotificationProvider;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationProvider;

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
    private final Map<ActivationState, Map<EventAtom, Activation<EventAtom>>> activationsByState;
    private final Map<EventAtom, Map<ActivationState, Activation<EventAtom>>> activationsByEvent;
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
        this.specification = Objects.requireNonNull(specification, "Cannot create rule instance for null specification!");
        this.activationsByState = CollectionsFactory.createMap();
        this.activationsByEvent = CollectionsFactory.createMap();
        
        this.activationNotificationProvider = new DefaultActivationNotificationProvider();
    }
    
    public void setHandler(EventHandler<EventAtom> handler) {
        Preconditions.checkArgument(handler != null, "Handler cannot be null!");
        Preconditions.checkState(this.handler == null || handler.equals(this.handler), "Handler already set!");
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
        Objects.requireNonNull(activation, "Cannot fire null activation!");
        Objects.requireNonNull(context,"Cannot fire activation with null context");
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
     * @param activationState
     * @param atom
     * @param context
     */
    protected void doFire(final Activation<EventAtom> activation, final ActivationState activationState, final EventAtom atom, final Context context) {
        if (activationsByState.getOrDefault(activationState, Collections.emptyMap()).containsKey(atom)) {
            Collection<Job<EventAtom>> jobs = Optional.ofNullable(specification.getJobs(activationState)).orElse(Collections.emptyList());
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
        Objects.requireNonNull(activation, "Cannot perform state transition on null activation!");
        Objects.requireNonNull(event, "Cannot perform state transition with null event!");
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        EventAtom atom = activation.getAtom();
        if (nextActivationState != null) {
            // remove old activation
            Map<EventAtom, Activation<EventAtom>> byEvent = activationsByState.getOrDefault(activationState, Collections.emptyMap());
            Activation<EventAtom> removed = byEvent.remove(atom);
            if (removed != null && byEvent.isEmpty()) activationsByState.remove(activationState);
            activationsByEvent.computeIfPresent(atom, (k,map) -> {
                map.remove(activationState);
                return map.isEmpty()? null : map;
            });
            
            activation.setState(nextActivationState);
            if (!nextActivationState.isInactive()) {
                activationsByState.computeIfAbsent(nextActivationState, k -> CollectionsFactory.createMap()).put(atom, activation);
                activationsByEvent.computeIfAbsent(atom, k -> CollectionsFactory.createMap()).put(nextActivationState, activation);
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
     */
    @Override
    public boolean addActivationNotificationListener(final IActivationNotificationListener listener, final boolean fireNow) {
        return activationNotificationProvider.addActivationNotificationListener(listener, fireNow);
    }
    
    /**
     * Delegate method for {@link ActivationNotificationProvider#removeActivationNotificationListener}.
     * 
     * @param listener
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
        Preconditions.checkState(handler != null, "Cannot get filter, bacause handler is null!");
        return handler.getEventFilter();
    }

    public ActivationLifeCycle getLifeCycle() {
        return specification.getLifeCycle();
    }

    /**
     * 
     * @return a possibly live map of activations of a given atom
     * @since 2.3
     */
    public Map<ActivationState, Activation<EventAtom>> getActivationsFor(EventAtom atom) {
        return activationsByEvent.getOrDefault(atom, Collections.emptyMap());
    }
    
    
    /**
     * @return a stream of the current set of activations (live, do not modify while iterating)
     */
    public Stream<Activation<EventAtom>> streamAllActivations() {
        return activationsByState.values().stream().flatMap(byEvent -> byEvent.values().stream());
    }
    /**
     * 
     * @return an unmodifiable live view of the set of activations
     */
    public Set<Activation<EventAtom>> getAllActivations() {
        return allActivationsLiveView;
    }
    private final Set<Activation<EventAtom>> allActivationsLiveView = new Set<Activation<EventAtom>>() {

        @Override
        public int size() {
            // cheaply iterate over states, of which there are not as many as event atoms
            return activationsByState.entrySet().stream().mapToInt(entry -> entry.getValue().size()).sum();
        }

        @Override
        public boolean isEmpty() {
            return activationsByState.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Activation<?>) {
                Activation<?> activation = (Activation<?>) o;
                ActivationState state = activation.getState();
                Object atom = activation.getAtom();
                return activationsByEvent.getOrDefault(atom, Collections.emptyMap()).containsKey(state);
            } else return false;
        }

        @Override
        public Iterator<Activation<EventAtom>> iterator() {
            return streamAllActivations().iterator();
        }

        @Override
        public Object[] toArray() {
            return streamAllActivations().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // slightly suboptimal
            return streamAllActivations().collect(Collectors.toList()).toArray(a);
        }

        @Override
        public boolean add(Activation<EventAtom> e) {
            throw new UnsupportedOperationException("Unmodifiable view");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Unmodifiable view");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().allMatch(o -> this.contains(o));
        }

        @Override
        public boolean addAll(Collection<? extends Activation<EventAtom>> c) {
            throw new UnsupportedOperationException("Unmodifiable view");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Unmodifiable view");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Unmodifiable view");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Unmodifiable view");
        }
        
        @Override
        public int hashCode() {
            return streamAllActivations().mapToInt(act -> act.hashCode()).sum();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof Set<?>) {
                Set<?> set = (Set<?>) obj;
                return set.size() == this.size() && this.containsAll(set);
            } else return false;
        }
        
    };

    /**
     * 
     * @param state
     * @return the possibly live set of activations in the given state
     * @since 2.3
     */
    public Collection<Activation<EventAtom>> getActivationsFor(final ActivationState state) {
        Objects.requireNonNull(state, "Cannot return activations for null state");
        return activationsByState.getOrDefault(state, Collections.emptyMap()).values();
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
        for (Entry<ActivationState, Map<EventAtom, Activation<EventAtom>>> stateEntry : activationsByState.entrySet()) {
            for (Entry<EventAtom, Activation<EventAtom>> eventEntry : stateEntry.getValue().entrySet()) {
                Activation<EventAtom> activation = eventEntry.getValue();
                ActivationState activationState = activation.getState();
                activation.setState(specification.getLifeCycle().getInactiveState());
                activationNotificationProvider.notifyActivationRemoved(activation, activationState);
            }
        }
        this.activationNotificationProvider.dispose();
    }
    
    @Override
    public String toString() {
        return String.format("%s{spec=%s, activations=%s}", getClass().getName(), specification, activationsByState);
    }
}
