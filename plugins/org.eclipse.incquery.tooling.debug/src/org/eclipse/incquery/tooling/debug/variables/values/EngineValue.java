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
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugUtil;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugValue;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugVariable;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

/**
 * The value of an IncQuery Debug Variable which represents an {@link IncQueryEngine} instance. This is the starting
 * point in the value hierarchy of 'engine - matcher - match - match parameters'. 
 * <br><br>
 * It is crucial, which class name is used for capturing the
 * engine instances, because no subtyping relationship can be used with the Debug API. Consecutive variable retrieval
 * for the matchers and matches are 'easier' because the container data structure is used from the parent element (e.g.
 * a {@link HashMap}).
 * <br><br>
 * The children variables will be the matchers in the given engine.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class EngineValue extends IncQueryDebugValue implements Comparable<EngineValue> {

    private String cachedLabel;

    public EngineValue(JDIDebugTarget debugTarget, ThreadReference threadReference, Value value) {
        super(debugTarget, threadReference, value);
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            try {
                ObjectReference object = (ObjectReference) fValue;
                fVariables = new ArrayList<IJavaVariable>();

                ObjectReference matchersFieldValue = (ObjectReference) IncQueryDebugUtil.getField(object, "matchers");
                ObjectReference tableFieldValue = (ObjectReference) IncQueryDebugUtil.getField(matchersFieldValue,
                        "table");

                for (Value val : ((ArrayReference) tableFieldValue).getValues()) {
                    if (val != null) {
                        ObjectReference valueFieldValue = (ObjectReference) IncQueryDebugUtil.getField(
                                (ObjectReference) val, "value");
                        IncQueryDebugVariable var = new IncQueryDebugVariable(this.getJavaDebugTarget());
                        MatcherValue value = new MatcherValue(debugTarget, threadReference, valueFieldValue);
                        var.setValue(value);
                        fVariables.add(var);

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
                ObjectReference object = (ObjectReference) this.fValue;
                BooleanValue isManaged = (BooleanValue) IncQueryDebugUtil.invokeMethod(threadReference, object, "isManaged");
                ObjectReference emfRoot = (ObjectReference) IncQueryDebugUtil.getField(object, "emfRoot");
                ObjectReference resourceSet = null;

                if (isResourceSet(emfRoot)) {
                    resourceSet = emfRoot;
                } else if (isResource(emfRoot)) {
                    resourceSet = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference, emfRoot,
                            "getResourceSet");
                } else {
                    ObjectReference resource = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference,
                            emfRoot, "eResource");
                    resourceSet = (ObjectReference) IncQueryDebugUtil.invokeMethod(threadReference, resource,
                            "getResourceSet");
                }

                ObjectReference resources = (ObjectReference) IncQueryDebugUtil.getField(resourceSet, "resources");
                ArrayReference data = (ArrayReference) IncQueryDebugUtil.getField(resources, "data");

                StringBuilder sb = new StringBuilder();
                if (!isManaged.booleanValue()) {
                    sb.append("Advanced");
                }
                sb.append("IncQueryEngine on ");

                for (Value resource : data.getValues()) {
                    if (resource != null) {
                        ObjectReference uri = (ObjectReference) IncQueryDebugUtil.getField((ObjectReference) resource,
                                "uri");
                        ArrayReference segments = (ArrayReference) IncQueryDebugUtil.getField(uri, "segments");

                        int j = 0;
                        for (Value value : segments.getValues()) {
                            sb.append(((StringReference) value).value());
                            if (j < segments.length()) {
                                sb.append("/");
                            }
                            j++;
                        }
                    }
                    sb.append(" ");
                }

                cachedLabel = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedLabel;
    }

    private boolean isResourceSet(ObjectReference ref) {
        return IncQueryDebugUtil.getField(ref, "resources") != null;
    }

    private boolean isResource(ObjectReference ref) {
        return IncQueryDebugUtil.invokeMethod(threadReference, ref, "getResourceSet") != null;
    }

    @Override
    public int compareTo(EngineValue that) {
        return new Long(((ObjectReference) this.fValue).uniqueID()).compareTo(((ObjectReference) that.fValue)
                .uniqueID());
    }
}
