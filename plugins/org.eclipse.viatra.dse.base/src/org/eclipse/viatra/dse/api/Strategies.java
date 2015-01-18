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

import org.eclipse.viatra.dse.api.strategy.impl.DepthFirstStrategy;
import org.eclipse.viatra.dse.api.strategy.impl.FixedPriorityStrategy;
import org.eclipse.viatra.dse.api.strategy.impl.ParallelBFSStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
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

    public static IStrategy createDFSStrategy(int depthLimit) {
        return new DepthFirstStrategy(depthLimit);
    }

    public static IStrategy createFixedPriorityStrategy() {
        return createFixedPriorityStrategy(0);
    }

    public static IStrategy createFixedPriorityStrategy(int depthLimit) {
        return new FixedPriorityStrategy().withDepthLimit(depthLimit);
    }

    public static IStrategy createBFSStrategy() {
        return createBFSStrategy(0);
    }

    public static IStrategy createBFSStrategy(int depthLimit) {
        return new ParallelBFSStrategy(depthLimit);
    }
}
