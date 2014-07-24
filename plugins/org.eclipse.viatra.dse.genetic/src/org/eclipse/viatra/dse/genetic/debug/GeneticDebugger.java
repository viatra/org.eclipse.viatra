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
package org.eclipse.viatra.dse.genetic.debug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.genetic.core.InstanceData;

public class GeneticDebugger {

    private int configId = -1;
    private int runId = -1;
    private boolean debug = false;
    private int iteration = 1;
    private ArrayList<String> orderedObjectives;

    public GeneticDebugger(boolean isDebugEnabled) {
        this.debug = isDebugEnabled;
    }

    public void debug(List<InstanceData> populationToDebug) {

        if (!debug) {
            return;
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(runId + ".csv", true)));

            if (iteration <= 1) {
                orderedObjectives = printHeader(populationToDebug, out);
            }

            for (InstanceData instanceData : populationToDebug) {
                StringBuilder sb = new StringBuilder();

                sb.append(configId);
                sb.append(';');
                sb.append(runId);
                sb.append(';');
                sb.append(iteration);
                sb.append(';');
                sb.append(instanceData.trajectory.size());
                sb.append(';');
                sb.append(instanceData.sumOfConstraintViolationMeauserement);
                sb.append(';');

                for (String key : orderedObjectives) {
                    sb.append(instanceData.objectives.get(key));
                    sb.append(';');
                }

                sb.append(instanceData.rank - 1);
                sb.append(';');
                sb.append(instanceData.survive);
                sb.append(';');

                out.println(sb.toString());
            }

        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write file " + runId + ".", e);
        } finally {
            out.close();
        }

        iteration++;

    }

    private ArrayList<String> printHeader(List<InstanceData> populationToDebug, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("ConfigId;RunId;Iteration;Length;SoftConstraints;");
        InstanceData individual = populationToDebug.get(0);
        Set<String> objectives = individual.objectives.keySet();
        ArrayList<String> orderedObjectives = new ArrayList<String>(objectives);
        for (String objective : orderedObjectives) {
            sb.append(objective);
            sb.append(';');
        }
        sb.append("FrontIndex;Survive");
        out.println(sb.toString());
        return orderedObjectives;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public boolean isDebug() {
        return debug;
    }

}
