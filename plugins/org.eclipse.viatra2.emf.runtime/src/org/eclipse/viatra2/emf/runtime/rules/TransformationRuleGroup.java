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
package org.eclipse.viatra2.emf.runtime.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Helper collection for grouping transformation rules 
 */
@SuppressWarnings("rawtypes")
public class TransformationRuleGroup extends HashSet<Pair<BatchTransformationRule, EventFilter>> {

	private final class RuleTransformerFunction<Match extends IPatternMatch> implements
			Function<Pair<BatchTransformationRule<Match, ?>, EventFilter>, RuleSpecification<Match>> {
		@Override
		public RuleSpecification<Match> apply(Pair<BatchTransformationRule<Match, ?>, EventFilter> rule) {
			return rule.getKey().getRuleSpecification();
		}
	}
	
	private static final long serialVersionUID = 1L;

	public TransformationRuleGroup() {
		super();
	}
	
	public TransformationRuleGroup(BatchTransformationRule... rules) {
		super(rules.length);
		for (BatchTransformationRule rule : rules) {
			add(new Pair<BatchTransformationRule, EventFilter>(rule, null));
		}
	}
	
	public TransformationRuleGroup(Pair<BatchTransformationRule, EventFilter>... rules) {
		super(rules.length);
		for (Pair<BatchTransformationRule, EventFilter> rule : rules) {
			add(rule);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<RuleSpecification<?>> getRuleSpecifications() {
		return Sets.newHashSet(Collections2.transform(this, new RuleTransformerFunction()));
	}
	
	public Multimap<RuleSpecification<?>, EventFilter<?>> getFilteredRuleMap() {
		Multimap<RuleSpecification<?>, EventFilter<?>> map = HashMultimap.<RuleSpecification<?>, EventFilter<?>>create();
		for (Pair<BatchTransformationRule, EventFilter> element : this) {
			RuleSpecification spec = element.getKey().getRuleSpecification();
			EventFilter filter = element.getValue() != null ? element.getValue() : spec.createEmptyFilter();
			map.put(spec, filter);
		}
		return map;
	}
}
