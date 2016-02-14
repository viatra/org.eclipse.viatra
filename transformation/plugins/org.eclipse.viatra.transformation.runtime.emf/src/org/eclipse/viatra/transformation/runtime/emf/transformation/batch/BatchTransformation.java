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
package org.eclipse.viatra.transformation.runtime.emf.transformation.batch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.specific.RuleEngines;
import org.eclipse.viatra.transformation.runtime.emf.rules.BatchTransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.TransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

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
	protected final AdvancedViatraQueryEngine queryEngine;
	protected final boolean selfManagedEngines;
	protected final Context context;
	protected Set<BatchTransformationRule<?, ?>> rules = new HashSet<BatchTransformationRule<?,?>>();

	public static BatchTransformation forScope(EMFScope scope) throws IncQueryException {
		AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
		return new BatchTransformation(engine);
	}
	
	public static BatchTransformation forEngine(ViatraQueryEngine engine) {
		return forRuleEngine(RuleEngines.createIncQueryRuleEngine(engine), engine);
	}
	
	public static BatchTransformation forRuleEngine(RuleEngine ruleEngine, ViatraQueryEngine engine) {
		return new BatchTransformation(ruleEngine, AdvancedViatraQueryEngine.from(engine), false);
	}
	
	@Deprecated
	public BatchTransformation(Resource resource) throws IncQueryException {
		this(AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resource)));
	}

	@Deprecated
	public BatchTransformation(ResourceSet set) throws IncQueryException {
		this(AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(set)));
	}

	@Deprecated
	public BatchTransformation(RuleEngine ruleEngine, AdvancedViatraQueryEngine queryEngine) {
		this(ruleEngine, queryEngine, false);
	}
	
	private BatchTransformation(AdvancedViatraQueryEngine queryEngine) {
		this(RuleEngines.createIncQueryRuleEngine(queryEngine), queryEngine, true);
	}
	
	private BatchTransformation(RuleEngine ruleEngine,
			AdvancedViatraQueryEngine queryEngine, boolean selfManagedEngine) {
		this.ruleEngine = ruleEngine;
		this.queryEngine = queryEngine;
		this.selfManagedEngines = selfManagedEngine;
		
		context = Context.create();
	}
	
	public void addRule(@SuppressWarnings("rawtypes") BatchTransformationRule rule) {
		rules.add(rule);
	}
	
	@SuppressWarnings("rawtypes")
	public void addRules(TransformationRuleGroup<BatchTransformationRule> ruleGroup) {
		for (BatchTransformationRule rule : ruleGroup) {
			rules.add(rule);
		}
	}
	
	public void addRules(BatchTransformationRuleGroup ruleGroup) {
		for (BatchTransformationRule<?, ?> rule : ruleGroup) {
			rules.add(rule);
		}
	}
	
	
	public void initializeIndexes() throws IncQueryException {
		GenericQueryGroup.of(Iterables.toArray(Iterables.transform(rules, new Function<BatchTransformationRule<?, ?>, IQuerySpecification<?>>() {
			
			@Override
			public IQuerySpecification<?> apply(BatchTransformationRule<?,?> rule){
				return rule.getPrecondition();
			}
		}), IQuerySpecification.class)).prepare(queryEngine);
	}
	
	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}
	
	public ViatraQueryEngine getQueryEngine() {
		return queryEngine;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void dispose() {
		if (selfManagedEngines) {
			ruleEngine.dispose();
			queryEngine.dispose();
		}
	}

}
