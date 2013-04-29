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
package org.eclipse.incquery.runtime.evm.specific.rule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.MatchUpdateAdapter;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.Atom;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.evm.specific.DefaultAttributeMonitor;
import org.eclipse.incquery.runtime.evm.specific.event.PatternMatchAtom;

/**
 * This class implements a rule instance that creates activations based on the match set of a single matcher.
 * 
 * @author Abel Hegedus
 *
 */
public class SimpleMatcherRuleInstance<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> extends RuleInstance {
    
    private Matcher matcher;
    private IMatchUpdateListener<Match> matchUpdateListener;
    
    /**
     * Creates an instance using the given specification. 
     */
    protected SimpleMatcherRuleInstance(final SimpleMatcherRuleSpecification<Match,Matcher> specification, final Atom filter) {
        super(specification, filter);
    }
    
    /**
     * Prepares the instance with the given matcher.
     * 
     * @param matcher
     */
    protected void prepareInstance(final Matcher matcher) {
        prepareMatchUpdateListener();
        prepateAttributeMonitor();
        
        this.matcher = matcher;
        this.matcher.getEngine().addMatchUpdateListener(matcher, getMatchUpdateListener(), true);
//        this.matcher.addCallbackOnMatchUpdate(getMatchUpdateListener(), true);
    }
    
    /**
     * @return the matcher
     */
    public IncQueryMatcher<?> getMatcher() {
        return matcher;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.evm.api.RuleInstance#dispose()
     */
    @Override
    protected void dispose() {
        super.dispose();
        this.matcher.getEngine().removeMatchUpdateListener(matcher, getMatchUpdateListener());
//        this.matcher.removeCallbackOnMatchUpdate(getMatchUpdateListener());
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.evm.api.RuleInstance#getLogger()
     */
    @Override
    public Logger getLogger() {
        return matcher.getEngine().getLogger();
    }

    /**
     * @return the matchUpdateListener
     */
    protected IMatchUpdateListener<Match> getMatchUpdateListener() {
        return matchUpdateListener;
    }

    /**
     * @param matchUpdateListener the matchUpdateListener to set
     */
    public void setMatchUpdateListener(IMatchUpdateListener<Match> matchUpdateListener) {
        this.matchUpdateListener = matchUpdateListener;
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
    protected AttributeMonitor prepareAttributeMonitor(){
        return new DefaultAttributeMonitor<Match>();
    }

    /**
     * @return the attribute monitor listener
     */
    protected IAttributeMonitorListener prepareAttributeMonitorListener() {
        return new DefaultAttributeMonitorListener();
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
         * @param atom
         */
        protected void processMatchEvent(PatternMatchAtom<Match> atom) {
            checkNotNull(atom,"Cannot process null match!");
            
            // TODO check filter (this might be expensive!!!)
            if(!getFilter().isCompatibleWith(atom)) {
                return;
            }
            
            Map<ActivationState, Activation> column = getActivations().column(atom);
            if(column.size() > 0) {
                checkArgument(column.size() == 1, String.format("%s activations in the same rule for the same match",column.size() == 0 ? "No" : "Multiple"));
                Activation act = column.values().iterator().next();
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
        protected abstract void activationExists(Activation activation);
        
        /**
         * This method is called by processMatchEvent if the activation
         * does not exists for the given match.
         * 
         * @param match
         */
        protected abstract void activationMissing(PatternMatchAtom<Match> atom);
    }

    /**
     * Default implementation for the event handler when a match appears.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultMatchAppearProcessor extends DefaultMatchEventProcessor implements IMatchProcessor<Match> {
    
        @Override
        protected void activationExists(Activation activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_APPEARS);
        }
    
        @Override
        protected void activationMissing(PatternMatchAtom<Match> atom) {
            Activation activation = createActivation(atom);
            if(getSpecification().getLifeCycle().containsTo(ActivationState.UPDATED)) {
                getAttributeMonitor().registerFor(atom);
            }
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_APPEARS);
        }
    
        @Override
        public void process(Match match) {
            PatternMatchAtom<Match> atom = new PatternMatchAtom<Match>(match);
            processMatchEvent(atom);
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
        protected void activationExists(Activation activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_DISAPPEARS);
        }
    
        @Override
        protected void activationMissing(PatternMatchAtom<Match> atom) {
            getLogger().error(String.format("Atom %s disappeared without existing activation in rule instance %s!",atom,this));
        }
    
        @Override
        public void process(Match match) {
            PatternMatchAtom<Match> atom = new PatternMatchAtom<Match>(match);
            processMatchEvent(atom);
        }
    }

    /**
     * Default implementation for the event handler when a match updates.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultAttributeMonitorListener extends DefaultMatchEventProcessor implements IAttributeMonitorListener {
        @SuppressWarnings("unchecked")
        @Override
        public void notifyUpdate(final Atom atom) {
            if(atom instanceof PatternMatchAtom<?>) {
                processMatchEvent((PatternMatchAtom<Match>) atom);
            }
        }
    
        @Override
        protected void activationExists(Activation activation) {
            activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_UPDATES);
        }
    
        @Override
        protected void activationMissing(PatternMatchAtom<Match> atom) {
            getLogger().error(String.format("Atom %s updated without existing activation in rule instance %s!",atom,this));
        }
    
    }
}
