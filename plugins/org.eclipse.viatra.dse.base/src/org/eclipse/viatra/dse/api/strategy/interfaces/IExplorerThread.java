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
package org.eclipse.viatra.dse.api.strategy.interfaces;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.base.ExplorerThread;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * Default interface for a strategy that can be execute within the {@link DesignSpaceExplorer} as a valid design space
 * exploration strategy. It is currently unfinished, and the implementation {@link ExplorerThread} is currently hard wired
 * into the {@link DesignSpaceExplorer}.
 * 
 * Possible future implementation will provide a means to specify a custom {@link IExplorerThread}.
 * 
 * @author Földenyi Miklos & Nagy Andras Szabolcs
 * 
 */
public interface IExplorerThread extends Runnable {
    /**
     * Disposes of this strategy. Recursively callse dispose on the underlying {@link RuleEngine} and
     * {@link IncQueryEngine}. Calling this is only required if the design space exploration was launched in thread, as
     * the underlying engines get collected on the stop of the running {@link Thread}.
     */
    void dispose();

    /**
     * Returns the associated {@link ThreadContext} that houses all the thread specific data about the exploration
     * process, and is also the gateway to the {@link GlobalContext} which stores data relevant to the design space
     * exploration process as a whole.
     * 
     * @return the relevant {@link ThreadContext}.
     */
    ThreadContext getThreadContext();

    /**
     * Signals the {@link IExplorerThread} instance that execution should be stopped. By contract, the strategy is to stop
     * execution at the next stage of execution where stopping and exiting is appropriate.
     */
    void stopRunning();
}
