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

package org.eclipse.viatra.cep.core.api.patterns;

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

/**
 * An {@link EventPattern} instance that got matched, i.e. the related {@link EventToken} got into the
 * {@link FinalState} of the appropriate {@link Automaton}.
 * 
 * <p>
 * An object of this type should be instantiated at runtime when an {@link EventPattern} is placed into the
 * {@link FinalState}.
 * 
 * @author Istvan David
 * 
 */
public class ObservedComplexEventPattern implements IObservableComplexEventPattern {

    private EventPattern observablePattern;

    @Override
    public EventPattern getObservableEventPattern() {
        return observablePattern;
    }

    public ObservedComplexEventPattern(EventPattern observablePattern) {
        this.observablePattern = observablePattern;
    }
}
