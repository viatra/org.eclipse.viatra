/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewmodel.traceablilty.generic;

import java.util.Map;

import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;

import com.google.common.collect.Multimap;

public abstract class AbstractQuerySpecificationDescriptor {

    protected GenericTracedQuerySpecification tracedSpecification;
    private IQuerySpecification<?> specification;
    private Multimap<PParameter, PParameter> traceSources;
    private Map<PParameter, String> traceIds;

    public AbstractQuerySpecificationDescriptor(IQuerySpecification<?> specification,
            Multimap<PParameter, PParameter> traceSources, Map<PParameter, String> traceIds) {
        this.specification = specification;
        this.traceSources = traceSources;
        this.traceIds = traceIds;
    }

    public void initialize(String traceabilityId) throws QueryInitializationException {
        tracedSpecification = GenericTracedQuerySpecification.initiate(GenericReferencedQuerySpecification.initiate(
                specification, traceSources, traceIds, traceabilityId));
    }

    public GenericTracedQuerySpecification getTracedSpecification() {
        return tracedSpecification;
    }
}
