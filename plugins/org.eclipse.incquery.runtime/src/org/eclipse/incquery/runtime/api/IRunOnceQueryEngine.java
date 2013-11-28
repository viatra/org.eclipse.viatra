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
package org.eclipse.incquery.runtime.api;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A run-once query engine is used to get matches for queries without incremental support.
 * Users can create a query engine with a given {@link Notifier} as scope and use a query specification
 * to retrieve the current match set with this scope (see {@link #getAllMatches}).
 * 
 * @author Abel Hegedus
 * 
 */
public interface IRunOnceQueryEngine {

    /**
     * Returns the set of all matches for the given query in the scope of the engine.
     * 
     * @param querySpecification the query that is evaluated
     * @return matches represented as a Match object.
     */
    <Match extends IPatternMatch> Collection<Match> getAllMatches(
            final IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification) throws IncQueryException;

    /**
     * @return the scope of pattern matching, i.e. the root of the EMF model tree that this engine is attached to.
     */
    Notifier getScope();
    
    /**
     * The base index options specifies how the base index is built, including wildcard mode (defaults to false) and
     * dynamic EMF mode (defaults to false). See {@link NavigationHelper} for the explanation of wildcard mode and
     * dynamic EMF mode.
     * 
     * <p/> The returned options can be modified in order to affect subsequent calls of {@link #getAllMatches}.
     * 
     * @return the base index options used by the engine. 
     */
    BaseIndexOptions getBaseIndexOptions(); 
}
