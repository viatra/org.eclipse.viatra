package org.eclipse.viatra2.emf.runtime.transformation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.GenericPatternGroup;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.runtime.rules.BatchTransformationRule;
import org.eclipse.viatra2.emf.runtime.rules.TransformationRuleGroup;
import org.eclipse.viatra2.emf.runtime.rules.TransformationStatements;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * A base class for batch transformations.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public abstract class BatchTransformation {

	protected final RuleEngine ruleEngine;
	protected final AdvancedIncQueryEngine iqEngine;
	protected final boolean selfManagedEngines;
	protected TransformationStatements statements;
	protected final Context context;
	protected Set<BatchTransformationRule<?, ?>> rules = new HashSet<BatchTransformationRule<?,?>>();

	public BatchTransformation(Resource resource) throws IncQueryException {
		this(AdvancedIncQueryEngine.createUnmanagedEngine(resource));
	}

	public BatchTransformation(ResourceSet set) throws IncQueryException {
		this(AdvancedIncQueryEngine.createUnmanagedEngine(set));
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
		statements = new TransformationStatements(ruleEngine, context);
	}
	
	public void addRule(@SuppressWarnings("rawtypes") BatchTransformationRule rule) {
		rules.add(rule);
	}
	
	public void addRules(TransformationRuleGroup rules) {
		rules.addAll(rules);
	}
	
	public void initializeIndexes() throws IncQueryException {
		GenericPatternGroup.of(Iterables.toArray(Iterables.transform(rules, new Function<BatchTransformationRule<?, ?>, IQuerySpecification<?>>() {
			
			@Override
			public IQuerySpecification<?> apply(BatchTransformationRule<?,?> rule){
				return rule.getPrecondition();
			}
		}), IQuerySpecification.class)).prepare(iqEngine);
	}

	public void dispose() {
		if (selfManagedEngines) {
			ruleEngine.dispose();
			iqEngine.dispose();
		}
	}

}
