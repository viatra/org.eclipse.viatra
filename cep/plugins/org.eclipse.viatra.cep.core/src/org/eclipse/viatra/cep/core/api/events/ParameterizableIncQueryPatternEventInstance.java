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

import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

/**
 * Base class to be extended by <b>parameterizable</b> event instances originating from an <b>EMF-IncQuery</b> engine.
 * It provides functionality to store and access parameters by its supertype {@link ParameterizableEventInstance}; and
 * also provides functionality to store and access the {@link BasePatternMatch} the event instance is associated with.
 * 
 * <p>
 * This class is instantiated when the appropriate EMF-IncQuery pattern is matched on a given resource.
 * 
 * @author Istvan David
 * 
 */
public abstract class ParameterizableIncQueryPatternEventInstance extends ParameterizableEventInstance {
    private BasePatternMatch incQueryPattern;

    public ParameterizableIncQueryPatternEventInstance(EventSource eventSource) {
        super(eventSource);
    }

    /**
     * @return the associated {@link BasePatternMatch}
     */
    public BasePatternMatch getIncQueryPattern() {
        return incQueryPattern;
    }

    /**
     * @param incQueryPattern
     *            the {@link BasePatternMatch} to be associated with the event instance
     */
    public void setIncQueryPattern(BasePatternMatch incQueryPattern) {
        this.incQueryPattern = incQueryPattern;
    }
}