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

package org.eclipse.viatra.cep.core.api.evm;

import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

/**
 * EVM {@link ActivationState}s of an {@link EventPattern}. The states capture states when a rule engine should consider
 * a given {@link EventPattern} being activated or not - e.g. when the pattern gets matched.
 * 
 * @author Istvan David
 * 
 */
public enum CepActivationStates implements ActivationState {
    INACTIVE, ACTIVE;

    @Override
    public boolean isInactive() {
        return this.equals(INACTIVE);
    }
}
