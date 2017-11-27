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

import java.util.List;

/**
 * @author Gabor Bergmann
 * @since 1.7
 */
public final class TupleMaskIdentity extends TupleMask {

    TupleMaskIdentity(int[] indices, int sourceWidth) {
        super(indices, sourceWidth);
    }

    @Override
    public <T> List<T> transform(List<T> original) {
        return original;
    }
    
    @Override
    public Tuple transform(ITuple original) {
        return original.toImmutable();
    }
    
    @Override
    public TupleMask transform(TupleMask mask) {
        return mask;
    }

    @Override
    public Tuple revertFrom(ITuple masked) {
        return masked.toImmutable();
    }
    
}
