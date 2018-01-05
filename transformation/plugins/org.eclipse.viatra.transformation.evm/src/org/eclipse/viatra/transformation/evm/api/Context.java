/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A context is an associative store that is accessible during the
 * execution of the EVM, and thus usable by the Sceduler, Executor, RuleBase,
 *  Rule Instances, Activations and Jobs.
 * 
 * In order to separate different users from each other, values can
 * be accessed (get/put/remove) with a key.
 * 
 * @author Abel Hegedus
 *
 */
public class Context {

    private Map<String, Object> sessionData;
    
    protected Context() {
        this.sessionData = new HashMap<String, Object>();
    }
    
    /**
     * Static helper method to create an empty Context.
     */
    public static Context create() {
        return new Context();
    }
    
    /**
     * Returns the value corresponding to the given key.
     * 
     * @param key
     * @return the value in the context for the key
     */
    public Object get(final String key) {
        Objects.requireNonNull(key, "Cannot get value for null!");
        return sessionData.get(key);
    }
    
    /**
     * Puts the value into the context with the given key
     * 
     * @param key
     * @param value
     * @return the previous value for the key, or null
     */
    public Object put(final String key, final Object value) {
        Objects.requireNonNull(key, "Cannot put into null key!");
        Objects.requireNonNull(value, "Cannot put null value (use remove for deletion)!");
        return sessionData.put(key, value);
    }
    
    /**
     * Removes any existing value for the given key from the context.
     * 
     * @param key
     * @return the value if exists, null otherwise
     */
    public Object remove(final String key) {
        return sessionData.remove(key);
    }
    
    /**
     * Clears all data from the context.
     */
    protected void clear() {
        sessionData.clear();
    }
}
