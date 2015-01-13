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
package org.eclipse.viatra.dse.api;

import org.eclipse.viatra.dse.api.strategy.impl.DepthFirstNextTransition;
import org.eclipse.viatra.dse.api.strategy.impl.FixedPriorityNextTransition;
import org.eclipse.viatra.dse.api.strategy.impl.ParallelBFSNextTransition;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.base.ExplorerThread;

/**
 * Helper class for instantiating Strategies. To implement a new strategy use the {@link ExplorerThread} class.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public final class Strategies {

    private Strategies() {
    }

    public static INextTransition createDFSStrategy(int depthLimit) {
        return new DepthFirstNextTransition(depthLimit);
    }

    public static INextTransition createFixedPriorityStrategy(int depthLimit) {
        return createFixedPriorityStrategy(depthLimit, true);
    }

    public static INextTransition createFixedPriorityStrategy(int depthLimit,
            boolean tryHigherPriorityFirst) {
        return new FixedPriorityNextTransition(tryHigherPriorityFirst, true,
                depthLimit);
    }

    public static INextTransition createBFSStrategy() {
        return createBFSStrategy(0);
    }

    public static INextTransition createBFSStrategy(int depthLimit) {
        return new ParallelBFSNextTransition(depthLimit);
    }
}
