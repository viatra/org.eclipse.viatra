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

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * Provides common functionality of pattern-specific generated query specifications over the EMF scope.
 *
 * @author Bergmann Gábor
 * @author Mark Czotter
 */
public abstract class BaseGeneratedEMFQuerySpecification<Matcher extends IncQueryMatcher<? extends IPatternMatch>> extends
        BaseQuerySpecification<Matcher> {
	
	
    /**
     * Instantiates query specification for the given internal query representation.
	 */
    public BaseGeneratedEMFQuerySpecification(PQuery wrappedPQuery) {
        super(wrappedPQuery);
        // ensureInitializedInternalSneaky();
    }
    
    protected static IncQueryException processInitializerError(ExceptionInInitializerError err) {
        Throwable cause1 = err.getCause();
        if (cause1 instanceof RuntimeException) {
            Throwable cause2 = ((RuntimeException) cause1).getCause();
            if (cause2 instanceof IncQueryException) {
                return (IncQueryException) cause2;
            } else if (cause2 instanceof QueryInitializationException) {
                return new IncQueryException((QueryInitializationException) cause2);
            } 
        }
        throw err;
    }
        
	@Override
	public Class<? extends IncQueryScope> getPreferredScopeClass() {
		return EMFScope.class;
	}
    
}
