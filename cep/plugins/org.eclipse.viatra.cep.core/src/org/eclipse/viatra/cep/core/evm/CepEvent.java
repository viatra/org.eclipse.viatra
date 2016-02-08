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

package org.eclipse.viatra.cep.core.evm;

import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;

/**
 * EVM {@link Event} implementation.
 * 
 * @author Istvan David
 * 
 */
public class CepEvent implements Event<IObservableComplexEventPattern> {

    private CepEventType type;
    private IObservableComplexEventPattern atom;

    public CepEvent(CepEventType type, IObservableComplexEventPattern atom) {
        this.type = type;
        this.atom = atom;
    }

    @Override
    public EventType getEventType() {
        return type;
    }

    @Override
    public IObservableComplexEventPattern getEventAtom() {
        return atom;
    }

}
