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
package org.eclipse.incquery.runtime.evm.specific.resolver;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class FixedPriorityConflictSet implements ChangeableConflictSet {

    protected final Multimap<Integer, Activation<?>> priorityBuckets;
    private Map<RuleSpecification<?>, Integer> priorityMap;
    private FixedPriorityConflictResolver resolver;
    
    public FixedPriorityConflictSet(FixedPriorityConflictResolver resolver, Map<RuleSpecification<?>, Integer> priorities) {
        this.resolver = resolver;
        checkArgument(priorities != null, "Priority map cannot be null!");
        priorityMap = Maps.newHashMap(priorities);
        Map<Integer, Collection<Activation<?>>> treeMap = new TreeMap<Integer, Collection<Activation<?>>>();
        Multimap<Integer, Activation<?>> multimap = Multimaps.newSetMultimap(treeMap, new Supplier<Set<Activation<?>>>() {
            @Override
            public Set<Activation<?>> get() {
                return new HashSet<Activation<?>>();
            }
        });
        this.priorityBuckets = multimap;
    }
    
    @Override
    public Activation<?> getNextActivation() {
        Collection<Activation<?>> firstBucket = getFirstBucket();
        if(!firstBucket.isEmpty()) {
            return firstBucket.iterator().next();
        }
        return null;
    }

    private Collection<Activation<?>> getFirstBucket() {
        if(priorityBuckets.isEmpty()) {
            return Collections.emptySet();
        } else {
            Integer firstKey = priorityBuckets.keySet().iterator().next();
            Collection<Activation<?>> firstBucket = priorityBuckets.get(firstKey);
            return firstBucket;
        }
    }

    @Override
    public boolean addActivation(Activation<?> activation) {
        checkArgument(activation != null, "Activation cannot be null!");
        Integer rulePriority = getRulePriority(activation);
        return priorityBuckets.put(rulePriority, activation);
    }

    @Override
    public boolean removeActivation(Activation<?> activation) {
        checkArgument(activation != null, "Activation cannot be null!");
        Integer rulePriority = getRulePriority(activation);
        return priorityBuckets.remove(rulePriority, activation);
    }

    protected void setPriority(RuleSpecification<?> specification, int priority) {
        checkArgument(specification != null, "Specification cannot be null");
        Integer rulePriority = getRulePriority(specification);
        priorityMap.put(specification, priority);
        Set<Activation<?>> removed = new HashSet<Activation<?>>();
        for (Activation<?> act : priorityBuckets.get(rulePriority)) {
            if(specification.equals(act.getInstance().getSpecification())) {
                removed.add(act);
            }
        }
        priorityBuckets.get(rulePriority).removeAll(removed);
        priorityBuckets.putAll(priority, removed);
    }

    @Override
    public FixedPriorityConflictResolver getConflictResolver() {
        return resolver;
    }

    @Override
    public Set<Activation<?>> getNextActivations() {
        return Collections.unmodifiableSet(Sets.newHashSet(getFirstBucket()));
    }

    @Override
    public Set<Activation<?>> getConflictingActivations() {
        return Collections.unmodifiableSet(Sets.newHashSet(priorityBuckets.values()));
    }
    

    protected Integer getRulePriority(Activation<?> activation) {
        RuleSpecification<?> specification = activation.getInstance().getSpecification();
        return getRulePriority(specification);
    }

    protected Integer getRulePriority(RuleSpecification<?> specification) {
        Integer rulePriority = 0;
        if(priorityMap.containsKey(specification)) {
            rulePriority = priorityMap.get(specification);
        }
        return rulePriority;
    }
}