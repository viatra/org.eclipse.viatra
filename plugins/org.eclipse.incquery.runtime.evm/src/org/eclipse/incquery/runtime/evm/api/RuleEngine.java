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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 * 
 */
public class RuleEngine {

    /**
     * 
     */
    private static final String RULE_SPECIFICATION_MUST_BE_SPECIFIED = "Rule specification must be specified!";
    private Agenda agenda;

    protected RuleEngine(final Agenda agenda) {
        this.agenda = checkNotNull(agenda, "Cannot create rule engine with null agenda!");
    }

    public static RuleEngine create(final Agenda agenda) {
        return new RuleEngine(agenda);
    }

    /**
     * @return the agenda
     */
    protected Agenda getAgenda() {
        return agenda;
    }

    public IncQueryEngine getIncQueryEngine() {
        return getAgenda().getIncQueryEngine();
    }

    public Multimap<ActivationState, Activation<?>> getActivations() {
        return ImmutableMultimap.copyOf(agenda.getActivations());
    }

    public Set<Activation<?>> getActivations(final ActivationState state) {
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getActivations(state));
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return ImmutableSet.copyOf(agenda.getActivations(specification));
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            final RuleSpecification<Match> specification, final ActivationState state) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getInstance(specification).getActivations(state));
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean addRule(
            final RuleSpecification<Match> specification) {
        return addRule(specification, false);
    }
    
    public <Match extends IPatternMatch> boolean addRule(
            final RuleSpecification<Match> specification, boolean fireNow) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        RuleInstance<Match> instance = agenda.instantiateRule(specification);
        if(fireNow) {
            fireActivations(fireNow, instance);
        }
        return true;
    }

    protected <Match extends IPatternMatch> void fireActivations(boolean fireNow,
            RuleInstance<Match> instance) {
        Context context = new Context();
        for (Activation<Match> act : instance.getAllActivations()) {
            act.fire(context);
        }
    }

    public Set<RuleSpecification<? extends IPatternMatch>> getRules() {
        return ImmutableSet.copyOf(agenda.getRuleInstanceMap().keySet());
    }

    public <Match extends IPatternMatch> boolean removeRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return agenda.removeRule(specification);
    }

    public void dispose() {
        agenda.dispose();
    }
}
