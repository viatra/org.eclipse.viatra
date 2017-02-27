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
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
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
 * @noreference This class is not intended to be referenced by clients.
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
    private final PQueryFlattener flattener;
    private final LocalSearchRuntimeBasedStrategy plannerStrategy;
    private final PBodyNormalizer normalizer;
    private final IQueryRuntimeContext runtimeContext;
    private final LocalSearchHints configuration;
    private final IQueryBackendContext context;
    private final LocalSearchBackend backend;

    /**
     * @since 1.4
     */
    public LocalSearchPlanner(LocalSearchBackend backend, Logger logger, final LocalSearchHints configuration) {

        this.runtimeContext = backend.getRuntimeContext();
        this.configuration = configuration;
        flattener = new PQueryFlattener(configuration.getFlattenCallPredicate());
        /*
         * TODO https://bugs.eclipse.org/bugs/show_bug.cgi?id=439358: The normalizer is initialized with the false
         * parameter to turn off unary constraint elimination to work around an issue related to plan ordering: the
         * current implementation of the feature target checking operations expect that the source types were checked
         * before. However, this causes duplicate constraint checks in the search plan that might affect performance
         * negatively.
         */
        normalizer = new PBodyNormalizer(runtimeContext.getMetaContext()) {
            
            @Override
            protected boolean shouldCalculateImpliedTypes(PQuery query) {
                return false;
            }
        };

        plannerStrategy = new LocalSearchRuntimeBasedStrategy();
        this.backend = backend;

        context = backend.getBackendContext();
    }

    /**
     * Creates executable plans for the provided query. It is required to call one of the
     * <code>initializePlanner()</code> methods before calling this method.
     * 
     * @param querySpec
     * @param boundParameters
     *            a set of bound parameters
     * @return a mapping between ISearchOperation list and a mapping, that holds a PVariable-Integer mapping for the
     *         list of ISearchOperations
     * @throws QueryProcessingException
     */
    public Collection<SearchPlanForBody> plan(PQuery querySpec, Set<PParameter> boundParameters)
            throws QueryProcessingException {
        // 1. Preparation
        Set<PBody> normalizedBodies = prepareNormalizedBodies(querySpec);

        plansForBodies = Lists.newArrayListWithExpectedSize(normalizedBodies.size());

        for (PBody normalizedBody : normalizedBodies) {
            // 2. Plan creation
            // Context has matchers for the referred Queries (IQuerySpecifications)
            Set<PVariable> boundVariables = calculatePatternAdornmentForPlanner(boundParameters, normalizedBody);
            SubPlan plan = plannerStrategy.plan(normalizedBody, boundVariables, context, configuration);
            // 3. PConstraint -> POperation compilation step
            // * Pay extra caution to extend operations, when more than one variables are unbound
            POperationCompiler operationCompiler = new POperationCompiler(runtimeContext, backend,
                    configuration.isUseBase());
            List<ISearchOperation> compiledOperations = operationCompiler.compile(plan, boundParameters);
            // Store the variable mappings for the plans for debug purposes (traceability information)
            SearchPlanForBody compiledPlan = new SearchPlanForBody(normalizedBody,
                    operationCompiler.getVariableMappings(), plan, compiledOperations,
                    operationCompiler.getDependencies());

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

    private Object getConstraintKey(PConstraint constraint) {
        if (constraint instanceof TypeConstraint) {
            return ((TypeConstraint) constraint).getEquivalentJudgement();
        }
        // Do not check duplication for any other types
        return constraint;
    }

    private void removeDuplicateConstraints(Set<PBody> normalizedBodies) {
        for (PBody pBody : normalizedBodies) {
            pBody.setStatus(PQueryStatus.UNINITIALIZED);

            Map<Object, PConstraint> constraints = Maps.newHashMap();
            for (PConstraint constraint : pBody.getConstraints()) {
                Object key = getConstraintKey(constraint);
                // Retain first found instance of a constraint
                if (!constraints.containsKey(key)) {
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

    private Set<PVariable> calculatePatternAdornmentForPlanner(Set<PParameter> boundParameters, PBody normalizedBody) {
        Map<PParameter, PVariable> parameterMapping = Maps.newHashMap();
        for (ExportedParameter constraint : normalizedBody.getSymbolicParameters()) {
            parameterMapping.put(constraint.getPatternParameter(), constraint.getParameterVariable());
        }
        Set<PVariable> boundVariables = Sets.newHashSet();
        for (PParameter parameter : boundParameters) {
            PVariable mappedParameter = parameterMapping.get(parameter);
            if (mappedParameter == null) {
                // XXX In case of older (pre-1.4) VIATRA versions, PParameters were not stable, see bug 498348
                mappedParameter = normalizedBody.getVariableByNameChecked(parameter.getName());
            }
            boundVariables.add(mappedParameter);
        }
        return boundVariables;
    }

}
