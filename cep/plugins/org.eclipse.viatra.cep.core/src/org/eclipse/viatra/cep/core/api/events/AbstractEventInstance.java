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

package org.eclipse.viatra.cep.core.api.events;

import java.util.Date;

import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl;

/**
 * Base class to be extended by actual event instances. It extends the basic {@link Event} type by automatically setting
 * its type (see: {@link Event#getType()}) and the timestamp it was created (see:{@link Event#getTimestamp()}).
 * 
 * @author Istvan David
 * 
 */
public abstract class AbstractEventInstance extends EventImpl {
    public AbstractEventInstance(EventSource eventSource) {
        super();
        setSource(eventSource);
        setType(this.getClass().getCanonicalName());
        setTimestamp(new Date().getTime());
    }
}
