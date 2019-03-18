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
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.CommonQueryHintOptions
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.MappingTraceCollector
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(XtextRunner) 
@InjectWith(CustomizedEMFPatternLanguageInjectorProvider) 
class RewriterTraceTest {
    
    @Inject package ParseHelper<PatternModel> parseHelper

    @Test def void traceTest() throws Exception {
        var ResourceSetImpl rs = new ResourceSetImpl()
        val IRewriterTraceCollector traceCollector = new MappingTraceCollector();
        val Map<QueryHintOption<?>, Object> hints = newHashMap(CommonQueryHintOptions.normalizationTraceCollector -> traceCollector);
        
        var ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs), ViatraQueryEngineOptions.defineOptions.withDefaultHint(new QueryEvaluationHint(hints, ReteBackendFactory.INSTANCE)).build)
        var String patternCode = 
        '''
        package org.eclipse.viatra.query.patternlanguage.emf.tests
        import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
        pattern p(p : Pattern) = {
            Pattern(p);
        }
        '''
        val PatternModel model = parseHelper.parse(patternCode)
        val IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification = new SpecificationBuilder().
            getOrCreateSpecification(model.getPatterns().get(0))
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification)
                        
        val canonicalTraceables = PQueries.getTraceables(matcher.specification.internalQueryRepresentation)
        canonicalTraceables.forEach[canonicalTraceable |
            val rewrittenTraceables = traceCollector.getRewrittenTraceables(canonicalTraceable)
            // Assert that every element has a valid trace
            Assert.assertTrue("No rewritten/removed version of "+canonicalTraceable+" found!", rewrittenTraceables.findAny.present || traceCollector.isRemoved(canonicalTraceable))
        ]
    }
}
