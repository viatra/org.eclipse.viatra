/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.multithreading;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;

/**
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class DSEThreadPool extends ThreadPoolExecutor {

    private static final long THREAD_KEEP_ALIVE_IN_SECONDS = 60;

    public DSEThreadPool() {
        // Based on the Executors.newCachedThreadPool()
        super(0, getProcNumber(), THREAD_KEEP_ALIVE_IN_SECONDS, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    // helper for constructor
    private static int getProcNumber() {
        return Runtime.getRuntime().availableProcessors();
    }

    public boolean tryStartNewStrategy(IExplorerThread strategy) {

        if (!canStartNewThread()) {
            return false;
        }

        try {
            submit(strategy);
        } catch (RejectedExecutionException e) {
            return false;
        }

        return true;
    }

    public boolean canStartNewThread() {
        return getMaximumPoolSize() > getActiveCount();
    }
}
