/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.util.patternparser;

import java.util.List;

import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PatternParsingResults {
    protected PatternSetValidationDiagnostics diag;
    protected List<Pattern> patterns;
    
    public PatternParsingResults(List<Pattern> patterns, PatternSetValidationDiagnostics diag) {
        this.diag = diag;
        this.patterns = patterns;
    }
    
    public PatternParsingResults(PatternSetValidationDiagnostics diag) {
        this.diag = diag;
        this.patterns = Lists.newArrayList();
    }
    
    public boolean hasWarning() {
        return diag.getAllWarnings().size()>0;
    }
    
    public boolean hasError() {
        return diag.getAllErrors().size()>0;
    }
    
    public boolean validationOK() {
        return !hasError() && !hasWarning();
    }

    public Iterable<Issue> getAllDiagnostics() {
        return Iterables.concat(diag.getAllErrors(), diag.getAllWarnings());
    }
    
    public Iterable<Issue> getErrors() {
        return diag.getAllErrors();
    }
   
    public Iterable<Issue> getWarnings() {
        return diag.getAllWarnings();
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (Issue d : getAllDiagnostics()) {
            b.append(d.toString());
            b.append("\n");
        }
        return b.toString();
    }
    
    /**
     * In case of parsing errors, the returned contents is undefined.
     * @return
     */
    public List<Pattern> getPatterns() {
        return patterns;
    }

}
