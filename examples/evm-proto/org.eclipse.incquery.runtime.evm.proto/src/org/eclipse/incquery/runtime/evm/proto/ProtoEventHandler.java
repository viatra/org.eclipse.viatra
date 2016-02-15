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
package org.eclipse.incquery.runtime.evm.proto;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoEventHandler implements EventHandler<String> {

    private ProtoEventFilter filter;
    private ProtoEventSource source;
    private RuleInstance<String> instance;

    @Override
    public void handleEvent(Event<String> event) {
        ProtoEventType type = (ProtoEventType) event.getEventType();
        String eventAtom = event.getEventAtom();
        switch (type) {
        case PUSH:
            Activation<String> activation = instance.createActivation(eventAtom);
            instance.activationStateTransition(activation, type);
            break;
        default:
            System.err.println("Never happens!");
            break;
        }
    }

    @Override
    public EventSource<String> getSource() {
        return source;
    }

    @Override
    public EventFilter<String> getEventFilter() {
        return filter;
    }

    @Override
    public void dispose() {
    }

    /**
     * 
     */
    public ProtoEventHandler(ProtoEventSource source, ProtoEventFilter filter, RuleInstance<String> instance) {
        this.source = source;
        this.filter = filter;
        this.instance = instance;
    }
    
}
