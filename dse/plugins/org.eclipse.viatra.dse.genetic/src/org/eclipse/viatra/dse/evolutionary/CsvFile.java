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

public class CsvFile {

    protected String fileBasePath = "";
    protected String fileName;
    protected Character delimeter = ',';

    protected List<String> columnNamesInOrder = new ArrayList<>();

    protected List<Row> loadedRows;
    private File csvFile;
    private Path path;

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
            return false;
        }

        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)));
            out.println(getHeaderString());
            return true;
        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write csv file.", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return false;
    }
    
    public boolean appendRow(Row row) {
        if (csvFile == null) {
            throw new RuntimeException("Csv file is not created yet.");
        }
        
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)));
            out.println(rowIntoString(row));
            return true;
        } catch (IOException e) {
            Logger.getLogger(getClass()).error("Couldn't write csv file.", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return false;
    }

}
