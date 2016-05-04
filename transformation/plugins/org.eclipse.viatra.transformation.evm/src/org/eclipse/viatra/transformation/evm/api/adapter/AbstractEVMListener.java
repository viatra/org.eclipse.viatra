/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * Abstract {@link IEVMListener} implementation.
 * 
 * @author Peter Lunk
 *
 */
public class AbstractEVMListener implements IEVMListener {

    @Override
    public void initializeListener(ViatraQueryEngine engine) {
    }

    @Override
    public void beforeFiring(Activation<?> activation) {
    }

    @Override
    public void afterFiring(Activation<?> activation) {
    }

    @Override
    public void startTransaction(String transactionID) {
    }

    @Override
    public void endTransaction(String transactionID) {
    }

    @Override
    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
    }

    @Override
    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
    }

    @Override
    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
    }

    @Override
    public void addedRule(RuleSpecification<?> specification) {
    }

    @Override
    public void removedRule(RuleSpecification<?> specification) {
    }

    @Override
    public void disposeListener() {
    }

}
