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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.backend.CommonQueryHintOptions;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.BuildHelper;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PApply;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PEnumerate;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PJoin;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.POperation;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PStart;
import org.eclipse.viatra.query.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PDisjunctionRewriter;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PDisjunctionRewriterCacher;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.SurrogateQueryRewriter;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.rete.construction.plancompiler.CompilerHelper.JoinHelper;
import org.eclipse.viatra.query.runtime.rete.construction.plancompiler.CompilerHelper.PosetTriplet;
import org.eclipse.viatra.query.runtime.rete.recipes.AntiJoinRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.CountAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.DiscriminatorBucketRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.DiscriminatorDispatcherRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.EqualityFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ExpressionEnforcerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InequalityFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.JoinRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.Mask;
import org.eclipse.viatra.query.runtime.rete.recipes.MonotonicityInfo;
import org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesFactory;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleColumnAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.TransitiveClosureRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.TrimmerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.UniquenessEnforcerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledSubPlan;
import org.eclipse.viatra.query.runtime.rete.traceability.ParameterProjectionTrace;
import org.eclipse.viatra.query.runtime.rete.traceability.PlanningTrace;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.ReteHintOptions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Compiles queries and query plans into Rete recipes, traced by respectively a {@link CompiledQuery} or
 * {@link CompiledSubPlan}.
 * 
 * @author Bergmann Gabor
 *
 */
public class ReteRecipeCompiler {

    private final IQueryPlannerStrategy plannerStrategy;
    private final IQueryMetaContext metaContext;
    private final IQueryBackendHintProvider hintProvider;
    private final PDisjunctionRewriter normalizer;
    private final QueryAnalyzer queryAnalyzer;
    private final Logger logger;

    /**
     * @since 1.5
     */
    public ReteRecipeCompiler(IQueryPlannerStrategy plannerStrategy, Logger logger, IQueryMetaContext metaContext,
            IQueryCacheContext queryCacheContext, IQueryBackendHintProvider hintProvider, QueryAnalyzer queryAnalyzer) {
        super();
        this.plannerStrategy = plannerStrategy;
        this.logger = logger;
        this.metaContext = metaContext;
        this.queryAnalyzer = queryAnalyzer;
        this.normalizer = new PDisjunctionRewriterCacher(new SurrogateQueryRewriter(),
                new PBodyNormalizer(metaContext) {

                    @Override
                    protected boolean shouldExpandWeakenedAlternatives(PQuery query) {
                        QueryEvaluationHint hint = ReteRecipeCompiler.this.hintProvider.getQueryEvaluationHint(query);
                        Boolean expandWeakenedAlternativeConstraints = ReteHintOptions.expandWeakenedAlternativeConstraints
                                .getValueOrDefault(hint);
                        return expandWeakenedAlternativeConstraints;
                    }

                });
        this.hintProvider = hintProvider;
    }

    /**
     * @deprecated use
     *             {@link #ReteRecipeCompiler(IQueryPlannerStrategy, Logger, IQueryMetaContext, IQueryCacheContext, IQueryBackendHintProvider, QueryAnalyzer)}
     */
    @Deprecated
    public ReteRecipeCompiler(IQueryPlannerStrategy plannerStrategy, Logger logger, IQueryMetaContext metaContext,
            IQueryCacheContext queryCacheContext, IQueryBackendHintProvider hintProvider) {
        this(plannerStrategy, logger, metaContext, queryCacheContext, hintProvider, new QueryAnalyzer(metaContext));
    }

    final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;

    // INTERNALLY CACHED
    private Map<PBody, SubPlan> plannerCache = new HashMap<PBody, SubPlan>();
    private Set<PBody> planningInProgress = new HashSet<PBody>();

    private Map<PQuery, CompiledQuery> queryCompilerCache = new HashMap<PQuery, CompiledQuery>();
    private Set<PQuery> compilationInProgress = new HashSet<PQuery>();
    private Multimap<PQuery, RecursionCutoffPoint> recursionCutoffPoints = HashMultimap.create();
    private Map<SubPlan, CompiledSubPlan> subPlanCompilerCache = new HashMap<SubPlan, CompiledSubPlan>();
    private Map<ReteNodeRecipe, SubPlan> compilerBackTrace = new HashMap<ReteNodeRecipe, SubPlan>();

    /**
     * Clears internal state
     */
    public void reset() {
        plannerCache.clear();
        planningInProgress.clear();
        queryCompilerCache.clear();
        subPlanCompilerCache.clear();
        compilerBackTrace.clear();
    }

