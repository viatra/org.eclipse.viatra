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

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoRealm implements EventRealm {

    private Set<ProtoEventSource> sources = Sets.newHashSet();
    
    /**
     * 
     */
    public ProtoRealm() {
    }
    
    public void pushString(String push) {
        for (ProtoEventSource source : sources) {
            source.pushString(push);
        }
    }

    protected void addSource(ProtoEventSource source) {
        sources.add(source);
    }
}
