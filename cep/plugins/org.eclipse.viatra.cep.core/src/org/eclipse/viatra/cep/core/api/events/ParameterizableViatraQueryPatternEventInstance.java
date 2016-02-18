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

import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;

/**
 * Base class to be extended by <b>parameterizable</b> event instances originating from an <b>VIATRA Query</b> engine.
 * It provides functionality to store and access parameters by its supertype {@link ParameterizableEventInstance}; and
 * also provides functionality to store and access the {@link BasePatternMatch} the event instance is associated with.
 * 
 * <p>
 * This class is instantiated when the appropriate graph pattern is matched on a given resource.
 * 
 * @author Istvan David
 * 
 */
public abstract class ParameterizableViatraQueryPatternEventInstance extends ParameterizableEventInstance {
    private BasePatternMatch match;

    public ParameterizableViatraQueryPatternEventInstance(EventSource eventSource) {
        super(eventSource);
    }

    /**
     * @return the associated {@link BasePatternMatch}
     */
    public BasePatternMatch getPatternMatch() {
        return match;
    }

    /**
     * @param match
     *            the {@link BasePatternMatch} to be associated with the event instance
     */
    public void setQueryMatch(BasePatternMatch match) {
        this.match = match;
    }
}