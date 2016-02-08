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
