/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.tuple;

import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
public class TupleValueProvider implements IValueProvider {

    final Tuple tuple;
    final Map<String, Integer> indexMapping;
    
    /**
     * Wraps a tuple with an index mapping
     * @param tuple
     * @param indexMapping
     */
    public TupleValueProvider(Tuple tuple, Map<String, Integer> indexMapping) {
        super();
        this.tuple = tuple;
        this.indexMapping = indexMapping;
    }

    @Override
    public Object getValue(String variableName) {
        Integer index = indexMapping.get(variableName);
        if (index == null) {
            throw new IllegalArgumentException(String.format("Variable %s is not present in mapping.", variableName));
        }
        Object value = tuple.get(index);
        if (value == null) {
            throw new IllegalArgumentException(String.format("Variable %s is not found using index %d.", variableName, index));
        }
        return value;
    }

}
