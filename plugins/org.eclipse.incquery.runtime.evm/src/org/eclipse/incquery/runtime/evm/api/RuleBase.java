/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.specific.ArbitraryOrderConflictResolver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * An RuleBase is associated to each EMF instance model (more precisely {@link IncQueryEngine} and 
 * it is responsible for creating, managing and disposing rules in
 * the Rule Engine. It provides an unmodifiable view for the collection of applicable activations.
 * 
 * @author Tamas Szabo
 * 
 */
public class RuleBase {

    private final IncQueryEngine iqEngine;
    private final Multimap<RuleSpecification<? extends IPatternMatch>, RuleInstance<? extends IPatternMatch>> ruleInstanceMap;
    private Agenda agenda; 
    
    /**
     * Instantiates a new RuleBase instance with the given {@link IncQueryEngine}.
     * 
     * @param iqEngine
     *            the {@link IncQueryEngine} instance
     */
    protected RuleBase(final IncQueryEngine iqEngine) {
        this.iqEngine = checkNotNull(iqEngine, "Cannot create RuleBase with null IncQueryEngine");
        this.ruleInstanceMap = HashMultimap.create();
        this.agenda = new Agenda(this, new ArbitraryOrderConflictResolver());
    }

    /**
     * Instantiates the given specification over the IncQueryEngine of the RuleBase.
     * If the specification was already instantiated, the existing instance is returned.
     * 
     * @param specification the rule to be instantiated
     * @return the created or existing rule instance
     */
    protected <Match extends IPatternMatch> RuleInstance<Match> instantiateRule(
            final RuleSpecification<Match> specification, final Match filter) {
        checkNotNull(specification, "Cannot instantiate null rule!");
        checkNotNull(filter, "Cannot instantiate rule with null filter!");
        return internalInstantiateRule(specification, filter);
    }

    /**
     * Instantiates the given specification over the IncQueryEngine of the RuleBase.
     * If the specification was already instantiated, the existing instance is returned.
     * 
     * @param specification the rule to be instantiated
     * @return the created or existing rule instance
     */
    protected <Match extends IPatternMatch> RuleInstance<Match> instantiateRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, "Cannot instantiate null rule!");
        return internalInstantiateRule(specification, null);
    }

    private <Match extends IPatternMatch> RuleInstance<Match> internalInstantiateRule(final RuleSpecification<Match> specification,
            final Match filter) {
        if(ruleInstanceMap.containsKey(specification)) {
            return findInstance(specification, filter);
        }
        RuleInstance<Match> rule = specification.instantiateRule(iqEngine, filter);
        rule.addActivationNotificationListener(agenda.getActivationListener(), true);
        ruleInstanceMap.put(specification, rule);
        return rule;
    }

    /**
     * Removes and disposes of a rule instance. 
     * @param instance
     * @return true, if the instance was part of the RuleBase
     */
    protected <Match extends IPatternMatch> boolean removeRule(
            final RuleInstance<Match> instance) {
        checkNotNull(instance, "Cannot remove null rule instance!");
        return removeRule(instance.getSpecification(), instance.getFilter());
    }

    /**
     * Removes and disposes of a rule instance with the given specification.
     * 
     * @param specification
     * @param filter the partial match used as filter
     * @return true, if the specification had an instance in the RuleBase
     */
    protected <Match extends IPatternMatch> boolean removeRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, "Cannot remove null rule specification!");
        return internalRemoveRule(specification, null);
    }
    
    /**
     * Removes and disposes of a rule instance with the given specification.
     * 
     * @param specification
     * @param filter the partial match used as filter
     * @return true, if the specification had an instance in the RuleBase
     */
    protected <Match extends IPatternMatch> boolean removeRule(
            final RuleSpecification<Match> specification, Match filter) {
        checkNotNull(specification, "Cannot remove null rule specification!");
        checkNotNull(filter, "Cannot remove instance for null filter");
        return internalRemoveRule(specification, filter);
    }

    private <Match extends IPatternMatch> boolean internalRemoveRule(final RuleSpecification<Match> specification, Match filter) {
        RuleInstance<? extends IPatternMatch> instance = findInstance(specification, filter);
        if (instance != null) {
            instance.dispose();
            ruleInstanceMap.remove(specification, instance);
            return true;
        }
        return false;
    }

    /**
     * Disposes of each rule instance managed by the agenda.
     * 
     */
    protected void dispose() {
        for (RuleInstance<? extends IPatternMatch> instance : ruleInstanceMap
                .values()) {
            instance.dispose();
        }
    }

    /**
     * @return the iqEngine
     */
    public IncQueryEngine getIncQueryEngine() {
        return iqEngine;
    }

    /**
     * @return an unmodifiable view of the ruleInstanceMap
     */
    public Multimap<RuleSpecification<? extends IPatternMatch>, RuleInstance<? extends IPatternMatch>> getRuleInstanceMap() {
        return Multimaps.unmodifiableMultimap(ruleInstanceMap);
    }

    /**
     * @return an immutable copy of the set of rule instances
     */
    public Set<RuleInstance<? extends IPatternMatch>> getRuleInstances() {
        return ImmutableSet.copyOf(ruleInstanceMap.values());
    }

    /**
     * Returns the unfiltered instance managed by the RuleBase for the given specification.
     * 
     * @param specification
     * @return the instance, if it exists, null otherwise
     */
    public <Match extends IPatternMatch> RuleInstance<Match> getInstance(final RuleSpecification<Match> specification) {
        return findInstance(specification, null);
    }

    /**
     * Returns the filtered instance managed by the RuleBase for the given specification.
     * 
     * @param specification
     * @param filter the partial match to be used as filter
     * @return the instance, if it exists, null otherwise
     */
    public <Match extends IPatternMatch> RuleInstance<Match> getInstance(
            final RuleSpecification<Match> specification, Match filter) {
        checkNotNull(specification, "Cannot get instance for null specification");
        checkNotNull(filter, "Cannot get instance for null filter");
        
        return findInstance(specification, filter);
    }

    @SuppressWarnings("unchecked")
    private <Match extends IPatternMatch> RuleInstance<Match> findInstance(RuleSpecification<Match> specification, Match filter) {
        Collection<RuleInstance<? extends IPatternMatch>> instances = ruleInstanceMap.get(specification);
        if(instances.size() > 0) {
            
            Match realFilter = checkNotEmpty(filter);
            for (RuleInstance<? extends IPatternMatch> ruleInstance : instances) {
                IPatternMatch instanceFilter = ruleInstance.getFilter();
                if(realFilter != null && instanceFilter != null) {
                    if(realFilter.equals(instanceFilter)){
                        return (RuleInstance<Match>) ruleInstance;
                    }
                }
                if(realFilter == null && instanceFilter == null){
                    return (RuleInstance<Match>) ruleInstance;
                }
            }
        }
        return null;
    }

    /** 
     * Check a given match for emptyness.
     * 
     * @return null, if the match is empty, the match itself otherwise 
     */
    private <Match extends IPatternMatch> Match checkNotEmpty(Match match) {
        if(match != null) {
            for(Object o : match.toArray()) {
                if(o != null) {
                    return match;
                }
            }
        }
        return null;
    }

    /**
     * @return the agenda
     */
    public Agenda getAgenda() {
        return agenda;
    }
    
//    
//
//    /**
//     * Returns the activations for the given specification, if it has
//     * an instance in the RuleBase.
//     *
//     * @param specification
//     * @return the activations for the specification, if exists, empty set otherwise
//     */
//    public <Match extends IPatternMatch> Collection<Activation<Match>> getActivations(
//            final RuleSpecification<Match> specification) {
//                return getActivations(specification, null);
//            }
//
//    /**
//     * Returns the activations for the given specification, if it has
//     * an instance in the RuleBase.
//     *
//     * @param specification
//     * @param filter the partial match to be used as filter
//     * @return the activations for the specification, if exists, empty set otherwise
//     */
//    public <Match extends IPatternMatch> Collection<Activation<Match>> getActivations(
//            final RuleSpecification<Match> specification, Match filter) {
//        RuleInstance<Match> instance = getInstance(specification, filter);
//        if (instance == null) {
//            return Collections.emptySet();
//        } else {
//            return instance.getAllActivations();
//        }
//    }


}
