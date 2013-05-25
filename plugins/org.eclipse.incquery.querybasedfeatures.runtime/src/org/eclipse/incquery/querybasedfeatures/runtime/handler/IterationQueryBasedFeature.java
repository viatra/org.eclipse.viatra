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

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Abel Hegedus
 *
 */
public abstract class IterationQueryBasedFeature extends QueryBasedFeature {

    /**
     * @param feature
     * @param kind
     * @param keepCache
     */
    protected IterationQueryBasedFeature(EStructuralFeature feature, boolean keepCache) {
        super(feature, keepCache);
    }
    
    @Override
    protected void processDisappearedMatch(IPatternMatch signature) {
        ENotificationImpl notification = lostMatchIteration(signature);
        if(notification != null) {
            appendNotificationToList(notification);
        }
    }
    
    @Override
    protected void processAppearedMatch(IPatternMatch signature) {
        ENotificationImpl notification = newMatchIteration(signature);
        if (notification != null) {
            appendNotificationToList(notification);
        }
    }
    
    @Override
    protected void afterUpdate() {}
    
    @Override
    protected void beforeUpdate() {}

    /**
     * Called each time when a new match is found for Iteration kind
     * 
     * @param signature
     * @return notification to be sent, if one is necessary
     */
    protected abstract ENotificationImpl newMatchIteration(IPatternMatch signature);

    /**
     * Called each time when a match is lost for Iteration kind
     * 
     * @param signature
     * @return notification to be sent, if one is necessary
     */
    protected abstract ENotificationImpl lostMatchIteration(IPatternMatch signature);

    public abstract Object getValueIteration(Object source);
}
