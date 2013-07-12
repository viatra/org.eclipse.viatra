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

import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoEventSourceSpecification implements EventSourceSpecification<String> {

    private String prefix;

    @Override
    public EventFilter<String> createEmptyFilter() {
        return new ProtoEventFilter(prefix);
    }

    @Override
    public AbstractRuleInstanceBuilder<String> getRuleInstanceBuilder(final EventRealm realm) {
        return new AbstractRuleInstanceBuilder<String>() {
            
            @Override
            public void prepareRuleInstance(RuleInstance<String> ruleInstance, EventFilter<String> filter) {
                ProtoEventSource source = new ProtoEventSource(ProtoEventSourceSpecification.this, (ProtoRealm) realm);
                ProtoEventHandler handler = new ProtoEventHandler(source, (ProtoEventFilter) filter, ruleInstance);
                source.addHandler(handler);
            }
        };
    }
    
    /**
     * 
     */
    public ProtoEventSourceSpecification(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public EventFilter<String> createFilter(String atom) {
        return new ProtoEventFilter(prefix+atom);
    }

}
