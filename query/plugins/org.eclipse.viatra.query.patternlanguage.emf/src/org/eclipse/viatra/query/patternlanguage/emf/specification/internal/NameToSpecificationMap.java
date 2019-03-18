/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

/**
 * A helper class for storing a mapping between a set of fully qualified names and {@link IQuerySpecification} instances. The
 * VIATRA Query builder maintains one of them during building, while for the generic API the user might want to manage it.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class NameToSpecificationMap implements Map<String, IQuerySpecification<?>> {

    Map<String, IQuerySpecification<?>> map;
    
    public NameToSpecificationMap() {
        map = new HashMap<>();
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
        map = new HashMap<>(source);
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
     * @return an optional specification with the selected status
     */
    public Optional<IQuerySpecification<?>> getSpecificationWithStatus(final PQueryStatus status) {
        return getSpecificationStreamWithStatus(status).findAny();
    }
    
    /**
     * Returns a collection of specifications with the selected status
     * @param status
     * @return a non-null (but possibly empty) specification list
     */
    public Collection<IQuerySpecification<?>> getSpecificationsWithStatus(final PQueryStatus status) {
        return getSpecificationStreamWithStatus(status).collect(Collectors.toList());
    }
    
    private Stream<IQuerySpecification<?>> getSpecificationStreamWithStatus(final PQueryStatus status) {
        return map.values().stream().filter(specification -> specification.getInternalQueryRepresentation().getStatus().equals(status));
    }
}
