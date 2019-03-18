/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;

/**
 * This conflict resolver uses Integer value priorities assigned to rules.
 * You can set priorities with the {@link #setPriority(RuleSpecification, int)} method.
 * The activations of rules with the lowest priority value will be the next activations.   
 * 
 * @author Abel Hegedus
 *
 */
public class FixedPriorityConflictResolver extends ReconfigurableConflictResolver<FixedPriorityConflictSet> {

    protected Map<RuleSpecification<?>, Integer> priorities;
    /**
     * @since 2.1
     */
    protected final int defaultPriority;
    
    /**
     * Initializes the conflict resolver with a default priority of 0.
     */
    public FixedPriorityConflictResolver() {
        this(0);
    }

    /**
     * Initializes the conflict resolver with a given default priority value
     * @since 2.1
     */
    public FixedPriorityConflictResolver(int defaultPriority) {
        priorities = new HashMap<>();
        this.defaultPriority = defaultPriority;
        
    }
    
    /**
     * Sets the priority for the given specification.
     * The activations of rules with the lowest priority value will be the next activations
     * while rules with higher priority values will only be included in the conflicting activations set.
     * The default priority is set when the resolver is created; if unspecified, it is 0.
     * 
     * @param specification
     * @param priority
     */
    public void setPriority(RuleSpecification<?> specification, int priority) {
        Preconditions.checkArgument(specification != null, "Specification cannot be null!");
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
        return new FixedPriorityConflictSet(this, priorities, defaultPriority);
    }

    
}
