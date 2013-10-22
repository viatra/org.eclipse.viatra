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
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.incquery.querybasedfeatures.runtime.handler.QueryBasedFeatures;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.internal.apiimpl.GenericQuerySpecification;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegateFactory implements Factory {


    private final Map<Notifier, WeakReference<AdvancedIncQueryEngine>> engineMap;
    
    private final Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> specificationMap;
    
    /**
     * 
     */
    public QueryBasedFeatureSettingDelegateFactory() {
        engineMap = new WeakHashMap<Notifier, WeakReference<AdvancedIncQueryEngine>>();
        specificationMap = Maps.newHashMap();
    }
    
    /**
     * @return the specificationMap
     */
    public Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getSpecificationMap() {
        return specificationMap;
    }
    
    protected AdvancedIncQueryEngine getEngineForNotifier(Notifier notifier, boolean dynamicEMFMode) throws IncQueryException {
        if(dynamicEMFMode) {
            WeakReference<AdvancedIncQueryEngine> reference = engineMap.get(notifier);
            if(reference != null && reference.get() != null) {
                return reference.get();
            } else {
                AdvancedIncQueryEngine unmanagedEngine = AdvancedIncQueryEngine.createUnmanagedEngine(notifier, false, dynamicEMFMode);
                engineMap.put(notifier, new WeakReference<AdvancedIncQueryEngine>(unmanagedEngine));
                return unmanagedEngine;
            }
        } else {
            return AdvancedIncQueryEngine.from(IncQueryEngine.on(notifier));
        }
    }
    
    @Override
    public SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature) {
        SettingDelegate result = null;
        
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpec = findQuerySpecification(eStructuralFeature);
        if(querySpec != null) {
            if (querySpec instanceof GenericQuerySpecification) {
                result = createSettingDelegate(eStructuralFeature, querySpec, true, true);                
            }
            else {
                result = createSettingDelegate(eStructuralFeature, querySpec, false, false);
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

    public IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> findQuerySpecification(
            EStructuralFeature eStructuralFeature) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpec = null;
        EAnnotation annotation = eStructuralFeature.getEAnnotation(QueryBasedFeatures.ANNOTATION_SOURCE);
        if(annotation != null) {
            String patternFQN = annotation.getDetails().get(QueryBasedFeatures.PATTERN_FQN_KEY);
            if(specificationMap.containsKey(patternFQN)) {
                querySpec = specificationMap.get(patternFQN);
            } else {
                querySpec = QuerySpecificationRegistry.getQuerySpecification(patternFQN);
            // TODO let's use Pattern Registry instead (requires added dependency!)
//                List<IPatternInfo> patternInfosByFQN = PatternRegistry.INSTANCE.getPatternInfosByFQN(patternFQN);
//                if(patternInfosByFQN.size() > 0) {
//                    querySpec = patternInfosByFQN.get(0).getQuerySpecification();
//                    if(patternInfosByFQN.size() > 1) {
//                        IncQueryLoggingUtil.getDefaultLogger().warn("Multiple patterns (" + patternInfosByFQN + ") registered for FQN " + patternFQN);
//                    }
//                }
            }
        }
        return querySpec;
    }
    
    public SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification, boolean isResourceScope, boolean dynamicEMFMode) {
        Preconditions.checkArgument(querySpecification != null, "Query specification cannot be null!");
        return new QueryBasedFeatureSettingDelegate(eStructuralFeature, this, querySpecification, isResourceScope, dynamicEMFMode);
    }

}
