/** 
 * Copyright (c) 2010-2015, Grill Balazs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balazs - initial API and implementation
 * Peter Lunk - EMFScope support added
 */
package org.eclipse.viatra.query.testing.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess;
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord;

public class PatternBasedMatchSetModelProvider implements IMatchSetModelProvider {

    private QueryEvaluationHint hint;
    private SnapshotHelper helper;

    public PatternBasedMatchSetModelProvider(QueryEvaluationHint hint) {
        this(hint,new HashMap<String, JavaObjectAccess>());
    }
    
    public PatternBasedMatchSetModelProvider(QueryEvaluationHint hint, Map<String, JavaObjectAccess> accessmap) {
        this.hint = hint;
        this.helper = new SnapshotHelper(accessmap);
    }

    private AdvancedViatraQueryEngine engine;

    @Override
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(EMFScope scope,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
                    throws ViatraQueryException {
        
        if (engine == null){
            engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
        }
        ViatraQueryMatcher<Match> matcher = (ViatraQueryMatcher<Match>) ((AdvancedViatraQueryEngine) engine)
                .getMatcher(querySpecification, hint);
        return helper.createMatchSetRecordForMatcher(matcher,
                filter == null ? matcher.newEmptyMatch() : filter);

    }

	@Override
	public boolean updatedByModify() {
		return true;
	}


    @Override
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(ResourceSet rs,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
            throws ViatraQueryException {
        
       return getMatchSetRecord(new EMFScope(rs), querySpecification, filter);
    }
    
    @Override
    public void dispose() {
        if (engine != null) {
            engine.dispose();
            engine = null;
        }
    }
}
