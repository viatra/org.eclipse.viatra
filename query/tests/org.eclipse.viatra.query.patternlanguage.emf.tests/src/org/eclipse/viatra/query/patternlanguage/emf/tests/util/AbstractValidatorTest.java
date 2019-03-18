/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.util;

import static org.eclipse.emf.common.util.Diagnostic.INFO;

import java.util.Iterator;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.Pred;

public abstract class AbstractValidatorTest {

    protected DiagnosticPredicate getErrorCode(String issueId) {
        return AssertableDiagnostics.errorCode(issueId);
    }

    protected DiagnosticPredicate getWarningCode(String issueId) {
        return AssertableDiagnostics.warningCode(issueId);
    }

    protected DiagnosticPredicate getInfoCode(String issueId) {
        return new Pred(INFO, null, issueId, null);
    }

    protected void assertOK(AssertableDiagnostics diagnostics) {
        Iterator<Diagnostic> it = diagnostics.getAllDiagnostics().iterator();
        if (it.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append("There are expected to be no diagnostics but the following were found: \n");
            while (it.hasNext()) {
                Diagnostic next = it.next();
                sb.append(next.getMessage());
                sb.append("\n");
            }
            throw new AssertionError(sb.toString());
        }
    }
}
