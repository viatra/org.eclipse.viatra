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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ConflictSet;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver.FixedPriorityConflictSet;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class FixedPriorityConflictResolver extends ReconfigurableConflictResolver<FixedPriorityConflictSet> {

    private Map<RuleSpecification<?>, Integer> priorities;
    
    /**
     * 
     */
    public FixedPriorityConflictResolver() {
        priorities = Maps.newHashMap();
    }

    public void setPriority(RuleSpecification<?> specification, int priority) {
        checkArgument(specification != null, "Specification cannot be null!");
        Integer oldPriority = priorities.get(specification);
        if(oldPriority != null && oldPriority == priority) {
            return; // no change required
        }
        priorities.put(specification, priority);
        Set<WeakReference<FixedPriorityConflictSet>> sets = getConflictSets();
        for (WeakReference<FixedPriorityConflictSet> weakReference : sets) {
            FixedPriorityConflictSet conflictSet = weakReference.get();
            if(conflictSet != null) {
                conflictSet.setPriority(specification, priority);
            }
        }
    }
    
    @Override
    protected FixedPriorityConflictSet createReconfigurableConflictSet() {
        return new FixedPriorityConflictSet(this, priorities);
    }

    private static Integer getRulePriority(Activation<?> activation, Map<RuleSpecification<?>, Integer> priorityMap) {
        RuleSpecification<?> specification = activation.getRule().getSpecification();
        return getRulePriority(specification, priorityMap);
    }

    private static Integer getRulePriority(RuleSpecification<?> specification, Map<RuleSpecification<?>, Integer> priorityMap) {
        Integer rulePriority = 0;
        if(priorityMap.containsKey(specification)) {
            rulePriority = priorityMap.get(specification);
        }
        return rulePriority;
    }

    public static final class FixedPriorityConflictSet implements ConflictSet {

        private final Multimap<Integer, Activation<?>> priorityBuckets;
        private Map<RuleSpecification<?>, Integer> cachedPriorities;
        private FixedPriorityConflictResolver resolver;
        
        /**
         * 
         */
        protected FixedPriorityConflictSet(FixedPriorityConflictResolver resolver, Map<RuleSpecification<?>, Integer> priorities) {
            this.resolver = resolver;
            checkArgument(priorities != null, "Priority map cannot be null!");
            cachedPriorities = Maps.newHashMap(priorities);
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
            Integer rulePriority = getRulePriority(activation, cachedPriorities);
            return priorityBuckets.put(rulePriority, activation);
        }

        @Override
        public boolean removeActivation(Activation<?> activation) {
            checkArgument(activation != null, "Activation cannot be null!");
            Integer rulePriority = getRulePriority(activation, cachedPriorities);
            return priorityBuckets.remove(rulePriority, activation);
        }

        protected void setPriority(RuleSpecification<?> specification, int priority) {
            checkArgument(specification != null, "Specification cannot be null");
            Integer rulePriority = getRulePriority(specification, cachedPriorities);
            cachedPriorities.put(specification, priority);
            Set<Activation<?>> removed = new HashSet<Activation<?>>();
            for (Activation<?> act : priorityBuckets.get(rulePriority)) {
                if(specification.equals(act.getRule().getSpecification())) {
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
        
    }
    
}
