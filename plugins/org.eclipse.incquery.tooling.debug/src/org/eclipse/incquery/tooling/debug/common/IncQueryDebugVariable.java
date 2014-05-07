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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;

import com.sun.jdi.Type;
import com.sun.jdi.Value;

/**
 * An IncQuery Debug variable which has a specific JDI value attached.  
 * Variables will be displayed in the Eclipse Debug View.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public class IncQueryDebugVariable extends JDIVariable {

    private IValue modelValue;
    
    public IncQueryDebugVariable(JDIDebugTarget target) {
        super(target);
    }

    @Override
    public String getSignature() throws DebugException {
        try {
            return ((JDIValue) getValue()).getSignature();
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public String getGenericSignature() throws DebugException {
        try {
            return ((JDIValue) getValue()).getGenericSignature();
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public String getName() throws DebugException {
        if (getValue() instanceof IncQueryDebugValue) {
            return ((IncQueryDebugValue) getValue()).getLabel();
        }
        return null;
    }

    @Override
    public String getReferenceTypeName() throws DebugException {
        try {
            return ((JDIValue) getValue()).getReferenceTypeName();
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    protected Value retrieveValue() throws DebugException {
        return null;
    }

    @Override
    protected Type getUnderlyingType() throws DebugException {
        return null;
    }
    
    @Override
    public void setValue(IValue value) throws DebugException {
        if (value instanceof JDIValue) {
            this.modelValue = value;
        }
    }
    
    @Override
    public IValue getValue() throws DebugException {
        return this.modelValue;
    }
}
