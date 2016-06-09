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
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PQueryFlattener;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.RewriterException;

import com.google.common.base.Function;
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
    private List<SearchPlanForBody> plansForBodies;

    public PDisjunction getFlatDisjunction() {
        return flatDisjunction;
    }

    public PDisjunction getNormalizedDisjunction() {
        return normalizedDisjunction;
    }

    public List<SubPlan> getPlansForBodies() {
        return Lists.transform(plansForBodies, new Function<SearchPlanForBody, SubPlan>() {

            @Override
            public SubPlan apply(SearchPlanForBody input) {
                return input.getPlan();
            }
        });
    }

    // Externally set tools for planning
    private PQueryFlattener flattener;
    private LocalSearchRuntimeBasedStrategy plannerStrategy;
    private PBodyNormalizer normalizer;
    private POperationCompiler operationCompiler;
    private Logger logger;
    private IQueryMetaContext metaContext;
    private IQueryRuntimeContext runtimeContext;
    private Map<String, Object> hints;

    public void initializePlanner(PQueryFlattener pQueryFlattener, Logger logger, IQueryMetaContext metaContext, IQueryRuntimeContext runtimeContext,
            PBodyNormalizer pBodyNormalizer, LocalSearchRuntimeBasedStrategy localSearchPlannerStrategy,
            POperationCompiler pOperationCompiler, Map<String, Object> hints) {
        this.flattener = pQueryFlattener;
        this.logger = logger;
        this.metaContext = metaContext;
        this.runtimeContext = runtimeContext;
        this.normalizer = pBodyNormalizer;
        this.plannerStrategy = localSearchPlannerStrategy;
        this.operationCompiler = pOperationCompiler;
        this.hints = hints;
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
    public Collection<SearchPlanForBody> plan(PQuery querySpec, Set<Integer> boundVarIndices)
            throws QueryProcessingException {

        // 1. Preparation
        Set<PBody> normalizedBodies = prepareNormalizedBodies(querySpec);

        plansForBodies = Lists.newArrayListWithExpectedSize(normalizedBodies.size());

        for (PBody normalizedBody : normalizedBodies) {
            // 2. Plan creation
            // Context has matchers for the referred Queries (IQuerySpecifications)
            Set<PVariable> boundVariables = calculatePatternAdornmentForPlanner(boundVarIndices, normalizedBody);
            SubPlan plan = plannerStrategy.plan(normalizedBody, logger, boundVariables, metaContext, runtimeContext, hints);
            // 3. PConstraint -> POperation compilation step
            // TODO finish (revisit?) the implementation of the compile function
            // * Pay extra caution to extend operations, when more than one variables are unbound
            List<ISearchOperation> compiledOperations = operationCompiler.compile(plan, boundVarIndices);
            // Store the variable mappings for the plans for debug purposes (traceability information)
            SearchPlanForBody compiledPlan = new SearchPlanForBody(normalizedBody, operationCompiler.getVariableMappings(), plan, compiledOperations);
            
            plansForBodies.add(compiledPlan);
        }

        return plansForBodies;
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
        
        removeDuplicateConstraints(normalizedBodies);
        
        return normalizedBodies;
    }

    private void removeDuplicateConstraints(Set<PBody> normalizedBodies) {
        for (PBody pBody : normalizedBodies) {
            pBody.setStatus(PQueryStatus.UNINITIALIZED);
            
            Map<String, PConstraint> constraints = Maps.newHashMap();
            for(PConstraint constraint : pBody.getConstraints()){
                String key = constraint.toString();
                // Retain first found instance of a constraint
                if (!constraints.containsKey(key)){
                    constraints.put(key, constraint);
                }
            }
            
            // Retain collected constraints, remove everything else
            pBody.getConstraints().retainAll(constraints.values());
            pBody.setStatus(PQueryStatus.OK);
        }
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
