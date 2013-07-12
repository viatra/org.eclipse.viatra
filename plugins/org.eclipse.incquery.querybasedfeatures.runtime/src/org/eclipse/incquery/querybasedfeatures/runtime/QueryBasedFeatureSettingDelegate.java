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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.incquery.querybasedfeatures.runtime.handler.QueryBasedFeatures;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * TODO process pattern annotation for specific settings when initializing (e.g. source, target, keepCache, etc)
 * 
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegate extends BasicSettingDelegate.Stateless {

    /**
     * Weak hash map for keeping the created objects for each notifier
     */
    private final Map<AdvancedIncQueryEngine,WeakReference<QueryBasedFeature>> queryBasedFeatures = new WeakHashMap<AdvancedIncQueryEngine, WeakReference<QueryBasedFeature>>();

    private final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification;

    private final QueryBasedFeatureSettingDelegateFactory delegateFactory;

    private final boolean dynamicEMFMode;
    
    private boolean isResourceScope;
    
    /**
     * Constructs a new {@link QueryBasedFeatureSettingDelegate} instance based on the given parameters.
     * The scope of the incquery engine in this case will be the one provided by {@link QueryBasedFeatureHelper.prepareNotifierForSource({@link InternalEObject})}.
     *  
     * @param eStructuralFeature the parent structural feature of the setting delegate
     * @param factory the factory used to create incquery engine for the setting delegate
     * @param querySpecification the query specification used for the evaluation of the setting delegate
     * @param dynamicEMFMode indicates whether the engine should be created in dynamic EMF mode
     */
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> QueryBasedFeatureSettingDelegate(EStructuralFeature eStructuralFeature,
            QueryBasedFeatureSettingDelegateFactory factory,
            IQuerySpecification<Matcher> querySpecification, boolean dynamicEMFMode) {
        this(eStructuralFeature, factory, querySpecification, false, dynamicEMFMode);
    }
    
    /**
     * Constructs a new {@link QueryBasedFeatureSettingDelegate} instance based on the given parameters.
     * 
     * @param eStructuralFeature the parent structural feature of the setting delegate
     * @param factory the factory used to create incquery engine for the setting delegate
     * @param querySpecification the query specification used for the evaluation of the setting delegate
     * @param isResourceScope indicates whether the {@link Resource} of the {@link InternalEObject} is enough as a scope during the evaluation of the setting delegate 
     * @param dynamicEMFMode indicates whether the engine should be created in dynamic EMF mode
     */
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> QueryBasedFeatureSettingDelegate(EStructuralFeature eStructuralFeature,
            QueryBasedFeatureSettingDelegateFactory factory,
            IQuerySpecification<Matcher> querySpecification, 
            boolean isResourceScope, boolean dynamicEMFMode) {
        super(eStructuralFeature);
        this.delegateFactory = factory;
        this.querySpecification = querySpecification;
        this.dynamicEMFMode = dynamicEMFMode;
        this.isResourceScope = isResourceScope;
        
        // TODO annotation processing to be done here
    }

    @Override
    protected Object get(InternalEObject owner, boolean resolve, boolean coreType) {
        
        // TODO this can be expensive to do
        Notifier notifierForSource = null;
        if (isResourceScope) {
            notifierForSource = owner.eResource();
        }
        if (notifierForSource == null) {
            notifierForSource = QueryBasedFeatureHelper.prepareNotifierForSource(owner);    
        }
                
        AdvancedIncQueryEngine engine = null;
        try {
            engine = delegateFactory.getEngineForNotifier(notifierForSource, dynamicEMFMode);
        } catch (IncQueryException e) {
            IncQueryLoggingUtil.getDefaultLogger().error("Engine preparation failed", e);
            throw new IllegalStateException("Engine preparation failed", e);
        }
        
        WeakReference<QueryBasedFeature> weakReference = queryBasedFeatures.get(engine);
        QueryBasedFeature queryBasedFeature = weakReference == null ? null : weakReference.get();
        if(queryBasedFeature == null) {
            // TODO use annotation values (keepCache)
            if(eStructuralFeature.isMany()) {
                queryBasedFeature  = QueryBasedFeatures.newMultiValueFeatue(eStructuralFeature, true);
            } else {
                queryBasedFeature = QueryBasedFeatures.newSingleValueFeature(eStructuralFeature, true);
            }
            if(queryBasedFeature != null) {
                queryBasedFeatures.put(engine, new WeakReference<QueryBasedFeature>(queryBasedFeature));
            }
        }
        
        if (!queryBasedFeature.isInitialized()) {

            try {
                @SuppressWarnings("unchecked")
                IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) this.querySpecification
                        .getMatcher(engine);
                List<String> parameterNames = matcher.getParameterNames();
                if (!queryBasedFeature.isInitialized()) {
                    // TODO use annotation values (source, target)
                    queryBasedFeature.initialize(matcher, parameterNames.get(0), parameterNames.get(1));
                    queryBasedFeature.startMonitoring();
                }
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
