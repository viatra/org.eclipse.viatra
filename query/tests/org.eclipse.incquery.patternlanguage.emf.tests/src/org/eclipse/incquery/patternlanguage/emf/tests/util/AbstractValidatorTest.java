/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.tests.util;

import static org.eclipse.emf.common.util.Diagnostic.INFO;

import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.Pred;

public abstract class AbstractValidatorTest{

	protected DiagnosticPredicate getErrorCode(String issueId) {
		return AssertableDiagnostics.errorCode(issueId);
	}
	protected DiagnosticPredicate getWarningCode(String issueId) {
		return AssertableDiagnostics.warningCode(issueId);
	}
	protected DiagnosticPredicate getInfoCode(String issueId) {
	    return new Pred(INFO, null, issueId, null);
	}
}
