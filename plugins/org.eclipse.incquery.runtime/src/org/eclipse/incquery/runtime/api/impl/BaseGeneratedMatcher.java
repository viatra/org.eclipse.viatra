/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Provides common functionality of pattern-specific generated matchers.
 * 
 * Currently unused.
 * 
 * @author Bergmann Gábor
 * @param <Match>
 * 
 */
public abstract class BaseGeneratedMatcher<Match extends IPatternMatch> extends BaseMatcher<Match> {


    public BaseGeneratedMatcher(IncQueryEngine engine,
            IQuerySpecification<? extends BaseMatcher<Match>> querySpecification) throws IncQueryException {
        super(engine, querySpecification);
    }


}
