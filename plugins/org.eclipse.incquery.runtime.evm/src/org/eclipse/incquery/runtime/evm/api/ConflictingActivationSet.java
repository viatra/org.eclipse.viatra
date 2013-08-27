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

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 */
public class ConflictingActivationSet implements OrderedActivationSet{

    private final ConflictSet conflictSet;
    private final RuleBase ruleBase;
    private final Multimap<RuleSpecification<?>, EventFilter<?>> specificationFilters;
    public IActivationNotificationListener listener;

    /**
     *
     */
    public ConflictingActivationSet(final RuleBase ruleBase, final ConflictSet conflictSet, final Multimap<RuleSpecification<?>, EventFilter<?>> specificationFilters) {
        this.ruleBase = ruleBase;
        this.conflictSet = conflictSet;
        this.specificationFilters = specificationFilters;
        this.listener = new ConflictSetUpdatingListener(conflictSet);
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

    @SuppressWarnings("unchecked")
    private <EventAtom> EventFilter<? super EventAtom> toTypedFilter(final RuleSpecification<EventAtom> ruleSpecification,
            final EventFilter<?> eventFilter) {
        return (EventFilter<? super EventAtom>) eventFilter;
    }

    @Override
    public Activation<?> getNextActivation() {
        return conflictSet.getNextActivation();
    }

    @Override
    public Set<Activation<?>> getNextActivations() {
        return conflictSet.getNextActivations();
    }

    @Override
    public Set<Activation<?>> getConflictingActivations() {
        return conflictSet.getConflictingActivations();
    }
}
