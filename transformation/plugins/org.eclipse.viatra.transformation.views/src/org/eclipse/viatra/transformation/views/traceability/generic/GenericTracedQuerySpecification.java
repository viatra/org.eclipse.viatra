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

import java.util.Set;

import org.eclipse.viatra.query.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra.query.runtime.api.GenericQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

import com.google.common.collect.Multimap;

/**
 * Abstract IQuerySpecification implementation for resolving traced objects defined in annotations.
 * 
 * @author Csaba Debreceni
 *
 */
public class GenericTracedQuerySpecification extends GenericQuerySpecification<GenericPatternMatcher> {

    private GenericReferencedQuerySpecification referencedQuerySpecification;

    private GenericTracedQuerySpecification(GenericTracedPQuery wrappedPQuery,
            GenericReferencedQuerySpecification referencedQuerySpecification) {
        super(wrappedPQuery);
        this.referencedQuerySpecification = referencedQuerySpecification;
    }

    public static GenericTracedQuerySpecification initiate(GenericReferencedQuerySpecification referenced) throws QueryInitializationException {
        return new GenericTracedQuerySpecification(GenericTracedQuerySpecification.calculateTracedQuery((GenericReferencedPQuery) referenced
                .getInternalQueryRepresentation()), referenced);
    }

    private static GenericTracedPQuery calculateTracedQuery(GenericReferencedPQuery referencedQuery)
            throws QueryInitializationException {
        return new GenericTracedPQuery(referencedQuery);
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

    public Multimap<PParameter, PParameter> getReferenceSources() {
        return ((GenericTracedPQuery) getInternalQueryRepresentation()).getReferenceSources();
    }

    public final Set<PParameter> getReferenceParameters() {
        return ((GenericTracedPQuery) getInternalQueryRepresentation()).getReferenceParameters();
    }

    public GenericReferencedQuerySpecification getReferencedSpecification() {
        return referencedQuerySpecification;
    }
    
    @Override
    public GenericPatternMatcher instantiate() throws ViatraQueryException {
        return new GenericPatternMatcher(this);
    }
}
