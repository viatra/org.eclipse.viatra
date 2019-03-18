/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Peter Lunk, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Iterator;

/**
 * Default {@link IExecutor} implementation, that fires a provided set of activations. The activations are accessed via
 * an {@link Iterator}
 * 
 * @author Abel Hegedus, Peter Lunk
 *
 */
public class Executor implements IExecutor {
    private Context context;

    /**
     * Creates an executor with a new Context. Executors are usually created as part of an ExecutionSchema through the
     * EventDrivenVM.createExecutionSchema methods.
     * 
     */
    public Executor() {
        this.context = Context.create();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void startExecution(String transactionID) {
    }

    @Override
    public void execute(Iterator<Activation<?>> activations) {
        while (activations.hasNext()) {
            activations.next().fire(context);
        }

    }

    @Override
    public void endExecution(String transactionID) {
    }

}
