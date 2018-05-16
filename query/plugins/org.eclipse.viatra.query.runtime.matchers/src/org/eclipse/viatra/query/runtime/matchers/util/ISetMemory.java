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

import java.util.function.BiConsumer;

/**
 * An {@link IMemory} that always contains values with a 0 or +1 multiplicity.
 * 
 * <p> In case a write operation causes underflow or overflow, an {@link IllegalStateException} is thrown.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public interface ISetMemory<T> extends IMemory<T> {
    
    @Override
    default void forEachEntryWithMultiplicities(BiConsumer<T, Integer> entryConsumer) {
        for (T t : this.distinctValues()) entryConsumer.accept(t, 1);
    }

}
