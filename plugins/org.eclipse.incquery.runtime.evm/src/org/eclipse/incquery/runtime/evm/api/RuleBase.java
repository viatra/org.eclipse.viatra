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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

/**
 * An RuleBase is associated to an {@link EventRealm} and 
 * it is responsible for creating, managing and disposing rules in
 * the Rule Engine. It provides an unmodifiable view for the collection of applicable activations.
 * 
 * @author Tamas Szabo
 * 
 */
public class RuleBase {

    private final EventRealm eventRealm;
    private final Table<RuleSpecification<?>,EventFilter<?>,RuleInstance<?>> ruleInstanceTable;
    private final Agenda agenda; 
    private Logger logger;
    
    /**
     * Instantiates a new RuleBase instance with the given {@link EventRealm}.
     * 
     * @param eventRealm
     *            the {@link EventRealm} instance
     */
    protected RuleBase(final EventRealm eventRealm) {
        this.eventRealm = checkNotNull(eventRealm, "Cannot create RuleBase with null event source");
        this.ruleInstanceTable = HashBasedTable.create();
        this.agenda = new Agenda(this, new ArbitraryOrderConflictResolver());
        this.logger = Logger.getLogger(this.toString());
    }

    /**
     * Instantiates the given specification over the EventRealm of the RuleBase.
     * If the specification was already instantiated, the existing instance is returned.
     * 
     * @param specification the rule to be instantiated
     * @return the created or existing rule instance
     */
    protected <EventAtom> RuleInstance<EventAtom> instantiateRule(
            final RuleSpecification<EventAtom> specification, final EventFilter<EventAtom> filter) {
        checkNotNull(specification, "Cannot instantiate null rule!");
        checkNotNull(filter, "Cannot instantiate rule with null filter!");
        if(ruleInstanceTable.containsRow(specification)) {
            return findInstance(specification, filter);
        }
        RuleInstance<EventAtom> rule = specification.instantiateRule(eventRealm, filter);
        rule.addActivationNotificationListener(agenda.getActivationListener(), true);
        ruleInstanceTable.put(specification, filter, rule);
        return rule;
    }

    /**
     * Removes and disposes of a rule instance. 
     * @param instance
     * @return true, if the instance was part of the RuleBase
     */
    protected <EventAtom> boolean removeRule(
            final RuleInstance<EventAtom> instance) {
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
    protected <EventAtom> boolean removeRule(
            final RuleSpecification<EventAtom> specification, EventFilter<EventAtom> filter) {
        checkNotNull(specification, "Cannot remove null rule specification!");
        checkNotNull(filter, "Cannot remove instance for null filter");
        RuleInstance<?> instance = findInstance(specification, filter);
        if (instance != null) {
            instance.dispose();
            ruleInstanceTable.remove(specification, filter);
            return true;
        }
        return false;
    }

    /**
     * Disposes of each rule instance managed by the agenda.
     * 
     */
    protected void dispose() {
        for (RuleInstance<?> instance : ruleInstanceTable
                .values()) {
            instance.dispose();
        }
    }

    /**
     * @return the eventRealm
     */
    public EventRealm getEventRealm() {
        return eventRealm;
    }

    public Multimap<RuleSpecification<?>, EventFilter<?>> getRuleSpecificationMultimap(){
        Multimap<RuleSpecification<?>, EventFilter<?>> ruleMap = HashMultimap.create();
        Map<RuleSpecification<?>, Map<EventFilter<?>, RuleInstance<?>>> rowMap = ruleInstanceTable.rowMap();
        for (Entry<RuleSpecification<?>, Map<EventFilter<?>, RuleInstance<?>>> entry : rowMap.entrySet()) {
            ruleMap.putAll(entry.getKey(), entry.getValue().keySet());
        }
        return ruleMap;
    }
    
    /**
     * @return an immutable copy of the set of rule instances
     */
    public Set<RuleInstance<?>> getRuleInstances() {
        return ImmutableSet.copyOf(ruleInstanceTable.values());
    }

    /**
     * Returns the filtered instance managed by the RuleBase for the given specification.
     * 
     * @param specification
     * @param filter the partial match to be used as filter
     * @return the instance, if it exists, null otherwise
     */
    public <EventAtom> RuleInstance<EventAtom> getInstance(
            final RuleSpecification<EventAtom> specification, EventFilter<EventAtom> filter) {
        checkNotNull(specification, "Cannot get instance for null specification");
        checkNotNull(filter, "Cannot get instance for null filter");
        
        return findInstance(specification, filter);
    }

    @SuppressWarnings("unchecked")
    private <EventAtom> RuleInstance<EventAtom> findInstance(RuleSpecification<EventAtom> specification, EventFilter<EventAtom> filter) {
//        Collection<RuleInstance> instances = ruleInstanceTable.get(specification);
//        if(instances.size() > 0) {
//            
//            // Atom realFilter = checkNotEmpty(filter);
//            Atom realFilter = filter;
//            // always use filter (EmptyAtom.INSTANCE)
//            for (RuleInstance ruleInstance : instances) {
//                Atom instanceFilter = ruleInstance.getFilter();
//                if (realFilter != null && instanceFilter != null && realFilter.equals(instanceFilter)) {
//                    return ruleInstance;
//                }
//                if(realFilter == null && instanceFilter == null){
//                    return ruleInstance;
//                }
//            }
//        }
//        return null;
//        EventFilter<EventAtom> realFilter = filter;
//        if(filter.isEmpty()) {
//            realFilter = EmptyAtom.INSTANCE;
//        }
        return (RuleInstance<EventAtom>) ruleInstanceTable.get(specification, filter);
        
    }

    /**
     * @return the agenda
     */
    public Agenda getAgenda() {
        return agenda;
    }
    
    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }
    
}
