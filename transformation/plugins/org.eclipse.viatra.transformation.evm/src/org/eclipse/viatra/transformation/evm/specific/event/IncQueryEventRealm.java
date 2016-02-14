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
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

public class IncQueryEventRealm implements EventRealm {

    private ViatraQueryEngine engine;
    
    protected IncQueryEventRealm(ViatraQueryEngine engine) {
        checkArgument(engine != null, "Cannot create event realm for null engine!");
        this.engine = engine;
    }
    
    public ViatraQueryEngine getEngine() {
        return engine;
    }

    protected <Match extends IPatternMatch> IncQueryEventSource<Match> createSource(
            EventSourceSpecification<Match> sourceSpecification) throws IncQueryException {
        checkArgument(sourceSpecification instanceof IncQueryEventSourceSpecification,
                "Source definition must be IncQueryEventSourceSpecification!");
        IncQueryEventSource<Match> eventSource = new IncQueryEventSource<Match>(this,
                (IncQueryEventSourceSpecification<Match>) sourceSpecification);
        eventSource.prepareSource();
        return eventSource;
    };
    
    
    public static IncQueryEventRealm create(ViatraQueryEngine engine) {
        return new IncQueryEventRealm(engine);
    }

    public static <Match extends IPatternMatch> IncQueryEventSourceSpecification<Match> createSourceSpecification(IQuerySpecification<? extends ViatraQueryMatcher<Match>> factory){
        return new IncQueryEventSourceSpecification<Match>(factory);
    }
    
}
