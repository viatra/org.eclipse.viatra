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

import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Lists;

public class DiagnosticCollector implements IAcceptor<Issue>{
    private List<Issue> errors = Lists.newArrayList();
    private List<Issue> warnings = Lists.newArrayList();
    
    @Override
    public void accept(Issue t) {
        switch(t.getSeverity()) {
        case ERROR:
            errors.add(t);
            break;
        case WARNING:
            warnings.add(t);
            break;
        default:
            break;
        }
    }

    public List<Issue> getErrors() {
        return errors;
    }

    public List<Issue> getWarnings() {
        return warnings;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty() || !errors.isEmpty();
    }   
}
