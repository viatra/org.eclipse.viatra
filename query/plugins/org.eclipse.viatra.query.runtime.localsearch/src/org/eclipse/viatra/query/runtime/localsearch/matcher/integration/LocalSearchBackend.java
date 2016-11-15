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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.plan.CachingPlanProvider;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryResultProviderAccess;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchBackend implements IQueryBackend {

    IQueryBackendHintProvider hintProvider;
	IQueryRuntimeContext runtimeContext;
	IQueryResultProviderAccess resultProviderAccess;
	Logger logger;
	IPlanProvider planProvider;
	private final Set<ILocalSearchAdapter> adapters = Sets.newHashSet();
	
	// Cache
	Table<EDataType, EClass, Set<EAttribute>> eAttributesByTypeForEClass;
    
	private final Multimap<PQuery, LocalSearchResultProvider> resultProviderCache = ArrayListMultimap.create();
	
    /**
     * @since 1.5
     */
    public LocalSearchBackend(IQueryBackendContext context) {
        super();
		this.logger = context.getLogger();
		this.runtimeContext = context.getRuntimeContext();
		this.resultProviderAccess = context.getResultProviderAccess();
        this.hintProvider = context.getHintProvider();
        this.eAttributesByTypeForEClass = HashBasedTable.create();
        this.planProvider = new CachingPlanProvider(logger);
    }


    @Override
    public IQueryResultProvider getResultProvider(PQuery query) throws QueryProcessingException {
        return getResultProvider(query, null);
    }
    
    /**
     * @since 1.4
     */
    @Override
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint hints)
            throws QueryProcessingException {
        
        IMatcherCapability requestedCapability = hintProvider.getQueryEvaluationHint(query).overrideBy(hints).calculateRequiredCapability(query);
        for(LocalSearchResultProvider existingResultProvider : resultProviderCache.get(query)){
            if (requestedCapability.canBeSubstitute(existingResultProvider.getCapabilites())){
                return existingResultProvider;
            }
        }
        
        LocalSearchResultProvider resultProvider = new LocalSearchResultProvider(this, logger, runtimeContext, resultProviderAccess, hintProvider, query, planProvider, hints);
        resultProviderCache.put(query, resultProvider);
        resultProvider.prepare();
        return resultProvider;
    }
    
    @Override
    public void dispose() {  
        eAttributesByTypeForEClass.clear();
        resultProviderCache.clear();
    }

	@Override
	public boolean isCaching() {
		return false;
	}

	@Override
	public IQueryResultProvider peekExistingResultProvider(PQuery query) {
		return null;
	}

	public Table<EDataType, EClass, Set<EAttribute>> geteAttributesByTypeForEClass() {
	    return eAttributesByTypeForEClass;
	}

	/**
	 * @since 1.4
     */
    public IQueryRuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
    
    /**
     * @since 1.4
     */
    public IQueryBackendHintProvider getHintProvider() {
        return hintProvider;
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
     */
    List<ILocalSearchAdapter> getAdapters() {
        return Lists.newArrayList(adapters);
    }
    
}
