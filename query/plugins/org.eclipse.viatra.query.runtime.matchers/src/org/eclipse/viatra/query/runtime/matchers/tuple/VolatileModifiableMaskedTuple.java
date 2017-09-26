/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.tuple;

import com.google.common.base.Preconditions;

/**
 * A masked tuple implementation that allows modifying the backing tuple.
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public class VolatileModifiableMaskedTuple extends VolatileMaskedTuple implements IModifiableTuple {

    private IModifiableTuple modifiableTuple;

    public VolatileModifiableMaskedTuple(IModifiableTuple source, TupleMask mask) {
        super(source, mask);
        modifiableTuple = source;
    }

    public VolatileModifiableMaskedTuple(TupleMask mask) {
        this(null, mask);
    }
    
    @Override
    public void updateTuple(ITuple newSource) {
        Preconditions.checkArgument(newSource instanceof IModifiableTuple, "Provided tuple does not support updates");
        this.updateTuple((IModifiableTuple)newSource);
    }
    
    public void updateTuple(IModifiableTuple newSource) {
        super.updateTuple(newSource);
        modifiableTuple = newSource;
    }

    @Override
    public void set(int index, Object value) {
        mask.set(modifiableTuple, index, value);
    }
}
