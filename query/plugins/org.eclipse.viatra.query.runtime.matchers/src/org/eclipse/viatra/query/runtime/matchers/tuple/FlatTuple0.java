/*******************************************************************************
 * Copyright (c) 2010-2017, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.tuple;

/**
 * Flat tuple with statically known arity of 0.
 * 
 * @author Gabor Bergmann
 * @since 1.7
 *
 */
public final class FlatTuple0 extends BaseFlatTuple {
    protected static final FlatTuple0 INSTANCE = new FlatTuple0();

    private FlatTuple0() {
        calcHash();
    }
    
    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Object get(int index) {
        throw raiseIndexingError(index);
    }

    private static final Object[] NULLARY_ARRAY = new Object[0];
    
    @Override
    public Object[] getElements() {
        return NULLARY_ARRAY;
    }
    
    @Override
    protected boolean internalEquals(Tuple other) {
        return 0 == other.getSize();
    }
}
