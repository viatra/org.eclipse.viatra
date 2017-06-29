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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
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
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryResultProviderAccess;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;
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

    private final LocalSearchBackend backend;
    private final IQueryBackendHintProvider hintProvider;
    private final AdvancedViatraQueryEngine engine;
    private final IQueryRuntimeContext runtimeContext;
    private final PQuery query;
    private final IQueryResultProviderAccess resultProviderAccess;
    private final QueryEvaluationHint userHints;

    private final IPlanProvider planProvider;
    private final ISearchContext searchContext;

    private IQueryRuntimeContext getRuntimeContext(){
        return ((LocalSearchBackend)backend).getRuntimeContext();
    }
    
    private LocalSearchMatcher createMatcher(IPlanDescriptor plan, final ISearchContext searchContext){
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

        return new LocalSearchMatcher(plan, executors,
                Collections.max(parameterSizes));
    }
    
    private IPlanDescriptor createPlan(MatcherReference key, IPlanProvider planProvider) throws QueryProcessingException {
        LocalSearchHints configuration = overrideDefaultHints(key.getQuery());
        IPlanDescriptor plan = planProvider.getPlan(backend.getBackendContext(), configuration, key);
        return plan;
    }


    private LocalSearchHints overrideDefaultHints(PQuery pQuery) {
        return LocalSearchHints.getDefaultOverriddenBy(
                computeOverridingHints(pQuery));
    }

    /** 
     * Combine with {@link QueryHintOption#getValueOrDefault(QueryEvaluationHint)} to access 
     *  hint settings not covered by {@link LocalSearchHints} 
     */
    private QueryEvaluationHint computeOverridingHints(PQuery pQuery) {
        return hintProvider.getQueryEvaluationHint(pQuery).overrideBy(userHints);
    }

    /**
     * @throws QueryProcessingException 
     * @since 1.5
     */
    public LocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider) throws QueryProcessingException {
        this(backend, context, query, planProvider, null);
    }

    private Iterator<MatcherReference> computeExpectedAdornments(){
        return Iterators.transform(overrideDefaultHints(query).getAdornmentProvider().getAdornments(query).iterator(), new Function<Set<PParameter>, MatcherReference>() {

            @Override
            public MatcherReference apply(Set<PParameter> input) {
                return new MatcherReference(query, input, userHints);
            }
        });
    }
    
    /**
     * @throws QueryProcessingException 
     * @since 1.5
     */
    public LocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) throws QueryProcessingException {
        this.backend = backend;
        this.resultProviderAccess = context.getResultProviderAccess();
        this.hintProvider = context.getHintProvider();
        // XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
        engine = (AdvancedViatraQueryEngine) hintProvider;
        this.query = query;

        this.planProvider = planProvider;
        this.userHints = userHints;
        this.runtimeContext = context.getRuntimeContext();
        
        try {
            searchContext = new ISearchContext.SearchContext(context, engine.getBaseIndex(), userHints, backend.getCache());
        } catch (ViatraQueryException e) {
            throw new QueryProcessingException("Could not create search context for {1}", new String[]{query.getFullyQualifiedName()}, e.getMessage(), query, e);
        }
    }
    
    /**
     * Prepare this result provider. This phase is separated from the constructor to allow the backend to cache its instance before
     * requesting preparation for its dependencies.
     * @since 1.5
     */
    public void prepare() throws QueryProcessingException{
        try {
            runtimeContext.coalesceTraversals(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    runtimeContext.ensureWildcardIndexing(IndexingService.STATISTICS);
                    prepareDirectDependencies();
                    runtimeContext.executeAfterTraversal(new Runnable() {
                        
                        @Override
                        public void run() {
                            try {
                                preparePlansForExpectedAdornments();
                            } catch (QueryProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    return null;
                }
            });
        } catch (InvocationTargetException e) {
            throw new QueryProcessingException("Error while building required indexes: %s", new String[]{e.getTargetException().getMessage()}, "Error while building required indexes.", query, e);
        }
    }
    
    private void preparePlansForExpectedAdornments() throws QueryProcessingException{
     // Plan for possible adornments
        Iterator<MatcherReference> iterator = computeExpectedAdornments();
        while(iterator.hasNext()){
            IPlanDescriptor plan = planProvider.getPlan(backend.getBackendContext(), overrideDefaultHints(query), iterator.next());
            // Index keys
            try {
                indexKeys(plan.getIteratedKeys());
            } catch (InvocationTargetException e) {
                throw new QueryProcessingException(e.getMessage(), null, e.getMessage(), query, e);
            }
            //Prepare dependencies
            for(SearchPlanForBody body: plan.getPlan()){
                for(MatcherReference dependency : body.getDependencies()){
                    try {
                        searchContext.getMatcher(dependency);
                    } catch (LocalSearchException e) {
                        throw new QueryProcessingException("Could not prepare dependency {1}", new String[]{dependency.toString()}, e.getMessage(), query, e);
                    }
                }
            }
        }

    }
    
    private void prepareDirectDependencies() throws QueryProcessingException{
        // Do not prepare for any adornment at this point
        IAdornmentProvider adornmentProvider = new IAdornmentProvider() {
            
            @Override
            public Iterable<Set<PParameter>> getAdornments(PQuery query) {
                return Collections.emptySet();
            }
        };
        @SuppressWarnings("rawtypes")
        QueryEvaluationHint hints = new QueryEvaluationHint(Collections.<QueryHintOption, Object>singletonMap(LocalSearchHintOptions.ADORNMENT_PROVIDER, adornmentProvider), null);
        for(PQuery dep : getDirectPositiveDependencies()){
            resultProviderAccess.getResultProvider(dep, hints);
        }
    }
    
    private Set<PQuery> getDirectPositiveDependencies(){
        IFlattenCallPredicate flattenPredicate = overrideDefaultHints(query).getFlattenCallPredicate();
        Queue<PQuery> queue = new LinkedList<PQuery>();
        Set<PQuery> visited = new HashSet<PQuery>();
        Set<PQuery> result = new HashSet<PQuery>();
        queue.add(query);
        
        while(!queue.isEmpty()){
            PQuery next = queue.poll();
            visited.add(next);
            for(PBody body : next.getDisjunctBodies().getBodies()){
                for(PositivePatternCall ppc : body.getConstraintsOfType(PositivePatternCall.class)){
                    PQuery dep = ppc.getSupplierKey();
                    if (flattenPredicate.shouldFlatten(ppc)){
                        if (!visited.contains(dep)){
                            queue.add(dep);
                        }
                    }else{
                        result.add(dep);
                    }
                }
            }
        }
        return result;
    }

    private LocalSearchMatcher initializeMatcher(Object[] parameters) {
        try {
            return newLocalSearchMatcher(parameters);
        } catch (QueryProcessingException | ViatraQueryException e) {
            throw new RuntimeException(e);
        }

    }

    public LocalSearchMatcher newLocalSearchMatcher(Object[] parameters)
            throws ViatraQueryException, QueryProcessingException {

        final Set<PParameter> adornment = Sets.newHashSet();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                adornment.add(query.getParameters().get(i));
            }
        }

        final MatcherReference reference = new MatcherReference(query, adornment, userHints);
        
        IPlanDescriptor plan = createPlan(reference, planProvider);
        if (overrideDefaultHints(reference.getQuery()).isUseBase()){
            try {
                indexKeys(plan.getIteratedKeys());
            } catch (InvocationTargetException e) {
                throw new ViatraQueryException("Could not index keys","Could not index keys", e);
            }
        }
        
        LocalSearchMatcher matcher = createMatcher(plan, searchContext);
        matcher.addAdapters(backend.getAdapters());
        return matcher;
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
