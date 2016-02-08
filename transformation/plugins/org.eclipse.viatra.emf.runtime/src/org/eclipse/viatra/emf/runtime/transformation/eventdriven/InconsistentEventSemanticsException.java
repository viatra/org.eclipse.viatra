/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.transformation.eventdriven;

public class InconsistentEventSemanticsException extends Exception {
    private static final long serialVersionUID = 3645910677281157585L;
    private static final String ERROR_MSG = "Inconsistent event semantics.";

    public InconsistentEventSemanticsException() {
        super(ERROR_MSG);
    }

    public InconsistentEventSemanticsException(String previousSemantics, String newSemantics) {
        super(getErrorMsg(previousSemantics, newSemantics));
    }

    private static String getErrorMsg(String previousSemantics, String newSemantics) {
        return ERROR_MSG + " (Mixing " + previousSemantics + " and " + newSemantics + " is not valid.)";
    }
}
