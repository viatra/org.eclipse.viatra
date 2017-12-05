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

import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanProvider;
import org.eclipse.viatra.query.runtime.localsearch.plan.SimplePlanProvider;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.util.ICache;
import org.eclipse.viatra.query.runtime.matchers.util.PurgableCache;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class LocalSearchBackend implements IQueryBackend {

    IQueryBackendContext context;
    IPlanProvider planProvider;
    private final Set<ILocalSearchAdapter> adapters = Sets.newHashSet();
    
    private final PurgableCache generalCache;
    
    private final Multimap<PQuery, AbstractLocalSearchResultProvider> resultProviderCache = ArrayListMultimap.create();
    
    /**
     * @since 1.5
     */
    public LocalSearchBackend(IQueryBackendContext context) {
        super();
        this.context = context;
        this.generalCache = new PurgableCache();
        this.planProvider = new SimplePlanProvider(context.getLogger());
    }

    @Override
    public void flushUpdates() {
        
    }
    
    @Override
    public IQueryResultProvider getResultProvider(PQuery query) {
        return getResultProvider(query, null);
    }
    
    /**
     * @since 1.4
     */
    @Override
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint hints) {
        
        IMatcherCapability requestedCapability = getHintProvider().getQueryEvaluationHint(query).overrideBy(hints).calculateRequiredCapability(query);
        for(AbstractLocalSearchResultProvider existingResultProvider : resultProviderCache.get(query)){
            if (requestedCapability.canBeSubstitute(existingResultProvider.getCapabilites())){
                return existingResultProvider;
            }
        }
        
        AbstractLocalSearchResultProvider resultProvider = initializeResultProvider(query, hints);
        resultProviderCache.put(query, resultProvider);
        resultProvider.prepare();
        return resultProvider;
    }
    
    /**
     * @throws ViatraQueryRuntimeException 
     * @since 1.7
     */
    protected abstract AbstractLocalSearchResultProvider initializeResultProvider(PQuery query, QueryEvaluationHint hints);
    
    @Override
    public void dispose() {  
        resultProviderCache.clear();
        generalCache.purge();
    }

    @Override
    public boolean isCaching() {
        return false;
    }

    @Override
    public IQueryResultProvider peekExistingResultProvider(PQuery query) {
        return null;
    }

    /**
     * @since 1.4
     */
    public IQueryRuntimeContext getRuntimeContext() {
        return context.getRuntimeContext();
    }
    
    
    /**
     * @since 1.5
     */
    public QueryAnalyzer getQueryAnalyzer() {
        return context.getQueryAnalyzer();
    }


    /**
     * @since 1.4
     */
    public IQueryBackendHintProvider getHintProvider() {
        return context.getHintProvider();
    }
    
    /**
     * @since 1.5
     */
    public void addAdapter(ILocalSearchAdapter adapter){
        adapters.add(adapter);
    }
    
    /**
     * @since 1.5
     */
    public void removeAdapter(ILocalSearchAdapter adapter){
        adapters.remove(adapter);
    }
    
    /**
     * Return a copy of the current adapters
     * @since 1.7
     */
    public List<ILocalSearchAdapter> getAdapters() {
        return Lists.newArrayList(adapters);
    }
    
    /**
     * @since 1.5
     */
    public IQueryBackendContext getBackendContext() {
        return context;
    }
    
    /**
     * Returns the internal cache of the backend
     * @since 1.7
     * @noreference This method is not intended to be referenced by clients.
     */
    public ICache getCache() {
        return generalCache;
    }
}
