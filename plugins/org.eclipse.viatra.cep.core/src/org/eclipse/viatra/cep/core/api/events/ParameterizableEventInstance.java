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

import org.eclipse.viatra.cep.core.metamodels.events.IEventSource;

import com.google.common.collect.Lists;

public abstract class ParameterizableEventInstance extends AbstractEventInstance {
    protected List<Object> parameters = Lists.newArrayList();

    public ParameterizableEventInstance(IEventSource eventSource) {
        super(eventSource);
    }

    public Object getParameter(int i) {
        return parameters.get(i);
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
