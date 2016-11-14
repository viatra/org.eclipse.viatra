/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt

import org.eclipse.viatra.transformation.evm.api.event.EventType.RuleEngineEventType
import org.eclipse.viatra.transformation.evm.specific.lifecycle.UnmodifiableActivationLifeCycle

class JDTActivationLifeCycle extends UnmodifiableActivationLifeCycle {
	
	new() {
		super(JDTActivationState.INACTIVE);

		internalAddStateTransition(JDTActivationState.INACTIVE, JDTEventType.APPEARED, JDTActivationState.APPEARED);
		internalAddStateTransition(JDTActivationState.APPEARED, JDTEventType.DISAPPEARED, JDTActivationState.INACTIVE);
		internalAddStateTransition(JDTActivationState.APPEARED, RuleEngineEventType.FIRE, JDTActivationState.FIRED);
		internalAddStateTransition(JDTActivationState.FIRED, JDTEventType.UPDATED, JDTActivationState.UPDATED);
		internalAddStateTransition(JDTActivationState.FIRED, JDTEventType.DISAPPEARED, JDTActivationState.DISAPPEARED);
		internalAddStateTransition(JDTActivationState.UPDATED, RuleEngineEventType.FIRE, JDTActivationState.FIRED);
		internalAddStateTransition(JDTActivationState.UPDATED, JDTEventType.DISAPPEARED, JDTActivationState.DISAPPEARED);
		internalAddStateTransition(JDTActivationState.DISAPPEARED, JDTEventType.APPEARED, JDTActivationState.UPDATED);
		internalAddStateTransition(JDTActivationState.DISAPPEARED, RuleEngineEventType.FIRE, JDTActivationState.INACTIVE);
	}
}