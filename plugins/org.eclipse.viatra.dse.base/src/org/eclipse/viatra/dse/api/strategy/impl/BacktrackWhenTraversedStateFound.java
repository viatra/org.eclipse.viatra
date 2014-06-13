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
package org.eclipse.viatra.dse.api.strategy.impl;

import org.eclipse.viatra.dse.api.strategy.interfaces.ITraversedStateFound;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;

/**
 * This strategy component makes the exploration process step back whenever a step results in a state, that has been
 * found before.
 */
public class BacktrackWhenTraversedStateFound implements ITraversedStateFound {

    @Override
    public void traversedStateFound(ThreadContext context, TraversalStateType traversedState) {
        context.getDesignSpaceManager().undoLastTransformation();
    }

}
