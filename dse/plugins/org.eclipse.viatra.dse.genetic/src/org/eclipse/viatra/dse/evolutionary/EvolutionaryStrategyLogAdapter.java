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
import java.util.concurrent.TimeUnit;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IEvolutionaryStrategyAdapter;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

import com.google.common.base.Stopwatch;

public class EvolutionaryStrategyLogAdapter implements IEvolutionaryStrategyAdapter {

    protected CsvFile csv;

    public EvolutionaryStrategyLogAdapter() {
        csv = new CsvFile();
        csv.columnNamesInOrder.addAll(DseCsvConstants.resultConstants);
        csv.fileName = "evolutionary-log";
    }

    private int configId = -1;
    private int runId = -1;
    private int iteration = 1;
    private Stopwatch stopwatch;
    private Row row = new Row();
    private ThreadContext context;

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        for (IObjective objective : context.getObjectives()) {
            csv.columnNamesInOrder.add(objective.getName());
        }
        csv.createCsvFile();
        stopwatch = Stopwatch.createStarted();
    }

    @Override
    public void iterationCompleted(List<TrajectoryFitness> currentPopulation,
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation,
            List<TrajectoryFitness> survivedPopulation, boolean stop) {

        stopwatch.stop();
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        for (TrajectoryFitness trajectoryFitness : survivedPopulation) {

            row.add(DseCsvConstants.configId, configId);
            row.add(DseCsvConstants.runId, runId);
            row.add(DseCsvConstants.iteration, iteration);
            row.add(DseCsvConstants.runTime, elapsedTime);
            row.add(DseCsvConstants.length, trajectoryFitness.trajectory.length);

            row.add(DseCsvConstants.trajectory, "\"" + Arrays.toString(trajectoryFitness.trajectory) + "\"");

            row.add(DseCsvConstants.rank, trajectoryFitness.rank);
            row.add(DseCsvConstants.survive, trajectoryFitness.survive);

            row.add(DseCsvConstants.valid, trajectoryFitness.fitness.isSatisifiesHardObjectives());
            for (IObjective objective : context.getObjectives()) {
                row.add(objective.getName(), trajectoryFitness.fitness.get(objective.getName()));
            }

            csv.appendRow(row);
        }

        iteration++;

        stopwatch = Stopwatch.createStarted();

    }

    public CsvFile getCsvFile() {
        return csv;
    }

    public void setCsvFileName(String fileName) {
        csv.setFileName(fileName);
    }

    public void setCsvFileName(String basePath, String fileName) {
        csv.setFileBasePath(basePath);
        csv.setFileName(fileName);
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

}
