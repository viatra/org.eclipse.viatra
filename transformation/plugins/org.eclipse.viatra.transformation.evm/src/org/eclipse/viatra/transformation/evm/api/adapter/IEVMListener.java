/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * Interface that defines the methods of EVM listener objects. The interface contains callback methods for various EVM
 * events. Through these methods {@link IEVMListener} implementations can observe the internal state of an EVM program.
 * 
 * @author Peter Lunk
 */
public interface IEVMListener {

    public void initializeListener();

    public void beforeFiring(final Activation<?> activation);

    public void afterFiring(final Activation<?> activation);

    public void startTransaction(String transactionID);

    public void endTransaction(String transactionID);

    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event);

    public void activationCreated(Activation<?> activation, ActivationState inactiveState);

    public void activationRemoved(Activation<?> activation, ActivationState oldState);

    public void addedRule(final RuleSpecification<?> specification);

    public void removedRule(final RuleSpecification<?> specification);

    public void disposeListener();

}
