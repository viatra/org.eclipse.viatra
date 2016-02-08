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

package org.eclipse.viatra.query.runtime.matchers.psystem;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class KeyedEnumerablePConstraint<KeyType> extends EnumerablePConstraint {

    protected KeyType supplierKey;

    public KeyedEnumerablePConstraint(PBody pBody, Tuple variablesTuple,
            KeyType supplierKey) {
        super(pBody, variablesTuple);
        this.supplierKey = supplierKey;
    }

    @Override
    protected String toStringRestRest() {
        return supplierKey == null ? "$any(null)" : keyToString();
    }

    protected abstract String keyToString();

    public KeyType getSupplierKey() {
        return supplierKey;
    }

}
