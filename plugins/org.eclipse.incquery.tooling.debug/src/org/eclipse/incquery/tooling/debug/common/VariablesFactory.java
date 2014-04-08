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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

@SuppressWarnings("restriction")
public class VariablesFactory {

    public List<IJavaVariable> getVariables(JDIStackFrame wrappedStackFrame, ThreadReference threadReference) {
        return new ArrayList<IJavaVariable>();
    }

    public VirtualMachine getVirtualMachine(JDIStackFrame wrappedStackFrame) {
        try {
            JDIObjectValue classLoaderObject = (JDIObjectValue) wrappedStackFrame.getReferenceType().getClassLoaderObject();
            if (classLoaderObject != null) {
                ClassLoaderReference classLoaderReference = (ClassLoaderReference) classLoaderObject.getUnderlyingObject();
                return classLoaderReference.virtualMachine();
            }
        } catch (DebugException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
