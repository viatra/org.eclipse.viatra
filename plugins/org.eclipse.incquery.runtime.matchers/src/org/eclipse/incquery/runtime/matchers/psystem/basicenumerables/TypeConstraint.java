/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * Represents a type constraint with using an undefined number of parameters. Subclasses are distinguished by the number
 * of parameters and their inferred type information. Such a constraint maintains how to output its type information.
 *
 * @author Zoltan Ujhelyi
 *
 */
public abstract class TypeConstraint extends KeyedEnumerablePConstraint<Object> {

    private final String typeString;

    public TypeConstraint(PBody pSystem, Tuple variablesTuple, Object supplierKey, String typeString) {
        super(pSystem, variablesTuple, supplierKey);
        this.typeString = typeString;
    }

    @Override
    protected String keyToString() {
        return typeString;
    }

    public String getTypeString() {
        return typeString;
    }

}