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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanDescriptor;
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
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameterDirection;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
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

    private IQueryRuntimeContext getRuntimeContext(){
        return ((LocalSearchBackend)backend).getRuntimeContext();
    }
    
    private IPlanDescriptor createPlan(MatcherReference key, IPlanProvider planProvider,
            final ISearchContext searchContext) throws QueryProcessingException {

        LocalSearchHints configuration = overrideDefaultHints(key.getQuery());
        
        IPlanDescriptor plan = planProvider.getPlan((LocalSearchBackend) backend, configuration, key);
        Collection<SearchPlanForBody> compiledPlans = Lists.newArrayList(plan.getPlan());

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
        return plan;
    }


    private LocalSearchHints overrideDefaultHints(PQuery pQuery) {
        return LocalSearchHints
           .parse(LocalSearchHints.getDefault().build().overrideBy(hintProvider.getQueryEvaluationHint(pQuery).overrideBy(userHints)));
    }

    private void collectDependencies(Iterable<SearchPlanForBody> compiledPlans, Set<MatcherReference> dependencies) {
        for (SearchPlanForBody plan : compiledPlans) {
            for (MatcherReference dependency : plan.getDependencies()) {
                dependencies.add(new MatcherReference(dependency.getQuery(), dependency.getAdornment(), userHints));
            }
        }
    }

    /**
     * @throws QueryProcessingException 
     * @since 1.4
     */
    public LocalSearchResultProvider(IQueryBackend backend, Logger logger, IQueryRuntimeContext runtimeContext,
            IQueryCacheContext cacheContext, IQueryBackendHintProvider hintProvider, PQuery query,
            IPlanProvider planProvider) throws QueryProcessingException {
        this(backend, logger, runtimeContext, cacheContext, hintProvider, query, planProvider, null);
    }

    private Iterator<MatcherReference> computeAllPossibleAdornments(final PQuery query, final QueryEvaluationHint hints){
        final Set<PParameter> ins = Sets.filter(Sets.newHashSet(query.getParameters()), PQueries.parameterDirectionPredicate(PParameterDirection.IN));
        Set<PParameter> inouts = Sets.filter(Sets.newHashSet(query.getParameters()), PQueries.parameterDirectionPredicate(PParameterDirection.IN));
        Set<Set<PParameter>> possibleInouts = Sets.powerSet(inouts);
        return Iterators.transform(possibleInouts.iterator(), new Function<Set<PParameter>, MatcherReference>() {

            @Override
            public MatcherReference apply(Set<PParameter> input) {
                Set<PParameter> adornment = Sets.union(ins, input);
                return new MatcherReference(query, adornment, hints);
            }
        });
    }
    
    /**
     * @throws QueryProcessingException 
     * @since 1.4
     */
    public LocalSearchResultProvider(IQueryBackend backend, Logger logger, IQueryRuntimeContext runtimeContext,
            IQueryCacheContext cacheContext, IQueryBackendHintProvider hintProvider,  PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) throws QueryProcessingException {
        this.backend = backend;
        this.cacheContext = cacheContext;
        this.hintProvider = hintProvider;
        this.query = query;

        this.planProvider = planProvider;
        this.userHints = userHints;
        
        // Plan for possible adornments
        Iterator<MatcherReference> iterator = computeAllPossibleAdornments(query, userHints);
        while(iterator.hasNext()){
            planProvider.getPlan((LocalSearchBackend) backend, overrideDefaultHints(query), iterator.next());
        }
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

        Set<IInputKey> iteratedKeys = Collections.emptySet();

        final Set<PParameter> adornment = Sets.newHashSet();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                adornment.add(query.getParameters().get(i));
            }
        }

        final MatcherReference reference = new MatcherReference(query, adornment, userHints);
        Set<MatcherReference> dependencies = Sets.newHashSet(reference);
        Set<MatcherReference> processedDependencies = Sets.newHashSet();
        Set<MatcherReference> todo = Sets.difference(dependencies, processedDependencies);

        while (!todo.isEmpty()) {
            final MatcherReference dependency = todo.iterator().next();
            IPlanDescriptor plan = createPlan(dependency, planProvider, searchContext);
            if (overrideDefaultHints(dependency.getQuery()).isUseBase()){
                iteratedKeys = Sets.union(iteratedKeys, plan.getIteratedKeys());
            }
            collectDependencies(plan.getPlan(), dependencies);
            processedDependencies.add(dependency);
        }

        try {
            indexKeys(iteratedKeys);
        } catch (InvocationTargetException e) {
            throw new ViatraQueryException("Could not index keys","Could not index keys", e);
        }
        return searchContext.getMatcher(reference);
    }
    
    private void indexKeys(final Iterable<IInputKey> keys) throws InvocationTargetException{
        final IQueryRuntimeContext qrc = getRuntimeContext();
        qrc.coalesceTraversals(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for(IInputKey key : keys){
                    qrc.ensureIndexed(key, IndexingService.INSTANCES);
                }
                return null;
            }
        });
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
        LocalSearchHints configuration = overrideDefaultHints(query);
        return configuration;
    }
    
}
