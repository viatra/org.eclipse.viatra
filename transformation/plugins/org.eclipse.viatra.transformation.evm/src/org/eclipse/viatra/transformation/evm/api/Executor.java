/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Peter Lunk - revised EVM structure for adapter support
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
