/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import java.util.function.Consumer;

import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.api.MatchUpdateAdapter;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.adapter.EventSourceAdapter;
import org.eclipse.viatra.transformation.evm.notification.IAttributeMonitorListener;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;

public class ViatraQueryEventSource<Match extends IPatternMatch> extends EventSourceAdapter<Match> {
    
    private final ViatraQueryMatcher<Match> matcher;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private IMatchUpdateListener<Match> matchUpdateListener;
    
    protected ViatraQueryEventSource(ViatraQueryEventRealm realm, ViatraQueryEventSourceSpecification<Match> sourceDefinition) {
        super(sourceDefinition, realm);
        ViatraQueryMatcher<Match> _matcher = sourceDefinition.getMatcher(realm.getEngine());
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
        final AdvancedViatraQueryEngine engine = (AdvancedViatraQueryEngine)this.matcher.getEngine();
        if(handlersEmpty && !engine.isDisposed()) {
            engine.removeMatchUpdateListener(this.matcher, matchUpdateListener);
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
        return atom -> notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.UPDATED, atom));
    }
    
    /**
     * Initializes the corresponding match update listener
     * @return the prepared update listener; must not be null
     */
    protected IMatchUpdateListener<Match> prepareMatchUpdateListener(){
        Consumer<Match> matchAppearProcessor = match -> notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.CREATED, match));
        Consumer<Match> matchDisppearProcessor = match -> notifyHandlers(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.DELETED, match));
        return new MatchUpdateAdapter<Match>(matchAppearProcessor, matchDisppearProcessor);
    }
    
    private void resendEventsForExistingMatches(final EventHandler<Match> handler) {
        matcher.forEachMatch(match -> handler.handleEvent(new ViatraQueryEvent<Match>(CRUDEventTypeEnum.CREATED, match)));
    }

    @Override
    public void dispose() {
        final AdvancedViatraQueryEngine engine = (AdvancedViatraQueryEngine)this.matcher.getEngine();
        if (!engine.isDisposed()) {
            engine.removeMatchUpdateListener(this.matcher, matchUpdateListener);
        }
        super.dispose();
    }

    public IAttributeMonitorListener<Match> getAttributeMonitorListener() {
        Preconditions.checkState(attributeMonitorListener != null, "Event source not prepared yet!");
        return attributeMonitorListener;
    }
    
}
