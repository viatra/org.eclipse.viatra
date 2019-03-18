/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests.api

import com.google.inject.Inject
import java.util.Map
import java.util.Map.Entry
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.CommonQueryHintOptions
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.MappingTraceCollector
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.viatra.query.testing.core.coverage.CoverageState
import org.eclipse.viatra.query.testing.core.coverage.ReteCoverage
import org.eclipse.viatra.query.testing.core.coverage.ReteNetworkTrace
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(XtextRunner) 
@InjectWith(CustomizedEMFPatternLanguageInjectorProvider)
class CoverageTest {
    @Inject package ParseHelper<PatternModel> parseHelper

    /** 
     * Test basic coverage functionality
     */
    @Test def void coverageTest() throws Exception {
        val ResourceSetImpl rs = new ResourceSetImpl()
         val IRewriterTraceCollector traceCollector = new MappingTraceCollector();
        val Map<QueryHintOption<?>, Object> hints = newHashMap(CommonQueryHintOptions.normalizationTraceCollector -> traceCollector);
        
        val engineOptions = ViatraQueryEngineOptions.defineOptions.withDefaultHint(new QueryEvaluationHint(hints, ReteBackendFactory.INSTANCE)).build
        val ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs), engineOptions)
        val String patternCode ='''
        package org.eclipse.viatra.query.patternlanguage.emf.tests
        import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
        pattern p(p) = {
            Pattern(p);
        }'''
        
        val PatternModel model = parseHelper.parse(patternCode)
        
        val specification = new SpecificationBuilder().getOrCreateSpecification(model.patterns.head)
        val matcher = engine.getMatcher(specification)
        
        val tracer = new ReteNetworkTrace(matcher, traceCollector)
        val emptyCoverage = new ReteCoverage(matcher).reteCoverage
        val matcherCoverage = tracer.traceCoverage(matcher, emptyCoverage)
        for (Entry<PTraceable, CoverageState> entry : matcherCoverage.elementCoverage.entrySet()) {
            System.out.println('''«entry.getKey()» -> «entry.getValue()»''')
        }
        Assert.assertTrue(matcherCoverage.elementCoverage.values.forall[it == CoverageState.NOT_COVERED])
        Assert.assertEquals(0f, matcherCoverage.aggregatedCoveragePercent, 0)

        val Resource resource = rs.createResource(URI.createURI("dummy.xmi"))
        resource.contents.add(model)
        
        val fullCoverage = new ReteCoverage(matcher).reteCoverage
        val fullmatcherCoverage = tracer.traceCoverage(matcher, fullCoverage);
        for (Entry<PTraceable, CoverageState> entry : fullmatcherCoverage.elementCoverage.entrySet()) {
            System.out.println('''«entry.getKey()» -> «entry.getValue()»''')
        }
        Assert.assertTrue(fullmatcherCoverage.elementCoverage.values.forall[it == CoverageState.COVERED])
        Assert.assertEquals(100f, fullmatcherCoverage.aggregatedCoveragePercent, 0)
    }
}
