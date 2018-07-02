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
package org.eclipse.viatra.transformation.views.traceability.generic;

import java.util.Map;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

import com.google.common.collect.Multimap;

public abstract class AbstractQuerySpecificationDescriptor {

    protected GenericTracedQuerySpecification tracedSpecification;
    private IQuerySpecification<?> specification;
    private Multimap<PParameter, PParameter> traceSources;
    private Multimap<PParameter, PParameter> referencedTraceSources;
    private Map<PParameter, String> traceIds;

    public AbstractQuerySpecificationDescriptor(IQuerySpecification<?> specification,
            Multimap<PParameter, PParameter> traceSources, Map<PParameter, String> traceIds) {
        this(specification, traceSources, traceSources, traceIds);
        
    }
    public AbstractQuerySpecificationDescriptor(IQuerySpecification<?> specification,
            Multimap<PParameter, PParameter> traceSources, Multimap<PParameter, PParameter> referencedTraceSources, Map<PParameter, String> traceIds) {
        this.specification = specification;
        this.traceSources = traceSources;
        this.referencedTraceSources = referencedTraceSources;
        this.traceIds = traceIds;
    }

    /**
     * @throws ViatraQueryRuntimeException
     */
    public void initialize(String traceabilityId) {
        tracedSpecification = GenericTracedQuerySpecification.initiate(GenericReferencedQuerySpecification.initiate(
                specification, referencedTraceSources, traceIds, traceabilityId), traceSources);
    }

    public GenericTracedQuerySpecification getTracedSpecification() {
        return tracedSpecification;
    }
}
