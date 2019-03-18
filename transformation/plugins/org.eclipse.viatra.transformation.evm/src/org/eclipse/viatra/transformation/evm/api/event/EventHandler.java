/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.event;


/**
 * Basic interface for handling a given event received from an {@link EventSource}.
 * 
 * @author Abel Hegedus
 *
 */
public interface EventHandler<EventAtom> {

    void handleEvent(Event<EventAtom> event);
    
    EventSource<EventAtom> getSource();
    
    EventFilter<? super EventAtom> getEventFilter();
    
    void dispose();
}
