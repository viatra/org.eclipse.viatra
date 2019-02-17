/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

/**
 * Helper collection for grouping transformation rules 
 */
public class TransformationRuleGroup<Rule extends ITransformationRule<?, ?>> extends HashSet<Rule> {
    
    private static final long serialVersionUID = 7057984500208333710L;
    
    public TransformationRuleGroup() {
        super();
    }
    
    @SafeVarargs
    public TransformationRuleGroup(Rule... rules) {
        super(rules.length);
        for (Rule rule : rules) {
            add(rule);
        }
    }
    
    public Set<RuleSpecification<?>> getRuleSpecifications() {
        return this.stream().filter(Objects::nonNull).map(Rule::getRuleSpecification).collect(Collectors.toSet());
    }
    
    /**
     * @since 2.0
     */
    public Map<RuleSpecification<?>, Set<EventFilter<?>>> getFilteredRuleMap() {
        Map<RuleSpecification<?>, Set<EventFilter<?>>> map = new HashMap<>();
        for (Rule element : this) {
            RuleSpecification<?> spec = element.getRuleSpecification();
            EventFilter<?> filter = element.getFilter() != null ? element.getFilter() : spec.createEmptyFilter();
            map.computeIfAbsent(spec, sp -> new HashSet<>()).add(filter);
        }
        return map;
    }
    
    /**
     * Ensures all rules are initialized on a given VIATRA Query engine
     * @since 2.2
     */
    public void prepareQueryEngine(ViatraQueryEngine engine) {
        GenericQueryGroup.of(stream().filter(Objects::nonNull)
                .map(ITransformationRule::getPrecondition)).prepare(engine);
    }
}
