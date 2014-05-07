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

/**
 * An instances of this class represents the value of an IncQuery Debug variable. It is also responsible for the
 * creation of the children variables.
 * <br><br>
 * WARNING: Java Reflection and the Java Debug API are used heavily for the creation of the label and the children
 * variables. Upon API changes (field name change, method name change) these calls can break easily and thus, need to be
 * adjusted accordingly.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public abstract class IncQueryDebugValue extends JDIValue {

    protected JDIDebugTarget debugTarget;
    protected List<IJavaVariable> fVariables;
    protected Value fValue;
    protected ThreadReference threadReference;
    protected String[] additionalData;

    public IncQueryDebugValue(JDIDebugTarget debugTarget, ThreadReference threadReference, Value value,
            String... additionalData) {
        super(debugTarget, value);
        this.debugTarget = debugTarget;
        this.fValue = value;
        this.additionalData = additionalData;
        this.threadReference = threadReference;
    }

    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        return Collections.emptyList();
    }

    /**
     * Returns the String representation of the value. This value will be used in the Eclipse Debug View.
     * 
     * @return the String representation of the value
     */
    public abstract String getLabel();
  
}
