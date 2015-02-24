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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.incquery.tooling.debug.common.IncQueryDebugValue;
import org.eclipse.incquery.tooling.debug.variables.ValueWrapper;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIArrayEntryVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIFieldVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;

/**
 * The value of an IncQuery Debug Variable which represents a match parameter. The children variables will be created by
 * the original {@link JDIValue#getVariables()} logic.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class MatchParameterValue extends IncQueryDebugValue {

    public MatchParameterValue(JDIDebugTarget debugTarget, ValueWrapper value, String... additionalData) {
        super(debugTarget, value, additionalData);
    }

    @Override
    public String getLabel() {
        return additionalData[0];
    }

    // Copied from JDIValue.getVariablesList()
    @Override
    protected synchronized List<IJavaVariable> getVariablesList() throws DebugException {
        if (fVariables != null) {
            return fVariables;
        } else if (fValue.isReference()) {
            fVariables = new ArrayList<IJavaVariable>();
            if (fValue.isArray()) {
                int length = fValue.getArrayLength();
                for (int i = 0; i < length; i++) {
                    fVariables.add(new JDIArrayEntryVariable(getJavaDebugTarget(), (ArrayReference) fValue.getValue(), i,
                            fLogicalParent));
                }
            } else {
                List<Field> fields = null;
                try {
                    ReferenceType refType = ((ObjectReference) fValue.getValue()).referenceType();
                    fields = refType.allFields();
                } catch (ObjectCollectedException e) {
                    return Collections.emptyList();
                } catch (RuntimeException e) {
                    targetRequestFailed(MessageFormat.format(
                            JDIDebugModelMessages.JDIValue_exception_retrieving_fields, new Object[] { e.toString() }),
                            e);
                    // execution will not reach this line, as
                    // #targetRequestFailed will thrown an exception
                    return null;
                }
                Iterator<Field> list = fields.iterator();
                while (list.hasNext()) {
                    Field field = list.next();
                    fVariables.add(new JDIFieldVariable((JDIDebugTarget) getDebugTarget(), field, (ObjectReference) fValue.getValue(),
                            fLogicalParent));
                }
                Collections.sort(fVariables, new Comparator<IJavaVariable>() {
                    public int compare(IJavaVariable a, IJavaVariable b) {
                        return sortChildren(a, b);
                    }
                });
            }

            return fVariables;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getValueString() throws DebugException {
        return super.getValueString();
    }
}
