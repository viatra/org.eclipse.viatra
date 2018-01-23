/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.Set;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * @author Gabor Bergmann
 * @since 2.0
 */
public class EclipseCollectionsSetMemory<Value> extends UnifiedSet<Value> implements ISetMemory<Value> {
    @Override
    public int getCount(Value value) {
        return super.contains(value) ? 1 : 0;
    }

    @Override
    public boolean containsNonZero(Value value) {
        return super.contains(value);
    }

    @Override
    public boolean addOne(Value value) {
        return super.add(value);
    }

    @Override
    public boolean addSigned(Value value, int count) {
        if (count == 1) return addOne(value);
        else if (count == -1) return removeOne(value); 
        else throw new IllegalStateException();
    }

    @Override
    public boolean removeOne(Value value) {
        if (!super.remove(value))
            throw new IllegalStateException();
        return true;
    }

    @Override
    public void clearAllOf(Value value) {
        super.remove(value);
    }

    @Override
    public Set<Value> distinctValues() {
        return this;
    }
}
