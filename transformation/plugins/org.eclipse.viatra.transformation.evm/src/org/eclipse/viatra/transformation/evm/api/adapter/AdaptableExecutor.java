/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.Iterator;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.IExecutor;

/**
 * {@link IExecutor} implementation that uses the {@link AdaptableEVM} to add
 * external functions to certain points of an EVM based program.
 * 
 * @author Peter Lunk
 *
 */
public class AdaptableExecutor implements IExecutor {
    private final IExecutor delegatedExecutor;
    private final AdaptableEVM evm;

    public AdaptableExecutor(IExecutor delegatedExecutor, AdaptableEVM evm) {
        this.delegatedExecutor = delegatedExecutor;
        this.evm = evm;
    }

    @Override
    public void startExecution(String transactionID) {
        evm.startTransaction(transactionID);
    }

    @Override
    public void execute(Iterator<Activation<?>> activations) {
        Iterator<Activation<?>> ac = evm.getExecutableActivations(activations);

        while (ac.hasNext()) {
            Activation<?> activation = ac.next();
            evm.beforeFiring(activation);
            activation.fire(getContext());
            evm.afterFiring(activation);
        }
    }

    @Override
    public void endExecution(String transactionID) {
        evm.endTransaction(transactionID);
    }

    @Override
    public Context getContext() {
        return delegatedExecutor.getContext();
    }
}
