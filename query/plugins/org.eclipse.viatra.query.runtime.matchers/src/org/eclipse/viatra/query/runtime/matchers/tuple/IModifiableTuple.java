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

/**
 * A tuple that allows modifying the underlying value. Should not be used for non-volatile tuples.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
public interface IModifiableTuple extends ITuple {

    /**
     * Sets the selected value for a tuple
     * 
     * @pre: 0 <= index < getSize()
     * 
     */
    void set(int index, Object value);

}
