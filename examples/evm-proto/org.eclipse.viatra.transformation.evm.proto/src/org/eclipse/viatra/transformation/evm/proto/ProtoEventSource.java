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
package org.eclipse.viatra.transformation.evm.proto;

import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoEventSource implements EventSource<String> {

    private ProtoEventSourceSpecification spec;
    private ProtoRealm realm;
    private Set<ProtoEventHandler> handlers = Sets.newHashSet();

    @Override
    public EventSourceSpecification<String> getSourceSpecification() {
        return spec;
    }

    @Override
    public EventRealm getRealm() {
        return realm;
    }

    @Override
    public void dispose() {
    }

    /**
     * 
     */
    public ProtoEventSource(ProtoEventSourceSpecification spec, ProtoRealm realm) {
        this.spec = spec;
        this.realm = realm;
        realm.addSource(this);
    }

    protected void addHandler(ProtoEventHandler handler) {
        handlers.add(handler);
    }
    
    /**
     * @param push
     */
    protected void pushString(String push) {
        ProtoEvent event = new ProtoEvent(ProtoEventType.PUSH, push);
        for (ProtoEventHandler handler : handlers) {
            handler.handleEvent(event);
        }
    }
    
}
