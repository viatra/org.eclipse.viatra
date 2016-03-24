/** 
 * Copyright (c) 2010-2015, Grill Bal�zs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Bal�zs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord;

public class PatternBasedMatchSetModelProvider implements IMatchSetModelProvider {

    private QueryEvaluationHint hint;

    public PatternBasedMatchSetModelProvider(QueryEvaluationHint hint) {
        this.hint = hint;
    }

    private AdvancedViatraQueryEngine engine;

    @Override
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet resourceSet,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
                    throws ViatraQueryException {
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resourceSet));
        ViatraQueryMatcher<Match> matcher = (ViatraQueryMatcher<Match>) ((AdvancedViatraQueryEngine) engine)
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

	@Override
	public boolean updatedByModify() {
		return true;
	}
}
