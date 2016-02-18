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

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.MatchUpdateAdapter;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.adapter.EventSourceAdapter;
import org.eclipse.viatra.transformation.evm.notification.IAttributeMonitorListener;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;

public class ViatraQueryEventSource<Match extends IPatternMatch> extends EventSourceAdapter<Match> {
    
    private final ViatraQueryMatcher<Match> matcher;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private IMatchUpdateListener<Match> matchUpdateListener;
    
    protected ViatraQueryEventSource(ViatraQueryEventRealm realm, ViatraQueryEventSourceSpecification<Match> sourceDefinition) throws ViatraQueryException {
        super(sourceDefinition, realm);
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> factory = sourceDefinition.getQuerySpecification();
        ViatraQueryMatcher<Match> _matcher = factory.getMatcher(realm.getEngine());
        this.matcher = _matcher;
    }

    public ViatraQueryMatcher<Match> getMatcher() {
        return matcher;
    }

    @Override
    protected void beforeHandlerAdded(EventHandler<Match> handler, boolean handlersEmpty) {
        resendEventsForExistingMatches(handler);
        if(handlersEmpty) {
            ((AdvancedViatraQueryEngine)this.matcher.getEngine()).addMatchUpdateListener(this.matcher, matchUpdateListener, false);
        }
    }
    
    @Override
    protected void afterHandlerRemoved(EventHandler<Match> handler, boolean handlersEmpty) {
        if(handlersEmpty) {
            ((AdvancedViatraQueryEngine)this.matcher.getEngine()).removeMatchUpdateListener(this.matcher, matchUpdateListener);
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
                notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.UPDATED, atom));
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
                notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.CREATED, match));
            }
        };
        IMatchProcessor<Match> matchDisppearProcessor = new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.DELETED, match));
            }
        };
        return new MatchUpdateAdapter<Match>(matchAppearProcessor, matchDisppearProcessor);
    }
    
    private void resendEventsForExistingMatches(final EventHandler<Match> handler) {
        matcher.forEachMatch(new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                handler.handleEvent(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.CREATED, match));
            }
        });
    }

    @Override
    public void dispose() {
        ((AdvancedViatraQueryEngine)this.matcher.getEngine()).removeMatchUpdateListener(this.matcher, matchUpdateListener);
        super.dispose();
    }

    public IAttributeMonitorListener<Match> getAttributeMonitorListener() {
        checkState(attributeMonitorListener != null, "Event source not prepared yet!");
        return attributeMonitorListener;
    }
    
}
