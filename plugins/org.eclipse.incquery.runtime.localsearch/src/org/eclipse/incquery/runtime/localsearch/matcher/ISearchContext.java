/*******************************************************************************
 * Copyright (c) 2010-2014, stampie, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   stampie - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.matcher;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseFactory;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Maps;

/**
 * The {@link ISearchContext} interface allows search operations to reuse platform services such as the indexer.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public interface ISearchContext{
    
    NavigationHelper getBaseIndex();
    
    /**
     * Temporary load function for storing local search matchers
     * @param matcher
     */
    void loadMatcher(LocalSearchMatcher matcher);
    
    /**
     * Returns a matcher for a selected query specification.
     * 
     * TODO should return a generic concept, based on the current IncQueryEngine
     * @param query
     * @return
     */
    LocalSearchMatcher getMatcher(PQuery query);
    
    public class SearchContext implements ISearchContext {

        final NavigationHelper baseIndex;
        
        final Logger logger = Logger.getLogger(getClass());
        
        Map<PQuery, LocalSearchMatcher> knownMatchers = Maps.newHashMap();
        
        public SearchContext(ResourceSet set, Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) throws IncQueryBaseException {
            baseIndex = IncQueryBaseFactory.getInstance().createNavigationHelper(set, false, logger);
            baseIndex.registerObservedTypes(classes, dataTypes, features);
        }
        
        public SearchContext(NavigationHelper baseIndex, Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
            this.baseIndex = baseIndex;
            baseIndex.registerObservedTypes(classes, dataTypes, features);
        }
        
        @Override
        public NavigationHelper getBaseIndex() {
            return baseIndex;
        }

        @Override
        public void loadMatcher(LocalSearchMatcher matcher) {
            knownMatchers.put(matcher.getQuerySpecification(), matcher);
            
        }

        @Override
        public LocalSearchMatcher getMatcher(PQuery query) {
            if (!knownMatchers.containsKey(query)) {
                //TODO a generic local search matcher could be initialized here
                throw new UnsupportedOperationException("No on-the-fly matcher initialization supported.");
            }
            return knownMatchers.get(query);
        }
        
    }
}
