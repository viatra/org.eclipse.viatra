/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * Interface that defines the methods of EVM listener objects. The interface contains callback methods for various EVM
 * events. Through these methods {@link IEVMListener} implementations can observe the internal state of an EVM program.
 * 
 * @author Peter Lunk
 */
public interface IEVMListener {

    public void initializeListener(ViatraQueryEngine engine);

    public void beforeFiring(final Activation<?> activation);

    public void afterFiring(final Activation<?> activation);

    public void startTransaction(String transactionID);

    public void endTransaction(String transactionID);

    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event);

    public void activationCreated(Activation<?> activation, ActivationState inactiveState);

    public void activationRemoved(Activation<?> activation, ActivationState oldState);

    public void addedRule(final RuleSpecification<?> specification, EventFilter<?> filter);

    public void removedRule(final RuleSpecification<?> specification, EventFilter<?> filter);

    public void disposeListener();

}
