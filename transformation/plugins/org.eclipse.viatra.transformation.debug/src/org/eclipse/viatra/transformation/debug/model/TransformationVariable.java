/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class TransformationVariable extends TransformationDebugElement implements IVariable{
    public static final String RULE_VARIABLE_NAME = "Rule Specification";
    public static final String STATE_VARIABLE_NAME = "Activation State";
    public static final String ATOM_VARIABLE_NAME = "Event Atom";
    private String name;
    private TransformationValue value;
    
    public TransformationVariable(TransformationDebugTarget target, String name, TransformationValue value) {
        super(target);
        this.name = name;
        this.value = value;
    }
    
    @Override
    public void setValue(String expression) throws DebugException {
    }

    @Override
    public void setValue(IValue value) throws DebugException {
    }

    @Override
    public boolean supportsValueModification() {
        return false;
    }

    @Override
    public boolean verifyValue(String expression) throws DebugException {
        return false;
    }

    @Override
    public boolean verifyValue(IValue value) throws DebugException {
        return false;
    }

    @Override
    public IValue getValue() throws DebugException {
        return value;
    }

    @Override
    public String getName() throws DebugException {
        return name;
    }

    @Override
    public String getReferenceTypeName() throws DebugException {
        return value.getReferenceTypeName();
    }

    @Override
    public boolean hasValueChanged() throws DebugException {
        return false;
    }
    
}
