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
package org.eclipse.viatra.addon.querybasedfeatures.runtime.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public class SingleValueQueryBasedFeature extends QueryBasedFeature {

    private final Map<InternalEObject, Object> singleRefMemory = new HashMap<InternalEObject, Object>();
    private final Map<InternalEObject, Object> updateMemory = new HashMap<InternalEObject, Object>();
        
    /**
     * @param feature
     * @param kind
     * @param keepCache
     */
    protected SingleValueQueryBasedFeature(EStructuralFeature feature, boolean keepCache) {
        super(feature, keepCache);
    }
    
    public Object getSingleReferenceValue(Object source) {
        if (isCached()) {
            return singleRefMemory.get(source);
        } else {
            if (!isInitialized()) {
                return null;
            }
            IPatternMatch match = getMatcher().newEmptyMatch();
            match.set(getSourceParamName(), source);
            if (getMatcher().countMatches(match) > 1) {
                String message = "[QueryBasedFeature] Single reference derived feature has multiple possible values, returning one arbitrary value";
                ViatraQueryLoggingUtil.getLogger(getClass()).warn(message);
            }
            IPatternMatch patternMatch = getMatcher().getOneArbitraryMatch(match);
            if (patternMatch != null) {
                return getTargetValue(patternMatch);
            } else {
                return null;
            }
        }
    }

    protected void processAppearedMatch(IPatternMatch signature) {
        Object target = getTargetValue(signature);
        InternalEObject source = getSourceValue(signature);
        if (target != null) {
            if (updateMemory.get(source) != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[QueryBasedFeature] Space-time continuum breached (should never happen): multiple values for single feature!\n");
                sb.append("\n >> First value: ").append(source).append(" -> ").append(updateMemory.get(source));
                sb.append("\n >> Second value: ").append(source).append(" -> ").append(target);
                ViatraQueryLoggingUtil.getLogger(getClass()).error(sb.toString());
            } else {
                // must handle later (either in lost matches or after that)
                updateMemory.put(source, target);
            }
        }
    }
    
    protected void processDisappearedMatch(IPatternMatch signature) {
        Object target = getTargetValue(signature);
        InternalEObject source = (InternalEObject) getSourceValue(signature);
        if (target != null) {
            Object updateValue = updateMemory.get(source);
            if (updateValue != null) {
                appendNotificationToList(new ENotificationImpl(source, Notification.SET, getFeature(), target,
                        updateValue));
                setSingleRefMemory(source, updateValue);
                updateMemory.remove(source);
            } else {
                appendNotificationToList(new ENotificationImpl(source, Notification.SET, getFeature(), target, null));
                setSingleRefMemory(source, null);
            }
        }
    }
    
    /**
     * @param singleRefMemory
     *            the singleRefMemory to set
     */
    private void setSingleRefMemory(InternalEObject source, Object singleRefMemory) {
        if (isCached()) {
            this.singleRefMemory.put(source, singleRefMemory);
        }
    }
    
    protected void afterUpdate() {
        if (!updateMemory.isEmpty()) {
            for (InternalEObject source : updateMemory.keySet()) {
                appendNotificationToList(new ENotificationImpl(source, Notification.SET, getFeature(), null,
                        updateMemory.get(source)));
                setSingleRefMemory(source, updateMemory.get(source));
            }
            updateMemory.clear();
        }
    }

    protected void beforeUpdate() {
        updateMemory.clear();
    }

    @Override
    public QueryBasedFeatureKind getKind() {
        return QueryBasedFeatureKind.SINGLE_REFERENCE;
    }

    @Override
    public Object getValue(Object source) {
        return getSingleReferenceValue(source);
    }
    
}
