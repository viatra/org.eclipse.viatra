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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.objectives.impl.ConstraintsObjective;

import com.google.common.base.Stopwatch;

public class GeneticDebugger {

    private static final char COMA = ',';
    private int configId = -1;
    private int runId = -1;
    private boolean debug = false;
    private int iteration = 1;
    private ArrayList<String> orderedObjectives;
    private ArrayList<String> orderedSoftConstraints;
    private String csvName;
    private Stopwatch stopwatch;
    private GlobalContext gc;

    public GeneticDebugger(boolean isDebugEnabled, GlobalContext gc) {
        this.debug = isDebugEnabled;
        this.gc = gc;
        stopwatch = Stopwatch.createStarted();
    }

    public List<String> getCustomColumns() {
        return null;
    }

    public void appendCustomResults(StringBuilder sb, InstanceData instanceData) {
    }

    public void debug(List<InstanceData> populationToDebug) {

        if (!debug) {
            return;
        }

        stopwatch.stop();
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        PrintWriter out = null;
        try {
            File f = new File(csvName);
            boolean isFileExists = f.exists();

            out = new PrintWriter(new BufferedWriter(new FileWriter(csvName, true)));

            if (!isFileExists) {
                printHeader(populationToDebug, out);
            }

            for (InstanceData instanceData : populationToDebug) {
                StringBuilder sb = new StringBuilder();

                sb.append(configId);
                sb.append(COMA);
                sb.append(runId);
                sb.append(COMA);
                sb.append(iteration);
                sb.append(COMA);
                sb.append(elapsedTime);
                sb.append(COMA);
                sb.append(instanceData.trajectory.size());
                sb.append(COMA);

                for (String key : orderedSoftConstraints) {
                    sb.append(instanceData.violations.get(key));
                    sb.append(COMA);
                }

                for (String key : orderedObjectives) {
                    sb.append(instanceData.objectives.get(key));
                    sb.append(COMA);
                }

                sb.append(instanceData.rank - 1);
                sb.append(COMA);
                sb.append(instanceData.survive);
                sb.append(COMA);

                appendCustomResults(sb, instanceData);

                out.println(sb.toString());
            }

        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write file " + csvName + ".", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        iteration++;

        stopwatch = Stopwatch.createStarted();

    }

    private void printHeader(List<InstanceData> populationToDebug, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("ConfigId,RunId,Iteration,RunTime[ms],Length,");

        InstanceData individual = populationToDebug.get(0);

        ConstraintsObjective genObjective = (ConstraintsObjective) gc.getLeveledObjectives()[0][0];
        orderedSoftConstraints = new ArrayList<String>(genObjective.getSoftNames());
        Collections.sort(orderedSoftConstraints);
        for (String softConstraint : orderedSoftConstraints) {
            sb.append(softConstraint);
            sb.append(COMA);
        }

        Set<String> objectives = individual.objectives.keySet();
        orderedObjectives = new ArrayList<String>(objectives);
        Collections.sort(orderedObjectives);
        for (String objective : orderedObjectives) {
            sb.append(objective);
            sb.append(COMA);
        }

        sb.append("FrontIndex,Survive");

        if (getCustomColumns() != null) {
            for (String column : getCustomColumns()) {
                sb.append(COMA);
                sb.append(column);
            }
        }

        out.println(sb.toString());
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

    public void setCsvName(String csvName) {
        this.csvName = csvName;
    }

    public void resetIteration() {
        iteration = 1;
    }

}
