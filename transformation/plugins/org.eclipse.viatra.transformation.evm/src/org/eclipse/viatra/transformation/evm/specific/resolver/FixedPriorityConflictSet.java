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
package org.eclipse.viatra.transformation.evm.specific.resolver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * This conflict set resolves conflicts between activations based on
 * Integer valued priorities assigned to rules.
 * The activations of rules with the lowest priority value will be the next activations.
 * 
 * See {@link FixedPriorityConflictResolver} for more details.
 * 
 * @author Abel Hegedus
 *
 */
public class FixedPriorityConflictSet implements ChangeableConflictSet {

    protected final Multimap<Integer, Activation<?>> priorityBuckets;
    private Map<RuleSpecification<?>, Integer> priorityMap;
    private FixedPriorityConflictResolver resolver;
    
    public FixedPriorityConflictSet(FixedPriorityConflictResolver resolver, Map<RuleSpecification<?>, Integer> priorities) {
        this.resolver = resolver;
        Preconditions.checkArgument(priorities != null, "Priority map cannot be null!");
        priorityMap = new HashMap<>(priorities);
        Map<Integer, Collection<Activation<?>>> treeMap = new TreeMap<Integer, Collection<Activation<?>>>();
        Multimap<Integer, Activation<?>> multimap = Multimaps.newSetMultimap(treeMap, HashSet::new);
        this.priorityBuckets = multimap;
    }
    
    /**
     * Returns one of the activations of one of the rules with the lowest priority.
     */
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
        Preconditions.checkArgument(activation != null, "Activation cannot be null!");
        Integer rulePriority = getRulePriority(activation);
        return priorityBuckets.put(rulePriority, activation);
    }

    @Override
    public boolean removeActivation(Activation<?> activation) {
        Preconditions.checkArgument(activation != null, "Activation cannot be null!");
        Integer rulePriority = getRulePriority(activation);
        return priorityBuckets.remove(rulePriority, activation);
    }

    protected void setPriority(RuleSpecification<?> specification, int priority) {
        Preconditions.checkArgument(specification != null, "Specification cannot be null");
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

    /**
     * Returns the set of activations of rules with the lowest priority.
     */
    @Override
    public Set<Activation<?>> getNextActivations() {
        return Collections.unmodifiableSet(new HashSet<>(getFirstBucket()));
    }

    /**
     * Returns all conflicting activations.
     */
    @Override
    public Set<Activation<?>> getConflictingActivations() {
        return Collections.unmodifiableSet(new HashSet<>(priorityBuckets.values()));
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