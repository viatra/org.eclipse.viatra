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
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.api.GenericMatchProcessor;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * This is a generic pattern matcher for any VIATRA Query pattern, with
 * "interpretative" query execution. Use the pattern matcher on a given model
 * via {@link #on(ViatraQueryEngine, Pattern)}, e.g. in conjunction with
 * {@link ViatraQueryEngine#on(Notifier)}.
 * <p>
 * Whenever available, consider using the pattern-specific generated matcher API
 * instead.
 * 
 * <p>
 * Matches of the pattern will be represented as {@link GenericPatternMatch}.
 * 
 * @author Bergmann Gábor
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @see GenericQuerySpecification
 * 
 * @deprecated direct reference not recommended. As of 0.9, clients should use
 *             {@link SpecificationBuilder} to convert EMF patterns to
 *             {@link IQuerySpecification}s, or
 *             {@link org.eclipse.viatra.query.runtime.api.GenericPatternMatcher}
 *             for other purposes. Note that the class
 *             {@link GenericPatternMatch} has also been moved.
 */
@Deprecated
public class GenericPatternMatcher extends
		org.eclipse.viatra.query.runtime.api.GenericPatternMatcher {

	protected GenericPatternMatcher(ViatraQueryEngine engine,
			GenericQuerySpecification specification) throws ViatraQueryException {
		super(engine, specification);
	}

	/**
	 * Initializes the pattern matcher within an existing VIATRA Query engine.
	 * If the pattern matcher is already constructed in the engine, only a
	 * light-weight reference is returned. The match set will be incrementally
	 * refreshed upon updates.
	 * 
	 * @param engine
	 *            the existing VIATRA Query engine in which this matcher will be
	 *            created.
	 * @param pattern
	 *            the VIATRA Query pattern for which the matcher is to be
	 *            constructed.
	 * @throws ViatraQueryException
	 *             if an error occurs during pattern matcher creation
	 */
	public static GenericPatternMatcher on(ViatraQueryEngine engine,
			Pattern pattern) throws ViatraQueryException {
		try {
			return on(engine, new GenericQuerySpecification(
					new GenericEMFPatternPQuery(pattern)));
		} catch (QueryInitializationException e) {
			throw new ViatraQueryException(e);
		}
	}

	/**
	 * Initializes the pattern matcher within an existing VIATRA Query engine.
	 * If the pattern matcher is already constructed in the engine, only a
	 * light-weight reference is returned. The match set will be incrementally
	 * refreshed upon updates.
	 * 
	 * @param engine
	 *            the existing VIATRA Query engine in which this matcher will be
	 *            created.
	 * @param querySpecification
	 *            the query specification for which the matcher is to be
	 *            constructed.
	 * @throws ViatraQueryException
	 *             if an error occurs during pattern matcher creation
	 */
	public static GenericPatternMatcher on(ViatraQueryEngine engine,
			GenericQuerySpecification querySpecification)
			throws ViatraQueryException {
		// check if matcher already exists
		GenericPatternMatcher matcher = engine
				.getExistingMatcher(querySpecification);
		if (matcher == null) {
			matcher = new GenericPatternMatcher(engine, querySpecification);
			// do not have to "put" it into engine.matchers,
			// reportMatcherInitialized() will take care of it
		}
		return matcher;
	}

}
