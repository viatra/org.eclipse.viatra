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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class can be used for running tests configured by a csv file (each row is a test case) and create
 * the corresponding csv results file.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public abstract class BaseTestRunner {

    private static final String CSV = ".csv";
    private static final String defaultResultFileName = "results";
    private String configFileName = "config";

    protected String resultsFolderName;
    private String resultsFileName;

    public static class BaseResult {

        public int configId;
        public int runId;
        public String report = "Successful";
        public double runTime = -1;
        public double memBefore;
        public double memAfter;

        public String toString() {
            return configId + "," + runId + "," + report + "," + runTime + "," + memBefore + "," + memAfter;
        }

        public static String header() {
            return "ConfigId,RunId,Report,RunTime[s],MemBefore,MemAfter";
        }

    }

    /**
     * Returns a string which will be the header of the results file. The string should contain one line with column
     * names separated by semicolons. Note that there are a few predefined columns, see {@link BaseResult} for details.
     * 
     * @return Column names separated by semicolons.
     */
    public abstract String getResultsHeader();

    /**
     * Runs the desired tests.
     * 
     * @param configRow
     *            The configuration row.
     * @param result
     *            The base results, used to fill the attributes {@code report} and {@code runTime}.
     * @return the results separated by semicolons, in the order defined by the
     *         {@link BaseTestRunner#getResultsHeader()} method.
     * 
     * @throws Exception 
     */
    public abstract String runTestWithConfig(Row configRow, BaseResult result) throws Exception;

    public void runTests() throws Exception {

        
        List<String> configKeysInOrder = new ArrayList<String>();
        int numberOfRows = countRecordsInFile(configKeysInOrder);

        boolean timesColumn = configKeysInOrder.contains("Times");

        resolveResultsFolderName();
        new File(resultsFolderName).mkdir();
        resultsFileName = resultsFolderName + File.separator + defaultResultFileName + CSV;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(configFileName + CSV));
            br.readLine();
            int i = 1;
            int times = 0;
            int maxTimes = 1;
            for (String line; (line = br.readLine()) != null;) {

                Row configRow = new Row(configKeysInOrder);
                String[] values = line.split(",");
                for (int index = 0; index < values.length; index++) {
                    configRow.add(configKeysInOrder.get(index), values[index]);
                }

                if (timesColumn) {
                    maxTimes = configRow.getValueAsInteger("Times");
                }

                while (times++ < maxTimes) {

                    System.gc();
                    System.gc();
                    System.gc();
                    System.gc();
                    System.gc();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                    long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    BaseResult result = new BaseResult();
                    result.memBefore = (memBefore / 1024) / 1024;
                    result.configId = i;
                    result.runId = times;
                    String stringResult = runTestWithConfig(configRow, result);

                    long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    result.memAfter = (memAfter / 1024) / 1024;

                    if (i == 1 && times == 1) {
                        printHeader(BaseResult.header() + "," + getResultsHeader());
                    }
                    appendResultToFile(result.toString() + "," + stringResult);

                    if (timesColumn) {
                        System.out.println("Finished " + times + "/" + maxTimes + " with config row " + i + "/"
                                + numberOfRows + " in " + result.runTime + " secundum.");
                        if (times == maxTimes) {
                            ++i;
                        }
                    } else {
                        System.out.println("Finished " + i++ + "/" + numberOfRows + " in " + result.runTime
                                + " secundum.");
                    }
                }

                times = 0;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }

    }

    private int countRecordsInFile(List<String> configKeysInOrder) throws IOException {
        int length = 0;
        String[] keys;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(configFileName + CSV));

            String header = br.readLine();
            keys = header.split(",");
            for (String string : keys) {
                configKeysInOrder.add(string);
            }

            while (br.readLine() != null) {
                ++length;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return length;
    }

    private void printHeader(String header) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(resultsFileName, false)));
            out.println(header);
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void resolveResultsFolderName() {
        int i = 1;
        String resultsFolderBaseName = configFileName + "-" + defaultResultFileName;
        resultsFolderName = resultsFolderBaseName;
        File f;
        do {
            f = new File(resultsFolderName);
            if (f.exists() && f.isDirectory()) {
                resultsFolderName = resultsFolderBaseName + ++i;
            }
        } while (f.exists() && f.isDirectory());
    }

    private void appendResultToFile(String result) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(resultsFileName, true)));
            out.println(result);
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public String getConfigFileName() {
        return configFileName;
    }
    
    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }
}
