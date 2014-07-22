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

import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

import com.google.common.collect.Lists;

/**
 * Base class to be extended by <b>parameterizable</b> event instances. It provides functionality to store and access
 * parameters.
 * 
 * @author Istvan David
 * 
 */
public abstract class ParameterizableEventInstance extends AbstractEventInstance {
    protected List<Object> parameters = Lists.newArrayList();

    public ParameterizableEventInstance(EventSource eventSource) {
        super(eventSource);
    }

    /**
     * Get a parameter by its position in the parameter list.
     * 
     * @param i
     *            the position of the parameter in the parameter list
     * @return the parameter {@link Object}
     */
    public Object getParameter(int i) {
        return parameters.get(i);
    }

    /**
     * @return the list of all parameters
     */
    public List<Object> getParameters() {
        return parameters;
    }
}
