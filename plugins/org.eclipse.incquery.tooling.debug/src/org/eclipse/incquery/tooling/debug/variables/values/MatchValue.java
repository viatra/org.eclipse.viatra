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
import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.incquery.runtime.api.IPatternMatch;
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
 * The value of an IncQuery Debug Variable which represents an {@link IPatternMatch} instance. Children variables will
 * be the pattern match parameters.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class MatchValue extends IncQueryDebugValue {

    public MatchValue(JDIDebugTarget debugTarget, ThreadReference threadReference, Value value,
            String... additionalData) {
        super(debugTarget, threadReference, value, additionalData);
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            try {
                ObjectReference object = (ObjectReference) fValue;
                fVariables = new ArrayList<IJavaVariable>();
                ArrayReference parameters = (ArrayReference) IncQueryDebugUtil.invokeMethod(threadReference, object,
                        "toArray");
                ObjectReference parameterNames = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference,
                        object, "parameterNames");
                ArrayReference list = (ArrayReference) IncQueryDebugUtil.invokeMethod(threadReference, parameterNames,
                        "toArray");

                for (int i = 0; i < parameters.length(); i++) {
                    IncQueryDebugVariable var = new IncQueryDebugVariable(this.getJavaDebugTarget());
                    MatchParameterValue value = new MatchParameterValue(debugTarget, threadReference,
                            parameters.getValue(i), ((StringReference) list.getValue(i)).value());
                    var.setValue(value);
                    fVariables.add(var);

                }
                return fVariables;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
    }

    @Override
    public String getLabel() {
        return "match";
    }

    @Override
    public String getValueString() throws DebugException {
        ObjectReference value = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference,
                (ObjectReference) fValue, "prettyPrint");
        if (value != null) {
            return ((StringReference) value).value();
        } else {
            return super.getValueString();
        }
    }
}
