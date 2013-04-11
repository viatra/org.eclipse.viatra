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
import java.util.Comparator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.MatchUpdateAdapter;
import org.eclipse.incquery.runtime.evm.notification.ActivationNotificationProvider;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationProvider;
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.evm.specific.DefaultAttributeMonitor;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

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
public abstract class RuleInstance<Match extends IPatternMatch> implements IActivationNotificationProvider{

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
                for (Activation<Match> activation : getAllActivations()) {
                    listener.activationChanged(activation, ActivationState.INACTIVE, ActivationLifeCycleEvent.MATCH_APPEARS);
                }
            }
        }
    }

    /**
     * This class is the common supertype for default event prcessors
     *  in the rule instance.
     *  
     * @author Abel Hegedus
     *
     */
    private abstract class DefaultMatchEventProcessor {
        
        /**
         * This method is called with the match corresponding to the
         * activation that is affected by the event.
         * 
         * @param match
         */
        protected void processMatchEvent(Match match) {
            checkNotNull(match,"Cannot process null match!");
            
            // TODO check filter (this might be expensive!!!)
            if(!match.isCompatibleWith(filter)) {
                return;
            }
            
            Map<ActivationState, Activation<Match>> column = activations.column(match);
            if(column.size() > 0) {
                checkArgument(column.size() == 1, String.format("%s activations in the same rule for the same match",column.size() == 0 ? "No" : "Multiple"));
                Activation<Match> act = column.values().iterator().next();
                activationExists(act);
            } else {
                activationMissing(match);
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
        protected abstract void activationMissing(Match match);
    }
    
    /**
     * Default implementation for the event handler when a match appears.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultMatchAppearProcessor extends DefaultMatchEventProcessor implements IMatchProcessor<Match> {

        @Override
        protected void activationExists(Activation<Match> activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_APPEARS);
        }

        @Override
        protected void activationMissing(Match match) {
            Activation<Match> activation = new Activation<Match>(RuleInstance.this, match);
            if(specification.getLifeCycle().containsTo(ActivationState.UPDATED)) {
                attributeMonitor.registerFor(match);
            }
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_APPEARS);
        }

        @Override
        public void process(Match match) {
            processMatchEvent(match);
        }
    }

    /**
     * Default implementation for the event handler when a match disappears.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultMatchDisappearProcessor extends DefaultMatchEventProcessor implements IMatchProcessor<Match> {

        @Override
        protected void activationExists(Activation<Match> activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_DISAPPEARS);
        }

        @Override
        protected void activationMissing(Match match) {
            getLogger().error(String.format("Match %s disappeared without existing activation in rule instance %s!",match,this));
        }

        @Override
        public void process(Match match) {
            processMatchEvent(match);
        }
    }

    /**
     * Default implementation for the event handler when a match updates.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultAttributeMonitorListener extends DefaultMatchEventProcessor implements IAttributeMonitorListener<Match> {
        @Override
        public void notifyUpdate(final Match match) {
            processMatchEvent(match);
        }

        @Override
        protected void activationExists(Activation<Match> activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_UPDATES);
        }

        @Override
        protected void activationMissing(Match match) {
            getLogger().error(String.format("Match %s updated without existing activation in rule instance %s!",match,this));
        }

    }

    private final RuleSpecification<Match> specification;
    private Table<ActivationState, Match, Activation<Match>> activations;
    private ActivationNotificationProvider activationNotificationProvider;
    private IMatchUpdateListener<Match> matchUpdateListener;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private AttributeMonitor<Match> attributeMonitor;
    private final Match filter;
    
    /**
     * @return the filter
     */
    public Match getFilter() {
        return filter;
    }

    /**
     * Creates an instance using a RuleSpecification.
     * 
     * @param specification
     * @param engine
     * @throws IllegalArgumentException if filter is mutable
     */
    protected RuleInstance(final RuleSpecification<Match> specification, Match filter) {
        this.specification = checkNotNull(specification, "Cannot create rule instance for null specification!");
        Comparator<Match> columnComparator = specification.getComparator();
        Ordering<ActivationState> rowComparator = Ordering.natural();
        if(columnComparator != null) {
            this.activations = TreeBasedTable.create(rowComparator, columnComparator);
        } else {
            this.activations = HashBasedTable.create();
        }
        
        this.activationNotificationProvider = new DefaultActivationNotificationProvider();
        
        if(filter != null) {
            Preconditions.checkArgument(!filter.isMutable(),String.format("Mutable filter %s is used in rule instance!",filter));
        }
        this.filter = filter;
    }

    /**
     * Prepares the event processors for match appearance and disappearance.
     */
    protected void prepareMatchUpdateListener() {
        IMatchProcessor<Match> matchAppearProcessor = checkNotNull(prepareMatchAppearProcessor(), "Prepared match appearance processor is null!");
        IMatchProcessor<Match> matchDisppearProcessor = checkNotNull(prepareMatchDisppearProcessor(), "Prepared match disappearance processor is null!");
        this.setMatchUpdateListener(new MatchUpdateAdapter<Match>(matchAppearProcessor,
                matchDisppearProcessor));
    }
    
    /**
     * Prepares the attribute monitor
     */
    protected void prepateAttributeMonitor() {
        this.attributeMonitorListener = checkNotNull(prepareAttributeMonitorListener(), "Prepared attribute monitor listener is null!");
        this.attributeMonitor = checkNotNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        this.attributeMonitor.addAttributeMonitorListener(attributeMonitorListener);
    }

    /**
     * @return the match appears event processor
     */
    protected IMatchProcessor<Match> prepareMatchAppearProcessor() {
        return new DefaultMatchAppearProcessor();
    }
    
    /**
     * @return the match disappears event processor
     */
    protected IMatchProcessor<Match> prepareMatchDisppearProcessor() {
        return new DefaultMatchDisappearProcessor();
    }

    /**
     * @return the attribute monitor
     */
    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        return new DefaultAttributeMonitor<Match>();
    }

    /**
     * @return the attribute monitor listener
     */
    protected IAttributeMonitorListener<Match> prepareAttributeMonitorListener() {
        return new DefaultAttributeMonitorListener();
    }
    
    /**
     * Fires the given activation using the supplied context.
     * Delegates to the doFire method
     * 
     * @param activation
     * @param context
     */
    public void fire(final Activation<Match> activation, final Context context) {
        checkNotNull(activation, "Cannot fire null activation!");
        checkNotNull(context,"Cannot fire activation with null context");
        ActivationState activationState = activation.getState();
        Match patternMatch = activation.getPatternMatch();

        doFire(activation, activationState, patternMatch, context);
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
    protected void doFire(final Activation<Match> activation, final ActivationState activationState, final Match patternMatch, final Context context) {
        if (activations.contains(activationState, patternMatch)) {
            Collection<Job<Match>> jobs = specification.getJobs(activationState);
            activationStateTransition(activation, ActivationLifeCycleEvent.ACTIVATION_FIRES);
            for (Job<Match> job : jobs) {
                job.execute(activation, context);
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
    protected ActivationState activationStateTransition(final Activation<Match> activation, final ActivationLifeCycleEvent event) {
        checkNotNull(activation, "Cannot perform state transition on null activation!");
        checkNotNull(event, "Cannot perform state transition with null event!");
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        Match patternMatch = activation.getPatternMatch();
        if (nextActivationState != null) {
            activations.remove(activationState, patternMatch);
            activation.setState(nextActivationState);
            if (!nextActivationState.equals(ActivationState.INACTIVE)) {
                activations.put(nextActivationState, patternMatch, activation);
            } else {
                attributeMonitor.unregisterFor(patternMatch);
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
    public RuleSpecification<Match> getSpecification() {
        return specification;
    }
    
    /**
     * @return the matchUpdateListener
     */
    protected IMatchUpdateListener<Match> getMatchUpdateListener() {
        return matchUpdateListener;
    }

    /**
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * @param matchUpdateListener the matchUpdateListener to set
     */
    public void setMatchUpdateListener(IMatchUpdateListener<Match> matchUpdateListener) {
        this.matchUpdateListener = matchUpdateListener;
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
     * 
     * @return the live table of activations
     */
    public Table<ActivationState, Match, Activation<Match>> getActivations() {
        return activations;
    }
    
    
    /**
     * 
     * @return the live set of activations
     */
    public Collection<Activation<Match>> getAllActivations() {
        return activations.values();
    }

    /**
     * 
     * @param state
     * @return the live set of activations in the given state
     */
    public Collection<Activation<Match>> getActivations(final ActivationState state) {
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
        for (Cell<ActivationState, Match, Activation<Match>> cell : activations.cellSet()) {
            Activation<Match> activation = cell.getValue();
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
