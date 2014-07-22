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

import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;

import com.google.common.collect.Sets;

/**
 * EVM {@link EventSource} implementation.
 * 
 * @author Istvan David
 * 
 */
public class CepEventSource implements EventSource<IObservableComplexEventPattern> {

    private CepRealm realm;
    private CepEventSourceSpecification specification;
    private Automaton automaton;
    private Set<CepEventHandler> handlers = Sets.newHashSet();

    public CepEventSource(CepEventSourceSpecification specification, CepRealm realm) {
        this.specification = specification;
        this.realm = realm;
        realm.addSource(this);
    }

    @Override
    public EventSourceSpecification<IObservableComplexEventPattern> getSourceSpecification() {
        return specification;
    }

    @Override
    public EventRealm getRealm() {
        return realm;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public void dispose() {
    }

    public void addHandler(CepEventHandler handler) {
        handlers.add(handler);
    }

    public void pushEvent(CepEventType type, IObservableComplexEventPattern op) {
        CepEvent event = new CepEvent(type, op);
        for (CepEventHandler handler : handlers) {
            handler.handleEvent(event);
        }
    }
}