/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven;

import java.util.List;

import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.resolver.FixedPriorityConflictResolver;

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
