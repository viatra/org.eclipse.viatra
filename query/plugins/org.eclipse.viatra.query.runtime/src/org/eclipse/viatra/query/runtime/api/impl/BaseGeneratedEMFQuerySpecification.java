/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.api.impl;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * Provides common functionality of pattern-specific generated query specifications over the EMF scope.
 *
 * @author Bergmann Gábor
 * @author Mark Czotter
 */
public abstract class BaseGeneratedEMFQuerySpecification<Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> extends
        BaseQuerySpecification<Matcher> {
    
    
    /**
     * Instantiates query specification for the given internal query representation.
     */
    public BaseGeneratedEMFQuerySpecification(PQuery wrappedPQuery) {
        super(wrappedPQuery);
    }
    
    @Override
    public Class<? extends QueryScope> getPreferredScopeClass() {
        return EMFScope.class;
    }
    
}
