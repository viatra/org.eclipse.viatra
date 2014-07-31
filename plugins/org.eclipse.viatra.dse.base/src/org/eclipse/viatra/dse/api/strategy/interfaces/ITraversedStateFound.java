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

import org.eclipse.viatra.dse.api.strategy.ExplorerThread;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;

/**
 * This interface is the part of the strategy building blocks. Defines a method which is called by the
 * {@link ExplorerThread#execute()} if an already traversed state has been found.
 * 
 * @author Andras Szabolcs Nagy
 */
public interface ITraversedStateFound {

    /**
     * Defines an action when an already traversed state has been found.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations.
     */
    void traversedStateFound(ThreadContext context, TraversalStateType traversalState);
}
