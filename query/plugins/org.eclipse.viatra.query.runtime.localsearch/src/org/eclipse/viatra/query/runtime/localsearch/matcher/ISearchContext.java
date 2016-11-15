/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFBaseIndexWrapper;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.IAdornmentProvider;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryResultProviderAccess;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * The {@link ISearchContext} interface allows search operations to reuse platform services such as the indexer.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public interface ISearchContext{
    
    NavigationHelper getBaseIndex();
    
    /**
     * @param classes
     * @param dataTypes
     * @param features
     */
    void registerObservedTypes(Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features);
    
    /**
     * Returns a matcher for a selected query specification.
     * 
     * @param reference
     * @throws QueryProcessingException 
     * @since 1.5
     */
    IQueryResultProvider getMatcher(MatcherReference reference) throws LocalSearchException;
    
    public class SearchContext implements ISearchContext {

        final NavigationHelper navigationHelper;
        final IQueryResultProviderAccess resultProviderAccess;
        final QueryEvaluationHint overrideHints;
        
        final Logger logger = Logger.getLogger(getClass());
        
        /**
         * @since 1.5
         */
        public SearchContext(IBaseIndex baseIndex, IQueryResultProviderAccess resultProviderAccess, QueryEvaluationHint overrideHints) throws ViatraQueryException {
            //XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
            this.navigationHelper = ((EMFBaseIndexWrapper)baseIndex).getNavigationHelper();
            this.resultProviderAccess = resultProviderAccess;
            this.overrideHints = overrideHints;
        }

        public void registerObservedTypes(Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
            if (this.navigationHelper.isInWildcardMode()) {
                // In wildcard mode, everything is registered (+ register throws an exception)
                return;
            }
            this.navigationHelper.registerObservedTypes(classes, dataTypes, features, IndexingLevel.FULL);
        }
        
        @Override
        public NavigationHelper getBaseIndex() {
            return navigationHelper;
        }

        /**
         * @throws QueryProcessingException 
         * @since 1.5
         */
        @Override
        public IQueryResultProvider getMatcher(final MatcherReference reference) throws LocalSearchException {
            // Inject adornment for referenced pattern
            IAdornmentProvider adornmentProvider = new IAdornmentProvider() {
                
                @Override
                public Iterable<Set<PParameter>> getAdornments(PQuery query) {
                    if (query.equals(reference.query)){
                        return Collections.singleton(reference.adornment);
                    }
                    return Collections.emptySet();
                }
            };
            @SuppressWarnings("rawtypes")
            QueryEvaluationHint hints = new QueryEvaluationHint(Collections.<QueryHintOption, Object>singletonMap(LocalSearchHintOptions.ADORNMENT_PROVIDER, adornmentProvider), null);
            if (overrideHints != null){
                hints = overrideHints.overrideBy(hints);
            }
                    
            try {
                return resultProviderAccess.getResultProvider(reference.getQuery(), overrideHints);
            } catch (QueryProcessingException e) {
                throw new LocalSearchException("Could not access referenced query: "+reference, e);
            }
        }
        
    }
}
