package org.eclipse.viatra2.emf.runtime.transformation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.runtime.rules.BatchTransformationRule;

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

	public BatchTransformation(Resource resource) throws IncQueryException {
		iqEngine = AdvancedIncQueryEngine.createUnmanagedEngine(resource);
		ruleEngine = RuleEngines.createIncQueryRuleEngine(iqEngine);
		selfManagedEngines = true;
	}

	public BatchTransformation(ResourceSet set) throws IncQueryException {
		iqEngine = AdvancedIncQueryEngine.createUnmanagedEngine(set);
		ruleEngine = RuleEngines.createIncQueryRuleEngine(iqEngine);
		selfManagedEngines = true;
	}

	public BatchTransformation(RuleEngine ruleEngine,
			AdvancedIncQueryEngine iqEngine) {
		this.ruleEngine = ruleEngine;
		this.iqEngine = iqEngine;
		selfManagedEngines = false;
	}

	/**
	 * This method can be used to initialize all indexes required for the rules
	 * returned by {@link #getRules()} method. If the method is not called, the
	 * indexes are initialized on a per-rule basis on their first use.
	 * 
	 * @throws IncQueryException
	 */
	public void initializeIndexes() throws IncQueryException {
		try {
			iqEngine.getBaseIndex().coalesceTraversals(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					for (BatchTransformationRule<?, ?> rule : getRules()) {
						iqEngine.getMatcher(rule.getPrecondition());
					}
					return null;
				}

			});
		} catch (InvocationTargetException e) {
			throw new IncQueryException("Error initializing pattern matcher: "
					+ e.getMessage(), "Error initializing pattern matcher",
					(Exception) e.getTargetException());
		}
	}

	@SuppressWarnings("rawtypes") //Workaround for Xtend type inference issue
	protected abstract Collection<BatchTransformationRule> getRules();

	public void dispose() {
		if (selfManagedEngines) {
			ruleEngine.dispose();
			iqEngine.dispose();
		}
	}
}
