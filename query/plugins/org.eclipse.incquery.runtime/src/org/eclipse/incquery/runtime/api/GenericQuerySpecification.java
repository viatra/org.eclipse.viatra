/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api;

import org.eclipse.incquery.runtime.api.impl.BaseQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * This is a generic query specification for IncQuery pattern matchers, for "interpretative" query execution. 
 * Should be subclassed by query specification implementations specific to query languages.
 *
 * <p>
 * When available, consider using the pattern-specific generated matcher API instead.
 *
 * <p>
 * The created matcher will be of type {@link GenericPatternMatcher}. Matches of the pattern will be represented as
 * {@link GenericPatternMatch}.
 * 
 * <p>
 * Note for overriding (if you have your own query language or ): 
 * Derived classes should use {@link #defaultInstantiate(IncQueryEngine)} for implementing 
 * {@link #instantiate(IncQueryEngine)} if they use {@link GenericPatternMatcher} proper.
 *
 * @see GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 * @noinstantiate This class is not intended to be instantiated by end-users.
 * @since 0.9
 */
public abstract class GenericQuerySpecification<Matcher extends GenericPatternMatcher> extends
		BaseQuerySpecification<Matcher> {

    /**
     * Instantiates query specification for the given internal query representation.
	 */
	public GenericQuerySpecification(PQuery wrappedPQuery) {
		super(wrappedPQuery);
	}

	@Override
	public GenericPatternMatch newEmptyMatch() {
		return GenericPatternMatch.newEmptyMatch(this);
	}

	@Override
	public GenericPatternMatch newMatch(Object... parameters) {
		return GenericPatternMatch.newMatch(this, parameters);
	}

	/**
	 * Derived classes should use this implementation of {@link #instantiate(IncQueryEngine)} 
	 * if they use {@link GenericPatternMatcher} proper.
	 */
	protected GenericPatternMatcher defaultInstantiate(IncQueryEngine engine) throws IncQueryException {
		return GenericPatternMatcher.instantiate(engine, this);
	}

}