/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.Arrays;
import java.util.List;

public class DseCsvConstants {

    public static final String configId = "ConfigId";
    public static final String runId = "RunId";
    public static final String iteration = "Iteration";
    public static final String runTime = "RunTime[ms]";
    public static final String rank = "Rank";
    public static final String survive = "Survive";
    public static final String length = "Length";
    public static final String trajectory = "Trajectory";
    public static final String valid = "Valid";

    public static final List<String> resultConstants = Arrays.asList(
            configId,
            runId,
            iteration,
            runTime,
            rank,
            survive,
            length,
            trajectory,
            valid
            );
}