    /**
     * Returns a {@link CompiledQuery} compiled from a query
     */
    public CompiledQuery getCompiledForm(PQuery query) throws QueryProcessingException {
        CompiledQuery compiled = queryCompilerCache.get(query);
        if (compiled == null) {

            IRewriterTraceCollector traceCollector = CommonQueryHintOptions.normalizationTraceCollector
                    .getValueOrDefault(hintProvider.getQueryEvaluationHint(query));
            if (traceCollector != null) {
                traceCollector.addTrace(query, query);
            }

            boolean reentrant = !compilationInProgress.add(query);
            if (reentrant) { // oops, recursion into body in progress
                RecursionCutoffPoint cutoffPoint = new RecursionCutoffPoint(query, getHints(query), metaContext);
                recursionCutoffPoints.put(query, cutoffPoint);
                return cutoffPoint.getCompiledQuery();
            } else { // not reentrant, therefore no recursion, do the compilation
                try {
                    compiled = compileProduction(query);
                    queryCompilerCache.put(query, compiled);
                    // backTrace.put(compiled.getRecipe(), plan);

                    // if this was a recursive query, mend all points where recursion was cut off
                    for (RecursionCutoffPoint cutoffPoint : recursionCutoffPoints.get(query)) {
                        cutoffPoint.mend(compiled);
                    }
                } finally {
                    compilationInProgress.remove(query);
                }
            }
        }
        return compiled;
    }

    /**
     * Returns a {@link CompiledSubPlan} compiled from a query plan
     */
    public CompiledSubPlan getCompiledForm(SubPlan plan) throws QueryProcessingException {
        CompiledSubPlan compiled = subPlanCompilerCache.get(plan);
        if (compiled == null) {
            compiled = doCompileDispatch(plan);
            subPlanCompilerCache.put(plan, compiled);
            compilerBackTrace.put(compiled.getRecipe(), plan);
        }
        return compiled;
    }

    public SubPlan getPlan(PBody pBody) throws QueryProcessingException {
        // if the query is not marked as being compiled, initiate compilation
        // (this is useful in case of recursion if getPlan() is the entry point)
        PQuery pQuery = pBody.getPattern();
        if (!compilationInProgress.contains(pQuery))
            getCompiledForm(pQuery);

        // Is the plan already cached?
        SubPlan plan = plannerCache.get(pBody);
        if (plan == null) {
            boolean reentrant = !planningInProgress.add(pBody);
            if (reentrant) { // oops, recursion into body in progress
                throw new IllegalArgumentException(
                        "Planning-level recursion unsupported: " + pBody.getPattern().getFullyQualifiedName());
            } else { // not reentrant, therefore no recursion, do the planning
                try {
                    plan = plannerStrategy.plan(pBody, logger, metaContext);
                    plannerCache.put(pBody, plan);
                } finally {
                    planningInProgress.remove(pBody);
                }
            }
        }
        return plan;
    }

    private CompiledQuery compileProduction(PQuery query) throws QueryProcessingException {
        Collection<SubPlan> bodyPlans = new ArrayList<SubPlan>();
        normalizer.setTraceCollector(CommonQueryHintOptions.normalizationTraceCollector
                .getValueOrDefault(hintProvider.getQueryEvaluationHint(query)));
        for (PBody pBody : normalizer.rewrite(query).getBodies()) {
            SubPlan bodyPlan = getPlan(pBody);
            bodyPlans.add(bodyPlan);
        }
        return doCompileProduction(query, bodyPlans);
    }

    private CompiledQuery doCompileProduction(PQuery query, Collection<SubPlan> bodies)
            throws QueryProcessingException {
        // TODO skip production node if there is just one body and no projection needed?
        Map<PBody, RecipeTraceInfo> bodyFinalTraces = new HashMap<PBody, RecipeTraceInfo>();
        Collection<ReteNodeRecipe> bodyFinalRecipes = new HashSet<ReteNodeRecipe>();

        for (SubPlan bodyFinalPlan : bodies) {
            // skip over any projections at the end
            while (bodyFinalPlan.getOperation() instanceof PProject)
                bodyFinalPlan = bodyFinalPlan.getParentPlans().get(0);

            // TODO checkAndTrimEqualVariables may introduce superfluous trim,
            // but whatever (no uniqueness enforcer needed)

            // compile body
            final CompiledSubPlan compiledBody = getCompiledForm(bodyFinalPlan);

            // project to parameter list
            final PBody body = bodyFinalPlan.getBody();
            final List<PVariable> parameterList = body.getSymbolicParameterVariables();
            if (parameterList.equals(compiledBody.getVariablesTuple())) { // no projection needed
                bodyFinalTraces.put(body, compiledBody);
                bodyFinalRecipes.add(compiledBody.getRecipe());
            } else {
                TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledBody, parameterList);
                RecipeTraceInfo trimmerTrace = new ParameterProjectionTrace(body, trimmerRecipe, compiledBody);
                bodyFinalTraces.put(body, trimmerTrace);
                bodyFinalRecipes.add(trimmerRecipe);
            }
        }

