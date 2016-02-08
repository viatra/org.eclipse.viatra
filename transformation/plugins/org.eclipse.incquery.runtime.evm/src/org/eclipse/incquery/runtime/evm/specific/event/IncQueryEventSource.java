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

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.MatchUpdateAdapter;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.adapter.EventSourceAdapter;
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class IncQueryEventSource<Match extends IPatternMatch> extends EventSourceAdapter<Match> {
    
    private final IncQueryMatcher<Match> matcher;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private IMatchUpdateListener<Match> matchUpdateListener;
    
    protected IncQueryEventSource(IncQueryEventRealm realm, IncQueryEventSourceSpecification<Match> sourceDefinition) throws IncQueryException {
        super(sourceDefinition, realm);
        IQuerySpecification<? extends IncQueryMatcher<Match>> factory = sourceDefinition.getQuerySpecification();
        IncQueryMatcher<Match> _matcher = factory.getMatcher(realm.getEngine());
        this.matcher = _matcher;
    }

    public IncQueryMatcher<Match> getMatcher() {
        return matcher;
    }

    @Override
    protected void beforeHandlerAdded(EventHandler<Match> handler, boolean handlersEmpty) {
        resendEventsForExistingMatches(handler);
        if(handlersEmpty) {
            ((AdvancedIncQueryEngine)this.matcher.getEngine()).addMatchUpdateListener(this.matcher, matchUpdateListener, false);
        }
    }
    
    @Override
    protected void afterHandlerRemoved(EventHandler<Match> handler, boolean handlersEmpty) {
        if(handlersEmpty) {
            ((AdvancedIncQueryEngine)this.matcher.getEngine()).removeMatchUpdateListener(this.matcher, matchUpdateListener);
        }
    }
   
    @Override
    protected void prepareSource() {
        this.attributeMonitorListener = prepareAttributeMonitorListener();
        this.matchUpdateListener = prepareMatchUpdateListener();
    }

    /**
     * Initializes an attribute monitor listener
     * @return the prepared attribute monitor listener; must not be null
     */
    protected IAttributeMonitorListener<Match> prepareAttributeMonitorListener() {
        return new IAttributeMonitorListener<Match>(){
            @Override
            public void notifyUpdate(Match atom) {
                notifyHandlers(new IncQueryEvent<Match>(IncQueryEventTypeEnum.MATCH_UPDATES, atom));
            }
        };
    }
    
    /**
     * Initializes the corresponding match update listener
     * @return the prepared update listener; must not be null
     */
    protected IMatchUpdateListener<Match> prepareMatchUpdateListener(){
        IMatchProcessor<Match> matchAppearProcessor = new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                notifyHandlers(new IncQueryEvent<Match>(IncQueryEventTypeEnum.MATCH_APPEARS, match));
            }
        };
        IMatchProcessor<Match> matchDisppearProcessor = new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                notifyHandlers(new IncQueryEvent<Match>(IncQueryEventTypeEnum.MATCH_DISAPPEARS, match));
            }
        };
        return new MatchUpdateAdapter<Match>(matchAppearProcessor, matchDisppearProcessor);
    }
    
    private void resendEventsForExistingMatches(final EventHandler<Match> handler) {
        matcher.forEachMatch(new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                handler.handleEvent(new IncQueryEvent<Match>(IncQueryEventTypeEnum.MATCH_APPEARS, match));
            }
        });
    }

    @Override
    public void dispose() {
        ((AdvancedIncQueryEngine)this.matcher.getEngine()).removeMatchUpdateListener(this.matcher, matchUpdateListener);
        super.dispose();
    }

    public IAttributeMonitorListener<Match> getAttributeMonitorListener() {
        checkState(attributeMonitorListener != null, "Event source not prepared yet!");
        return attributeMonitorListener;
    }
    
}
