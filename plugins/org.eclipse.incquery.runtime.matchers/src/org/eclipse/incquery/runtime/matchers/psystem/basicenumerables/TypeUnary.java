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
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 *
 */
public class TypeUnary extends TypeConstraint implements ITypeInfoProviderConstraint {

    public TypeUnary(PBody pSystem, PVariable variable, Object typeKey, String typeString) {
        super(pSystem, new FlatTuple(variable), typeKey, typeString);
    }

    @Override
    public Object getTypeInfo(PVariable variable) {
        if (variable.equals(variablesTuple.get(0)))
            return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapUnary(supplierKey);
        return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
    }

}
