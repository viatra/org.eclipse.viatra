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
package org.eclipse.viatra.dse.genetic.interfaces;

import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;

public interface IInitialPopulationSelector extends LocalSearchStrategyBase {

    /**
     * The initial population selector must call the
     * {@link IStoreChild#addChild(org.eclipse.viatra.dse.base.ThreadContext)} method when a potentially good trajectory
     * is found.
     * 
     * @param store
     *            An interface with the call back method.
     */
    void setChildStore(IStoreChild store);

    /**
     * Sets the populations size in the initialization phase. The engine expects this amount of child from the
     * implementation of this interface via the {@link IStoreChild} implementation.
     * 
     * @param populationSize
     *            Expected number of children.
     */
    void setPopulationSize(int populationSize);

}
