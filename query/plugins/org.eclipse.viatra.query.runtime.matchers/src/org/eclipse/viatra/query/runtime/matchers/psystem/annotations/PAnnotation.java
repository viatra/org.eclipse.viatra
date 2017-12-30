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
package org.eclipse.viatra.query.runtime.matchers.psystem.annotations;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;

/**
 * A container describing query annotations
 * @author Zoltan Ujhelyi
 *
 */
public class PAnnotation {

    private final String name;
    private MutableMultimap<String, Object> attributes = FastListMultimap.newMultimap();

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
     * @since 2.0
     */
    public Optional<Object> getFirstValue(String attributeName) {
        return getAllValues(attributeName).stream().findFirst();
    }
    
    /**
     * Returns the value of the first occurrence of an attribute
     * @param attributeName
     * @return the attribute value, or null, if attribute is not available
     * @since 2.0
     */
    public <T> Optional<T> getFirstValue(String attributeName, Class<T> clazz) {
        return getAllValues(attributeName).stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
    }

    /**
     * Returns all values of a selected attribute
     * @param attributeName
     * @return a non-null, but possibly empty list of attributes
     */
    public List<Object> getAllValues(String attributeName) {
        return attributes.get(attributeName).toList();
    }
    
    /**
     * Executes a consumer over all attributes. A selected attribute name (key) can appear (and thus consumed) multiple times.
     * @since 2.0
     */
    public void forEachValue(BiConsumer<String, Object> consumer) {
        attributes.forEachKeyValue(consumer::accept);
    }
}
