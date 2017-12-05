/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.internal

import java.util.Map
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.api.IPatternExecutionAnalyzer
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess

/**
 * This implementation adds support to call analyzers
 * 
 * @since 1.6
 */
class AnalyzedPatternBasedMatchSetModelProvider extends PatternBasedMatchSetModelProvider {
    
    private final Iterable<? extends IPatternExecutionAnalyzer> analyzers;
    
    new(QueryEvaluationHint hint, Map<String, JavaObjectAccess> accessmap, Iterable<? extends IPatternExecutionAnalyzer> analyzers) {
        super(hint, accessmap)
        this.analyzers = analyzers;
    }
    
    override protected getEngineOptions() {
        val superOptions = super.engineOptions
        val updatedHint = analyzers.fold(superOptions.engineDefaultHints, [r, t | t.configure(r)])
        ViatraQueryEngineOptions.copyOptions(superOptions).withDefaultHint(updatedHint).build
    }
    
    override protected <Match extends IPatternMatch> createMatcher(
        EMFScope scope, 
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, 
        QueryEvaluationHint hint
        ) {
        val matcher = super.createMatcher(scope, querySpecification, hint)
        analyzers.forEach[processMatcher(matcher)]
        return matcher;
    }
    
}