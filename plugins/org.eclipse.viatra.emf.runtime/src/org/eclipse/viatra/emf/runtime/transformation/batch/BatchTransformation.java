/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi, Istvan David and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.transformation.batch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.GenericPatternGroup;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.emf.runtime.rules.TransformationRuleGroup;
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRule;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * A base class for batch transformations.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class BatchTransformation {

	protected final RuleEngine ruleEngine;
	protected final AdvancedIncQueryEngine iqEngine;
	protected final boolean selfManagedEngines;
	protected final Context context;
	protected Set<BatchTransformationRule<?, ?>> rules = new HashSet<BatchTransformationRule<?,?>>();

	public BatchTransformation(Resource resource) throws IncQueryException {
		this(AdvancedIncQueryEngine.createUnmanagedEngine(new EMFScope(resource)));
	}

	public BatchTransformation(ResourceSet set) throws IncQueryException {
		this(AdvancedIncQueryEngine.createUnmanagedEngine(new EMFScope(set)));
	}

	public BatchTransformation(RuleEngine ruleEngine,
			AdvancedIncQueryEngine iqEngine) {
		this(ruleEngine, iqEngine, false);
	}
	
	private BatchTransformation(AdvancedIncQueryEngine iqEngine) {
		this(RuleEngines.createIncQueryRuleEngine(iqEngine), iqEngine, true);
	}
	
	private BatchTransformation(RuleEngine ruleEngine,
			AdvancedIncQueryEngine iqEngine, boolean selfManagedEngine) {
		this.ruleEngine = ruleEngine;
		this.iqEngine = iqEngine;
		this.selfManagedEngines = selfManagedEngine;
		
		context = Context.create();
	}
	
	public void addRule(@SuppressWarnings("rawtypes") BatchTransformationRule rule) {
		rules.add(rule);
	}
	
	public void addRules(TransformationRuleGroup ruleGroup) {
		for (Pair<?, ?> pair : ruleGroup) {
			Object key = pair.getKey();
			if (!(key instanceof BatchTransformationRule)) {
				continue;
			}
			rules.add((BatchTransformationRule<?, ?>) key);
		}
	}
	
	public void initializeIndexes() throws IncQueryException {
		GenericPatternGroup.of(Iterables.toArray(Iterables.transform(rules, new Function<BatchTransformationRule<?, ?>, IQuerySpecification<?>>() {
			
			@Override
			public IQuerySpecification<?> apply(BatchTransformationRule<?,?> rule){
				return rule.getPrecondition();
			}
		}), IQuerySpecification.class)).prepare(iqEngine);
	}
	
	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}
	
	public IncQueryEngine getIqEngine() {
		return iqEngine;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void dispose() {
		if (selfManagedEngines) {
			ruleEngine.dispose();
			iqEngine.dispose();
		}
	}

}
