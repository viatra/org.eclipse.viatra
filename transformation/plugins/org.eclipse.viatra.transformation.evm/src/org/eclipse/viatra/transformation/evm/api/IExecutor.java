/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Iterator;

/**
 * Interface that defines the main entry points of EVM based execution.
 * 
 * @author Peter Lunk
 *
 */
public interface IExecutor {

    /**
     * Signals the beginning of the execution,It receives the transaction ID as a parameter
     * 
     * @param transactionID
     */
    public void startExecution(String transactionID);

    /**
     * executes a given set of EVM {@link Activation}. Make sure that if this method is used, the call is enclosed by
     * {@link startExecution} and {@link endExecution} calls.
     * 
     * @param activations
     *            {@link Iterator} that enables the executor to access the Activations to be executed.
     */
    public void execute(Iterator<Activation<?>> activations);

    /**
     * Signals the end of the execution,It receives the transaction ID as a parameter
     * 
     * @param transactionID
     */
    public void endExecution(String transactionID);

    /**
     * Returns the {@link Context} of the executor. The return value should never be null.
     */
    public Context getContext();
}
