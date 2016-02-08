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
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventSourceSpecification<Match extends IPatternMatch> implements EventSourceSpecification<Match> {

    private IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification;
    private final EventFilter<Match> EMPTY_FILTER = new IncQuerySinglePatternMatchEventFilter<Match>();
    
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
