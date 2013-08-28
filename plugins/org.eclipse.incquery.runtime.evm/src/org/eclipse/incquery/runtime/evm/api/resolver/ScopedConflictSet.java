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
package org.eclipse.incquery.runtime.evm.api.resolver;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleBase;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 */
public class ScopedConflictSet implements ConflictSet{

    private final ChangeableConflictSet changeableConflictSet;
    private final RuleBase ruleBase;
    private final Multimap<RuleSpecification<?>, EventFilter<?>> specificationFilters;
    public IActivationNotificationListener listener;

    /**
     *
     */
    public <CSet extends ChangeableConflictSet> ScopedConflictSet(final RuleBase ruleBase, final ConflictResolver<CSet> conflictResolver, final Multimap<RuleSpecification<?>, EventFilter<?>> specificationFilters) {
        this.ruleBase = ruleBase;
        this.changeableConflictSet = conflictResolver.createConflictSet();
        this.specificationFilters = ImmutableMultimap.copyOf(specificationFilters);
        this.listener = new ConflictSetUpdater(changeableConflictSet);
        for (final Entry<RuleSpecification<?>, EventFilter<?>> entry : specificationFilters.entries()) {
            final RuleSpecification<?> ruleSpecification = entry.getKey();
            registerListenerFromInstance(ruleSpecification, entry.getValue());
        }
    }

    /**
     * @return the specificationFilters
     */
    public Multimap<RuleSpecification<?>, EventFilter<?>> getSpecificationFilters() {
        return specificationFilters;
    }

    /**
     * @return the listener
     */
    protected IActivationNotificationListener getListener() {
        return listener;
    }

    public void dispose() {
        for (final Entry<RuleSpecification<?>, EventFilter<?>> entry : specificationFilters.entries()) {
            final RuleSpecification<?> ruleSpecification = entry.getKey();
            unregisterListenerFromInstance(ruleSpecification, entry.getValue());
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
    public ConflictResolver<? extends ChangeableConflictSet> getConflictResolver() {
        return changeableConflictSet.getConflictResolver();
    }
}
