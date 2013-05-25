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
package org.eclipse.incquery.runtime.evm.specific.event;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 */
public class LightweightAttributeMonitor<MatchType extends IPatternMatch> extends AttributeMonitor<MatchType> {

    private LightweightEObjectObserver observer;
    private Multimap<EObject, MatchType> observedMultimap;
    private NavigationHelper navigationHelper;
    
    /**
     * 
     */
    public LightweightAttributeMonitor(NavigationHelper helper) {
        super();
        this.navigationHelper = helper;
        this.observer = new LightweightEObjectObserver() {
            
            @Override
            public void notifyFeatureChanged(EObject host, EStructuralFeature feature, Notification notification) {
                Collection<MatchType> matches = observedMultimap.get(host);
                for (MatchType matchType : matches) {
                    notifyListeners(matchType);
                }
            }
        };
        this.observedMultimap = HashMultimap.create();
    }
    
    @Override
    public void registerFor(MatchType atom) {
        Collection<EObject> allEObjects = findAllEObjects(atom);
        for (EObject eObject : allEObjects) {
            navigationHelper.addLightweightEObjectObserver(observer, eObject);
            observedMultimap.put(eObject, atom);
        }
    }

    @Override
    public void unregisterForAll() {
        for (EObject eobj : observedMultimap.keySet()) {
            navigationHelper.removeLightweightEObjectObserver(observer, eobj);
        }
    }

    @Override
    public void unregisterFor(MatchType atom) {
        Collection<EObject> allEObjects = findAllEObjects(atom);
        for (EObject eObject : allEObjects) {
            navigationHelper.removeLightweightEObjectObserver(observer, eObject);
            observedMultimap.remove(eObject, atom);
        }
    }

    private Collection<EObject> findAllEObjects(MatchType atom){
        Collection<EObject> eobjs = Lists.newArrayList();
        for (String param : atom.parameterNames()) {
            Object location = atom.get(param);
            if(location instanceof EObject) {
                eobjs.add((EObject) location);
            }
        }
        return eobjs;
    }
    
}
