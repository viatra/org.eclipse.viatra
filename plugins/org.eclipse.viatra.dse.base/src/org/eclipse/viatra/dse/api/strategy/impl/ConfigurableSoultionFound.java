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
 * This strategy component makes the exploration process stop if the predefined number of solutions are found. If that
 * number is zero, than it never stops.
 * 
 * @author Andras Szabolcs Nagy
 */
public class ConfigurableSoultionFound implements ISolutionFound {

    private int waitForXSolutions = 0;
    private int actNumberOfSolutions = 0;

    public ConfigurableSoultionFound() {
    }

    public ConfigurableSoultionFound(int waitForXSolutions) {
        this.waitForXSolutions = waitForXSolutions;
    }

    @Override
    public ExecutationType solutionFound(ThreadContext context, Solution solution) {

        if (waitForXSolutions == 0) {
            return ExecutationType.CONTINUE;
        } else {
            actNumberOfSolutions++;
            if (waitForXSolutions <= actNumberOfSolutions) {
                return ExecutationType.STOP_ALL;
            } else {
                return ExecutationType.CONTINUE;
            }
        }
    }

    public int getWaitForXSolutions() {
        return waitForXSolutions;
    }

    public void setWaitForXSolutions(int waitForXSolutions) {
        this.waitForXSolutions = waitForXSolutions;
    }

    public int getActNumberOfSolutions() {
        return actNumberOfSolutions;
    }

    public void setActNumberOfSolutions(int actNumberOfSolutions) {
        this.actNumberOfSolutions = actNumberOfSolutions;
    }

}
