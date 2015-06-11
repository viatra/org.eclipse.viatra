/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.planner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PQueryFlattener;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.RewriterException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchPlanner {

    // Fields to track and debug the workflow
    // Internal data
    private PDisjunction flatDisjunction;
    private PDisjunction normalizedDisjunction;
    private List<SubPlan> plansForBodies;

    public PDisjunction getFlatDisjunction() {
        return flatDisjunction;
    }

    public PDisjunction getNormalizedDisjunction() {
        return normalizedDisjunction;
    }

    public List<SubPlan> getPlansForBodies() {
        return plansForBodies;
    }

    // Externally set tools for planning
    private PQueryFlattener flattener;
    private LocalSearchRuntimeBasedStrategy plannerStrategy;
    private PBodyNormalizer normalizer;
    private POperationCompiler operationCompiler;
    private Logger logger;
    private IQueryMetaContext metaContext;
    private IQueryRuntimeContext runtimeContext;

    public void initializePlanner(PQueryFlattener pQueryFlattener, Logger logger, IQueryMetaContext metaContext, IQueryRuntimeContext runtimeContext,
            PBodyNormalizer pBodyNormalizer, LocalSearchRuntimeBasedStrategy localSearchPlannerStrategy,
            POperationCompiler pOperationCompiler) {
        this.flattener = pQueryFlattener;
        this.logger = logger;
        this.metaContext = metaContext;
        this.runtimeContext = runtimeContext;
        this.normalizer = pBodyNormalizer;
        this.plannerStrategy = localSearchPlannerStrategy;
        this.operationCompiler = pOperationCompiler;
    }

    /**
     * Creates executable plans for the provided query. It is required to call one of the
     * <code>initializePlanner()</code> methods before calling this method.
     * 
     * @param querySpec
     * @param boundVarIndices
     *            a set of integers representing the variables that are bound
     * @return a mapping between ISearchOperation list and a mapping, that holds a PVariable-Integer mapping for the
     *         list of ISearchOperations
     * @throws QueryProcessingException
     */
    public Map<List<ISearchOperation>, Map<PVariable, Integer>> plan(PQuery querySpec, Set<Integer> boundVarIndices)
            throws QueryProcessingException {

        // 1. Preparation
        Set<PBody> normalizedBodies = prepareNormalizedBodies(querySpec);

        // 2. Plan creation
        // Context has matchers for the referred Queries (IQuerySpecifications)
        plansForBodies = Lists.newArrayList();

        for (PBody normalizedBody : normalizedBodies) {
            Set<PVariable> boundVariables = calculatePatternAdornmentForPlanner(boundVarIndices, normalizedBody);
            SubPlan plan = plannerStrategy.plan(normalizedBody, logger, boundVariables, metaContext, runtimeContext);
            plansForBodies.add(plan);
        }

        // 3. PConstraint -> POperation compilation step
        Map<List<ISearchOperation>, Map<PVariable, Integer>> compiledSubPlans = Maps.newHashMap();
        // TODO finish (revisit?) the implementation of the compile function
        // * Pay extra caution to extend operations, when more than one variables are unbound
        for (SubPlan subPlan : plansForBodies) {
            List<ISearchOperation> compiledOperations = operationCompiler.compile(subPlan, boundVarIndices);
            // Store the variable mappings for the plans for debug purposes (traceability information)
            compiledSubPlans.put(compiledOperations, operationCompiler.getVariableMappings());
        }

        return compiledSubPlans;
    }

    private Set<PBody> prepareNormalizedBodies(PQuery querySpec) throws RewriterException {
        // Preparation steps
        // Flatten
        flatDisjunction = flattener.rewrite(querySpec.getDisjunctBodies());
        Set<PBody> flatBodies = flatDisjunction.getBodies();
        prepareFlatBodiesForNormalize(flatBodies);

        // Normalize
        normalizedDisjunction = normalizer.rewrite(flatDisjunction);
        Set<PBody> normalizedBodies = normalizedDisjunction.getBodies();
        return normalizedBodies;
    }

    private void prepareFlatBodiesForNormalize(Set<PBody> flatBodies) {
        // Revert status to be able to rewrite
        // XXX Needed because the current implementation of the Normalizer requires mutable bodies
        for (PBody pBody : flatBodies) {
            pBody.setStatus(PQueryStatus.UNINITIALIZED);
        }
    }

    private Set<PVariable> calculatePatternAdornmentForPlanner(Set<Integer> boundVarIndices, PBody normalizedBody) {
        Set<PVariable> boundVariables = Sets.<PVariable> newHashSet();
        for (Integer i : boundVarIndices) {
            boundVariables.add(normalizedBody.getSymbolicParameterVariables().get(i));
        }
        return boundVariables;
    }

}
