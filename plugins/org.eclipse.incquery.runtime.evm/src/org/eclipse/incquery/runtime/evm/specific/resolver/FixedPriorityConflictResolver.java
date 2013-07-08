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
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;

import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class FixedPriorityConflictResolver extends ReconfigurableConflictResolver<FixedPriorityConflictSet> {

    protected Map<RuleSpecification<?>, Integer> priorities;
    
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

    
}
