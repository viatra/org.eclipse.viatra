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
package org.eclipse.viatra.transformation.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

public class ViatraQueryEventRealm implements EventRealm {

    private ViatraQueryEngine engine;
    
    protected ViatraQueryEventRealm(ViatraQueryEngine engine) {
        checkArgument(engine != null, "Cannot create event realm for null engine!");
        this.engine = engine;
    }
    
    public ViatraQueryEngine getEngine() {
        return engine;
    }

    protected <Match extends IPatternMatch> ViatraQueryEventSource<Match> createSource(
            EventSourceSpecification<Match> sourceSpecification) throws ViatraQueryException {
        checkArgument(sourceSpecification instanceof ViatraQueryEventSourceSpecification,
                "Source definition must be ViatraQueryEventSourceSpecification!");
        ViatraQueryEventSource<Match> eventSource = new ViatraQueryEventSource<Match>(this,
                (ViatraQueryEventSourceSpecification<Match>) sourceSpecification);
        eventSource.prepareSource();
        return eventSource;
    };
    
    
    public static ViatraQueryEventRealm create(ViatraQueryEngine engine) {
        return new ViatraQueryEventRealm(engine);
    }

    public static <Match extends IPatternMatch> ViatraQueryEventSourceSpecification<Match> createSourceSpecification(IQuerySpecification<? extends ViatraQueryMatcher<Match>> factory){
        return new ViatraQueryEventSourceSpecification<Match>(factory);
    }
    
}