        CompiledQuery compiled = CompilerHelper.makeQueryTrace(query, bodyFinalTraces, bodyFinalRecipes,
                getHints(query), metaContext);

        return compiled;
    }

    private CompiledSubPlan doCompileDispatch(SubPlan plan) throws QueryProcessingException {
        final POperation operation = plan.getOperation();
        if (operation instanceof PEnumerate) {
            return doCompileEnumerate(((PEnumerate) operation).getEnumerablePConstraint(), plan);
        } else if (operation instanceof PApply) {
            final PConstraint pConstraint = ((PApply) operation).getPConstraint();
            if (pConstraint instanceof EnumerablePConstraint) {
                CompiledSubPlan primaryParent = getCompiledForm(plan.getParentPlans().get(0));
                PlanningTrace secondaryParent = doEnumerateDispatch(plan, (EnumerablePConstraint) pConstraint);
                return compileToNaturalJoin(plan, primaryParent, secondaryParent);
            } else if (pConstraint instanceof DeferredPConstraint) {
                return doDeferredDispatch((DeferredPConstraint) pConstraint, plan);
            } else {
                throw new IllegalArgumentException("Unsupported PConstraint in query plan: " + plan.toShortString());
            }
        } else if (operation instanceof PJoin) {
            return doCompileJoin((PJoin) operation, plan);
        } else if (operation instanceof PProject) {
            return doCompileProject((PProject) operation, plan);
        } else if (operation instanceof PStart) {
            return doCompileStart((PStart) operation, plan);
        } else {
            throw new IllegalArgumentException("Unsupported POperation in query plan: " + plan.toShortString());
        }
    }

    private CompiledSubPlan doDeferredDispatch(DeferredPConstraint constraint, SubPlan plan)
            throws QueryProcessingException {
        final SubPlan parentPlan = plan.getParentPlans().get(0);
        final CompiledSubPlan parentCompiled = getCompiledForm(parentPlan);
        if (constraint instanceof Equality) {
            return compileDeferred((Equality) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExportedParameter) {
            return compileDeferred((ExportedParameter) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof Inequality) {
            return compileDeferred((Inequality) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof NegativePatternCall) {
            return compileDeferred((NegativePatternCall) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof PatternMatchCounter) {
            return compileDeferred((PatternMatchCounter) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof AggregatorConstraint) {
            return compileDeferred((AggregatorConstraint) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExpressionEvaluation) {
            return compileDeferred((ExpressionEvaluation) constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof TypeFilterConstraint) {
            return compileDeferred((TypeFilterConstraint) constraint, plan, parentPlan, parentCompiled);
        }
        throw new UnsupportedOperationException("Unknown deferred constraint " + constraint);
    }

    private CompiledSubPlan compileDeferred(Equality constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) {
        if (constraint.isMoot())
            return parentCompiled.cloneFor(plan);

        Integer index1 = parentCompiled.getPosMapping().get(constraint.getWho());
        Integer index2 = parentCompiled.getPosMapping().get(constraint.getWithWhom());

        if (index1 != null && index2 != null && index1.intValue() != index2.intValue()) {
            Integer indexLower = Math.min(index1, index2);
            Integer indexHigher = Math.max(index1, index2);

            EqualityFilterRecipe equalityFilterRecipe = FACTORY.createEqualityFilterRecipe();
            equalityFilterRecipe.setParent(parentCompiled.getRecipe());
            equalityFilterRecipe.getIndices().add(indexLower);
            equalityFilterRecipe.getIndices().add(indexHigher);

            return new CompiledSubPlan(plan, parentCompiled.getVariablesTuple(), equalityFilterRecipe, parentCompiled);
        } else {
            throw new IllegalArgumentException(String.format("Unable to interpret %s after compiled parent %s",
                    plan.toShortString(), parentCompiled.toString()));
        }
    }

    /**
     * Precondition: constantTrace must map to a ConstantRecipe, and all of its variables must be contained in
     * toFilterTrace.
     */
    private CompiledSubPlan compileConstantFiltering(SubPlan plan, PlanningTrace toFilterTrace,
            ConstantRecipe constantRecipe, List<PVariable> filteredVariables) {
        PlanningTrace resultTrace = toFilterTrace;

        int constantVariablesSize = filteredVariables.size();
        for (int i = 0; i < constantVariablesSize; ++i) {
            Object constantValue = constantRecipe.getConstantValues().get(i);
            PVariable filteredVariable = filteredVariables.get(i);
            int filteredColumn = resultTrace.getVariablesTuple().indexOf(filteredVariable);

            DiscriminatorDispatcherRecipe dispatcherRecipe = FACTORY.createDiscriminatorDispatcherRecipe();
            dispatcherRecipe.setDiscriminationColumnIndex(filteredColumn);
            dispatcherRecipe.setParent(resultTrace.getRecipe());

            PlanningTrace dispatcherTrace = new PlanningTrace(plan, resultTrace.getVariablesTuple(), dispatcherRecipe,
                    resultTrace);

            DiscriminatorBucketRecipe bucketRecipe = FACTORY.createDiscriminatorBucketRecipe();
            bucketRecipe.setBucketKey(constantValue);
            bucketRecipe.setParent(dispatcherRecipe);

            PlanningTrace bucketTrace = new PlanningTrace(plan, dispatcherTrace.getVariablesTuple(), bucketRecipe,
                    dispatcherTrace);

            resultTrace = bucketTrace;
        }

        return resultTrace.cloneFor(plan);
    }

    private CompiledSubPlan compileDeferred(ExportedParameter constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) {
        return parentCompiled.cloneFor(plan);
    }

    private CompiledSubPlan compileDeferred(Inequality constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) {
        if (constraint.isEliminable())
            return parentCompiled.cloneFor(plan);

        Integer index1 = parentCompiled.getPosMapping().get(constraint.getWho());
        Integer index2 = parentCompiled.getPosMapping().get(constraint.getWithWhom());

        if (index1 != null && index2 != null && index1.intValue() != index2.intValue()) {
            Integer indexLower = Math.min(index1, index2);
            Integer indexHigher = Math.max(index1, index2);

            InequalityFilterRecipe inequalityFilterRecipe = FACTORY.createInequalityFilterRecipe();
            inequalityFilterRecipe.setParent(parentCompiled.getRecipe());
            inequalityFilterRecipe.setSubject(indexLower);
            inequalityFilterRecipe.getInequals().add(indexHigher);

            return new CompiledSubPlan(plan, parentCompiled.getVariablesTuple(), inequalityFilterRecipe,
                    parentCompiled);
        } else {
            throw new IllegalArgumentException(String.format("Unable to interpret %s after compiled parent %s",
                    plan.toShortString(), parentCompiled.toString()));
        }
    }

    private CompiledSubPlan compileDeferred(TypeFilterConstraint constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) throws QueryProcessingException {
        final IInputKey inputKey = constraint.getInputKey();
        if (!metaContext.isStateless(inputKey))
            throw new UnsupportedOperationException(
                    "Non-enumerable input keys are currently supported in Rete only if they are stateless, unlike "
                            + inputKey);

        final Tuple constraintVariables = constraint.getVariablesTuple();
        final List<PVariable> parentVariables = parentCompiled.getVariablesTuple();

        Mask mask; // select elements of the tuple to check against extensional relation
        if (Tuples.flatTupleOf(parentVariables.toArray()).equals(constraintVariables)) {
            mask = null; // lucky case, parent signature equals that of input key
        } else {
            List<PVariable> variables = new ArrayList<PVariable>();
            for (Object variable : constraintVariables.getElements()) {
                variables.add((PVariable) variable);
            }
            mask = CompilerHelper.makeProjectionMask(parentCompiled, variables);
        }
        InputFilterRecipe inputFilterRecipe = RecipesHelper.inputFilterRecipe(parentCompiled.getRecipe(), inputKey,
                inputKey.getStringID(), mask);
        return new CompiledSubPlan(plan, parentVariables, inputFilterRecipe, parentCompiled);
    }

    private CompiledSubPlan compileDeferred(NegativePatternCall constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) throws QueryProcessingException {
        final PlanningTrace callTrace = referQuery(constraint.getReferredQuery(), plan,
                constraint.getActualParametersTuple());

        JoinHelper joinHelper = new JoinHelper(plan, parentCompiled, callTrace);
        final RecipeTraceInfo primaryIndexer = joinHelper.getPrimaryIndexer();
        final RecipeTraceInfo secondaryIndexer = joinHelper.getSecondaryIndexer();

        AntiJoinRecipe antiJoinRecipe = FACTORY.createAntiJoinRecipe();
        antiJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        antiJoinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());

        return new CompiledSubPlan(plan, parentCompiled.getVariablesTuple(), antiJoinRecipe, primaryIndexer,
                secondaryIndexer);
    }

    private CompiledSubPlan compileDeferred(PatternMatchCounter constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) throws QueryProcessingException {
        final PlanningTrace callTrace = referQuery(constraint.getReferredQuery(), plan,
                constraint.getActualParametersTuple());

        // hack: use some mask computations (+ the indexers) from a fake natural join against the called query
        JoinHelper fakeJoinHelper = new JoinHelper(plan, parentCompiled, callTrace);
        final RecipeTraceInfo primaryIndexer = fakeJoinHelper.getPrimaryIndexer();
        final RecipeTraceInfo callProjectionIndexer = fakeJoinHelper.getSecondaryIndexer();

        final List<PVariable> sideVariablesTuple = fakeJoinHelper.getSecondaryMask()
                .transform(callTrace.getVariablesTuple());
        /* if (!booleanCheck) */ sideVariablesTuple.add(constraint.getResultVariable());

        CountAggregatorRecipe aggregatorRecipe = FACTORY.createCountAggregatorRecipe();
        aggregatorRecipe.setParent((ProjectionIndexerRecipe) callProjectionIndexer.getRecipe());
        PlanningTrace aggregatorTrace = new PlanningTrace(plan, sideVariablesTuple, aggregatorRecipe,
                callProjectionIndexer);

        IndexerRecipe aggregatorIndexerRecipe = FACTORY.createAggregatorIndexerRecipe();
        aggregatorIndexerRecipe.setParent(aggregatorRecipe);
        // aggregatorIndexerRecipe.setMask(RecipesHelper.mask(
        // sideVariablesTuple.size(),
        // //use same indices as in the projection indexer
        // // EVEN if result variable already visible in left parent
        // fakeJoinHelper.getSecondaryMask().indices
        // ));

        int aggregatorWidth = sideVariablesTuple.size();
        int aggregateResultIndex = aggregatorWidth - 1;

        aggregatorIndexerRecipe.setMask(CompilerHelper.toRecipeMask(TupleMask.omit(
                // aggregate according all but the last index
                aggregateResultIndex, aggregatorWidth)));
        PlanningTrace aggregatorIndexerTrace = new PlanningTrace(plan, sideVariablesTuple, aggregatorIndexerRecipe,
                aggregatorTrace);

        JoinRecipe naturalJoinRecipe = FACTORY.createJoinRecipe();
        naturalJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        naturalJoinRecipe.setRightParent(aggregatorIndexerRecipe);
        naturalJoinRecipe.setRightParentComplementaryMask(RecipesHelper.mask(aggregatorWidth,
                // extend with last element only - the computation value
                aggregateResultIndex));

        // what if the new variable already has a value?
        // even if already known, we add the new result variable, so that it can be filtered at the end
        // boolean alreadyKnown = parentPlan.getVisibleVariables().contains(constraint.getResultVariable());

        final List<PVariable> aggregatedVariablesTuple = new ArrayList<PVariable>(parentCompiled.getVariablesTuple());
        aggregatedVariablesTuple.add(constraint.getResultVariable());

        PlanningTrace joinTrace = new PlanningTrace(plan, aggregatedVariablesTuple, naturalJoinRecipe, primaryIndexer,
                aggregatorIndexerTrace);

        return CompilerHelper.checkAndTrimEqualVariables(plan, joinTrace).cloneFor(plan);
        // if (!alreadyKnown) {
        // return joinTrace.cloneFor(plan);
        // } else {
        // //final Integer equalsWithIndex = parentCompiled.getPosMapping().get(parentCompiled.getVariablesTuple());
        // }
    }

    private CompiledSubPlan compileDeferred(AggregatorConstraint constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) throws QueryProcessingException {
        final PlanningTrace callTrace = referQuery(constraint.getReferredQuery(), plan,
                constraint.getActualParametersTuple());

        // hack: use some mask computations (+ the indexers) from a fake natural join against the called query
        JoinHelper fakeJoinHelper = new JoinHelper(plan, parentCompiled, callTrace);
        final RecipeTraceInfo primaryIndexer = fakeJoinHelper.getPrimaryIndexer();
        TupleMask callGroupMask = fakeJoinHelper.getSecondaryMask();

        final List<PVariable> sideVariablesTuple = callGroupMask.transform(callTrace.getVariablesTuple());
        /* if (!booleanCheck) */ sideVariablesTuple.add(constraint.getResultVariable());

        IMultisetAggregationOperator<?, ?, ?> operator = constraint.getAggregator().getOperator();

        SingleColumnAggregatorRecipe columnAggregatorRecipe = FACTORY.createSingleColumnAggregatorRecipe();
        columnAggregatorRecipe.setParent(callTrace.getRecipe());
        columnAggregatorRecipe.setMultisetAggregationOperator(operator);

        int columnIndex = constraint.getAggregatedColumn();
        IPosetComparator posetComparator = null;
        boolean deleteRederiveEvaluation = ReteHintOptions.deleteRederiveEvaluation.getValueOrDefault(getHints(plan));
        Mask groupMask = CompilerHelper.toRecipeMask(callGroupMask);

        columnAggregatorRecipe.setDeleteRederiveEvaluation(deleteRederiveEvaluation);
        if (deleteRederiveEvaluation) {
            List<PParameter> parameters = constraint.getReferredQuery().getParameters();
            IInputKey key = parameters.get(columnIndex).getDeclaredUnaryType();
            if (key != null && metaContext.isPosetKey(key)) {
                posetComparator = metaContext.getPosetComparator(Collections.singleton(key));
            }
        }

        if (posetComparator == null) {
            columnAggregatorRecipe.setGroupByMask(groupMask);
            columnAggregatorRecipe.setAggregableIndex(columnIndex);
        } else {
            MonotonicityInfo monotonicityInfo = FACTORY.createMonotonicityInfo();
            monotonicityInfo.setCoreMask(groupMask);
            monotonicityInfo.setPosetMask(CompilerHelper.toRecipeMask(
                    TupleMask.selectSingle(columnIndex, constraint.getActualParametersTuple().getSize())));
            monotonicityInfo.setPosetComparator(posetComparator);
            columnAggregatorRecipe.setOptionalMonotonicityInfo(monotonicityInfo);
        }

        ReteNodeRecipe aggregatorRecipe = columnAggregatorRecipe;
        PlanningTrace aggregatorTrace = new PlanningTrace(plan, sideVariablesTuple, aggregatorRecipe, callTrace);

        IndexerRecipe aggregatorIndexerRecipe = FACTORY.createAggregatorIndexerRecipe();
        aggregatorIndexerRecipe.setParent(aggregatorRecipe);

        int aggregatorWidth = sideVariablesTuple.size();
        int aggregateResultIndex = aggregatorWidth - 1;

        aggregatorIndexerRecipe.setMask(CompilerHelper.toRecipeMask(TupleMask.omit(
                // aggregate according all but the last index
                aggregateResultIndex, aggregatorWidth)));
        PlanningTrace aggregatorIndexerTrace = new PlanningTrace(plan, sideVariablesTuple, aggregatorIndexerRecipe,
                aggregatorTrace);

        JoinRecipe naturalJoinRecipe = FACTORY.createJoinRecipe();
        naturalJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        naturalJoinRecipe.setRightParent(aggregatorIndexerRecipe);
        naturalJoinRecipe.setRightParentComplementaryMask(RecipesHelper.mask(aggregatorWidth,
                // extend with last element only - the computation value
                aggregateResultIndex));

        // what if the new variable already has a value?
        // even if already known, we add the new result variable, so that it can be filtered at the end
        // boolean alreadyKnown = parentPlan.getVisibleVariables().contains(constraint.getResultVariable());

        final List<PVariable> finalVariablesTuple = new ArrayList<PVariable>(parentCompiled.getVariablesTuple());
        finalVariablesTuple.add(constraint.getResultVariable());

        PlanningTrace joinTrace = new PlanningTrace(plan, finalVariablesTuple, naturalJoinRecipe, primaryIndexer,
                aggregatorIndexerTrace);

        return CompilerHelper.checkAndTrimEqualVariables(plan, joinTrace).cloneFor(plan);
        // if (!alreadyKnown) {
        // return joinTrace.cloneFor(plan);
        // } else {
        // //final Integer equalsWithIndex = parentCompiled.getPosMapping().get(parentCompiled.getVariablesTuple());
        // }
    }

    private CompiledSubPlan compileDeferred(ExpressionEvaluation constraint, SubPlan plan, SubPlan parentPlan,
            CompiledSubPlan parentCompiled) {
        Map<String, Integer> tupleNameMap = new HashMap<String, Integer>();
        for (String name : constraint.getEvaluator().getInputParameterNames()) {
            Map<? extends Object, Integer> index = parentCompiled.getPosMapping();
            PVariable variable = constraint.getPSystem().getVariableByNameChecked(name);
            Integer position = index.get(variable);
            tupleNameMap.put(name, position);
        }

        final PVariable outputVariable = constraint.getOutputVariable();
        final boolean booleanCheck = outputVariable == null;

        // TODO determine whether expression is costly
        boolean cacheOutput = ReteHintOptions.cacheOutputOfEvaluatorsByDefault.getValueOrDefault(getHints(plan));
        // for (PAnnotation pAnnotation :
        // plan.getBody().getPattern().getAnnotationsByName(EXPRESSION_EVALUATION_ANNOTATION"")) {
        // for (Object value : pAnnotation.getAllValues("expensive")) {
        // if (value instanceof Boolean)
        // cacheOutput = (boolean) value;
        // }
        // }

        ExpressionEnforcerRecipe enforcerRecipe = booleanCheck ? FACTORY.createCheckRecipe()
                : FACTORY.createEvalRecipe();
        enforcerRecipe.setParent(parentCompiled.getRecipe());
        enforcerRecipe.setExpression(RecipesHelper.expressionDefinition(constraint.getEvaluator()));
        enforcerRecipe.setCacheOutput(cacheOutput);
        for (Entry<String, Integer> entry : tupleNameMap.entrySet()) {
            enforcerRecipe.getMappedIndices().put(entry.getKey(), entry.getValue());
        }

        final List<PVariable> enforcerVariablesTuple = new ArrayList<PVariable>(parentCompiled.getVariablesTuple());
        if (!booleanCheck)
            enforcerVariablesTuple.add(outputVariable);
        PlanningTrace enforcerTrace = new PlanningTrace(plan, enforcerVariablesTuple, enforcerRecipe, parentCompiled);

        return CompilerHelper.checkAndTrimEqualVariables(plan, enforcerTrace).cloneFor(plan);
    }

    private CompiledSubPlan doCompileJoin(PJoin operation, SubPlan plan) throws QueryProcessingException {
        final List<CompiledSubPlan> compiledParents = getCompiledFormOfParents(plan);
        final CompiledSubPlan leftCompiled = compiledParents.get(0);
        final CompiledSubPlan rightCompiled = compiledParents.get(1);

        return compileToNaturalJoin(plan, leftCompiled, rightCompiled);
    }

    private CompiledSubPlan compileToNaturalJoin(SubPlan plan, final PlanningTrace leftCompiled,
            final PlanningTrace rightCompiled) {
        // CHECK IF SPECIAL CASE

        // Is constant filtering applicable?
        if (ReteHintOptions.useDiscriminatorDispatchersForConstantFiltering.getValueOrDefault(getHints(plan))) {
            if (leftCompiled.getRecipe() instanceof ConstantRecipe
                    && rightCompiled.getVariablesTuple().containsAll(leftCompiled.getVariablesTuple())) {
                return compileConstantFiltering(plan, rightCompiled, (ConstantRecipe) leftCompiled.getRecipe(),
                        leftCompiled.getVariablesTuple());
            }
            if (rightCompiled.getRecipe() instanceof ConstantRecipe
                    && leftCompiled.getVariablesTuple().containsAll(rightCompiled.getVariablesTuple())) {
                return compileConstantFiltering(plan, leftCompiled, (ConstantRecipe) rightCompiled.getRecipe(),
                        rightCompiled.getVariablesTuple());
            }
        }

        // ELSE: ACTUAL JOIN
        JoinHelper joinHelper = new JoinHelper(plan, leftCompiled, rightCompiled);
        return new CompiledSubPlan(plan, joinHelper.getNaturalJoinVariablesTuple(), joinHelper.getNaturalJoinRecipe(),
                joinHelper.getPrimaryIndexer(), joinHelper.getSecondaryIndexer());
    }

    private CompiledSubPlan doCompileProject(PProject operation, SubPlan plan) throws QueryProcessingException {
        final List<CompiledSubPlan> compiledParents = getCompiledFormOfParents(plan);
        final CompiledSubPlan compiledParent = compiledParents.get(0);

        List<PVariable> projectedVariables = new ArrayList<PVariable>(operation.getToVariables());
        // Determinizing... TODO add smarter ordering here?
        Collections.sort(projectedVariables, new Comparator<PVariable>() {
            @Override
            public int compare(PVariable o1, PVariable o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        final TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledParent, projectedVariables);

        if (BuildHelper.areAllVariablesDetermined(plan.getParentPlans().get(0), projectedVariables, queryAnalyzer,
                true)) {
            // skip uniqueness enforcement if unneeded
            return new CompiledSubPlan(plan, projectedVariables, trimmerRecipe, compiledParent);
        } else {
            UniquenessEnforcerRecipe recipe = FACTORY.createUniquenessEnforcerRecipe();

            boolean deleteRederiveEvaluation = ReteHintOptions.deleteRederiveEvaluation
                    .getValueOrDefault(getHints(plan));

            if (deleteRederiveEvaluation) {
                PosetTriplet triplet = CompilerHelper.computePosetInfo(projectedVariables, plan.getBody(), metaContext);

                if (triplet.comparator != null) {
                    MonotonicityInfo info = FACTORY.createMonotonicityInfo();
                    info.setCoreMask(triplet.coreMask);
                    info.setPosetMask(triplet.posetMask);
                    info.setPosetComparator(triplet.comparator);
                    recipe.setOptionalMonotonicityInfo(info);
                }
            }

            RecipeTraceInfo trimTrace = new PlanningTrace(plan, projectedVariables, trimmerRecipe, compiledParent);
            recipe.setDeleteRederiveEvaluation(deleteRederiveEvaluation);
            recipe.getParents().add(trimmerRecipe);

            return new CompiledSubPlan(plan, projectedVariables, recipe, trimTrace);
        }
    }

    private CompiledSubPlan doCompileStart(PStart operation, SubPlan plan) {
        if (!operation.getAPrioriVariables().isEmpty()) {
            throw new IllegalArgumentException("Input variables unsupported by Rete: " + plan.toShortString());
        }
        final ConstantRecipe recipe = FACTORY.createConstantRecipe();
        recipe.getConstantValues().clear();

        return new CompiledSubPlan(plan, new ArrayList<PVariable>(), recipe);
    }

    private CompiledSubPlan doCompileEnumerate(EnumerablePConstraint constraint, SubPlan plan)
            throws QueryProcessingException {
        final PlanningTrace trimmedTrace = doEnumerateAndDeduplicate(constraint, plan);

        return trimmedTrace.cloneFor(plan);
    }

    private PlanningTrace doEnumerateAndDeduplicate(EnumerablePConstraint constraint, SubPlan plan)
            throws QueryProcessingException {
        final PlanningTrace coreTrace = doEnumerateDispatch(plan, constraint);
        final PlanningTrace trimmedTrace = CompilerHelper.checkAndTrimEqualVariables(plan, coreTrace);
        return trimmedTrace;
    }

    private PlanningTrace doEnumerateDispatch(SubPlan plan, EnumerablePConstraint constraint)
            throws QueryProcessingException {
        if (constraint instanceof BinaryTransitiveClosure) {
            return compileEnumerable(plan, (BinaryTransitiveClosure) constraint);
        } else if (constraint instanceof ConstantValue) {
            return compileEnumerable(plan, (ConstantValue) constraint);
            // } else if (constraint instanceof Containment) {
            // return compileEnumerable(plan, (Containment) constraint);
            // } else if (constraint instanceof Generalization) {
            // return compileEnumerable(plan, (Generalization) constraint);
            // } else if (constraint instanceof Instantiation) {
            // return compileEnumerable(plan, (Instantiation) constraint);
        } else if (constraint instanceof PositivePatternCall) {
            return compileEnumerable(plan, (PositivePatternCall) constraint);
        } else if (constraint instanceof TypeConstraint) {
            return compileEnumerable(plan, (TypeConstraint) constraint);
        }
        throw new UnsupportedOperationException("Unknown enumerable constraint " + constraint);
    }

    private PlanningTrace compileEnumerable(SubPlan plan, BinaryTransitiveClosure constraint)
            throws QueryProcessingException {
        final PQuery referredQuery = constraint.getSupplierKey();
        final PlanningTrace callTrace = referQuery(referredQuery, plan, constraint.getVariablesTuple());

        final TransitiveClosureRecipe recipe = FACTORY.createTransitiveClosureRecipe();
        recipe.setParent(callTrace.getRecipe());

        return new PlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe, callTrace);
    }

    private PlanningTrace compileEnumerable(SubPlan plan, PositivePatternCall constraint)
            throws QueryProcessingException {
        final PQuery referredQuery = constraint.getReferredQuery();
        return referQuery(referredQuery, plan, constraint.getVariablesTuple());
    }

    private PlanningTrace compileEnumerable(SubPlan plan, TypeConstraint constraint) {
        final IInputKey inputKey = constraint.getSupplierKey();
        final InputRecipe recipe = RecipesHelper.inputRecipe(inputKey, inputKey.getStringID(), inputKey.getArity());
        return new PlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
    }

    private PlanningTrace compileEnumerable(SubPlan plan, ConstantValue constraint) {
        final ConstantRecipe recipe = FACTORY.createConstantRecipe();
        recipe.getConstantValues().add(constraint.getSupplierKey());
        return new PlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
    }

    // TODO handle recursion
    private PlanningTrace referQuery(PQuery query, SubPlan plan, Tuple actualParametersTuple)
            throws QueryProcessingException {
        CompiledQuery compiledQuery = getCompiledForm(query);
        return new PlanningTrace(plan, CompilerHelper.convertVariablesTuple(actualParametersTuple),
                compiledQuery.getRecipe(), compiledQuery.getParentRecipeTraces());
    }

    protected List<CompiledSubPlan> getCompiledFormOfParents(SubPlan plan) throws QueryProcessingException {
        List<CompiledSubPlan> results = new ArrayList<CompiledSubPlan>();
        for (SubPlan parentPlan : plan.getParentPlans()) {
            results.add(getCompiledForm(parentPlan));
        }
        return results;
    }

    /**
     * Returns an unmodifiable view of currently cached compiled queries.
     */
    public Map<PQuery, CompiledQuery> getCachedCompiledQueries() {
        return Collections.unmodifiableMap(queryCompilerCache);
    }

    /**
     * Returns an unmodifiable view of currently cached query plans.
     */
    public Map<PBody, SubPlan> getCachedQueryPlans() {
        return Collections.unmodifiableMap(plannerCache);
    }

    private QueryEvaluationHint getHints(SubPlan plan) {
        return getHints(plan.getBody().getPattern());
    }

    private QueryEvaluationHint getHints(PQuery pattern) {
        return hintProvider.getQueryEvaluationHint(pattern);
    }
}