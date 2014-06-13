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

import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * This interface is the part of the strategy building blocks. Defines a method which determines if the current state
 * satisfies the constraints.
 * 
 * @author Andras Szabolcs Nagy
 */
public interface ICheckConstraints {

    /**
     * Determines whether the current state satisfies the constraints.
     * 
     * @param context
     *            The {@link ThreadContext} which contains the necessary information.
     * @return True if the state is valid.
     */
    boolean checkConstraints(ThreadContext context);

}
