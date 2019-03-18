/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class CsvFile {

    protected String fileBasePath = "";
    protected String fileName;
    protected Character delimeter = ',';

    private File csvFile;
    private Path path;

    protected List<String> columnNamesInOrder = new ArrayList<>();

    public String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        for (String string : columnNamesInOrder) {
            sb.append(string);
            sb.append(delimeter);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String rowIntoString(Row row) {
        StringBuilder sb = new StringBuilder();
        for (String key : columnNamesInOrder) {
            String value = row.get(key);
            sb.append(value);
            sb.append(delimeter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public boolean createCsvFile() {
        path = Paths.get(fileBasePath, fileName + ".csv");
        csvFile = new File(path.toUri());
        if (csvFile.exists()) {
            Logger.getLogger(getClass()).warn("File " + fileName + ".csv already exists!");
            return false;
        }

        try (
                FileWriter fw = new FileWriter(path.toString(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)){
            out.println(getHeaderString());
            return true;
        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write csv file.", e);
        }
        return false;
    }

    public boolean appendRow(Row row) {
        Preconditions.checkState(csvFile != null, "Csv file is not created yet.");

        try (
                FileWriter fw = new FileWriter(path.toString(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(rowIntoString(row));
            return true;
        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write csv file.", e);
        }
        return false;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileBasePath(String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public String getFileBasePath() {
        return fileBasePath;
    }

    public List<String> getColumnNamesInOrder() {
        return columnNamesInOrder;
    }

    public void setColumnNamesInOrder(List<String> columnNamesInOrder) {
        this.columnNamesInOrder = columnNamesInOrder;
    }

}
