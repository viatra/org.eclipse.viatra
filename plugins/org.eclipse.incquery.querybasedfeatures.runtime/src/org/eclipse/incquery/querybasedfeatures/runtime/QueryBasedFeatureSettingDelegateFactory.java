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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicSettingDelegate;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureSettingDelegateFactory implements Factory {


    private final boolean useManagedEngines;
    private final Map<Notifier, WeakReference<AdvancedIncQueryEngine>> engineMap;
    
    /**
     * 
     */
    public QueryBasedFeatureSettingDelegateFactory() {
        useManagedEngines = true;
        engineMap = null;
    }
    
    /**
     * 
     */
    public QueryBasedFeatureSettingDelegateFactory(boolean useManagedEngines) {
        this.useManagedEngines = useManagedEngines;
        engineMap = new WeakHashMap<Notifier, WeakReference<AdvancedIncQueryEngine>>();
    }
    
    
    
    protected AdvancedIncQueryEngine getEngineForNotifier(Notifier notifier) throws IncQueryException {
        if(useManagedEngines) {
            return AdvancedIncQueryEngine.from(IncQueryEngine.on(notifier));
        } else {
            
            WeakReference<AdvancedIncQueryEngine> reference = engineMap.get(notifier);
            if(reference != null && reference.get() != null) {
                return reference.get();
            } else {
                AdvancedIncQueryEngine unmanagedEngine = AdvancedIncQueryEngine.createUnmanagedEngine(notifier);
                engineMap.put(notifier, new WeakReference<AdvancedIncQueryEngine>(unmanagedEngine));
                return unmanagedEngine;
            }
        }
    }
    
    @Override
    public SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature) {
        SettingDelegate result = null;
        
        try {
            result = new QueryBasedFeatureSettingDelegate(eStructuralFeature, this);
        } catch (IncQueryException e) {
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

}
