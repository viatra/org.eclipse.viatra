/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.transformation.eventdriven;

import java.util.List;

import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver;

public class RuleOrderBasedFixedPriorityResolver extends FixedPriorityConflictResolver {
    public RuleOrderBasedFixedPriorityResolver() {
        super();
    }

    public void setPrioritiesFromScratch(List<RuleSpecification<?>> ruleSpecGroup) {
        priorities.clear();
        setPriorities(ruleSpecGroup);
    }

    public void setPriorities(List<RuleSpecification<?>> ruleSpecGroup) {
        int priority = ruleSpecGroup.size() + priorities.size();

        for (RuleSpecification<?> rule : ruleSpecGroup) {
            this.setPriority(rule, priority);
            priority--;
        }
    }
}
