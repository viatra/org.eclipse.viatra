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
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.debug.common.ViatraQueryDebugValue;
import org.eclipse.viatra.query.tooling.debug.common.ViatraQueryDebugVariable;
import org.eclipse.viatra.query.tooling.debug.variables.ValueWrapper;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;

/**
 * The value of an Viatra Query Debug Variable which represents an {@link ViatraQueryMatcher} instance. Children variables are 
 * the pattern matches.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class MatcherValue extends ViatraQueryDebugValue {

    public MatcherValue(JDIDebugTarget debugTarget, ValueWrapper value) {
        super(debugTarget, value);
    }

    @Override
    public String getLabel() {
        try {
            ValueWrapper patternName = fValue.invoke("getPatternName");
            if (patternName.getValue() != null) {
                return ((StringReference) patternName.getValue()).value();
            } else {
                return null;
            }
        } catch (Exception e) {
            ViatraQueryLoggingUtil.getLogger(MatcherValue.class).error("Label initialization has failed!", e);
            return null;
        }
    }

    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else {
            fVariables = new ArrayList<>();

            ValueWrapper matchesWrapper = fValue.invoke("getAllMatches").get("elementData");
            if (matchesWrapper.isArray()) {
                ArrayReference matches = (ArrayReference) matchesWrapper.getValue();
                if (matches.length() > 0) { // XXX https://bugs.eclipse.org/bugs/show_bug.cgi?id=478279 necessary because of bug in org.eclipse.jdi.internal.ArrayReferenceImpl.getValues
                    for (Value match : matches.getValues()) {
                        if (match != null) {
                            ValueWrapper wrappedMatch = ValueWrapper.wrap(match, fValue.getThreadReference());
                            ViatraQueryDebugVariable var = new ViatraQueryDebugVariable(this.getJavaDebugTarget());
                            MatchValue value = new MatchValue(debugTarget, wrappedMatch);
                            var.setValue(value);
                            fVariables.add(var);
                        }
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
