/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.incquery.testing.core;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord;

/**
 * 
 *
 */
public class PatternBasedMatchSetModelProvider implements IMatchSetModelProvider {

    private QueryEvaluationHint hint;

    public PatternBasedMatchSetModelProvider(QueryEvaluationHint hint) {
        this.hint = hint;
    }

    private AdvancedIncQueryEngine engine;

    @Override
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet resourceSet,
            IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, Match filter)
                    throws IncQueryException {
        engine = AdvancedIncQueryEngine.createUnmanagedEngine(new EMFScope(resourceSet));
        IncQueryMatcher<Match> matcher = (IncQueryMatcher<Match>) ((AdvancedIncQueryEngine) engine)
                .getMatcher(querySpecification, hint);
        return new SnapshotHelper().createMatchSetRecordForMatcher(matcher,
                filter == null ? matcher.newEmptyMatch() : filter);

    }

    @Override
    public void dispose() {
        if (engine != null) {
            engine.dispose();
            engine = null;
        }
    }
}
