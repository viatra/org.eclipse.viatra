/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.Arrays;
import java.util.List;

public class DseCsvConstants {

    public static final String ConfigId = "ConfigId";
    public static final String RunId = "RunId";
    public static final String Iteration = "Iteration";
    public static final String RunTime = "RunTime[ms]";
    public static final String Length = "Length";
    public static final String Fitness = "Fitness";
    public static final String Trajectory = "Trajectory";
    public static final String Rank = "Rank";
    public static final String Survive = "Survive";

    public static final List<String> resultConstants = Arrays.asList(
            ConfigId,
            RunId,
            Iteration,
            RunTime,
            Length,
            Fitness,
            Trajectory,
            Rank,
            Survive
            );
}
