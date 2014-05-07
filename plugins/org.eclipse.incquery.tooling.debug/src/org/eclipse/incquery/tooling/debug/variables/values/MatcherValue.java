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
package org.eclipse.incquery.tooling.debug.variables.values;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugUtil;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugValue;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugVariable;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

/**
 * The value of an IncQuery Debug Variable which represents an {@link IncQueryMatcher} instance. 
 * Children variables will be the pattern matches.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class MatcherValue extends IncQueryDebugValue {

    public MatcherValue(JDIDebugTarget debugTarget, ThreadReference threadReference, Value value) {
        super(debugTarget, threadReference, value);
    }

    @Override
    public String getLabel() {
        try {
            ObjectReference object = (ObjectReference) fValue;
            StringReference patternName = (StringReference) IncQueryDebugUtil.invokeMethod(threadReference, object, "getPatternName");
            return patternName.value();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            ObjectReference object = (ObjectReference) fValue;
            fVariables = new ArrayList<IJavaVariable>();

            ObjectReference currentMatches = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference, object, "getAllMatches");
            ArrayReference elementDataFieldValue = (ArrayReference) IncQueryDebugUtil.getField(currentMatches, "elementData");
                   
            if (elementDataFieldValue.length() > 0) {
                for (Value val : elementDataFieldValue.getValues()) {
                    if (val != null) {
                        IncQueryDebugVariable var = new IncQueryDebugVariable(this.getJavaDebugTarget());
                        MatchValue value = new MatchValue(debugTarget, threadReference, val);
                        var.setValue(value);
                        fVariables.add(var);
                    }
                }
            }
            return fVariables;
        }
    }
 
    @Override
    public String getValueString() throws DebugException {
        String label = getLabel();
        return (label == null ? super.getValueString() : label);
    }
}
