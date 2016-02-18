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
package org.eclipse.viatra.transformation.views.traceablilty.generic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra.query.runtime.api.GenericQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Abstract IQuerySpecification implementation for resolving traced objects defined in annotations.
 * 
 * @author Csaba Debreceni
 *
 * @param <Matcher>
 */
public class GenericReferencedQuerySpecification extends
        GenericQuerySpecification<GenericPatternMatcher> {

    private IQuerySpecification<?> baseSpecification;

    public GenericReferencedQuerySpecification(GenericReferencedPQuery wrappedPQuery, IQuerySpecification<?> baseSpecification) {
        super(wrappedPQuery);        
        this.baseSpecification = baseSpecification;
    }
    
    @Override
    public Class<? extends QueryScope> getPreferredScopeClass() {
        return EMFScope.class;
    }
    
    @Override
    protected GenericPatternMatcher instantiate(ViatraQueryEngine engine) throws ViatraQueryException {
         GenericPatternMatcher matcher = defaultInstantiate(engine);
         return matcher;
    }
    
    public static GenericReferencedQuerySpecification initiate(IQuerySpecification<?> specification, Multimap<PParameter, PParameter> traceSources,
            Map<PParameter, String> traceIds, String traceabilityId) throws QueryInitializationException {
        
        GenericReferencedPQuery query = calculateReferencedQuery(specification.getInternalQueryRepresentation(), traceSources, traceIds, traceabilityId);        
        return new GenericReferencedQuerySpecification(query, specification);
    }
    
    private static GenericReferencedPQuery calculateReferencedQuery(PQuery original, Multimap<PParameter, PParameter> traceSources,
            Map<PParameter, String> traceIds, String traceabilityId) throws QueryInitializationException {        
        return new GenericReferencedPQuery(original, traceSources, traceIds, traceabilityId);
    }

    protected Multimap<PParameter, PParameter> getReferenceSources() {
        return ((GenericReferencedPQuery) getInternalQueryRepresentation()).getReferenceSources();
    }
    
    public final Set<PParameter> getReferenceParameters() {
        return ((GenericReferencedPQuery) getInternalQueryRepresentation()).getReferenceParameters();
    }
    
    public final IQuerySpecification<?> getBaseSpecification() {
        return baseSpecification;
    }
    
    public IPatternMatch createFromBaseMatch(IPatternMatch base) {
        Collection<Object> objs = Lists.newArrayList(); 
        for (String param : getParameterNames()) {
            if(base.parameterNames().contains(param))
                objs.add(base.get(param));
            else
                objs.add(null);
        }
        
        return this.newMatch(objs.toArray());
    }
}
