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
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PDisjunctionRewriter;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PDisjunctionRewriterCacher;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PQueryFlattener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 */
public class LocalSearchPlanner {

    // Externally set tools for planning
    private final PDisjunctionRewriter preprocessor;
    private final LocalSearchRuntimeBasedStrategy plannerStrategy;
    private final IQueryRuntimeContext runtimeContext;
    private final LocalSearchHints configuration;
    private final IQueryBackendContext context;
//    private final LocalSearchBackend backend;

    /**
     * @since 1.4
     * @deprecated
     */
    @Deprecated
    public LocalSearchPlanner(LocalSearchBackend backend, Logger logger, final LocalSearchHints configuration) {
        this(backend.getBackendContext(), logger, configuration);
    }
    
    /**
     * @since 1.7
     */
    public LocalSearchPlanner(IQueryBackendContext backendContext, Logger logger, final LocalSearchHints configuration) {

        this.runtimeContext = backendContext.getRuntimeContext();
        this.configuration = configuration;
        PQueryFlattener flattener = new PQueryFlattener(configuration.getFlattenCallPredicate());
        /*
         * TODO https://bugs.eclipse.org/bugs/show_bug.cgi?id=439358: The normalizer is initialized with the false
         * parameter to turn off unary constraint elimination to work around an issue related to plan ordering: the
         * current implementation of the feature target checking operations expect that the source types were checked
         * before. However, this causes duplicate constraint checks in the search plan that might affect performance
         * negatively.
         */
        PBodyNormalizer normalizer = new PBodyNormalizer(runtimeContext.getMetaContext()) {
            
            @Override
            protected boolean shouldCalculateImpliedTypes(PQuery query) {
                return false;
            }
        };
        preprocessor = new PDisjunctionRewriterCacher(flattener, normalizer);

        plannerStrategy = new LocalSearchRuntimeBasedStrategy();

        context = backendContext;
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
        preprocessor.setTraceCollector(configuration.getTraceCollector());
        Set<PBody> normalizedBodies = preprocessor.rewrite(querySpec.getDisjunctBodies()).getBodies();

        List<SearchPlanForBody> plansForBodies = Lists.newArrayListWithExpectedSize(normalizedBodies.size());

        for (PBody normalizedBody : normalizedBodies) {
            // 2. Plan creation
            // Context has matchers for the referred Queries (IQuerySpecifications)
            Set<PVariable> boundVariables = calculatePatternAdornmentForPlanner(boundParameters, normalizedBody);
            SubPlan plan = plannerStrategy.plan(normalizedBody, boundVariables, context, configuration);
            // 3. PConstraint -> POperation compilation step
            // * Pay extra caution to extend operations, when more than one variables are unbound
            POperationCompiler operationCompiler = new POperationCompiler(runtimeContext, configuration.isUseBase());
            List<ISearchOperation> compiledOperations = operationCompiler.compile(plan, boundParameters);
            // Store the variable mappings for the plans for debug purposes (traceability information)
            SearchPlanForBody compiledPlan = new SearchPlanForBody(normalizedBody,
                    operationCompiler.getVariableMappings(), plan, compiledOperations,
                    operationCompiler.getDependencies());

            plansForBodies.add(compiledPlan);
        }

        return plansForBodies;
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
