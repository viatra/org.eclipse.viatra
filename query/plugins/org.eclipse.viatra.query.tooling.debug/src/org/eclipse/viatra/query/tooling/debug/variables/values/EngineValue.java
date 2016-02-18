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
package org.eclipse.viatra.query.tooling.debug.variables.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.debug.common.ViatraQueryDebugValue;
import org.eclipse.viatra.query.tooling.debug.common.ViatraQueryDebugVariable;
import org.eclipse.viatra.query.tooling.debug.variables.NameConstants;
import org.eclipse.viatra.query.tooling.debug.variables.ValueWrapper;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;

/**
 * The value of an VIATRA Query Debug Variable which represents an {@link ViatraQueryEngine} instance. This is the root element of the 
 * 'engine - matcher - match - match parameters' content hierarchy. <br>
 * <br>
 * It is crucial, which class name is used for capturing the engine instances, because no subtyping relationship can be
 * used with the Debug API. Consecutive variable retrieval for the matchers and matches is 'easier' because the
 * container data structure is used from the parent element (e.g. a {@link HashMap}). <br>
 * <br>
 * The children variables are the matchers in the given engine.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class EngineValue extends ViatraQueryDebugValue implements Comparable<EngineValue> {

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
                            ViatraQueryDebugVariable var = new ViatraQueryDebugVariable(getJavaDebugTarget());
                            MatcherValue value = new MatcherValue(debugTarget, valueFieldValue);
                            var.setValue(value);
                            fVariables.add(var);
                        }
                    }
                }
                return fVariables;
            } catch (Exception e) {
                ViatraQueryLoggingUtil.getLogger(EngineValue.class).error("Couldn't retrieve the list of debug variables!", e);
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
                sb.append("ViatraQueryEngine on ");

                if (scope.getValue().type().name().matches(NameConstants.EMF_SCOPE_NAME)) {
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
                ViatraQueryLoggingUtil.getLogger(EngineValue.class).error("Label initialization has failed!", e);
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
    
    @Override
    public int hashCode() {
        return this.fValue.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            EngineValue that = (EngineValue) obj;
            return this.fValue.equals(that.fValue);
        }
    }
}
