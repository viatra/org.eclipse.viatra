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
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.evm.specific.DefaultAttributeMonitor;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

/**
 * TODO write documentation
 *  - manage activation set
 *  - reference rule specification
 *  - reference matcher
 *  - register match listener on matcher
 *  - send activation state changes to listeners
 * 
 * @author Abel Hegedus
 * 
 */
public abstract class RuleInstance<Match extends IPatternMatch>{

    /**
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

    private abstract class DefaultMatchEventProcessor {
        
        protected void processMatchEvent(Match match) {
            checkNotNull(match,"Cannot process null match!");
            Map<ActivationState, Activation<Match>> column = activations.column(match);
            if(column.size() > 0) {
                checkArgument(column.size() == 1, String.format("%s activations in the same rule for the same match",column.size() == 0 ? "No" : "Multiple"));
                Activation<Match> act = column.values().iterator().next();
                activationExists(act);
            } else {
                activationMissing(match);
            }
        }
        
        protected abstract void activationExists(Activation<Match> activation);
        protected abstract void activationMissing(Match match);
    }
    
    /**
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

    /**
     * created only through a RuleSpec
     * 
     * @param specification
     * @param engine
     */
    protected RuleInstance(final RuleSpecification<Match> specification) {
        this.specification = checkNotNull(specification, "Cannot create rule instance for null specification!");
        
        
        Comparator<Match> columnComparator = specification.getComparator();
        Ordering<ActivationState> rowComparator = Ordering.natural();
        if(columnComparator != null) {
            this.activations = TreeBasedTable.create(rowComparator, columnComparator);
        } else {
            this.activations = HashBasedTable.create();
        }
        
        this.activationNotificationProvider = new DefaultActivationNotificationProvider();

    }

    protected void prepareMatchUpdateListener() {
        IMatchProcessor<Match> matchAppearProcessor = checkNotNull(prepareMatchAppearProcessor(), "Prepared match appearance processor is null!");
        IMatchProcessor<Match> matchDisppearProcessor = checkNotNull(prepareMatchDisppearProcessor(), "Prepared match disappearance processor is null!");
        this.setMatchUpdateListener(new MatchUpdateAdapter<Match>(matchAppearProcessor,
                matchDisppearProcessor));
    }
    
    protected void prepateAttributeMonitor() {
        this.attributeMonitorListener = checkNotNull(prepareAttributeMonitorListener(), "Prepared attribute monitor listener is null!");
        this.attributeMonitor = checkNotNull(prepareAttributeMonitor(), "Prepared attribute monitor is null!");
        this.attributeMonitor.addCallbackOnMatchUpdate(attributeMonitorListener);
    }

    protected IMatchProcessor<Match> prepareMatchAppearProcessor() {
        return new DefaultMatchAppearProcessor();
    }
    
    protected IMatchProcessor<Match> prepareMatchDisppearProcessor() {
        return new DefaultMatchDisappearProcessor();
    }

    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        return new DefaultAttributeMonitor<Match>();
    }

    protected IAttributeMonitorListener<Match> prepareAttributeMonitorListener() {
        return new DefaultAttributeMonitorListener();
    }
    
    public void fire(final Activation<Match> activation, final Context context) {
        checkNotNull(activation, "Cannot fire null activation!");
        checkNotNull(context,"Cannot fire activation with null context");
        ActivationState activationState = activation.getState();
        Match patternMatch = activation.getPatternMatch();

        doFire(activation, activationState, patternMatch, context);
    }

    protected void doFire(final Activation<Match> activation, final ActivationState activationState, final Match patternMatch, final Context context) {
        if (activations.contains(activationState, patternMatch)) {
            Collection<Job<Match>> jobs = specification.getJobs(activationState);
            for (Job<Match> job : jobs) {
                job.execute(activation, context);
            }
            activationStateTransition(activation, ActivationLifeCycleEvent.ACTIVATION_FIRES);
            
        }
    }


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

    protected boolean addActivationNotificationListener(final IActivationNotificationListener listener, final boolean fireNow) {
        return activationNotificationProvider.addActivationNotificationListener(listener, fireNow);
    }

    protected boolean removeActivationNotificationListener(final IActivationNotificationListener listener) {
        return activationNotificationProvider.removeActivationNotificationListener(listener);
    }

    
    public Table<ActivationState, Match, Activation<Match>> getActivations() {
        return activations;
    }
    
    
    /**
     * 
     * @return
     */
    public Collection<Activation<Match>> getAllActivations() {
        return activations.values();
    }

    /**
     * 
     * @param state
     * @return
     */
    public Collection<Activation<Match>> getActivations(final ActivationState state) {
        checkNotNull(state, "Cannot return activations for null state");
        return activations.row(state).values();
    }

    /**
     * Rule instances are managed by their Agenda, they should be disposed through that!
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
        this.attributeMonitor.removeCallbackOnMatchUpdate(attributeMonitorListener);
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
