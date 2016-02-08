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

package org.eclipse.viatra.query.patternlanguage.emf.specification;

import org.eclipse.viatra.query.runtime.api.GenericMatchProcessor;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.IncQueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

/**
 * This is a generic query specification for EMF-IncQuery pattern matchers, for "interpretative" query execution. Instantiate the
 * specification with any registered pattern (or through a {@link SpecificationBuilder} instance), and then use the specification 
 * to obtain an actual pattern matcher operating on a given model.
 *
 * <p>
 * When available, consider using the pattern-specific generated matcher API instead.
 *
 * <p>
 * The created matcher will be of type org.eclipse.viatra.query.runtime.api.GenericPatternMatcher. Matches of the pattern will be represented as
 * GenericPatternMatch.
 *
 * @see org.eclipse.viatra.query.runtime.api.GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public class GenericQuerySpecification 
	extends org.eclipse.viatra.query.runtime.api.GenericQuerySpecification<GenericPatternMatcher> 
{
	protected GenericEMFPatternPQuery genericEMFPatternPQuery;

	public GenericQuerySpecification(GenericEMFPatternPQuery genericEMFPatternPQuery) {
		super(genericEMFPatternPQuery);
		this.genericEMFPatternPQuery = genericEMFPatternPQuery;
	}

	@Override
	public GenericEMFPatternPQuery getInternalQueryRepresentation() {
		return genericEMFPatternPQuery;
	}

    @Override
    public GenericPatternMatcher instantiate(IncQueryEngine engine) throws IncQueryException {
        return GenericPatternMatcher.on(engine, this);
    }

	@Override
	public Class<? extends IncQueryScope> getPreferredScopeClass() {
		return EMFScope.class;
	}

	
    
}
