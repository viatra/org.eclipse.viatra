/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.plan;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.localsearch.planner.LocalSearchPlanner;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.EMFOperationCompiler;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.IOperationCompiler;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A plan provider implementation which caches previously calculated plans to avoid re-planning for the same adornment
 * 
 * @author Grill Balázs
 * @since 1.4
 * @deprecated Starting from version 1.7 plan provider is not expected to cache plans, as the result provider does the
 *             caching instead. Use {@link SimplePlanProvider} instead.
 */
@Deprecated
public class CachingPlanProvider implements IPlanProvider {

    private final Multimap<MatcherReference, IPlanDescriptor> cache = ArrayListMultimap.create();
    
    private final Logger logger;
    
    public CachingPlanProvider(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * @deprecated use {@link #getPlan(IQueryBackendContext, LocalSearchHints, MatcherReference)} instead
     */
    @Deprecated
    public IPlanDescriptor getPlan(LocalSearchBackend backend, final LocalSearchHints configuration, MatcherReference key)
            throws QueryProcessingException {
        IOperationCompiler compiler = new EMFOperationCompiler(backend.getRuntimeContext(), configuration.isUseBase());
        return getPlan(backend.getBackendContext(), compiler, configuration, key);
    }
    
    @Override
    public IPlanDescriptor getPlan(IQueryBackendContext backend, IOperationCompiler compiler, final LocalSearchHints configuration, MatcherReference key)
            throws QueryProcessingException {
        
        if (cache.containsKey(key)){
            
            return cache.get(key).iterator().next();
            
        }else{
            LocalSearchPlanner planner = new LocalSearchPlanner(backend, compiler, logger, configuration);
            
            Collection<SearchPlanForBody> plansForBodies = planner.plan(key.getQuery(), key.getAdornment());
          
            IPlanDescriptor plan = new PlanDescriptor(key.getQuery(), plansForBodies, key.getAdornment());
            cache.put(key, plan);
            return plan;

        }
    }

}
