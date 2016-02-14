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
package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * A helper class for storing a mapping between a set of fully qualified names and {@link IQuerySpecification} instances. The
 * IncQuery builder maintains one of them during building, while for the generic API the user might want to manage it.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class NameToSpecificationMap implements Map<String, IQuerySpecification<?>> {

    Map<String, IQuerySpecification<?>> map;
    
    public NameToSpecificationMap() {
        map = new HashMap<String, IQuerySpecification<?>>();
    }
    
    public NameToSpecificationMap(IQuerySpecification<?>... specifications) {
        this();
        for (IQuerySpecification<?> specification : specifications) {
            map.put(specification.getFullyQualifiedName(), specification);
        }
    }
    
    public NameToSpecificationMap(Collection<? extends IQuerySpecification<?>> specifications) {
        this();
        for (IQuerySpecification<?> specification : specifications) {
            map.put(specification.getFullyQualifiedName(), specification);
        }
    }
    
    public NameToSpecificationMap(Map<String, IQuerySpecification<?>> source) {
        map = new HashMap<String, IQuerySpecification<?>>(source);
    }

    /**
     * Initializes a pattern-specification mapping with the contents of an existing {@link ViatraQueryEngine}. </p>
     * <p>
     * <strong>Warning</strong> It is assumed that each query specification in the engine has a unique fqn - if the
     * assumption fails, the resulting map is unspecified.
     * 
     * @param engine
     */
    public NameToSpecificationMap(ViatraQueryEngine engine) {
        this();
        for (ViatraQueryMatcher<?> matcher : engine.getCurrentMatchers()) {
            IQuerySpecification<?> specification = matcher.getSpecification();
            map.put(specification.getFullyQualifiedName(), specification);
        }
    }
    
    @Override
    public void clear() {
        map.clear();
        
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, IQuerySpecification<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public IQuerySpecification<?> get(Object key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public IQuerySpecification<?> put(String key, IQuerySpecification<?> value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends IQuerySpecification<?>> values) {
        map.putAll(values);
    }

    @Override
    public IQuerySpecification<?> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<IQuerySpecification<?>> values() {
        return map.values();
    }
    
    /**
     * Returns a specification with the selected status
     * @param status
     * @return a specification with the selected status, or null if no such specification is available
     */
    public IQuerySpecification<?> getSpecificationWithStatus(final PQueryStatus status) {
        return Iterables.getFirst(getSpecificationsWithStatus(status), null);
    }
    
    /**
     * Returns a collection of specifications with the selected status
     * @param status
     * @return a non-null (but possibly empty) specification list
     */
    public Collection<IQuerySpecification<?>> getSpecificationsWithStatus(final PQueryStatus status) {
        return Collections2.filter(map.values(), new Predicate<IQuerySpecification<?>>() {

            @Override
            public boolean apply(IQuerySpecification<?> specification) {
                return specification.getInternalQueryRepresentation().getStatus().equals(status);
            }
        });
    }
}
