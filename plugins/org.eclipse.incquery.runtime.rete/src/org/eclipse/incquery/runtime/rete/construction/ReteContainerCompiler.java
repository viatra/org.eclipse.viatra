/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.LeftInheritanceTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IterableIndexer;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.recipes.AntiJoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.CheckRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.incquery.runtime.rete.recipes.EqualityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.EvalRecipe;
import org.eclipse.incquery.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.InequalityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.JoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.SingleParentNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TransitiveClosureRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TypeInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.traceability.QueryPlanRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * An operation compiler implementation for a Rete container.
 * 
 * @author Gabor Bergmann
 * 
 */
public class ReteContainerCompiler
		implements IOperationCompiler, Cloneable {

	protected Map<SubPlan, QueryPlanRecipeTraceInfo> planToRecipe = new HashMap<SubPlan, QueryPlanRecipeTraceInfo>();
	private final RecipesFactory recipesFactory = RecipesFactory.eINSTANCE;
	
    
    // only if provided by putOnTab
    protected PQuery pattern = null;
    protected IPatternMatcherContext context = null;

    protected QueryPlanRecipeTraceInfo getRecipeTrace(SubPlan plan) {
        return planToRecipe.get(plan);
    }
    
    /**
     * Constructs the builder attached to the head container. Prerequisite: engine has its network and boundary fields
     * initialized
     */
    public ReteContainerCompiler(ReteEngine engine) {
        super();
    }

    public void reinitialize() {
    }
    
    public void patternFinished(PQuery pattern, IPatternMatcherContext context, Address<? extends Receiver> collector) {
    	final NodeToPatternTraceInfo traceInfo = new NodeToPatternTraceInfo(pattern);
		collector.getContainer().resolveLocal(collector).assignTraceInfo(traceInfo);
    };

    public SubPlan buildTrimmer(SubPlan parentPlan, TupleMask trimMask, boolean enforceUniqueness) {
        Address<? extends Supplier> trimmer = nodeProvisioner.accessTrimmerNode(getHandle(parentPlan), trimMask);
        final Tuple trimmedVariables = trimMask.transform(parentPlan.getVariablesTuple());
        Address<? extends Supplier> resultNode;
        if (enforceUniqueness) {
        	resultNode = nodeProvisioner.accessUniquenessEnforcerNode(trimmer, trimmedVariables.getSize());
        } else {
        	resultNode = trimmer;
        }
		return trace(new SubPlan(parentPlan, trimmedVariables), resultNode);
    }

    public void buildConnection(SubPlan parentPlan, Address<? extends Receiver> collector) {
        reteNet.connectRemoteNodes(getHandle(parentPlan), collector, true);
        boundary.registerParentPlanForReceiver(collector, parentPlan);
    }

    
    @Override
    public SubPlan buildProduction(Collection<SubPlan> projectedBodies, Map<String, Integer> posMapping) {
    	final ProductionRecipe recipe = recipesFactory.createProductionRecipe();
    	recipe.setPattern(pattern);
    	
    	Collection<QueryPlanRecipeTraceInfo> parentTraces = new ArrayList<QueryPlanRecipeTraceInfo>();
    	for (SubPlan subPlan : projectedBodies) {
			final QueryPlanRecipeTraceInfo parentTrace = getExistingParentTrace(subPlan);
			parentTraces.add(parentTrace);
			recipe.getParents().add(parentTrace.getRecipe());
		}
    	recipe.getMappedIndices(TODO);
    	return traceExplicit(new SubPlan(new FlatTuple(elements)), parentTraces);
    }    
    
    public SubPlan buildStartingPlan(Object[] constantValues, Object[] constantNames) {
    	final ConstantRecipe recipe = recipesFactory.createConstantRecipe();
    	recipe.getConstantValues().addAll(Arrays.asList(constantValues));
    	return trace(new SubPlan(new FlatTuple(constantNames)), recipe);
    }

    public SubPlan buildEqualityChecker(SubPlan parentPlan, int[] indices) {
    	final EqualityFilterRecipe recipe = recipesFactory.createEqualityFilterRecipe();
    	singleParentForRecipe(parentPlan, recipe);
    	for (int i : indices) recipe.getIndices().add(i); 
        return trace(new SubPlan(parentPlan), recipe);
    }

    public SubPlan buildInjectivityChecker(SubPlan parentPlan, int subject, int[] inequalIndices) {
    	final InequalityFilterRecipe recipe = recipesFactory.createInequalityFilterRecipe();
    	singleParentForRecipe(parentPlan, recipe);
    	recipe.setSubject(subject);
    	for (int i : inequalIndices) recipe.getInequals().add(i); 
        return trace(new SubPlan(parentPlan), recipe);
    }

    @Override
    public SubPlan buildTransitiveClosure(SubPlan parentPlan) {
    	final TransitiveClosureRecipe recipe = recipesFactory.createTransitiveClosureRecipe();
    	singleParentForRecipe(parentPlan, recipe);
        return trace(new SubPlan(parentPlan), recipe);
    }

    @Override
    public SubPlan patternCallPlan(Tuple nodes, PQuery supplierKey)
            throws QueryPlannerException {
        return trace(new SubPlan(nodes), boundary.accessProduction(supplierKey));
    }

    public SubPlan transitiveInstantiationPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan directInstantiationPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan transitiveGeneralizationPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan directGeneralizationPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan transitiveContainmentPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan directContainmentPlan(Tuple nodes) {
        throw new UnsupportedOperationException();
    }

    public SubPlan binaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
    	final TypeInputRecipe recipe = RecipesHelper.binaryInputRecipe(supplierKey, supplierKey.toString());
        return trace(new SubPlan(nodes), recipe);
    }

    public SubPlan ternaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
        throw new UnsupportedOperationException();
    }

    public SubPlan unaryTypePlan(Tuple nodes, Object supplierKey) {
    	final TypeInputRecipe recipe = RecipesHelper.unaryInputRecipe(supplierKey, supplierKey.toString());
        return trace(new SubPlan(nodes), recipe);
    }

    
    public SubPlan buildBetaNode(SubPlan primaryPlan,
            SubPlan sidePlan, TupleMask primaryMask, TupleMask sideMask,
            TupleMask complementer, boolean negative) {
    	final QueryPlanRecipeTraceInfo primaryIndexer = getIndexerRecipe(primaryPlan, primaryMask);
    	final QueryPlanRecipeTraceInfo secondaryIndexer = getIndexerRecipe(sidePlan, sideMask);
        if (negative) {
        	final AntiJoinRecipe recipe = recipesFactory.createAntiJoinRecipe();
        	recipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        	recipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());

            return traceExplicit(new SubPlan(primaryPlan), recipe, primaryIndexer, secondaryIndexer);
        } else {
        	final JoinRecipe recipe = recipesFactory.createJoinRecipe();
        	recipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        	recipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
        	recipe.setRightParentComplementaryMask(RecipesHelper.mask(complementer.sourceWidth, complementer.indices));
        	
            Tuple newCalibrationPattern = complementer.combine(primaryPlan.getVariablesTuple(),
                    sidePlan.getVariablesTuple(), Options.enableInheritance, true);            
            return traceExplicit(new SubPlan(primaryPlan, sidePlan, newCalibrationPattern), recipe, primaryIndexer, secondaryIndexer);
        }
    }

    public SubPlan buildCounterBetaNode(SubPlan primaryPlan,
            SubPlan sidePlan, TupleMask primaryMask, TupleMask originalSideMask,
            TupleMask complementer, Object aggregateResultCalibrationElement) {
        Address<? extends IterableIndexer> primarySlot = nodeProvisioner.accessProjectionIndexer(getHandle(primaryPlan),
                primaryMask);
        Address<? extends Indexer> sideSlot = nodeProvisioner.accessCountOuterIndexer(getHandle(sidePlan), originalSideMask);

        Address<? extends Supplier> checker = nodeProvisioner.accessJoinNode(primarySlot, sideSlot,
                TupleMask.selectSingle(originalSideMask.indices.length, originalSideMask.indices.length + 1));

        Object[] newCalibrationElement = { aggregateResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(primaryPlan.getVariablesTuple(), newCalibrationElement);

        SubPlan result = new SubPlan(primaryPlan, newCalibrationPattern);

        return trace(result, checker);
    }
    
    @Override
    public SubPlan buildPredicateChecker(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap, SubPlan parentPlan) {
    	final CheckRecipe recipe = recipesFactory.createCheckRecipe();
    	singleParentForRecipe(parentPlan, recipe);
    	recipe.setExpression(RecipesHelper.expressionDefinition(evaluator));
        return trace(new SubPlan(parentPlan), recipe);
    }

    @Override
    public SubPlan buildFunctionEvaluator(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap,
            SubPlan parentPlan, Object computedResultCalibrationElement) {
    	final EvalRecipe recipe = recipesFactory.createEvalRecipe();
    	singleParentForRecipe(parentPlan, recipe);
    	recipe.setExpression(RecipesHelper.expressionDefinition(evaluator));
        
        Object[] newCalibrationElement = { computedResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(parentPlan.getVariablesTuple(), newCalibrationElement);
        
        return trace(new SubPlan(parentPlan, newCalibrationPattern), recipe);
    }

    /**
     * No need to distinguish
     */
    public IOperationCompiler putOnTab(PQuery effort, IPatternMatcherContext effortContext) {
    	final ReteContainerCompiler patternSpecific;
    	try {
    		patternSpecific = (ReteContainerCompiler) this.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
    	patternSpecific.pattern = effort;
    	patternSpecific.context = effortContext;
        return patternSpecific;
    }
    
    private SubPlan trace(SubPlan plan, ReteNodeRecipe recipe) {
    	Collection<QueryPlanRecipeTraceInfo> parentTraces = new ArrayList<QueryPlanRecipeTraceInfo>();
    	gatherParentTrace(parentTraces, plan.getPrimaryParentPlan());
    	gatherParentTrace(parentTraces, plan.getSecondaryParentPlan());
    	return traceExplicit(plan, recipe, parentTraces);
    }
    private SubPlan traceExplicit(SubPlan plan, ReteNodeRecipe recipe, Collection<QueryPlanRecipeTraceInfo> parentTraces) {
    	QueryPlanRecipeTraceInfo recipeTrace = new QueryPlanRecipeTraceInfo(plan, recipe, parentTraces);
    	return trace(plan, recipeTrace);
    }
    private SubPlan traceExplicit(SubPlan plan, ReteNodeRecipe recipe, QueryPlanRecipeTraceInfo... parentTraces) {
    	return traceExplicit(plan, recipe, Arrays.asList(parentTraces));
    }

	private SubPlan trace(SubPlan plan, QueryPlanRecipeTraceInfo recipeTrace) {
    	planToRecipe.put(plan, recipeTrace);
    	return plan;
    }
    
	private void gatherParentTrace(
			Collection<? super QueryPlanRecipeTraceInfo> parentTraces,
			SubPlan parentPlan) {
		if (parentPlan != null) {
			parentTraces.add(getExistingParentTrace(parentPlan));
		}
	}
	
	private void singleParentForRecipe(SubPlan parentPlan,
			final SingleParentNodeRecipe recipe) {
		final QueryPlanRecipeTraceInfo parentTrace = getExistingParentTrace(parentPlan);
		recipe.setParent(parentTrace.getRecipe());
	}

	private QueryPlanRecipeTraceInfo getExistingParentTrace(SubPlan parentPlan) {
		final QueryPlanRecipeTraceInfo parentTrace = getRecipeTrace(parentPlan);
		if (parentTrace == null)
			throw new IllegalStateException("No recipe trace constructed for parent plan " + parentPlan);
		return parentTrace;
	}
	
    private QueryPlanRecipeTraceInfo getIndexerRecipe(SubPlan parentPlan, TupleMask mask) {
    	final QueryPlanRecipeTraceInfo parentTrace = getExistingParentTrace(parentPlan);
		final ProjectionIndexerRecipe recipe = RecipesHelper.projectionIndexerRecipe(
    			parentTrace.getRecipe(), 
    			RecipesHelper.mask(mask.sourceWidth, mask.indices)
    	);
		return new QueryPlanRecipeTraceInfo(parentPlan, recipe, parentTrace);
    }

    

}
