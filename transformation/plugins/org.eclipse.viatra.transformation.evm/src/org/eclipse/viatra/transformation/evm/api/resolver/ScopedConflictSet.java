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
package org.eclipse.viatra.transformation.evm.api.resolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleBase;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Abel Hegedus
 *
 */
public class ScopedConflictSet implements ConflictSet{

    private final ChangeableConflictSet changeableConflictSet;
    private final RuleBase ruleBase;
    private final Map<RuleSpecification<?>, Set<EventFilter<?>>> specificationFilters;
    public IActivationNotificationListener listener;

    /**
     * @since 2.0
     */
    public ScopedConflictSet(final RuleBase ruleBase, final ConflictResolver conflictResolver, final Map<RuleSpecification<?>, Set<EventFilter<?>>> specificationFilters) {
        this.ruleBase = ruleBase;
        this.changeableConflictSet = conflictResolver.createConflictSet();
        this.specificationFilters = Collections.unmodifiableMap(specificationFilters.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> new HashSet<>(entry.getValue()))));
        this.listener = new ConflictSetUpdater(changeableConflictSet);
        for (final Entry<RuleSpecification<?>, Set<EventFilter<?>>> entry : specificationFilters.entrySet()) {
            final RuleSpecification<?> ruleSpecification = entry.getKey();
            entry.getValue().forEach(value -> registerListenerFromInstance(ruleSpecification, value));
        }
    }

    /**
     * @since 2.0
     */
    public Map<RuleSpecification<?>, Set<EventFilter<?>>> getSpecificationFilters() {
        return specificationFilters;
    }

    protected IActivationNotificationListener getListener() {
        return listener;
    }

    public void dispose() {
        for (final Entry<RuleSpecification<?>, Set<EventFilter<?>>> entry : specificationFilters.entrySet()) {
            final RuleSpecification<?> ruleSpecification = entry.getKey();
            entry.getValue().forEach(value -> unregisterListenerFromInstance(ruleSpecification, value));
        }
    }

    private <EventAtom> void unregisterListenerFromInstance(final RuleSpecification<EventAtom> ruleSpecification,
            final EventFilter<?> eventFilter) {
        final RuleInstance<?> instance = ruleBase.getInstance(ruleSpecification, toTypedFilter(ruleSpecification, eventFilter));
        if(instance != null) {
            instance.removeActivationNotificationListener(listener);
        }
    }

    private <EventAtom> void registerListenerFromInstance(final RuleSpecification<EventAtom> ruleSpecification,
            final EventFilter<?> eventFilter) {
        final RuleInstance<?> instance = ruleBase.getInstance(ruleSpecification, toTypedFilter(ruleSpecification, eventFilter));
        if(instance != null) {
            instance.addActivationNotificationListener(listener, true);
        }
    }

    @SuppressWarnings("unchecked")
    private <EventAtom> EventFilter<? super EventAtom> toTypedFilter(final RuleSpecification<EventAtom> ruleSpecification,
            final EventFilter<?> eventFilter) {
        return (EventFilter<? super EventAtom>) eventFilter;
    }

    @Override
    public Activation<?> getNextActivation() {
        return changeableConflictSet.getNextActivation();
    }

    @Override
    public Set<Activation<?>> getNextActivations() {
        return changeableConflictSet.getNextActivations();
    }

    @Override
    public Set<Activation<?>> getConflictingActivations() {
        return changeableConflictSet.getConflictingActivations();
    }

    @Override
    public ConflictResolver getConflictResolver() {
        return changeableConflictSet.getConflictResolver();
    }
}
