/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.evm;

import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;

/**
 * EVM {@link EventSourceSpecification} implementation.
 * 
 * @author Istvan David
 * 
 */
public class CepEventSourceSpecification implements EventSourceSpecification<IObservableComplexEventPattern> {

    private Automaton automaton;

    public CepEventSourceSpecification(Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public EventFilter<IObservableComplexEventPattern> createEmptyFilter() {
        return new EmptyEventFilter();
    }

    @Override
    public AbstractRuleInstanceBuilder<IObservableComplexEventPattern> getRuleInstanceBuilder(final EventRealm realm) {
        return new AbstractRuleInstanceBuilder<IObservableComplexEventPattern>() {
            @Override
            public void prepareRuleInstance(RuleInstance<IObservableComplexEventPattern> ruleInstance,
                    EventFilter<? super IObservableComplexEventPattern> filter) {
                CepEventSource source = new CepEventSource(CepEventSourceSpecification.this, (CepRealm) realm);
                CepEventHandler handler = new CepEventHandler(source, ruleInstance);
                source.setAutomaton(automaton);
                source.addHandler(handler);
            }
        };
    }
}
