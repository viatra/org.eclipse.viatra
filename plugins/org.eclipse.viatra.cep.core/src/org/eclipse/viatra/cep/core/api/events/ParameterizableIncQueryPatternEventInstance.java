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
import org.eclipse.viatra.cep.core.metamodels.events.IEventSource;

public abstract class ParameterizableIncQueryPatternEventInstance extends ParameterizableEventInstance {
    private BasePatternMatch incQueryPattern;

    public ParameterizableIncQueryPatternEventInstance(IEventSource eventSource) {
        super(eventSource);
    }

    public BasePatternMatch getIncQueryPattern() {
        return incQueryPattern;
    }

    public void setIncQueryPattern(BasePatternMatch incQueryPattern) {
        this.incQueryPattern = incQueryPattern;
    }
}