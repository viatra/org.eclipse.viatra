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
package org.eclipse.incquery.runtime.localsearch.matcher;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseFactory;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.emf.EMFBaseIndexWrapper;
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
     * @param classes
     * @param dataTypes
     * @param features
     */
    void registerObservedTypes(Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features);

    /**
     * Temporary load function for storing local search matchers
     * @param matcher
     */
    void loadMatcher(MatcherReference reference, LocalSearchMatcher matcher);
    
    /**
     * Returns a matcher for a selected query specification.
     * 
     * TODO should return a generic concept, based on the current IncQueryEngine
     * @param reference
     */
    LocalSearchMatcher getMatcher(MatcherReference reference);
    
    public class SearchContext implements ISearchContext {

        final NavigationHelper navigationHelper;
        
        final Logger logger = Logger.getLogger(getClass());
        
        Map<MatcherReference, LocalSearchMatcher> knownMatchers = Maps.newHashMap();
        
        public SearchContext(IBaseIndex baseIndex) {
            //XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
            this.navigationHelper = ((EMFBaseIndexWrapper)baseIndex).getNavigationHelper();
        }
        
        public SearchContext(ResourceSet set, Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) throws IncQueryBaseException {
            navigationHelper = IncQueryBaseFactory.getInstance().createNavigationHelper(set, false, logger);
            navigationHelper.registerObservedTypes(classes, dataTypes, features);
        }
        
        public SearchContext(IBaseIndex baseIndex, Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
            //XXX this is a problematic (and in long-term unsupported) solution, see bug 456815
            this.navigationHelper = ((EMFBaseIndexWrapper)baseIndex).getNavigationHelper();
            this.navigationHelper.registerObservedTypes(classes, dataTypes, features);
        }

        public void registerObservedTypes(Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
            this.navigationHelper.registerObservedTypes(classes, dataTypes, features);
        }
        
        @Override
        public NavigationHelper getBaseIndex() {
            return navigationHelper;
        }

        @Override
        public void loadMatcher(MatcherReference reference, LocalSearchMatcher matcher) {
            knownMatchers.put(reference, matcher);
            
        }

        @Override
        public LocalSearchMatcher getMatcher(MatcherReference reference) {
            if (!knownMatchers.containsKey(reference)) {
                //TODO a generic local search matcher could be initialized here
                throw new UnsupportedOperationException(String.format("No matcher for query %s initialized.", reference.getQuery().getFullyQualifiedName()));
            }
            return knownMatchers.get(reference);
        }
        
    }
}
