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

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
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

    private IMatcherFactory<? extends IncQueryMatcher<Match>> factory;
    private final EventFilter<Match> EMPTY_FILTER = new EventFilter<Match>() {
        @Override
        public boolean isProcessable(Match eventAtom) {
            return true;
        }
    };
    
    protected IncQueryEventSourceSpecification(IMatcherFactory<? extends IncQueryMatcher<Match>> factory) {
        checkArgument(factory != null, "Cannot create source definition for null factory!");
        this.factory = factory;
    }

    public EventFilter<Match> createFilter(Match eventAtom) {
        checkArgument(eventAtom != null, "Cannot create filter for null match, use createEmptyFilter() instead!");
        checkArgument(!eventAtom.isMutable(), "Cannot create filter for mutable match!");
        if(IncQueryEventRealm.isEmpty(eventAtom)) {
            return EMPTY_FILTER;
        } else {
            return new IncQueryEventFilter<Match>(eventAtom);
        }
    }

    @Override
    public EventFilter<Match> createEmptyFilter() {
        return EMPTY_FILTER;
    }
    
    /**
     * @return the factory
     */
    public IMatcherFactory<? extends IncQueryMatcher<Match>> getFactory() {
        return factory;
    }
    
    protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
        IncQueryMatcher<Match> matcher = factory.getMatcher(engine);
        return matcher;
    }
    
    @Override
    public AbstractRuleInstanceBuilder<Match> getRuleInstanceBuilder(EventRealm realm) {
        return new IncQueryRuleInstanceBuilder<Match>((IncQueryEventRealm) realm, this);
    }
    
}
