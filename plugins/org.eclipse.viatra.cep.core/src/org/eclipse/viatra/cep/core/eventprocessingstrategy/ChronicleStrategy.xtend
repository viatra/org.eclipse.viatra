/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
 
package org.eclipse.viatra.cep.core.eventprocessingstrategy

import org.eclipse.viatra.cep.core.engine.IEventModelManager
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory

/**
 * Strategy implementation for the <i>Chronicle</i> {@link EventContext}.
 * 
 * @author Istvan David
 * 
 */
class ChronicleStrategy extends AbstractStrategy {

	override EventContext getContext() {
		return EventContext::CHRONICLE
	}

	new(IEventModelManager eventModelManager) {
		super(eventModelManager)
	}
	
	override handleAutomatonResets(InternalModel model, AutomatonFactory factory) {
		return
	}

}
