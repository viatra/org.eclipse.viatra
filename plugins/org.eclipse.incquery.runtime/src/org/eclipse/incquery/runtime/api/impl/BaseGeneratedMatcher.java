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

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.matcher.RetePatternMatcher;

/**
 * Provides common functionality of pattern-specific generated matchers.
 * 
 * @author Bergmann Gábor
 * @param <Signature>
 * 
 */
public abstract class BaseGeneratedMatcher<Signature extends IPatternMatch> extends BaseMatcher<Signature> {

    protected IQuerySpecification<? extends BaseGeneratedMatcher<Signature>> querySpecification;

    public BaseGeneratedMatcher(IncQueryEngine engine,
            IQuerySpecification<? extends BaseGeneratedMatcher<Signature>> querySpecification) throws IncQueryException {
        super(engine, accessMatcher(engine, querySpecification.getPattern()), querySpecification.getPattern());
        this.querySpecification = querySpecification;
    }

    static RetePatternMatcher accessMatcher(IncQueryEngine engine, Pattern pattern) throws IncQueryException {
        checkPattern(engine, pattern);
        try {
            return engine.getReteEngine().accessMatcher(pattern);
        } catch (RetePatternBuildException e) {
            throw new IncQueryException(e);
        }
    }

    @Override
    public Pattern getPattern() {
        return querySpecification.getPattern();
    }

    @Override
    public String getPatternName() {
        return querySpecification.getPatternFullyQualifiedName();
    }

}
