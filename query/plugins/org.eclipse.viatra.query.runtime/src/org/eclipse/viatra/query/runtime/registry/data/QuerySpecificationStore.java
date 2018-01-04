/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.registry.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Internal data storage object that represents a query specification registry with a set of sources driven by
 * connectors. The sources must have unique identifiers.
 * 
 * @author Abel Hegedus
 *
 */
public class QuerySpecificationStore {

    private Map<String, RegistrySourceImpl> sources;

    /**
     * Creates a new instance with an empty identifier to source map.
     */
    public QuerySpecificationStore() {
        this.sources = new TreeMap<>();
    }

    /**
     * @return the live, modifiable identifier to source map
     */
    public Map<String, RegistrySourceImpl> getSources() {
        return sources;
    }
}
