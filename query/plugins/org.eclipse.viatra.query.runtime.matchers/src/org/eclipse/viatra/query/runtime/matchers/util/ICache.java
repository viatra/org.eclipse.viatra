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
package org.eclipse.viatra.query.runtime.matchers.util;

/**
 * A cache is a simple key-value pair that stores calculated values for specific key objects
 * 
 * <p>
 * <b>NOTE</b> These caches are not expected to be used outside query backend implementations 
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface ICache {

    /**
     * Return a selected value for the key object. If the value is not available in the cache yet, the given provider is
     * called once
     */
    <T> T getValue(Object key, Class<? extends T> clazz, IProvider<T> valueProvider);

}