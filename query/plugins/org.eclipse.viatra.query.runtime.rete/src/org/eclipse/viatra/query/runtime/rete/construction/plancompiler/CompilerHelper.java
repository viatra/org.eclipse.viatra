/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.construction.plancompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.TypeHelper;
import org.eclipse.viatra.query.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.recipes.EqualityFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerBasedAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.JoinRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.Mask;
import org.eclipse.viatra.query.runtime.rete.recipes.MonotonicityInfo;
import org.eclipse.viatra.query.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesFactory;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleColumnAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.TrimmerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledSubPlan;
import org.eclipse.viatra.query.runtime.rete.traceability.PlanningTrace;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.ReteHintOptions;

import com.google.common.collect.ImmutableSet;

/**
 * @author Bergmann Gabor
 *
 */
public class CompilerHelper {

    final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;

    /**
     * Makes sure that all variables in the tuple are different so that it can be used as {@link CompiledSubPlan}. If a
     * variable occurs multiple times, equality checks are applied and then the results are trimmed so that duplicates
     * are hidden. If no manipulation is necessary, the original trace is returned.
     * 
     * <p>
     * to be used whenever a constraint introduces new variables.
     */
    public static PlanningTrace checkAndTrimEqualVariables(SubPlan plan, final PlanningTrace coreTrace) {
        // are variables in the constraint all different?
        final List<PVariable> coreVariablesTuple = coreTrace.getVariablesTuple();
        final int constraintArity = coreVariablesTuple.size();
        final int distinctVariables = coreTrace.getPosMapping().size();
        if (constraintArity == distinctVariables) {
            // all variables occur exactly once in tuple
            return coreTrace;
        } else { // apply equality checks and trim

            // find the positions in the tuple for each variable
            Map<PVariable, SortedSet<Integer>> posMultimap = new HashMap<PVariable, SortedSet<Integer>>();
            List<PVariable> trimmedVariablesTuple = new ArrayList<PVariable>(distinctVariables);
            int[] trimIndices = new int[distinctVariables];
            for (int i = 0; i < constraintArity; ++i) {
                final PVariable variable = coreVariablesTuple.get(i);
                SortedSet<Integer> indexSet = posMultimap.get(variable);
                if (indexSet == null) { // first occurrence of variable
                    indexSet = new TreeSet<Integer>();
                    posMultimap.put(variable, indexSet);

                    // this is the first occurrence, set up trimming
                    trimIndices[trimmedVariablesTuple.size()] = i;
                    trimmedVariablesTuple.add(variable);
                }
                indexSet.add(i);
            }

            // construct equality checks for each variable occurring multiple times
            PlanningTrace lastTrace = coreTrace;
            for (Entry<PVariable, SortedSet<Integer>> entry : posMultimap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    EqualityFilterRecipe equalityFilterRecipe = FACTORY.createEqualityFilterRecipe();
                    equalityFilterRecipe.setParent(lastTrace.getRecipe());
                    equalityFilterRecipe.getIndices().addAll(entry.getValue());
                    lastTrace = new PlanningTrace(plan, coreVariablesTuple, equalityFilterRecipe, lastTrace);
                }
            }

            // trim so that each variable occurs only once
            TrimmerRecipe trimmerRecipe = FACTORY.createTrimmerRecipe();
            trimmerRecipe.setParent(lastTrace.getRecipe());
            trimmerRecipe.setMask(org.eclipse.viatra.query.runtime.rete.recipes.helper.RecipesHelper
                    .mask(constraintArity, trimIndices));
            return new PlanningTrace(plan, trimmedVariablesTuple, trimmerRecipe, lastTrace);
        }
    }

    /**
     * Extracts the variable list representation of the variables tuple.
     */
    public static List<PVariable> convertVariablesTuple(EnumerablePConstraint constraint) {
        return convertVariablesTuple(constraint.getVariablesTuple());
    }

    /**
     * Extracts the variable list representation of the variables tuple.
     */
    public static List<PVariable> convertVariablesTuple(Tuple variablesTuple) {
        List<PVariable> result = new ArrayList<PVariable>();
        for (Object o : variablesTuple.getElements())
            result.add((PVariable) o);
        return result;
    }

    /**
     * Returns a compiled indexer trace according to a mask
     */
    public static RecipeTraceInfo makeIndexerTrace(SubPlan planToCompile, PlanningTrace parentTrace, TupleMask mask) {
        final ReteNodeRecipe parentRecipe = parentTrace.getRecipe();
        if (parentRecipe instanceof IndexerBasedAggregatorRecipe
                || parentRecipe instanceof SingleColumnAggregatorRecipe)
            throw new IllegalArgumentException(
                    "Cannot take projection indexer of aggregator node at plan " + planToCompile);
        IndexerRecipe recipe = RecipesHelper.projectionIndexerRecipe(parentRecipe, toRecipeMask(mask));
        // final List<PVariable> maskedVariables = mask.transform(parentTrace.getVariablesTuple());
        return new PlanningTrace(planToCompile, /* maskedVariables */ parentTrace.getVariablesTuple(), recipe,
                parentTrace);
        // TODO add specialized indexer trace info?
    }

    /**
     * Creates a trimmer that keeps selected variables only.
     */
    protected static TrimmerRecipe makeTrimmerRecipe(final PlanningTrace compiledParent,
            List<PVariable> projectedVariables) {
        final Mask projectionMask = makeProjectionMask(compiledParent, projectedVariables);
        final TrimmerRecipe trimmerRecipe = ReteRecipeCompiler.FACTORY.createTrimmerRecipe();
        trimmerRecipe.setParent(compiledParent.getRecipe());
        trimmerRecipe.setMask(projectionMask);
        return trimmerRecipe;
    }

    public static Mask makeProjectionMask(final PlanningTrace compiledParent, Iterable<PVariable> projectedVariables) {
        List<Integer> projectionSourceIndices = new ArrayList<Integer>();
        for (PVariable pVariable : projectedVariables) {
            projectionSourceIndices.add(compiledParent.getPosMapping().get(pVariable));
        }
        final Mask projectionMask = RecipesHelper.mask(compiledParent.getRecipe().getArity(), projectionSourceIndices);
        return projectionMask;
    }

    /**
     * @since 1.6
     */
    public static final class PosetTriplet {
        public Mask coreMask;
        public Mask posetMask;
        public IPosetComparator comparator;
    }

    /**
     * @since 1.6
     */
    public static PosetTriplet computePosetInfo(List<PVariable> variables, PBody body, IQueryMetaContext context) {
        Map<PVariable, Set<IInputKey>> typeMap = TypeHelper.inferUnaryTypesFor(variables, body.getConstraints(),
                context);
        List<Set<IInputKey>> keys = new LinkedList<Set<IInputKey>>();

        for (int i = 0; i < variables.size(); i++) {
            keys.add(typeMap.get(variables.get(i)));
        }

        return computePosetInfo(keys, context);
    }

    /**
     * @since 1.6
     */
    public static PosetTriplet computePosetInfo(List<PParameter> parameters, IQueryMetaContext context) {
        List<Set<IInputKey>> keys = new LinkedList<Set<IInputKey>>();
        for (int i = 0; i < parameters.size(); i++) {
            IInputKey key = parameters.get(i).getDeclaredUnaryType();
            if (key == null) {
                keys.add(ImmutableSet.<IInputKey> of());
            } else {
                keys.add(ImmutableSet.of(parameters.get(i).getDeclaredUnaryType()));
            }
        }
        return computePosetInfo(keys, context);
    }
    
    

    /**
     * @since 1.6
     */
    public static PosetTriplet computePosetInfo(Iterable<Set<IInputKey>> keys, IQueryMetaContext context) {
        PosetTriplet result = new PosetTriplet();
        List<Integer> coreIndices = new ArrayList<Integer>();
        List<Integer> posetIndices = new ArrayList<Integer>();
        List<IInputKey> filtered = new ArrayList<IInputKey>();
        boolean posetKey = false;
        int i = -1;

        for (Set<IInputKey> _keys : keys) {
            ++i;
            posetKey = false;

            for (IInputKey key : _keys) {
                if (key != null && context.isPosetKey(key)) {
                    posetKey = true;
                    filtered.add(key);
                    break;
                }
            }

            if (posetKey) {
                posetIndices.add(i);
            } else {
                coreIndices.add(i);
            }
        }

        result.comparator = context.getPosetComparator(filtered);
        result.coreMask = RecipesHelper.mask(i, coreIndices);
        result.posetMask = RecipesHelper.mask(i, posetIndices);

        return result;
    }

    /**
     * Creates a recipe for a production node and the corresponding trace.
     * @since 1.6
     */
    public static CompiledQuery makeQueryTrace(PQuery query, Map<PBody, RecipeTraceInfo> bodyFinalTraces,
            Collection<ReteNodeRecipe> bodyFinalRecipes, QueryEvaluationHint hint, IQueryMetaContext context) {
        ProductionRecipe recipe = ReteRecipeCompiler.FACTORY.createProductionRecipe();

        boolean deleteRederiveEvaluation = ReteHintOptions.deleteRederiveEvaluation.getValueOrDefault(hint);

        if (deleteRederiveEvaluation) {
            PosetTriplet triplet = computePosetInfo(query.getParameters(), context);
            if (triplet.comparator != null) {
                MonotonicityInfo info = FACTORY.createMonotonicityInfo();
                info.setCoreMask(triplet.coreMask);
                info.setPosetMask(triplet.posetMask);
                info.setPosetComparator(triplet.comparator);
                recipe.setOptionalMonotonicityInfo(info);
            }
        }

        recipe.setDeleteRederiveEvaluation(deleteRederiveEvaluation);
        recipe.setPattern(query);
        recipe.setPatternFQN(query.getFullyQualifiedName());
        recipe.setTraceInfo(recipe.getPatternFQN());
        recipe.getParents().addAll(bodyFinalRecipes);
        for (int i = 0; i < query.getParameterNames().size(); ++i) {
            recipe.getMappedIndices().put(query.getParameterNames().get(i), i);
        }

        return new CompiledQuery(recipe, bodyFinalTraces, query);
    }

    /**
     * Calculated index mappings for a join, based on the common variables of the two parent subplans.
     * 
     * @author Gabor Bergmann
     * 
     */
    public static class JoinHelper {
        private TupleMask primaryMask;
        private TupleMask secondaryMask;
        private TupleMask complementerMask;
        private RecipeTraceInfo primaryIndexer;
        private RecipeTraceInfo secondaryIndexer;
        private JoinRecipe naturalJoinRecipe;
        private List<PVariable> naturalJoinVariablesTuple;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         */
        public JoinHelper(SubPlan planToCompile, PlanningTrace primaryCompiled, PlanningTrace callTrace) {
            super();

            Set<PVariable> primaryVariables = new LinkedHashSet<PVariable>(primaryCompiled.getVariablesTuple());
            Set<PVariable> secondaryVariables = new LinkedHashSet<PVariable>(callTrace.getVariablesTuple());
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(callTrace.getPosMapping().get(var));
            }
            List<Integer> primaryIndices = new ArrayList<Integer>(oldNodes);
            List<Integer> secondaryIndices = new ArrayList<Integer>(oldNodes);
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices.add(primaryCompiled.getPosMapping().get(var));
                    secondaryIndices.add(callTrace.getPosMapping().get(var));
                }
            }
            Collection<Integer> complementerIndices = introducingSecondaryIndices;

            primaryMask = TupleMask.fromSelectedIndices(primaryCompiled.getVariablesTuple().size(), primaryIndices);
            secondaryMask = TupleMask.fromSelectedIndices(callTrace.getVariablesTuple().size(), secondaryIndices);
            complementerMask = TupleMask.fromSelectedIndices(callTrace.getVariablesTuple().size(), complementerIndices);

            primaryIndexer = makeIndexerTrace(planToCompile, primaryCompiled, primaryMask);
            secondaryIndexer = makeIndexerTrace(planToCompile, callTrace, secondaryMask);

            naturalJoinRecipe = FACTORY.createJoinRecipe();
            naturalJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
            naturalJoinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
            naturalJoinRecipe.setRightParentComplementaryMask(CompilerHelper.toRecipeMask(complementerMask));

            naturalJoinVariablesTuple = new ArrayList<PVariable>(primaryCompiled.getVariablesTuple());
            for (int complementerIndex : complementerMask.indices)
                naturalJoinVariablesTuple.add(callTrace.getVariablesTuple().get(complementerIndex));
        }

        public TupleMask getPrimaryMask() {
            return primaryMask;
        }

        public TupleMask getSecondaryMask() {
            return secondaryMask;
        }

        public TupleMask getComplementerMask() {
            return complementerMask;
        }

        public RecipeTraceInfo getPrimaryIndexer() {
            return primaryIndexer;
        }

        public RecipeTraceInfo getSecondaryIndexer() {
            return secondaryIndexer;
        }

        public JoinRecipe getNaturalJoinRecipe() {
            return naturalJoinRecipe;
        }

        public List<PVariable> getNaturalJoinVariablesTuple() {
            return naturalJoinVariablesTuple;
        }

    }

    /**
     * @since 1.4
     */
    public static Mask toRecipeMask(TupleMask mask) {
        return RecipesHelper.mask(mask.sourceWidth, mask.indices);
    }

}
