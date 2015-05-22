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
package org.eclipse.viatra.dse.base;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;

/**
 * Default interface for a strategy that can be execute within the {@link DesignSpaceExplorer} as a valid design space
 * exploration strategy. It is currently unfinished, and the implementation {@link ExplorerThread} is currently hard
 * wired into the {@link DesignSpaceExplorer}.
 * 
 * Possible future implementation will provide a means to specify a custom {@link IExplorerThread}.
 * 
 * @author Földenyi Miklos & Nagy Andras Szabolcs
 * 
 */
public class ExplorerThread implements Runnable {

    private final ThreadContext threadContext;
    private GlobalContext globalContext;

    private final Logger logger = Logger.getLogger(this.getClass());
    private IStrategy strategy;

    public ExplorerThread(final ThreadContext context) {
        this.threadContext = context;
    }

    /**
     * Signals the {@link IExplorerThread} instance that execution should be stopped. By contract, the strategy is to
     * stop execution at the next stage of execution where stopping and exiting is appropriate.
     */
    /**
     * Makes the strategy (the thread) end it's last step, then exit.
     */
    public void stopRunning() {
        strategy.interruptStrategy();
    }

    /**
     * Starts the design space exploration. Returns only when
     * {@link ISolutionFoundHandler#solutionFound(Strategy, Solution)} method returns STOP or the
     * {@link LocalSearchStrategyBase#getNextTransition(ThreadContext)} method returns null.
     * 
     * If this main algorithm is not good for you, you can derive from this class and override this method.
     */
    public void run() {
        try {

            // init is called here, not in the constructor, because of
            // performance
            // (initialization happens in the new thread)
            threadContext.init();

            globalContext = threadContext.getGlobalContext();
            DesignSpaceManager dsm = threadContext.getDesignSpaceManager();

            strategy = threadContext.getStrategy();
            strategy.initStrategy(threadContext);

            logger.debug("Strategy started with state: " + dsm.getCurrentState().getId());

            strategy.explore();

            logger.debug("Strategy stopped on Thread " + Thread.currentThread());
            globalContext.strategyFinished(this);
            return;
        } catch (Throwable e) {
            logger.error("Thread stopped unexpectedly!", e);
            globalContext.registerException(e);
            throw new DSEException(e);
        } finally {
            globalContext.strategyFinished(this);
            dispose();
        }
    }

    /**
     * Disposes of this strategy. Recursively callse dispose on the underlying {@link RuleEngine} and
     * {@link IncQueryEngine}. Calling this is only required if the design space exploration was launched in thread, as
     * the underlying engines get collected on the stop of the running {@link Thread}.
     */
    public void dispose() {
        threadContext.getRuleEngine().dispose();
        // threadContext.getIncqueryEngine().dispose();
    }

    /**
     * Returns the associated {@link ThreadContext} that houses all the thread specific data about the exploration
     * process, and is also the gateway to the {@link GlobalContext} which stores data relevant to the design space
     * exploration process as a whole.
     * 
     * @return the relevant {@link ThreadContext}.
     */
    public ThreadContext getThreadContext() {
        return threadContext;
    }

}
