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
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;

/**
 * EVM {@link EventHandler} implementation.
 * 
 * @author Istvan David
 * 
 */
public class CepEventHandler implements EventHandler<IObservableComplexEventPattern> {

    private EmptyEventFilter emptyFilter;
    private CepEventSource source;
    private RuleInstance<IObservableComplexEventPattern> instance;

    @Override
    public void handleEvent(Event<IObservableComplexEventPattern> event) {
        IObservableComplexEventPattern eventAtom = event.getEventAtom();
        instance.activationStateTransition(instance.createActivation(eventAtom), CepEventType.APPEARED);
    }

    @Override
    public EventSource<IObservableComplexEventPattern> getSource() {
        return source;
    }

    @Override
    public EventFilter<IObservableComplexEventPattern> getEventFilter() {
        return emptyFilter;
    }

    @Override
    public void dispose() {
    }

    public CepEventHandler(CepEventSource source, RuleInstance<IObservableComplexEventPattern> instance) {
        this.source = source;
        this.emptyFilter = new EmptyEventFilter();
        this.instance = instance;
    }
}