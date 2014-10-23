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

import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;

/**
 * Factory class for obtaining an {@link IEventProcessingStrategy}.
 * 
 * @author Istvan David
 * 
 */
public final class EventProcessingStrategyFactory {

    private EventProcessingStrategyFactory() {}

    public static IEventProcessingStrategy getStrategy(EventContext context, IEventModelManager eventModelManager) {
        if (context == null) { // return default
            return new ChronicleStrategy(eventModelManager);
        }
        switch (context) {
        case CHRONICLE:
            return new ChronicleStrategy(eventModelManager);
        case STRICT_IMMEDIATE:
            return new StrictImmediateStrategy(eventModelManager);
        case IMMEDIATE:
            return new NormalImmediateStrategy(eventModelManager);
        case RECENT:
            throw new IllegalArgumentException(); // NOT IMPLEMENTED YET
        case UNRESTRICTED:
            throw new IllegalArgumentException(); // NOT IMPLEMENTED YET
        default:
            throw new IllegalArgumentException(); // SHALL NOT HAPPEN
        }
    }
}