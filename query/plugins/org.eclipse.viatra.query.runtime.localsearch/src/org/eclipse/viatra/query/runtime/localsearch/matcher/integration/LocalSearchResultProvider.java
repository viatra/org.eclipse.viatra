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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
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
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.localsearch.planner.LocalSearchPlanner;
import org.eclipse.viatra.query.runtime.localsearch.planner.LocalSearchRuntimeBasedStrategy;
import org.eclipse.viatra.query.runtime.localsearch.planner.POperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IUpdateable;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PQueryFlattener;
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
	private Logger logger;
	private IQueryRuntimeContext runtimeContext;
	private IQueryCacheContext cacheContext;

    private static class Planner {

        Map<List<ISearchOperation>, Map<PVariable, Integer>> operationListsWithVarMappings;
        private POperationCompiler compiler;
        private IQueryBackend backend;
        private IQueryBackendHintProvider hintProvider;
        private PQuery query;

        public Planner(IQueryBackend backend, IQueryBackendHintProvider hintProvider, PQuery query) {
            this.backend = backend;
            this.hintProvider = hintProvider;
            this.query = query;
        }

        public void createPlan(MatcherReference key, Logger logger, IQueryMetaContext metaContext, IQueryRuntimeContext runtimeContext, final ISearchContext searchContext)
                throws QueryProcessingException {
        	IFlattenCallPredicate flattenCallPredicate = new DefaultFlattenCallPredicate();
            PQueryFlattener flattener = new PQueryFlattener(flattenCallPredicate);
            PBodyNormalizer normalizer = new PBodyNormalizer(metaContext, false);
            
            Map<String, Object> hints = hintProvider.getHints(query);
            Boolean allowInverse = (Boolean) hints.get(LocalSearchHintKeys.ALLOW_INVERSE_NAVIGATION);
            allowInverse = allowInverse == null ? true : allowInverse; 
            Boolean useBase = (Boolean) hints.get(LocalSearchHintKeys.USE_BASE_INDEX);
            useBase = useBase == null ? true : useBase; 
            
            LocalSearchRuntimeBasedStrategy strategy = new LocalSearchRuntimeBasedStrategy(allowInverse,useBase);
            compiler = new POperationCompiler(runtimeContext, backend, useBase);

            LocalSearchPlanner planner = new LocalSearchPlanner();
            planner.initializePlanner(flattener, logger, metaContext, runtimeContext, normalizer, strategy, compiler, hints);
            operationListsWithVarMappings = planner.plan(key.getQuery(), key.getAdornment());

            Collection<SearchPlanExecutor> executors = Collections2.transform(operationListsWithVarMappings.entrySet(),
                    new Function<Entry<List<ISearchOperation>, Map<PVariable, Integer>>, SearchPlanExecutor>() {

                        @Override
                        public SearchPlanExecutor apply(Entry<List<ISearchOperation>, Map<PVariable, Integer>> input) {
                            final SearchPlan plan = new SearchPlan();
                            plan.addOperations(input.getKey());

                            return new SearchPlanExecutor(plan, searchContext, input.getValue());
                        }
                    });

            final Collection<Integer> parameterSizes = Collections2.transform(planner.getNormalizedDisjunction()
                    .getBodies(), new Function<PBody, Integer>() {

                @Override
                public Integer apply(PBody input) {
                    Set<PVariable> uniqueVariables = input.getUniqueVariables();
                    
                    // Calculate the equal pairs in the parameter list of the pattern, because they 
                    // should be present multiple times at the beginning of the matching frame, and the 
                    // matching frame should be large enough
                    List<ExportedParameter> symbolicParameters = Lists.newArrayList(input.getSymbolicParameters());
                    
                    int numberOfEqualParameterPairs = 0;
                    
                    for (int i = 0; i < symbolicParameters.size(); i++) {
                        for (int j = i+1; j < symbolicParameters.size(); j++) {
                            ExportedParameter exportedParameter1 = symbolicParameters.get(i);
                            ExportedParameter exportedParameter2 = symbolicParameters.get(j);
                            boolean toStringNotEquals = ! (exportedParameter1.toString().equals(exportedParameter2.toString()));
                            boolean parameterEquals = exportedParameter1.getParameterVariable().equals(exportedParameter2.getParameterVariable());
                            if(toStringNotEquals && parameterEquals){
                                numberOfEqualParameterPairs++;
                            }
                        }
                    }
                    return uniqueVariables.size() + numberOfEqualParameterPairs;
                }
            });

            int keySize = key.getQuery().getParameters().size();
            final LocalSearchMatcher matcher = new LocalSearchMatcher(key.getQuery(), executors, keySize, Collections.max(parameterSizes));
            searchContext.loadMatcher(key, matcher);
        }

        public void collectElementsToIndex(Set<EClass> classesToIndex, Set<EStructuralFeature> featuresToIndex,
                Set<EDataType> dataTypesToIndex) {
            for (List<ISearchOperation> plan : operationListsWithVarMappings.keySet()) {
                for (ISearchOperation operation : plan) {
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

        public void collectDependencies(Set<MatcherReference> dependencies) {
            dependencies.addAll(compiler.getDependencies());

        }

    }

    public LocalSearchResultProvider(IQueryBackend backend, Logger logger, IQueryRuntimeContext runtimeContext,
            IQueryCacheContext cacheContext, IQueryBackendHintProvider hintProvider, PQuery query) {
        this.backend = backend;
		this.logger = logger;
		this.runtimeContext = runtimeContext;
		this.cacheContext = cacheContext;
        this.hintProvider = hintProvider;
        this.query = query;

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

    public LocalSearchMatcher newLocalSearchMatcher(Object[] parameters) throws ViatraQueryException,
            QueryProcessingException {
        // XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
        ViatraQueryEngine engine = (ViatraQueryEngine) hintProvider;

        final ISearchContext searchContext = new ISearchContext.SearchContext(engine.getBaseIndex());
        
        
        Set<EClass> classesToIndex = Sets.newHashSet();
        Set<EStructuralFeature> featuresToIndex = Sets.newHashSet();
        Set<EDataType> dataTypesToIndex = Sets.newHashSet();
        
        final Set<Integer> adornment = Sets.newHashSet();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                adornment.add(Integer.valueOf(i));
            }
        }
        
        final MatcherReference reference = new MatcherReference(query, adornment);
        Set<MatcherReference> dependencies = Sets.newHashSet(reference);
        Set<MatcherReference> processedDependencies = Sets.newHashSet();
        Set<MatcherReference> todo = Sets.difference(dependencies, processedDependencies);
        
        while (!todo.isEmpty()) {
            final MatcherReference dependency = todo.iterator().next();
            Planner planner = new Planner(backend, hintProvider, query);
            planner.createPlan(dependency, logger, runtimeContext.getMetaContext(), runtimeContext, searchContext);
            planner.collectElementsToIndex(classesToIndex, featuresToIndex, dataTypesToIndex);
            planner.collectDependencies(dependencies);
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
            for (int i = 0; i < parameters.length; i++) {
                frame.setValue(i, parameters[i]);
            }
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
            for (int i = 0; i < parameters.length; i++) {
                frame.setValue(i, parameters[i]);
            }
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
            for (int i = 0; i < parameters.length; i++) {
                frame.setValue(i, parameters[i]);
            }
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

}
