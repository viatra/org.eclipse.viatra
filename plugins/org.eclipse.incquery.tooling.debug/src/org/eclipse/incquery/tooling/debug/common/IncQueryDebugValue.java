/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.debug.common;

import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

@SuppressWarnings("restriction")
public abstract class IncQueryDebugValue extends JDIValue {

    protected JDIDebugTarget debugTarget;
    protected List<IJavaVariable> fVariables;
    protected Value fValue;
    protected ThreadReference threadReference;
    protected String[] additionalData;

    public IncQueryDebugValue(JDIDebugTarget debugTarget, ThreadReference threadReference, Value value, String... additionalData) {
        super(debugTarget, value);
        this.debugTarget = debugTarget;
        this.fValue = value;
        this.additionalData = additionalData;
        this.threadReference = threadReference;
    }

    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        return Collections.emptyList();
    }

    public abstract String getLabel();
}
