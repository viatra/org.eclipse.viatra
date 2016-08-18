/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendToEStructuralFeatureSource;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverEClassInstances;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverEDatatypeInstances;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverEStructuralFeatureInstances;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanProvider;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IUpdateable;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchResultProvider implements IQueryResultProvider {

    private final IQueryBackend backend;
    private final IQueryBackendHintProvider hintProvider;
    private final PQuery query;
    private IQueryCacheContext cacheContext;
    private final QueryEvaluationHint userHints;

    private final IPlanProvider planProvider;

    private Collection<SearchPlanForBody> createPlan(MatcherReference key, IPlanProvider planProvider,
            final ISearchContext searchContext) throws QueryProcessingException {

        LocalSearchHints configuration = LocalSearchHints
                .parse(hintProvider.getQueryEvaluationHint(key.getQuery()).overrideBy(userHints));

        Collection<SearchPlanForBody> compiledPlans = Lists
                .newArrayList(planProvider.getPlan((LocalSearchBackend) backend, configuration, key).getPlan());

        Collection<SearchPlanExecutor> executors = Collections2.transform(compiledPlans,
                new Function<SearchPlanForBody, SearchPlanExecutor>() {

                    @Override
                    public SearchPlanExecutor apply(SearchPlanForBody input) {
                        final SearchPlan plan = new SearchPlan();
                        plan.addOperations(input.getCompiledOperations());

                        return new SearchPlanExecutor(plan, searchContext, input.getVariableKeys());
                    }
                });

        final Collection<Integer> parameterSizes = Collections2.transform(compiledPlans,
                new Function<SearchPlanForBody, Integer>() {

                    @Override
                    public Integer apply(SearchPlanForBody input) {
                        PBody body = input.getBody();
                        return body.getUniqueVariables().size();
                        // return Math.max(input.getSymbolicParameters().size(), input.getUniqueVariables().size());
                    }
                });

        final LocalSearchMatcher matcher = new LocalSearchMatcher(key.getQuery(), executors,
                Collections.max(parameterSizes));
        searchContext.loadMatcher(key, matcher);
        return compiledPlans;
    }

    private void collectElementsToIndex(Collection<SearchPlanForBody> compiledPlans, Set<EClass> classesToIndex,
            Set<EStructuralFeature> featuresToIndex, Set<EDataType> dataTypesToIndex) {
        for (SearchPlanForBody plan : compiledPlans) {
            for (ISearchOperation operation : plan.getCompiledOperations()) {
                if (operation instanceof ExtendToEStructuralFeatureSource) {
                    featuresToIndex.add(((ExtendToEStructuralFeatureSource) operation).getFeature());
                } else if (operation instanceof IterateOverEClassInstances) {
                    classesToIndex.add(((IterateOverEClassInstances) operation).getClazz());
                } else if (operation instanceof IterateOverEDatatypeInstances) {
                    dataTypesToIndex.add(((IterateOverEDatatypeInstances) operation).getDataType());
                } else if (operation instanceof IterateOverEStructuralFeatureInstances) {
                    featuresToIndex.add(((IterateOverEStructuralFeatureInstances) operation).getFeature());
                } else {
                    // No indexing required
                }
            }
        }

    }

    private void collectDependencies(Collection<SearchPlanForBody> compiledPlans, Set<MatcherReference> dependencies) {
        for (SearchPlanForBody plan : compiledPlans) {
            for (MatcherReference dependency : plan.getDependencies()) {
                dependencies.add(new MatcherReference(dependency.getQuery(), dependency.getAdornment(), userHints));
            }
        }
    }

    /**
     * @since 1.4
     */
    public LocalSearchResultProvider(IQueryBackend backend, Logger logger, IQueryRuntimeContext runtimeContext,
            IQueryCacheContext cacheContext, IQueryBackendHintProvider hintProvider, PQuery query,
            IPlanProvider planProvider) {
        this(backend, logger, runtimeContext, cacheContext, hintProvider, query, planProvider, null);
    }

    /**
     * @since 1.4
     */
    public LocalSearchResultProvider(IQueryBackend backend, Logger logger, IQueryRuntimeContext runtimeContext,
            IQueryCacheContext cacheContext, IQueryBackendHintProvider hintProvider, PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) {
        this.backend = backend;
        this.cacheContext = cacheContext;
        this.hintProvider = hintProvider;
        this.query = query;

        this.planProvider = planProvider;
        this.userHints = userHints;
    }

    private LocalSearchMatcher initializeMatcher(Object[] parameters) {
        try {
            return newLocalSearchMatcher(parameters);
        } catch (QueryProcessingException e) {
            throw new RuntimeException(e);
        } catch (ViatraQueryException e) {
            throw new RuntimeException(e);
        }

    }

    public LocalSearchMatcher newLocalSearchMatcher(Object[] parameters)
            throws ViatraQueryException, QueryProcessingException {
        // XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
        ViatraQueryEngine engine = (ViatraQueryEngine) hintProvider;

        final ISearchContext searchContext = new ISearchContext.SearchContext(engine.getBaseIndex());

        Set<EClass> classesToIndex = Sets.newHashSet();
        Set<EStructuralFeature> featuresToIndex = Sets.newHashSet();
        Set<EDataType> dataTypesToIndex = Sets.newHashSet();

        final Set<PParameter> adornment = Sets.newHashSet();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                adornment.add(query.getParameters().get(i));
            }
        }

        final MatcherReference reference = new MatcherReference(query, adornment);
        Set<MatcherReference> dependencies = Sets.newHashSet(reference);
        Set<MatcherReference> processedDependencies = Sets.newHashSet();
        Set<MatcherReference> todo = Sets.difference(dependencies, processedDependencies);

        while (!todo.isEmpty()) {
            final MatcherReference dependency = todo.iterator().next();
            Collection<SearchPlanForBody> compiledPlans = createPlan(dependency, planProvider, searchContext);
            collectElementsToIndex(compiledPlans, classesToIndex, featuresToIndex, dataTypesToIndex);
            collectDependencies(compiledPlans, dependencies);
            processedDependencies.add(dependency);
        }

        searchContext.registerObservedTypes(classesToIndex, dataTypesToIndex, featuresToIndex);
        return searchContext.getMatcher(reference);
    }

    @Override
    public Tuple getOneArbitraryMatch(Object[] parameters) {
        try {
            final LocalSearchMatcher matcher = initializeMatcher(parameters);
            final MatchingFrame frame = matcher.editableMatchingFrame();
            frame.setParameterValues(parameters);
            return matcher.getOneArbitraryMatch(frame);
        } catch (LocalSearchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countMatches(Object[] parameters) {
        try {
            final LocalSearchMatcher matcher = initializeMatcher(parameters);
            final MatchingFrame frame = matcher.editableMatchingFrame();
            frame.setParameterValues(parameters);
            return matcher.countMatches(frame);
        } catch (LocalSearchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<? extends Tuple> getAllMatches(Object[] parameters) {
        try {
            final LocalSearchMatcher matcher = initializeMatcher(parameters);
            final MatchingFrame frame = matcher.editableMatchingFrame();
            frame.setParameterValues(parameters);
            return matcher.getAllMatches(frame);
        } catch (LocalSearchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IQueryBackend getQueryBackend() {
        return backend;
    }

    @Override
    public void addUpdateListener(IUpdateable listener, Object listenerTag, boolean fireNow) {
        // throw new UnsupportedOperationException(UPDATE_LISTENER_NOT_SUPPORTED);
    }

    @Override
    public void removeUpdateListener(Object listenerTag) {
        // throw new UnsupportedOperationException(UPDATE_LISTENER_NOT_SUPPORTED);
    }

    /**
     * @since 1.4
     */
    public IMatcherCapability getCapabilites() {
        return hintProvider.getQueryEvaluationHint(query).overrideBy(userHints).calculateRequiredCapability(query);
    }
    
}
