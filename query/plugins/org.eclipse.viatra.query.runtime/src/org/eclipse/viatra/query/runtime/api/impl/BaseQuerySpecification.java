/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.api.impl;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * Base implementation of IQuerySpecification.
 *
 * @author Gabor Bergmann
 *
 */
public abstract class BaseQuerySpecification<Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> implements
        IQuerySpecification<Matcher> {

    
	protected final PQuery wrappedPQuery;
	
    protected abstract Matcher instantiate(ViatraQueryEngine engine) throws ViatraQueryException;

    
    
    
    /**
     * Instantiates query specification for the given internal query representation.
	 */
	public BaseQuerySpecification(PQuery wrappedPQuery) {
		super();
		this.wrappedPQuery = wrappedPQuery;
		wrappedPQuery.publishedAs().add(this);
	}


	@Override
    public PQuery getInternalQueryRepresentation() {
    	return wrappedPQuery;
    }
    
    @Override
    public Matcher getMatcher(Notifier emfRoot) throws ViatraQueryException {
        ViatraQueryEngine engine = ViatraQueryEngine.on(emfRoot);
        ensureInitializedInternal();
        return getMatcher(engine);
    }

    @Override
    public Matcher getMatcher(ViatraQueryEngine engine) throws ViatraQueryException {
    	ensureInitializedInternal();
    	if (engine.getScope().isCompatibleWithQueryScope(this.getPreferredScopeClass()))
    		return instantiate(engine);
    	else throw new ViatraQueryException(
    			String.format(
    					"Scope class incompatibility: the query %s is formulated over query scopes of class %s, " + 
    					" thus the query engine formulated over scope %s of class %s cannot evaluate it.", 
    					this.getFullyQualifiedName(), this.getPreferredScopeClass().getCanonicalName(),
    					engine.getScope(), engine.getScope().getClass().getCanonicalName()), 
    			"Incompatible scope classes of engine and query.");
    }

	protected void ensureInitializedInternal() throws ViatraQueryException {
		try {
			wrappedPQuery.ensureInitialized();
		} catch (QueryInitializationException e) {
			throw new ViatraQueryException(e);
		}
	}
	protected void ensureInitializedInternalSneaky() {
		try {
			wrappedPQuery.ensureInitialized();
		} catch (QueryInitializationException e) {
			throw new RuntimeException(e);
		}
	}
	
	

    // // EXPERIMENTAL
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain) throws ViatraQueryException {
    // return getMatcher(trDomain, 0);
    // }
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain, int numThreads) throws ViatraQueryException {
    // try {
    // ViatraQueryEngine engine = EngineManager.getInstance().getReteEngine(trDomain, numThreads);
    // return instantiate(engine);
    // } catch (RetePatternBuildException e) {
    // throw new ViatraQueryException(e);
    // }
    // }
	
	
	// // DELEGATIONS
	
	@Override
	public List<PAnnotation> getAllAnnotations() {
		return wrappedPQuery.getAllAnnotations();
	}
	@Override
	public List<PAnnotation> getAnnotationsByName(String annotationName) {
		return wrappedPQuery.getAnnotationsByName(annotationName);
	}
	@Override
	public PAnnotation getFirstAnnotationByName(String annotationName) {
		return wrappedPQuery.getFirstAnnotationByName(annotationName);
	}
	@Override
	public String getFullyQualifiedName() {
		return wrappedPQuery.getFullyQualifiedName();
	}
	@Override
	public List<String> getParameterNames() {
		return wrappedPQuery.getParameterNames();
	}
	@Override
	public List<PParameter> getParameters() {
		return wrappedPQuery.getParameters();
	}
	@Override
	public Integer getPositionOfParameter(String parameterName) {
		return wrappedPQuery.getPositionOfParameter(parameterName);
	}

}
