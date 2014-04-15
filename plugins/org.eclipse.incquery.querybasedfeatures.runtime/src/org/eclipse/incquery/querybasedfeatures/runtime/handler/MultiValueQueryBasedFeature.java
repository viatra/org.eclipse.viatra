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
package org.eclipse.incquery.querybasedfeatures.runtime.handler;

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
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeature;
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureKind;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public class MultiValueQueryBasedFeature extends QueryBasedFeature {

    /*
     * could use EObjectEList or similar to have notifications handled by EMF, but notification sending must be delayed
     * in order to avoid infinite notification loop
     */
    private final Map<InternalEObject, List<Object>> manyRefMemory = new HashMap<InternalEObject, List<Object>>();
    
    /**
     * @param feature
     * @param kind
     * @param keepCache
     */
    protected MultiValueQueryBasedFeature(EStructuralFeature feature,  boolean keepCache) {
        super(feature, keepCache);
    }

    public List<?> getManyReferenceValue(Object source) {
        if (isCached()) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                values = new BasicEList<Object>();
            }
            return values;
        } else {
            final List<Object> values = new BasicEList<Object>();
            if (!isInitialized()) {
                return values;
            }
            IPatternMatch match = getMatcher().newEmptyMatch();
            match.set(getSourceParamName(), source);
            getMatcher().forEachMatch(match, new IMatchProcessor<IPatternMatch>() {

                @Override
                public void process(IPatternMatch match) {
                    values.add(getTargetValue(match));
                }
            });
            return values;// matcher.getAllValues(targetParamName, match);
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
        InternalEObject source = (InternalEObject) getSourceValue(signature);
        if (target != null) {
            appendNotificationToList(new ENotificationImpl(source, Notification.REMOVE, getFeature(), target, null));
            removeFromManyRefMemory(source, target);
        }
    }
    
    private void addToManyRefMemory(InternalEObject source, Object added) {
        if (isCached()) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                values = new BasicEList<Object>();
                manyRefMemory.put(source, values);
            }
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
                IncQueryLoggingUtil.getLogger(getClass()).error(sb.toString());
            }
            values.remove(removed);
        }
    }

    @SuppressWarnings("rawtypes")
    public EList getManyReferenceValueAsEList(Object source) {
        Collection<?> values = getManyReferenceValue(source);
        if (values.size() > 0) {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, getFeature(), values.size(), values.toArray());
        } else {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, getFeature(), 0, null);
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
