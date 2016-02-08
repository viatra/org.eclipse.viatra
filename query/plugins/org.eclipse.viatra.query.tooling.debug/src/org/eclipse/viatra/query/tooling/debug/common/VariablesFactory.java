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
package org.eclipse.viatra.query.tooling.debug.common;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;

import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * Subclasses of this factory are responsible for the creation of {@link IJavaVariable}s, which will be 
 * displayed in the Debugger View.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public abstract class VariablesFactory {

    public abstract List<IJavaVariable> getVariables(JDIStackFrame wrappedStackFrame, ThreadReference threadReference);
    
    public VirtualMachine getVirtualMachine(JDIStackFrame wrappedStackFrame) {
        try {
            JDIObjectValue classLoaderObject = (JDIObjectValue) wrappedStackFrame.getReferenceType().getClassLoaderObject();
            if (classLoaderObject != null) {
                ClassLoaderReference classLoaderReference = (ClassLoaderReference) classLoaderObject.getUnderlyingObject();
                return classLoaderReference.virtualMachine();
            }
        } catch (DebugException e) {
            IncQueryLoggingUtil.getLogger(VariablesFactory.class).error("Couldn't retrieve the virtual machine instance!", e);
            return null;
        }
        return null;
    }

}
