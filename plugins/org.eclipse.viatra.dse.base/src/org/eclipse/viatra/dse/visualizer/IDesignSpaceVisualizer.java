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
package org.eclipse.viatra.dse.visualizer;

import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * 
 * An implementation of this interface is notified about the traversal of the design space from every traversing thread,
 * if registered to the {@link DesignSpaceExplorer}. Its purpose is to able to visualize the design space (a directed
 * graph with IDs of the nodes and transitions) and to able to visualize the order of the exploration (the trace of a
 * thread).
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public interface IDesignSpaceVisualizer extends IExploreEventHandler {

    /**
     * Initializes the instance with a starting thread's context. Can be called multiple times and concurrently.
     * 
     * @see DesignSpaceVisualizerOptions
     * @param context
     */
    void init(ThreadContext context);

    /**
     * Saves the captured data.
     */
    void save();

}
