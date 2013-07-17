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
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventSourceSpecification<Match extends IPatternMatch> implements EventSourceSpecification<Match> {

    private IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification;
    private final EventFilter<Match> EMPTY_FILTER = new IncQueryEventFilter<Match>();
    
    protected IncQueryEventSourceSpecification(IQuerySpecification<? extends IncQueryMatcher<Match>> factory) {
        checkArgument(factory != null, "Cannot create source definition for null querySpecification!");
        this.querySpecification = factory;
    }

    @Override
    public EventFilter<Match> createEmptyFilter() {
        return EMPTY_FILTER;
    }
    
    /**
     * @return the querySpecification
     */
    public IQuerySpecification<? extends IncQueryMatcher<Match>> getQuerySpecification() {
        return querySpecification;
    }
    
    protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
        IncQueryMatcher<Match> matcher = querySpecification.getMatcher(engine);
        return matcher;
    }
    
    @Override
    public AbstractRuleInstanceBuilder<Match> getRuleInstanceBuilder(EventRealm realm) {
        return new IncQueryRuleInstanceBuilder<Match>((IncQueryEventRealm) realm, this);
    }
    
}
