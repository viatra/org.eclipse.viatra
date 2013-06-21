/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.incquery.querybasedfeatures.runtime.handler.QueryBasedFeatures;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegate extends BasicSettingDelegate.Stateless {

    /**
     * Weak hash map for keeping the created objects for each notifier
     */
    private final Map<Notifier,WeakReference<QueryBasedFeature>> queryBasedFeatures = new WeakHashMap<Notifier, WeakReference<QueryBasedFeature>>();

    private final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification;
    
    
    /**
     * @param eStructuralFeature
     */
    public QueryBasedFeatureSettingDelegate(EStructuralFeature eStructuralFeature) throws IncQueryException {
        super(eStructuralFeature);

        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpec = null;
        EAnnotation annotation = eStructuralFeature.getEAnnotation(QueryBasedFeatures.ANNOTATION_SOURCE);
        if(annotation != null) {
            String patternFQN = annotation.getDetails().get(QueryBasedFeatures.PATTERN_FQN_KEY);
            querySpec = QuerySpecificationRegistry.getQuerySpecification(patternFQN);
        }
        querySpecification = querySpec;
         
        if(querySpecification == null) {
            throw new IncQueryException("Could not find query specification for feature","Query specification not found!");
        }
        
    }

    @Override
    protected Object get(InternalEObject owner, boolean resolve, boolean coreType) {
        
        // TODO this can be expensive to do
        Notifier notifierForSource = QueryBasedFeatureHelper.prepareNotifierForSource(owner);
        
        QueryBasedFeature queryBasedFeature = queryBasedFeatures.get(notifierForSource).get();
        if(queryBasedFeature == null) {
            if(eStructuralFeature.isMany()) {
                queryBasedFeature  = QueryBasedFeatures.newMultiValueFeatue(eStructuralFeature, true);
            } else {
                queryBasedFeature = QueryBasedFeatures.newSingleValueFeature(eStructuralFeature, true);
            }
            if(queryBasedFeature != null) {
                queryBasedFeatures.put(notifierForSource, new WeakReference<QueryBasedFeature>(queryBasedFeature));
            }
        }
        
        if (!queryBasedFeature.isInitialized()) {

            try {
                IncQueryEngine incQueryEngine = IncQueryEngine.on(notifierForSource);
                IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) querySpecification
                        .getMatcher(incQueryEngine);
                List<String> parameterNames = matcher.getParameterNames();
                queryBasedFeature.initialize(matcher, parameterNames.get(0), parameterNames.get(1));
                queryBasedFeature.startMonitoring();
            } catch (IncQueryException e) {
                IncQueryLoggingUtil.getDefaultLogger().error("Handler initialization failed", e);
            }
        }

        return queryBasedFeature.getValue(owner);
    }

    @Override
    protected boolean isSet(InternalEObject owner) {
        return false;
    }

}
