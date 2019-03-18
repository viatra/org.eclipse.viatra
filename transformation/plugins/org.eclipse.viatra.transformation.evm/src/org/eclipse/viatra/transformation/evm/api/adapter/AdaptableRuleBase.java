/*******************************************************************************
 * Copyright (c) 2010-2013, Peter Lunk, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.transformation.evm.api.Agenda;
import org.eclipse.viatra.transformation.evm.api.RuleBase;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;

/**
 * A {@link RuleBase} that allows {@link IEVMListener}s to listen to the creation and removal of EVM
 * {@link RuleSpecification}s
 * 
 * @author Peter Lunk
 *
 */
public class AdaptableRuleBase extends RuleBase {
    protected final AdaptableEVM vm;

    public AdaptableRuleBase(EventRealm eventRealm, Agenda agenda, AdaptableEVM adapterContainer) {
        super(eventRealm, agenda);
        this.vm = adapterContainer;
    }

    @Override
    protected <EventAtom> RuleInstance<EventAtom> instantiateRule(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        RuleInstance<EventAtom> instance = super.instantiateRule(specification, filter);
        vm.addedRule(specification, filter);
        return instance;
    }

    @Override
    protected <EventAtom> boolean removeRule(final RuleSpecification<EventAtom> specification,
            final EventFilter<? super EventAtom> filter) {
        boolean result = super.removeRule(specification, filter);
        vm.removedRule(specification, filter);
        return result;
    }
    
    @Override
    protected void dispose() {
        super.dispose();
        vm.dispose();
    }

}
