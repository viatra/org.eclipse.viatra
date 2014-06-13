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

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * This strategy component makes the exploration process stop if a solution is found. In effect, the exploration process
 * will only runs until the first solution.
 */
public class StopAtFirstSolutionFound implements ISolutionFound {

    @Override
    public ExecutationType solutionFound(ThreadContext context, Solution solution) {
        return ExecutationType.STOP_ALL;
    }

}
