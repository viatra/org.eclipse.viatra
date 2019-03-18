/*******************************************************************************
 * Copyright (c) 2010-2012, Balazs Grill, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.migrator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class FileStringReplacer {

    private final IFile file;
    
    private String data;
    
    public FileStringReplacer(IFile file) throws IOException, CoreException {
        this.file = file;
        try(InputStream input = file.getContents()){
            this.data = CharStreams.toString(new InputStreamReader(
                    input, Charsets.UTF_8));
        }
    }
    
    /**
     * Replace all matches of the given pattern with the given replacement and return true if
     * the number of matches were greater than zero.
     * @param pattern
     * @param replacement
     */
    public boolean replacePattern(String pattern, String replacement){
        String newData = data.replaceAll(pattern, replacement);
        boolean changed = !newData.equals(data);
        data = newData;
        return changed;
    }
    
    public void save() throws UnsupportedEncodingException, CoreException{
        file.setContents(new ByteArrayInputStream(data.getBytes(file.getCharset(true))), true, true, null);
    }
    
}
