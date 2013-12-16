/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import org.eclipse.incquery.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class TypeUnary extends
        KeyedEnumerablePConstraint<Object> implements ITypeInfoProviderConstraint {
    /**
     * @param buildable
     * @param variable
     * @param typeKey
     */
    public TypeUnary(PSystem pSystem, PVariable variable, Object typeKey) {
        super(pSystem, new FlatTuple(variable), typeKey);
    }

    @Override
    public Object getTypeInfo(PVariable variable) {
        if (variable.equals(variablesTuple.get(0)))
            return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapUnary(supplierKey);
        return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
    }

    @Override
    protected String keyToString() {
        return pSystem.getContext().printType(supplierKey);
    }

}
