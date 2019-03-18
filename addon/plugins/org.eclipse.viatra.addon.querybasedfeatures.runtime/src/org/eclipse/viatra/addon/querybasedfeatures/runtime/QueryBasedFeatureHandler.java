/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.MultiValueQueryBasedFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.SingleValueQueryBasedFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.SumQueryBasedFeature;

/**
 * This class is meant for handling clients with old generated code.
 * 
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatureHandler implements IQueryBasedFeatureHandler {

    private QueryBasedFeature queryBasedFeature;

    /**
     * 
     */
    protected QueryBasedFeatureHandler(QueryBasedFeature queryBasedFeature) {
        this.queryBasedFeature = queryBasedFeature;
        
    }
    
    @Override
    public Object getValue(Object source) {
        return queryBasedFeature.getValue(source);
    }

    @Override
    public int getIntValue(Object source) {
        if(queryBasedFeature instanceof SumQueryBasedFeature) {
            return ((SumQueryBasedFeature) queryBasedFeature).getIntValue(source);
        }
        throw new UnsupportedOperationException("Cannot return int value for this feature!");
    }

    @Override
    public Object getSingleReferenceValue(Object source) {
        if(queryBasedFeature instanceof SingleValueQueryBasedFeature) {
            return ((SingleValueQueryBasedFeature) queryBasedFeature).getSingleReferenceValue(source);
        }
        throw new UnsupportedOperationException("Cannot return single value for this feature!");
    }

    @Override
    public List<?> getManyReferenceValue(Object source) {
        if(queryBasedFeature instanceof MultiValueQueryBasedFeature) {
            return ((MultiValueQueryBasedFeature) queryBasedFeature).getManyReferenceValue(source);
        }
        throw new UnsupportedOperationException("Cannot return value list for this feature!");
    }

    @Override
    public EList getManyReferenceValueAsEList(Object source) {
        if(queryBasedFeature instanceof MultiValueQueryBasedFeature) {
            return ((MultiValueQueryBasedFeature) queryBasedFeature).getManyReferenceValueAsEList(source);
        }
        throw new UnsupportedOperationException("Cannot return EList of values for this feature!");
    }

}
