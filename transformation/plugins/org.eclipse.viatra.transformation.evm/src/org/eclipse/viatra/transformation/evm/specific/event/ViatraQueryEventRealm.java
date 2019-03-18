/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

public class ViatraQueryEventRealm implements EventRealm {

    private ViatraQueryEngine engine;
    
    protected ViatraQueryEventRealm(ViatraQueryEngine engine) {
        Preconditions.checkArgument(engine != null, "Cannot create event realm for null engine!");
        this.engine = engine;
    }
    
    public ViatraQueryEngine getEngine() {
        return engine;
    }

    protected <Match extends IPatternMatch> ViatraQueryEventSource<Match> createSource(
            EventSourceSpecification<Match> sourceSpecification) {
        Preconditions.checkArgument(sourceSpecification instanceof ViatraQueryEventSourceSpecification,
                "Source definition must be ViatraQueryEventSourceSpecification!");
        ViatraQueryEventSource<Match> eventSource = new ViatraQueryEventSource<Match>(this,
                (ViatraQueryEventSourceSpecification<Match>) sourceSpecification);
        eventSource.prepareSource();
        return eventSource;
    }
    
    
    public static ViatraQueryEventRealm create(ViatraQueryEngine engine) {
        return new ViatraQueryEventRealm(engine);
    }

    public static <Match extends IPatternMatch> ViatraQueryEventSourceSpecification<Match> createSourceSpecification(IQuerySpecification<? extends ViatraQueryMatcher<Match>> factory){
        return new ViatraQueryEventSourceSpecification<Match>(factory);
    }
    
}
