/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * A container describing query annotations
 * @author Zoltan Ujhelyi
 *
 */
public class PAnnotation {

    private final String name;
    private ListMultimap<String, Object> attributes = Multimaps.newListMultimap(new HashMap<String, Collection<Object>>(), new Supplier<List<Object>>() {

        @Override
        public List<Object> get() {
            return new ArrayList<Object>();
        }
    });

    public PAnnotation(String name) {
        this.name = name;

    }

    /**
     * Adds an attribute to the annotation
     * @param attributeName
     * @param value
     */
    public void addAttribute(String attributeName, Object value) {
        attributes.put(attributeName, value);
    }

    /**
     * Return the name of the annotation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the first occurrence of an attribute
     * @param attributeName
     * @return the attribute value, or null, if attribute is not available
     */
    public Object getFirstValue(String attributeName) {
        return Iterables.getFirst(getAllValues(attributeName), null);
    }

    /**
     * Returns all values of a selected attribute
     * @param attributeName
     * @return a non-null, but possibly empty list of attributes
     */
    public List<Object> getAllValues(String attributeName) {
        return attributes.get(attributeName);
    }
    /**
     * Returns all values of all attributes. A selected attribute name (key) can appear multiple times in the collection.
     */
    public Collection<Entry<String,Object>> getAllValues() {
        return attributes.entries();
    }
}
