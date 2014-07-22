/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.streams;

import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * Interface for stream managers. Different implementations could support custom stream handling strategies, such as
 * <ul>
 * <li>optimizing the number of streams based on the available resources,
 * <li>optimizing the algorithm for dispatching input {@link Event}s over various streams,
 * <li>etc.
 * </ul>
 * 
 * @author Istvan David
 * 
 */
public interface IStreamManager {
    /**
     * @return a new {@link EventStream}.
     */
    EventStream newEventStream();

    /**
     * @return the list of currently instantiated {@link EventStream}s.
     */
    List<EventStream> getEventStreams();
}
