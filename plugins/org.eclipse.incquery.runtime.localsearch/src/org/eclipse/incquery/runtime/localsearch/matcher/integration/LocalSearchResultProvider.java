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
package org.eclipse.incquery.runtime.localsearch.matcher.integration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.operations.check.BinaryTransitiveClosureCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.CountCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.incquery.runtime.localsearch.operations.extend.ExtendToEStructuralFeatureSource;
import org.eclipse.incquery.runtime.localsearch.operations.extend.IterateOverEClassInstances;
import org.eclipse.incquery.runtime.localsearch.operations.extend.IterateOverEDatatypeInstances;
import org.eclipse.incquery.runtime.localsearch.operations.extend.IterateOverEStructuralFeatureInstances;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlan;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.runtime.localsearch.planner.LocalSearchPlanner;
import org.eclipse.incquery.runtime.localsearch.planner.LocalSearchPlannerStrategy;
import org.eclipse.incquery.runtime.localsearch.planner.POperationCompiler;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.incquery.runtime.matchers.backend.IUpdateable;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PQueryFlattener;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchResultProvider implements IQueryResultProvider {

    private final static String UPDATE_LISTENER_NOT_SUPPORTED = "Local search backend does not support update listening.";
    private final IQueryBackend backend;
    private final IPatternMatcherRuntimeContext matcherContext;
    private final IQueryBackendHintProvider hintProvider;
    private final PQuery query;

    private static class Planner {

        List<List<ISearchOperation>> operations;
        private POperationCompiler compiler;

        public void createPlan(MatcherReference key, IPatternMatcherRuntimeContext matcherContext, final ISearchContext searchContext)
                throws QueryProcessingException {
            PQueryFlattener flattener = new PQueryFlattener();
            PBodyNormalizer normalizer = new PBodyNormalizer(matcherContext);
            LocalSearchPlannerStrategy strategy = new LocalSearchPlannerStrategy();
            compiler = new POperationCompiler();

            LocalSearchPlanner planner = new LocalSearchPlanner();
            planner.initializePlanner(flattener, matcherContext, normalizer, strategy, compiler);
            operations = planner.plan(key.getQuery(), key.getAdornment());

            Collection<SearchPlanExecutor> executors = Collections2.transform(operations,
                    new Function<List<ISearchOperation>, SearchPlanExecutor>() {

                        @Override
                        public SearchPlanExecutor apply(List<ISearchOperation> input) {
                            final SearchPlan plan = new SearchPlan();
                            plan.addOperations(input);

                            return new SearchPlanExecutor(plan, searchContext);
                        }
                    });

            final Collection<Integer> parameterSizes = Collections2.transform(planner.getNormalizedDisjunction()
                    .getBodies(), new Function<PBody, Integer>() {

                @Override
                public Integer apply(PBody input) {
                    return input.getUniqueVariables().size();
                }
            });

            int keySize = key.getQuery().getParameters().size();
            final LocalSearchMatcher matcher = new LocalSearchMatcher(key.getQuery(), executors, keySize, Collections.max(parameterSizes));
            searchContext.loadMatcher(key, matcher);
        }

        public void collectElementsToIndex(Set<EClass> classesToIndex, Set<EStructuralFeature> featuresToIndex,
                Set<EDataType> dataTypesToIndex) {
            for (List<ISearchOperation> plan : operations) {
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

    public LocalSearchResultProvider(IQueryBackend backend, IPatternMatcherRuntimeContext matcherContext,
            IQueryBackendHintProvider hintProvider, PQuery query) {
        this.backend = backend;
        this.matcherContext = matcherContext;
        this.hintProvider = hintProvider;
        this.query = query;

    }

    private LocalSearchMatcher initializeMatcher(Object[] parameters) {
        try {
            return newLocalSearchMatcher(parameters);
        } catch (QueryProcessingException e) {
            throw new RuntimeException(e);
        } catch (IncQueryException e) {
            throw new RuntimeException(e);
        }

    }

    public LocalSearchMatcher newLocalSearchMatcher(Object[] parameters) throws IncQueryException,
            QueryProcessingException {
        // XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
        IncQueryEngine engine = (IncQueryEngine) hintProvider;

        final ISearchContext searchContext = new ISearchContext.SearchContext(engine.getBaseIndex());
        
        
        Set<EClass> classesToIndex = Sets.newHashSet();
        Set<EStructuralFeature> featuresToIndex = Sets.newHashSet();
        Set<EDataType> dataTypesToIndex = Sets.newHashSet();
        
        final HashSet<Integer> adornment = Sets.newHashSet();
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
            Planner planner = new Planner();
            planner.createPlan(dependency, matcherContext, searchContext);
            planner.collectElementsToIndex(classesToIndex, featuresToIndex, dataTypesToIndex);
            planner.collectDependencies(dependencies);
            processedDependencies.add(dependency);
        }

        searchContext.registerObservedTypes(classesToIndex, dataTypesToIndex, featuresToIndex);
        return searchContext.getMatcher(reference);
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
