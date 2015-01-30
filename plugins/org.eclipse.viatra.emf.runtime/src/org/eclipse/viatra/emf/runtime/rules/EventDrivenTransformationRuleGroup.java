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
package org.eclipse.viatra.emf.runtime.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformationRule;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@SuppressWarnings("rawtypes")
public class EventDrivenTransformationRuleGroup extends
		HashSet<Pair<EventDrivenTransformationRule, EventFilter>> {

    private static final long serialVersionUID = 4335746113995333746L;

    private final class RuleTransformerFunction<Match extends IPatternMatch>
			implements
			Function<Pair<EventDrivenTransformationRule, EventFilter>, RuleSpecification<?>> {
		@Override
		public RuleSpecification<?> apply(
				Pair<EventDrivenTransformationRule, EventFilter> rule) {
			return rule.getKey().getRuleSpecification();
		}
	}

	public EventDrivenTransformationRuleGroup() {
		super();
	}

	public EventDrivenTransformationRuleGroup(
			EventDrivenTransformationRule... rules) {
		super(rules.length);
		for (EventDrivenTransformationRule rule : rules) {
			add(new Pair<EventDrivenTransformationRule, EventFilter>(rule, null));
		}
	}

	public EventDrivenTransformationRuleGroup(
			Pair<EventDrivenTransformationRule, EventFilter>... rules) {
		super(rules.length);
		for (Pair<EventDrivenTransformationRule, EventFilter> rule : rules) {
			add(rule);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<RuleSpecification<?>> getRuleSpecifications() {
		return Sets.newHashSet(Collections2.transform(this,
				new RuleTransformerFunction()));
	}

	public Multimap<RuleSpecification<?>, EventFilter<?>> getFilteredRuleMap() {
		Multimap<RuleSpecification<?>, EventFilter<?>> map = HashMultimap
				.<RuleSpecification<?>, EventFilter<?>> create();
		for (Pair<EventDrivenTransformationRule, EventFilter> element : this) {
			RuleSpecification spec = element.getKey().getRuleSpecification();
			EventFilter filter = element.getValue() != null ? element
					.getValue() : spec.createEmptyFilter();
			map.put(spec, filter);
		}
		return map;
	}
}
