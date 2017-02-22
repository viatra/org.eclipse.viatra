/*******************************************************************************
 * Copyright (c) 2010-2017, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.solutionstore;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.base.ThreadContext;

public class LogSolutionHandler implements ISolutionFoundHandler {

    Logger logger = Logger.getLogger(LogSolutionHandler.class);

    @Override
    public void solutionFound(ThreadContext context, SolutionTrajectory trajectory) {
        logger.info("Solution registered: " + trajectory.toPrettyString());
    }

    @Override
    public void solutionTriedToSave(ThreadContext context, SolutionTrajectory trajectory) {
        logger.debug("Not good enough solution: " + trajectory.toPrettyString());
    }
}