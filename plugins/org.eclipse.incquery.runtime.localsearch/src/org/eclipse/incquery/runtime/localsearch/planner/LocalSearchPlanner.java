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

import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PQueryFlattener;

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
    private IQueryPlannerStrategy plannerStrategy;
    private IPatternMatcherContext context;
    private PBodyNormalizer normalizer;
    private POperationCompiler operationCompiler;
    private ISearchPlanCodeGenerator codeGenerator;

    public void initializePlanner(PQueryFlattener pQueryFlattener, IPatternMatcherContext context,
            PBodyNormalizer pBodyNormalizer, IQueryPlannerStrategy localSearchPlannerStrategy,
            POperationCompiler pOperationCompiler) {
        initializePlanner(pQueryFlattener, context, pBodyNormalizer, localSearchPlannerStrategy, pOperationCompiler, null);
    }

    public void initializePlanner(PQueryFlattener pQueryFlattener, IPatternMatcherContext context,
            PBodyNormalizer pBodyNormalizer, IQueryPlannerStrategy localSearchPlannerStrategy,
            POperationCompiler pOperationCompiler, ISearchPlanCodeGenerator codeGenerator) {
        this.flattener = pQueryFlattener;
        this.context = context;
        this.normalizer = pBodyNormalizer;
        this.plannerStrategy = localSearchPlannerStrategy;
        this.operationCompiler = pOperationCompiler;
        this.codeGenerator = codeGenerator;
    }

    /**
     * Creates executable plans for the provided query. It is required to call one of the
     * <code>initializePlanner()</code> methods before calling this method.
     * 
     * @param boundVarIndices
     * @throws QueryProcessingException
     */
    public Map<List<ISearchOperation>, Map<PVariable, Integer>> plan(PQuery querySpec, Set<Integer> boundVarIndices)
            throws QueryProcessingException {

        // Flatten
        flatDisjunction = flattener.rewrite(querySpec.getDisjunctBodies());
        Set<PBody> flatBodies = flatDisjunction.getBodies();
        prepareFlatBodesForNormalize(flatBodies);

        // Normalize
        normalizedDisjunction = normalizer.rewrite(flatDisjunction);
        Set<PBody> normalizedBodies = normalizedDisjunction.getBodies();

        // Create plans for normalized bodies
        // Context has matchers for the referred Queries (IQuerySpecifications)
        plansForBodies = Lists.newArrayList();

        for (PBody normalizedBody : normalizedBodies) {
            preparePatternAdornmentForPlanner(boundVarIndices, normalizedBody);
            SubPlan plan = plannerStrategy.plan(normalizedBody, context);
            plansForBodies.add(plan);
        }

        // Compile (from POperations to ISearchOperations)
        Map<List<ISearchOperation>,Map<PVariable, Integer>> compiledSubPlans = Maps.newHashMap();
        // TODO finish (revisit?) the implementation of the compile function
        // Pay extra caution to extend operations, when more than one variables are unbound
        for (SubPlan subPlan : plansForBodies) {
            List<ISearchOperation> compiledOperations = operationCompiler.compile(subPlan, boundVarIndices);
            // Store the variable mappings for the plans for debug purposes (traceability information)
			compiledSubPlans.put(compiledOperations,operationCompiler.getVariableMappings());
        }

        // Generate code if generator is provided
		// TODO there is no code generator implementation yet
		// if (codeGenerator != null) {
		// 		codeGenerator.compile(compiledSubPlans);
		// }

        return compiledSubPlans;
    }

    private void prepareFlatBodesForNormalize(Set<PBody> flatBodies) {
        // Revert status to be able to rewrite
        // XXX Needed because the current implementation of the Normalizer requires mutable bodies
        for (PBody pBody : flatBodies) {
            pBody.setStatus(PQueryStatus.UNINITIALIZED);
        }
    }

    private void preparePatternAdornmentForPlanner(Set<Integer> boundVarIndices, PBody normalizedBody) {
        Set<PVariable> boundVariables = Sets.<PVariable> newHashSet();
        for (Integer i : boundVarIndices) {
            boundVariables.add(normalizedBody.getSymbolicParameterVariables().get(i));
        }
        ((LocalSearchPlannerStrategy) plannerStrategy).setBoundVariables(boundVariables);
    }
    
}
