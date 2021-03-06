/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Peter Lunk, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.api;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.api.resolver.ScopedConflictSet;

/**
 * An RuleBase is associated to an {@link EventRealm} and it is responsible for creating, managing and disposing rules
 * in the Rule Engine. It provides an unmodifiable view for the collection of applicable activations.
 *
 * @author Tamas Szabo, Peter Lunk
 *
 */
public class RuleBase {

    protected final EventRealm eventRealm;
    protected final Map<RuleSpecification<?>, Map<EventFilter<?>, RuleInstance<?>>> ruleInstanceTable;
    protected final Agenda agenda;
    protected final Logger logger;

    /**
     * Instantiates a new RuleBase instance with the given {@link EventRealm} and {@link Agenda}.
     *
     * @param eventRealm
     *            the {@link EventRealm} instance
     * @param agenda
     *            the {@link Agenda} instance
     */
    protected RuleBase(final EventRealm eventRealm, final Agenda agenda) {
        this.eventRealm = Objects.requireNonNull(eventRealm, "Cannot create RuleBase with null event source");
        this.ruleInstanceTable = CollectionsFactory.createMap();
        this.logger = agenda.getLogger();
        this.agenda = agenda;
    }

    /**
     * Instantiates the given specification over the EventRealm of the RuleBase. If the specification was already
     * instantiated, the existing instance is returned.
     *
     * @param specification
     *            the rule to be instantiated
     * @return the created or existing rule instance
     */
    protected <EventAtom> RuleInstance<EventAtom> instantiateRule(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        Objects.requireNonNull(specification, "Cannot instantiate null rule!");
        Objects.requireNonNull(filter, "Cannot instantiate rule with null filter!");
        final RuleInstance<EventAtom> instance = findInstance(specification, filter);
        if (instance != null) {
            return instance;
        }
        final RuleInstance<EventAtom> rule = specification.instantiateRule(eventRealm, filter);
        rule.addActivationNotificationListener(agenda.getActivationListener(), true);
        ruleInstanceTable.computeIfAbsent(specification, k -> CollectionsFactory.createMap()).put(filter, rule);
        return rule;
    }

    /**
     * Removes and disposes of a rule instance.
     * 
     * @param instance
     * @return true, if the instance was part of the RuleBase
     */
    protected <EventAtom> boolean removeRule(final RuleInstance<EventAtom> instance) {
        Objects.requireNonNull(instance, "Cannot remove null rule instance!");
        return removeRule(instance.getSpecification(), instance.getFilter());
    }

    /**
     * Removes and disposes of a rule instance with the given specification.
     *
     * @param specification
     * @param filter
     *            the partial match used as filter
     * @return true, if the specification had an instance in the RuleBase
     */
    protected <EventAtom> boolean removeRule(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        Objects.requireNonNull(specification, "Cannot remove null rule specification!");
        Objects.requireNonNull(filter, "Cannot remove instance for null filter");
        final RuleInstance<?> instance = findInstance(specification, filter);
        if (instance != null) {
            instance.dispose();
            ruleInstanceTable.compute(specification, (k, old) -> {
                old.remove(filter);
                return old.isEmpty() ? null: old; 
            });
            return true;
        }
        return false;
    }

    /**
     * Disposes of each rule instance managed by the agenda.
     *
     */
    protected void dispose() {
        for (final RuleInstance<?> instance : getRuleInstances()) {
            instance.dispose();
        }
    }

    public EventRealm getEventRealm() {
        return eventRealm;
    }

    /**
     * @since 2.0
     */
    public Map<RuleSpecification<?>, Set<EventFilter<?>>> getRuleSpecificationMultimap() {
        return ruleInstanceTable.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().keySet()));
    }

    /**
     * @return an immutable copy of the set of rule instances
     */
    public Set<RuleInstance<?>> getRuleInstances() {
        return ruleInstanceTable.values().stream().flatMap(instancesByFilter -> instancesByFilter.values().stream()).collect(Collectors.toSet());
    }

    /**
     * Returns the filtered instance managed by the RuleBase for the given specification.
     *
     * @param specification
     * @param filter
     *            the partial match to be used as filter
     * @return the instance, if it exists, null otherwise
     */
    public <EventAtom> RuleInstance<EventAtom> getInstance(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        Objects.requireNonNull(specification, "Cannot get instance for null specification");
        Objects.requireNonNull(filter, "Cannot get instance for null filter");

        return findInstance(specification, filter);
    }

    @SuppressWarnings("unchecked")
    protected <EventAtom> RuleInstance<EventAtom> findInstance(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        return (RuleInstance<EventAtom>) ruleInstanceTable.getOrDefault(specification, Collections.emptyMap()).get(filter);
    }

    /**
     * Creates a scoped conflict set of the enabled activations of the provided rule specifications and filters using
     * the given conflict resolver. The set will be incrementally updated until disposed.
     *
     * @param conflictResolver
     * @param specifications
     * @since 2.0
     */
    public ScopedConflictSet createScopedConflictSet(final ConflictResolver conflictResolver,
            final Map<RuleSpecification<?>, Set<EventFilter<?>>> specifications) {
        final ScopedConflictSet set = new ScopedConflictSet(this, conflictResolver, specifications);
        return set;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Logger getLogger() {
        return logger;
    }

}
