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
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.MatchUpdateAdapter;
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventHandler;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;
import org.eclipse.incquery.runtime.evm.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventSource<Match extends IPatternMatch> implements EventSource<Match> {
    
    private final IncQueryEventRealm realm;
    private final IncQueryEventSourceSpecification<Match> sourceDefinition;
    private final IncQueryMatcher<Match> matcher;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private final Set<EventHandler<Match>> handlers;
    private IMatchUpdateListener<Match> matchUpdateListener;
    
    /**
     * 
     */
    protected IncQueryEventSource(IncQueryEventRealm realm, IncQueryEventSourceSpecification<Match> sourceDefinition) throws IncQueryException {
        checkArgument(realm != null, "Cannot create event source for null realm!");
        checkArgument(sourceDefinition != null, "Cannot create event source for null source definition!");
        this.realm = realm;
        this.sourceDefinition = sourceDefinition;
        IQuerySpecification<? extends IncQueryMatcher<Match>> factory = sourceDefinition.getQuerySpecification();
        IncQueryMatcher<Match> newMatcher = factory.getMatcher(realm.getEngine());
        this.matcher = newMatcher;
        this.handlers = Sets.newHashSet();
    }

    @Override
    public EventSourceSpecification<Match> getSourceSpecification() {
        return sourceDefinition;
    }
    
    /**
     * @return the matcher
     */
    public IncQueryMatcher<Match> getMatcher() {
        return matcher;
    }

    @Override
    public EventRealm getRealm() {
        return realm;
    }

    protected boolean addHandler(EventHandler<Match> handler) {
        checkArgument(handler != null, "Handler cannot be null!");
        if(handlers.isEmpty()) {
            ((AdvancedIncQueryEngine)this.matcher.getEngine()).addMatchUpdateListener(this.matcher, matchUpdateListener, false);
        }
        return handlers.add(handler);
    }
    
    protected boolean removeHandler(EventHandler<Match> handler) {
        checkArgument(handler != null, "Handler cannot be null!");
        boolean removed = handlers.remove(handler);
        if(handlers.isEmpty()) {
            ((AdvancedIncQueryEngine)this.matcher.getEngine()).removeMatchUpdateListener(this.matcher, matchUpdateListener);
        }
        return removed;
    }
   
    public void notifyHandlers(Event<Match> event) {
        for (EventHandler<Match> handler : handlers) {
            handler.handleEvent(event);
        }
    }

    protected void prepareSource() {
        this.attributeMonitorListener = checkNotNull(prepareAttributeMonitorListener(),
                "Prepared attribute monitor listener is null!");
        this.matchUpdateListener = checkNotNull(prepareMatchUpdateListener(), "Prepared match update listener is null!");
    }

    /**
     * @return a new attribute monitor listener
     */
    protected IAttributeMonitorListener<Match> prepareAttributeMonitorListener() {
        return new IAttributeMonitorListener<Match>(){
            @Override
            public void notifyUpdate(Match atom) {
                notifyHandlers(new IncQueryEvent<Match>(IncQueryEventTypeEnum.MATCH_UPDATES, atom));
            }
        };
    }
    
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
    
    protected void resendEventsForExistingMatches(final EventHandler<Match> handler) {
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
        for (EventHandler<Match> handler : this.handlers) {
            handler.dispose();
        }
        this.handlers.clear(); // in case handler didn't remove itself
    }

    /**
     * @return
     */
    public IAttributeMonitorListener<Match> getAttributeMonitorListener() {
        checkState(attributeMonitorListener != null, "Event source not prepared yet!");
        return attributeMonitorListener;
    }
    
}
