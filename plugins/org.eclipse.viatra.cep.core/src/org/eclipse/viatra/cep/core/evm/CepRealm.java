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
import org.eclipse.viatra.cep.core.api.patterns.InTrapComplexEventPattern;
import org.eclipse.viatra.cep.core.api.patterns.ObservedComplexEventPattern;

import com.google.common.collect.Sets;

/**
 * EVM {@link EventRealm} implementation.
 * 
 * @author Istvan David
 * 
 */
public class CepRealm implements EventRealm {

    private Set<CepEventSource> sources = Sets.newHashSet();

    public void addSource(CepEventSource cepEventSource) {
        sources.add(cepEventSource);
    }

    public void forwardObservedEventPattern(ObservedComplexEventPattern op) {
        for (CepEventSource source : sources) {
            if (source.getAutomaton().getEventPattern().getId()
                    .equalsIgnoreCase(op.getObservableEventPattern().getId())) {
                source.pushEvent(CepEventType.APPEARED, op);
                break;
            }
        }
    }

    public void forwardFailedEventPattern(InTrapComplexEventPattern op) {
        for (CepEventSource source : sources) {
            if (source.getAutomaton().getEventPattern().getId()
                    .equalsIgnoreCase(op.getObservableEventPattern().getId())) {
                source.pushEvent(CepEventType.APPEARED, op);
                break;
            }
        }
    }

    public Set<CepEventSource> getSources() {
        return sources;
    }
}