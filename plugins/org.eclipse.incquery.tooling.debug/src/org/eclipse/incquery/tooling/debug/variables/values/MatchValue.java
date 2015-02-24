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
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugValue;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugVariable;
import org.eclipse.incquery.tooling.debug.variables.ValueWrapper;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.StringReference;

/**
 * The value of an IncQuery Debug Variable which represents an {@link IPatternMatch} instance. Children variables will
 * be the pattern match parameters.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class MatchValue extends IncQueryDebugValue {

    public MatchValue(JDIDebugTarget debugTarget, ValueWrapper value, String... additionalData) {
        super(debugTarget, value, additionalData);
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            try {
                fVariables = new ArrayList<IJavaVariable>();

                ValueWrapper parameters = fValue.invoke("toArray");
                ValueWrapper parameterNames = fValue.invoke("parameterNames").invoke("toArray");

                if (parameterNames.isArray() && parameters.isArray()) {
                    ArrayReference parametersArray = (ArrayReference) parameters.getValue();
                    ArrayReference parameterNamesArray = (ArrayReference) parameterNames.getValue();

                    for (int i = 0; i < parametersArray.length(); i++) {
                        IncQueryDebugVariable var = new IncQueryDebugVariable(this.getJavaDebugTarget());
                        String parameterName = ((StringReference) parameterNamesArray.getValue(i)).value();
                        ValueWrapper wrappedParameter = ValueWrapper.wrap(parametersArray.getValue(i),
                                fValue.getThreadReference());
                        MatchParameterValue value = new MatchParameterValue(debugTarget, wrappedParameter,
                                parameterName);
                        var.setValue(value);
                        fVariables.add(var);
                    }
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
        ValueWrapper value = fValue.invoke("prettyPrint");
        if (value.getValue() != null) {
            return ((StringReference) value.getValue()).value();
        } else {
            return super.getValueString();
        }
    }
}
