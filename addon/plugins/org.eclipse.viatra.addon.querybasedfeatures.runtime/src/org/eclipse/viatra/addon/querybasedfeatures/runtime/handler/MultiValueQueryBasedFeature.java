/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public class MultiValueQueryBasedFeature extends QueryBasedFeature {

    /*
     * could use EObjectEList or similar to have notifications handled by EMF, but notification sending must be delayed
     * in order to avoid infinite notification loop
     */
    private final Map<InternalEObject, List<Object>> manyRefMemory = new HashMap<>();
    
    protected MultiValueQueryBasedFeature(EStructuralFeature feature,  boolean keepCache) {
        super(feature, keepCache);
    }

    public List<?> getManyReferenceValue(Object source) {
        if (isCached()) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                values = new BasicEList<>();
            }
            return values;
        } else {
            final List<Object> values = new BasicEList<>();
            if (!isInitialized()) {
                return values;
            }
            IPatternMatch match = getMatcher().newEmptyMatch();
            match.set(getSourceParamName(), source);
            getMatcher().forEachMatch(match, m -> values.add(getTargetValue(m)));
            return values;
        }
    }

    protected void processAppearedMatch(IPatternMatch signature) {
        Object target = getTargetValue(signature);
        InternalEObject source = getSourceValue(signature);
        if (target != null) {
            appendNotificationToList(new ENotificationImpl(source, Notification.ADD, getFeature(), null, target));
            addToManyRefMemory(source, target);
        }
    }
    
    protected void processDisappearedMatch(IPatternMatch signature) {
        Object target = getTargetValue(signature);
        InternalEObject source = getSourceValue(signature);
        if (target != null) {
            appendNotificationToList(new ENotificationImpl(source, Notification.REMOVE, getFeature(), target, null));
            removeFromManyRefMemory(source, target);
        }
    }
    
    private void addToManyRefMemory(InternalEObject source, Object added) {
        if (isCached()) {
            List<Object> values = manyRefMemory.computeIfAbsent(source, s -> new BasicEList<>());
            values.add(added);
        }
    }

    private void removeFromManyRefMemory(InternalEObject source, Object removed) {
        if (isCached()) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null || !values.contains(removed)) {
                StringBuilder sb = new StringBuilder();
                sb.append("[QueryBasedFeature] Space-time continuum breached (should never happen): removing value from list that doesn't contains it!");
                sb.append("\n >> Non-existing value: ").append(source).append(" -> ").append(removed);
                ViatraQueryLoggingUtil.getLogger(getClass()).error(sb.toString());
            }
            values.remove(removed);
        }
    }

    @SuppressWarnings("rawtypes")
    public EList getManyReferenceValueAsEList(Object source) {
        Collection<?> values = getManyReferenceValue(source);
        if (values.isEmpty()) {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, getFeature(), 0, null);
        } else {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, getFeature(), values.size(), values.toArray());
        }
    }

    @Override
    public QueryBasedFeatureKind getKind() {
        return QueryBasedFeatureKind.MANY_REFERENCE;
    }

    @Override
    protected void afterUpdate() {}

    @Override
    protected void beforeUpdate() {}

    @Override
    public Object getValue(Object source) {
        return getManyReferenceValueAsEList(source);
    }
}
