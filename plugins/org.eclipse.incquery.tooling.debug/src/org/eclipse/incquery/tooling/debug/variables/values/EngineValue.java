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
import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugValue;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugVariable;
import org.eclipse.incquery.tooling.debug.variables.ClassNames;
import org.eclipse.incquery.tooling.debug.variables.ValueWrapper;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;

/**
 * The value of an IncQuery Debug Variable which represents an {@link IncQueryEngine} instance. This is the starting
 * point in the value hierarchy of 'engine - matcher - match - match parameters'. <br>
 * <br>
 * It is crucial, which class name is used for capturing the engine instances, because no subtyping relationship can be
 * used with the Debug API. Consecutive variable retrieval for the matchers and matches are 'easier' because the
 * container data structure is used from the parent element (e.g. a {@link HashMap}). <br>
 * <br>
 * The children variables will be the matchers in the given engine.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class EngineValue extends IncQueryDebugValue implements Comparable<EngineValue> {

    private String cachedLabel;

    public EngineValue(JDIDebugTarget debugTarget, ValueWrapper value) {
        super(debugTarget, value);
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            try {
                fVariables = new ArrayList<IJavaVariable>();
                ValueWrapper matchers = fValue.get("matchers").get("table");

                if (matchers.isArray()) {
                    for (Value val : ((ArrayReference) matchers.getValue()).getValues()) {
                        if (val != null) {
                            ValueWrapper wrappedValue = ValueWrapper.wrap(val, fValue.getThreadReference());
                            ValueWrapper valueFieldValue = wrappedValue.get("value");
                            IncQueryDebugVariable var = new IncQueryDebugVariable(getJavaDebugTarget());
                            MatcherValue value = new MatcherValue(debugTarget, valueFieldValue);
                            var.setValue(value);
                            fVariables.add(var);
                        }
                    }
                }
                return fVariables;
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public String getLabel() {
        if (cachedLabel == null) {
            try {
                ValueWrapper isManaged = fValue.invoke("isManaged");
                ValueWrapper scope = fValue.get("scope");

                StringBuilder sb = new StringBuilder();
                if (!((BooleanValue) isManaged.getValue()).booleanValue()) {
                    sb.append("Advanced");
                }
                sb.append("IncQueryEngine on ");

                if (scope.getValue().type().name().matches(ClassNames.EMF_SCOPE_NAME)) {
                    // emf scope
                    ValueWrapper emfRoot = scope.get("scopeRoot");
                    ValueWrapper resourceSet = null;

                    if (isResourceSet(emfRoot)) {
                        resourceSet = emfRoot;
                    } else if (isResource(emfRoot)) {
                        resourceSet = emfRoot.invoke("getResourceSet");
                    } else {
                        resourceSet = emfRoot.invoke("eResource").invoke("getResourceSet");
                    }

                    ValueWrapper resources = resourceSet.get("resources").get("data");

                    if (resources.isArray()) {
                        for (Value resource : ((ArrayReference) resources.getValue()).getValues()) {
                            if (resource != null) {
                                ValueWrapper wrappedResource = ValueWrapper.wrap(resource, fValue.getThreadReference());
                                ValueWrapper segments = wrappedResource.get("uri").get("segments");

                                int length = ((ArrayReference) segments.getValue()).length();
                                int j = 0;
                                for (Value value : ((ArrayReference) segments.getValue()).getValues()) {
                                    sb.append(((StringReference) value).value());
                                    if (j < length) {
                                        sb.append("/");
                                    }
                                    j++;
                                }
                                sb.append(" ");
                            }
                        }
                    }
                } else {
                    // other kind of scope
                    ValueWrapper wrappedValue = scope.invoke("toString");
                    sb.append(((StringReference) wrappedValue.getValue()).value());
                }

                cachedLabel = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedLabel;
    }

    private boolean isResourceSet(ValueWrapper value) {
        return value.get("resources").getValue() != null;
    }

    private boolean isResource(ValueWrapper value) {
        return value.invoke("getResourceSet").getValue() != null;
    }

    @Override
    public int compareTo(EngineValue that) {
        return this.fValue.compareTo(that.fValue);
    }
}
