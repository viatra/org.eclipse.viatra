/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * 
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegate extends BasicSettingDelegate.Stateless {

    /**
     * Weak hash map for keeping the created objects for each notifier
     */
    private final Map<AdvancedViatraQueryEngine,WeakReference<QueryBasedFeature>> queryBasedFeatures = new WeakHashMap<AdvancedViatraQueryEngine, WeakReference<QueryBasedFeature>>();

    private final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification;

    private final QueryBasedFeatureSettingDelegateFactory delegateFactory;

    private final boolean dynamicEMFMode;
    
    private boolean isResourceScope;

    private QueryBasedFeatureParameters parameters;
    
    /**
     * Constructs a new {@link QueryBasedFeatureSettingDelegate} instance based on the given parameters.
     * The scope of the VIATRA Query engine in this case will be the one provided by {@link QueryBasedFeatureHelper#prepareNotifierForSource(InternalEObject)}.
     *  
     * @param eStructuralFeature the parent structural feature of the setting delegate
     * @param factory the factory used to create VIATRA Query engine for the setting delegate
     * @param querySpecification the query specification used for the evaluation of the setting delegate
     * @param dynamicEMFMode indicates whether the engine should be created in dynamic EMF mode
     */
    public QueryBasedFeatureSettingDelegate(EStructuralFeature eStructuralFeature,
            QueryBasedFeatureSettingDelegateFactory factory,
            IQuerySpecification<?> querySpecification, boolean dynamicEMFMode) {
        this(eStructuralFeature, factory, querySpecification, false, dynamicEMFMode);
    }
    
    /**
     * Constructs a new {@link QueryBasedFeatureSettingDelegate} instance based on the given parameters.
     * 
     * @param eStructuralFeature the parent structural feature of the setting delegate
     * @param factory the factory used to create VIATRA Query engine for the setting delegate
     * @param querySpecification the query specification used for the evaluation of the setting delegate
     * @param isResourceScope indicates whether the {@link Resource} of the {@link InternalEObject} is enough as a scope during the evaluation of the setting delegate 
     * @param dynamicEMFMode indicates whether the engine should be created in dynamic EMF mode
     */
    public QueryBasedFeatureSettingDelegate(EStructuralFeature eStructuralFeature,
            QueryBasedFeatureSettingDelegateFactory factory,
            IQuerySpecification<?> querySpecification, 
            boolean isResourceScope, boolean dynamicEMFMode) {
        super(eStructuralFeature);
        this.delegateFactory = factory;
        this.querySpecification = querySpecification;
        this.dynamicEMFMode = dynamicEMFMode;
        this.isResourceScope = isResourceScope;
        
        parameters = new QueryBasedFeatureParameters(querySpecification);

        List<PAnnotation> qbfAnnotations = querySpecification.getAnnotationsByName("QueryBasedFeature");
        if(qbfAnnotations.isEmpty()) {
            // called probably by Xcore, use defaults
        } else if(qbfAnnotations.size() == 1) {
           PAnnotation annotation = qbfAnnotations.iterator().next();
           processQBFAnnotation(annotation);
        } else {
            // at least one of them has to specify this feature
            for (PAnnotation annotation : qbfAnnotations) {
                annotation.getFirstValue("feature", String.class)
                        .filter(featureParam -> eStructuralFeature.getName().equals(featureParam))
                        .ifPresent(featureParam -> processQBFAnnotation(annotation));
            }
        }
    }

    private void processQBFAnnotation(PAnnotation annotation) {
        parameters.sourceVar = annotation.getFirstValue("source", String.class).orElse(parameters.defaultSourceVar);
        parameters.targetVar = annotation.getFirstValue("target", String.class).orElse(parameters.defaultTargetVar);
        parameters.keepCache  = annotation.getFirstValue("keepCache", Boolean.class).orElse(parameters.defaultKeepCache);
        parameters.kind = annotation.getFirstValue("kind", String.class)
                .map(QueryBasedFeatureKind::parseKindString).orElse(parameters.defaultKind);
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
                
        QueryBasedFeature queryBasedFeature = initializeSettingDelegateInternal(notifierForSource);

        return queryBasedFeature.getValue(owner);
    }
    
    /**
     * 
     * Initializes the query based feature setting delegate using the given notifier as the root of the query engine
     * base index. This is usually the {@link ResourceSet} unless you know what you are doing.
     * 
     * @param rootNotifier
     *            the root of the indexing for the matcher driving the feature
     * @since 1.3
     */
    public void initializeSettingDelegate(Notifier rootNotifier) {
        Preconditions.checkArgument(rootNotifier != null, "Notifier cannot be null");
        initializeSettingDelegateInternal(rootNotifier);
    }

    private QueryBasedFeature initializeSettingDelegateInternal(Notifier notifierForSource) {
        AdvancedViatraQueryEngine engine = null;
        try {
            engine = delegateFactory.getEngineForNotifier(notifierForSource, dynamicEMFMode);
        } catch (ViatraQueryException e) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error("Engine preparation failed", e);
            throw new IllegalStateException("Engine preparation failed", e);
        }
        
        WeakReference<QueryBasedFeature> weakReference = queryBasedFeatures.get(engine);
        QueryBasedFeature queryBasedFeature = weakReference == null ? null : weakReference.get();
        if(queryBasedFeature == null) {
            queryBasedFeature = QueryBasedFeatureHelper.createQueryBasedFeature(eStructuralFeature, parameters.kind, parameters.keepCache);
            if(queryBasedFeature != null) {
                queryBasedFeatures.put(engine, new WeakReference<QueryBasedFeature>(queryBasedFeature));
            }
        }
        
        if (queryBasedFeature != null && !queryBasedFeature.isInitialized()) {
            initializeQueryBasedFeature(engine, queryBasedFeature);
        }
        return queryBasedFeature;
    }

    private void initializeQueryBasedFeature(AdvancedViatraQueryEngine engine, QueryBasedFeature queryBasedFeature) {
        try {
            List<QueryBasedFeature> delayedFeatures = delegateFactory.getDelayedFeatures().computeIfAbsent(engine, (k) -> new ArrayList<>());
            // query-based feature initialization is delayed, but list is used as ordered set
            if(!delayedFeatures.contains(queryBasedFeature)){
                delayedFeatures.add(queryBasedFeature);
                @SuppressWarnings("unchecked")
                ViatraQueryMatcher<IPatternMatch> matcher = (ViatraQueryMatcher<IPatternMatch>) this.querySpecification
                .getMatcher(engine);
                if (!queryBasedFeature.isInitialized()) {
                    queryBasedFeature.setMatcher(matcher);
                    queryBasedFeature.setSourceParamName(parameters.sourceVar);
                    queryBasedFeature.setTargetParamName(parameters.targetVar);
                    // the first feature in the list can initialize the rest
                    Iterator<QueryBasedFeature> iterator = delayedFeatures.iterator();
                    if(iterator.hasNext() && iterator.next().equals(queryBasedFeature)){
                        initializeDelayedFeature(queryBasedFeature, delayedFeatures);
                        // delayed query-based features are initialized 
                        ArrayList<QueryBasedFeature> delayedFeatureList = new ArrayList<>(delayedFeatures);
                        for (QueryBasedFeature delayedFeature : delayedFeatureList) {
                            initializeDelayedFeature(delayedFeature, delayedFeatures);
                        }
                    }
                }
            }
        } catch (ViatraQueryException e) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error("Handler initialization failed", e);
        }
    }

    private void initializeDelayedFeature(QueryBasedFeature queryBasedFeature, List<QueryBasedFeature> delayedFeatures) {
        queryBasedFeature.initialize(queryBasedFeature.getMatcher(), queryBasedFeature.getSourceParamName(), queryBasedFeature.getTargetParamName());
        queryBasedFeature.startMonitoring();
        delayedFeatures.remove(queryBasedFeature);
    }

    @Override
    protected boolean isSet(InternalEObject owner) {
        return false;
    }
    
    private class QueryBasedFeatureParameters{
        
        private String sourceVar;
        private String targetVar;
        
        private QueryBasedFeatureKind kind;
        private boolean keepCache;
        
        private final String defaultSourceVar;
        private final String defaultTargetVar;
        private final QueryBasedFeatureKind defaultKind;
        private final boolean defaultKeepCache;

        public QueryBasedFeatureParameters(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification) {
            List<String> parameterNames = querySpecification.getParameterNames();
            defaultSourceVar = sourceVar = parameterNames.get(0);
            defaultTargetVar = targetVar = parameterNames.get(1);
            defaultKeepCache = keepCache = true;
            if(eStructuralFeature.isMany()) {
                defaultKind = kind = QueryBasedFeatureKind.MANY_REFERENCE;
            } else {
                defaultKind = kind = QueryBasedFeatureKind.SINGLE_REFERENCE;
            }
        }
      }

}
