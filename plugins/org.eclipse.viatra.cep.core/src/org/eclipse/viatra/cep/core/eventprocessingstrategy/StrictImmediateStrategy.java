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

package org.eclipse.viatra.cep.core.eventprocessingstrategy;

import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;

/**
 * Strategy implementation for the <i>Strict Immediate</i> {@link EventContext}.
 * 
 * @author Istvan David
 * 
 */
public class StrictImmediateStrategy extends AbstractImmediateStrategy {

    @Override
    public EventContext getContext() {
        return EventContext.STRICT_IMMEDIATE;
    }

    public StrictImmediateStrategy(IEventModelManager eventModelManager) {
        super(eventModelManager);
    }

    @Override
    public void handleInitTokenCreation(InternalModel model, final AutomatonFactory factory,
            IObservableComplexEventPattern observedComplexEventPattern) {
        if (observedComplexEventPattern == null) {
            return;
        }
        super.handleInitTokenCreation(model, factory, observedComplexEventPattern);
    }
}
