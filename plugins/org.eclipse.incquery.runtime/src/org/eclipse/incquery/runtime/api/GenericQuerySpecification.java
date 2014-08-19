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

/**
 * This is a generic query specification for IncQuery pattern matchers, for "interpretative" query execution. 
 * Should be subclassed by query specification implementations specific to query languages.
 *
 * <p>
 * When available, consider using the pattern-specific generated matcher API instead.
 *
 * <p>
 * The created matcher will be of type GenericPatternMatcher. Matches of the pattern will be represented as
 * GenericPatternMatch.
 *
 * @see GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public abstract class GenericQuerySpecification<Matcher extends GenericPatternMatcher> extends
		BaseQuerySpecification<Matcher> {

	@Override
	public GenericPatternMatch newEmptyMatch() {
		return GenericPatternMatch.newEmptyMatch(this);
	}

	@Override
	public GenericPatternMatch newMatch(Object... parameters) {
		return GenericPatternMatch.newMatch(this, parameters);
	}

}