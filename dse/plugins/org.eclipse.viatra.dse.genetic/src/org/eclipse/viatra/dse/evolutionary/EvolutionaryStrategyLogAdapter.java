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
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

import com.google.common.base.Stopwatch;

public class EvolutionaryStrategyLogAdapter implements IEvolutionaryStrategyAdapter {

    protected CsvFile csv;

    public EvolutionaryStrategyLogAdapter() {
        csv = new CsvFile();
        csv.columnNamesInOrder = DseCsvConstants.resultConstants;
        csv.fileName = "evolutionary-log";
    }

    private int configId = -1;
    private int runId = -1;
    private int iteration = 1;
    private Stopwatch stopwatch;
    private Row row = new Row();

    @Override
    public void init(ThreadContext context) {
        csv.createCsvFile();
        stopwatch = Stopwatch.createStarted();
    }

    @Override
    public void iterationCompleted(List<TrajectoryFitness> currentPopulation,
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation,
            List<TrajectoryFitness> survivedPopulation, boolean stop) {

        stopwatch.stop();
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        for (TrajectoryFitness trajectoryFitness : currentPopulation) {

            row.add(DseCsvConstants.configId, configId);
            row.add(DseCsvConstants.runId, runId);
            row.add(DseCsvConstants.iteration, iteration);
            row.add(DseCsvConstants.runTime, elapsedTime);
            row.add(DseCsvConstants.length, trajectoryFitness.trajectory.length);

            row.add(DseCsvConstants.fitness, "\"" + trajectoryFitness.fitness + "\"");
            row.add(DseCsvConstants.trajectory, "\"" + Arrays.toString(trajectoryFitness.trajectory) + "\"");

            row.add(DseCsvConstants.rank, trajectoryFitness.rank - 1);
            row.add(DseCsvConstants.survive, trajectoryFitness.survive);

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
