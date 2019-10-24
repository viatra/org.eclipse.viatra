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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.QueryBasedFeatures;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegateFactory implements Factory {


    private final Map<Notifier, WeakReference<AdvancedViatraQueryEngine>> engineMap;
    
    private final Map<String, IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> specificationMap;
    
    private final Map<ViatraQueryEngine, List<QueryBasedFeature>> delayedFeatures;
    
    public QueryBasedFeatureSettingDelegateFactory() {
        engineMap = new WeakHashMap<Notifier, WeakReference<AdvancedViatraQueryEngine>>();
        specificationMap = new HashMap<>();
        delayedFeatures = CollectionsFactory.createMap();
    }
    
    public Map<String, IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getSpecificationMap() {
        return specificationMap;
    }
    
    /**
     * Returns a live multimap associating all QBFs waiting to be initialized to each VQ engine. 
     * Note that the multimap is currently only ever expected to grow, never deleted from or cleaned up. 
     */
    protected Map<ViatraQueryEngine, List<QueryBasedFeature>> getDelayedFeatures() {
        return delayedFeatures;
    }
    
    protected AdvancedViatraQueryEngine getEngineForNotifier(Notifier notifier, boolean dynamicEMFMode) {
        if(dynamicEMFMode) {
            WeakReference<AdvancedViatraQueryEngine> reference = engineMap.get(notifier);
            if(reference != null && reference.get() != null) {
                return reference.get();
            } else {
                AdvancedViatraQueryEngine unmanagedEngine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(notifier, new BaseIndexOptions().withDynamicEMFMode(dynamicEMFMode)));
                engineMap.put(notifier, new WeakReference<AdvancedViatraQueryEngine>(unmanagedEngine));
                return unmanagedEngine;
            }
        } else {
            return AdvancedViatraQueryEngine.from(ViatraQueryEngine.on(new EMFScope(notifier)));
        }
    }
    
    /**
     * 
     * Returns the setting delegate created by EMF for a query based feature. Users can call this method for query based
     * features to manually initialize the QBF without having a specific object that has this feature.
     * 
     * Call {@link QueryBasedFeatureSettingDelegate#initializeSettingDelegate(Notifier)} with the resource set on the
     * returned value for initialization.
     * 
     * @param eStructuralFeature
     * @return the delegate wrapped in optional or absent if it is null or not a query based feature
     * @since 2.0
     */
    public Optional<QueryBasedFeatureSettingDelegate> getSettingDelegate(EStructuralFeature eStructuralFeature) {
        QueryBasedFeatureSettingDelegate settingDelegate = null;
        if(eStructuralFeature instanceof EStructuralFeature.Internal) {
            EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal) eStructuralFeature;
            SettingDelegate delegate = internalFeature.getSettingDelegate();
            if(delegate instanceof QueryBasedFeatureSettingDelegate) {
                settingDelegate = (QueryBasedFeatureSettingDelegate) delegate;
            }
        }
        return Optional.ofNullable(settingDelegate);
    }
    
    @Override
    public SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature) {
        SettingDelegate result = null;
        
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpec = findQuerySpecification(eStructuralFeature);
        if(querySpec != null) {
            if (querySpec instanceof BaseGeneratedEMFQuerySpecification) {
                result = createSettingDelegate(eStructuralFeature, querySpec, false, false);
            } else {
                result = createSettingDelegate(eStructuralFeature, querySpec, true, true);                
            }
        } else {
            return new BasicSettingDelegate.Stateless(eStructuralFeature) {
                
                @Override
                protected boolean isSet(InternalEObject owner) {
                    return false;
                }
                
                @Override
                protected Object get(InternalEObject owner, boolean resolve, boolean coreType) {
                    if(eStructuralFeature.isMany()) {
                        return ECollections.EMPTY_ELIST;
                    } else {
                        return null;
                    }
                }
            };
        }
        
        return result;
    }

    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> findQuerySpecification(
            EStructuralFeature eStructuralFeature) {
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpec = null;
        EAnnotation annotation = eStructuralFeature.getEAnnotation(QueryBasedFeatures.ANNOTATION_SOURCE);
        if(annotation != null) {
            String patternFQN = annotation.getDetails().get(QueryBasedFeatures.PATTERN_FQN_KEY);
            if(specificationMap.containsKey(patternFQN)) {
                querySpec = specificationMap.get(patternFQN);
            } else {
                IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
                querySpec = registry.getDefaultView().getEntry(patternFQN).get();
            }
        }
        return querySpec;
    }
    
    public SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature,
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification, boolean isResourceScope, boolean dynamicEMFMode) {
        Preconditions.checkArgument(querySpecification != null, "Query specification cannot be null!");
        return new QueryBasedFeatureSettingDelegate(eStructuralFeature, this, querySpecification, isResourceScope, dynamicEMFMode);
    }

}
