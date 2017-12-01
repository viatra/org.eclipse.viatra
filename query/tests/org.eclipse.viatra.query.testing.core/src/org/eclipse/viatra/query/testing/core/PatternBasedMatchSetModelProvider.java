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
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess;
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord;

public class PatternBasedMatchSetModelProvider implements IMatchSetModelProvider {

    private QueryEvaluationHint engineHints;
    private SnapshotHelper helper;
    private AdvancedViatraQueryEngine engine;

    public PatternBasedMatchSetModelProvider(QueryEvaluationHint hint) {
        this(hint, new HashMap<String, JavaObjectAccess>());
    }

    public PatternBasedMatchSetModelProvider(QueryEvaluationHint engineHints, Map<String, JavaObjectAccess> accessmap) {
        this.engineHints = engineHints;
        this.helper = new SnapshotHelper(accessmap);
    }

    /**
     * Subclasses may override this method to customize engine options
     * 
     * @since 1.6
     */
    protected ViatraQueryEngineOptions getEngineOptions(){
        return ViatraQueryEngineOptions.DEFAULT;
    }
    
    /**
     * Subclasses may override this method to customize engine creation or to access created engine.
     * 
     * @since 1.6
     */
    protected AdvancedViatraQueryEngine getOrCreateEngine(EMFScope scope) {
        if (engine == null) {
            engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope, getEngineOptions());
        }
        return engine;
    }
    
    /**
     * Subclasses may override this method to customize matcher creation or to access created matcher.
     * 
     * @since 1.6
     */
    protected <Match extends IPatternMatch> ViatraQueryMatcher<Match> createMatcher(
            EMFScope scope, 
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, 
            QueryEvaluationHint hint) throws ViatraQueryException{
        return getOrCreateEngine(scope).getMatcher(querySpecification, hint);
    }
    
    @Override
    public <Match extends IPatternMatch> MatchSetRecord getMatchSetRecord(EMFScope scope,
            IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, Match filter)
            throws ViatraQueryException {
  
        ViatraQueryMatcher<Match> matcher = createMatcher(scope, querySpecification, engineHints);
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
