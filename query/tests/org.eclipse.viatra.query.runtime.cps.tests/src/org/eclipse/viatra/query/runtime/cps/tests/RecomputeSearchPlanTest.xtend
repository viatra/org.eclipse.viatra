/*******************************************************************************
 * Copyright (c) 2014-2016 Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import com.google.common.collect.Sets
import java.util.Collection
import junit.framework.AssertionFailedError
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.cps.tests.queries.SimpleCpsQueries
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.testing.core.MatchSetRecordDiff
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class RecomputeSearchPlanTest {
    @Parameters(name = "Backend: {0}, Model: {1}")
    def static Collection<Object[]> testData() {
        newArrayList(Sets.cartesianProduct(
            newHashSet(BackendType.LocalSearch, BackendType.LocalSearch_Generic),
            #{"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"},
            SimpleCpsQueries.instance.specifications
        ).map[it.toArray])
    }
    
    @Parameter(0)
    public BackendType backendType
    @Parameter(1)
    public String modelPath
    @Parameter(2)
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification
    ResourceSet rs
    EMFScope scope
    
    private static class MatchSetModelProvider extends PatternBasedMatchSetModelProvider {
        
        new(QueryEvaluationHint hint) {
            super(hint)
        }
        
        override getOrCreateEngine(EMFScope scope) {
                return super.getOrCreateEngine(scope);
        }
    }
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
        scope = new EMFScope(rs)
    }

    @Test
    def void recomputePlanTest() {
        val hint = backendType.hints
        val modelProvider = new MatchSetModelProvider(hint)
        val engine = modelProvider.getOrCreateEngine(scope)
        val backend = engine.getQueryBackend(hint.queryBackendFactory) as LocalSearchBackend
        
        val firstMatchSet = modelProvider.getMatchSetRecord(scope, querySpecification, null)
        
        backend.recomputePlans
        val secondMatchSet = modelProvider.getMatchSetRecord(scope, querySpecification, null)
        val diff = MatchSetRecordDiff.compute(firstMatchSet, secondMatchSet)
        if (!diff.empty) {
            throw new AssertionFailedError(diff.toString)
        }
    }
    
}
