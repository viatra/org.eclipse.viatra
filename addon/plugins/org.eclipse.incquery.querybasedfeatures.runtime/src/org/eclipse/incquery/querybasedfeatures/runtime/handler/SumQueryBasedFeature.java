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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureKind;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * FIXME write AggregateHandler if any EDataType should be allowed TODO notifications could be static final? to ensure
 * message ordering
 * 
 * @author Abel Hegedus
 * 
 * 
 */
public class SumQueryBasedFeature extends IterationQueryBasedFeature {

    private final Map<InternalEObject, Integer> counterMemory = new HashMap<InternalEObject, Integer>();

    /**
     * @param feature
     * @param kind
     * @param keepCache
     */
    protected SumQueryBasedFeature(EStructuralFeature feature, QueryBasedFeatureKind kind) {
        super(feature, false);
        if (!(feature instanceof EAttribute)) {
            IncQueryLoggingUtil.getLogger(getClass()).error(
                    "[IncqueryFeatureHandler] Invalid configuration (Aggregate can be used only with EAttribute)!");
        }
    }

    @Override
    protected ENotificationImpl newMatchIteration(IPatternMatch signature) {
        InternalEObject source = getSourceValue(signature);
        Integer oldValue = getIntValue(source);
        Integer delta = (Integer) getTargetValue(signature);
        if (delta != null && oldValue <= Integer.MAX_VALUE - delta) {
            int tempMemory = oldValue + delta;
            counterMemory.put(source, tempMemory);
            return new ENotificationImpl(source, Notification.SET, getFeature(), getIntValue(source), tempMemory);
        } else {
            IncQueryLoggingUtil
                    .getLogger(getClass())
                    .error(String
                            .format("[IncqueryFeatureHandler] Exception during update: The counter of %s for feature %s reached the maximum value of int!",
                                    source, getFeature()));
        }
        return null;
    }

    @Override
    protected ENotificationImpl lostMatchIteration(IPatternMatch signature) {
        InternalEObject source = getSourceValue(signature);
        Integer delta = (Integer) getTargetValue(signature);
        Integer value = counterMemory.get(source);
        if (value == null) {
            IncQueryLoggingUtil
                    .getLogger(getClass())
                    .error("[IncqueryFeatureHandler] Space-time continuum breached (should never happen): decreasing a counter with no previous value");
        } else if (value >= delta) {
            int tempMemory = value - delta;
            int oldValue = value;
            counterMemory.put(source, tempMemory);
            return new ENotificationImpl(source, Notification.SET, getFeature(), oldValue, tempMemory);
        } else {
            IncQueryLoggingUtil
                    .getLogger(getClass())
                    .error(String
                            .format("[IncqueryFeatureHandler] Exception during update: The counter of %s for feature %s cannot go below zero!",
                                    source, getFeature()));
        }
        return null;
    }

    @Override
    public Object getValueIteration(Object source) {
        return getIntValue(source);
    }

    public int getIntValue(Object source) {
        Integer result = counterMemory.get(source);
        if (result == null) {
            result = 0;
        }
        return result;
    }

    @Override
    public QueryBasedFeatureKind getKind() {
        return QueryBasedFeatureKind.SUM;
    }

    @Override
    public Object getValue(Object source) {
        return getIntValue(source);
    }

}
