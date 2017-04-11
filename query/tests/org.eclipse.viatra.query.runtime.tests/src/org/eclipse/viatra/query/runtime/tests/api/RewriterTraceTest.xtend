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
package org.eclipse.viatra.query.runtime.tests.api

import com.google.inject.Inject
import java.util.Map
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
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
import org.eclipse.viatra.query.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner) 
@InjectWith(EMFPatternLanguageInjectorProvider) 
class RewriterTraceTest {
    
    @Inject package ParseHelper<PatternModel> parseHelper

    @Test def void traceTest() throws Exception {
        var ResourceSetImpl rs = new ResourceSetImpl()
        val IRewriterTraceCollector traceCollector = new MappingTraceCollector();
        val Map<QueryHintOption, Object> hints = newHashMap(CommonQueryHintOptions.normalizationTraceCollector -> traceCollector);
        
        var ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs), ViatraQueryEngineOptions.defineOptions.withDefaultHint(new QueryEvaluationHint(hints, new ReteBackendFactory)).build)
        var String patternCode = 
        '''
        package org.eclipse.viatra.query.patternlanguage.emf.tests
        import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
        pattern p(p : Pattern) = {
            Pattern(p);
        }
        '''
        val PatternModel model = parseHelper.parse(patternCode)
        val IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification = new SpecificationBuilder().
            getOrCreateSpecification(model.getPatterns().get(0))
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification)
                        
        val originalConstraints = matcher.specification.internalQueryRepresentation.disjunctBodies.bodies.head.constraints
        Assert.assertFalse("Empty trace", traceCollector.knownDerivatives.empty)
        traceCollector.knownDerivatives.forEach[constraint |
            val pConstraintTraces = traceCollector.getPConstraintTraces(constraint)
            // Assert that every element has a valid trace
            Assert.assertFalse("Trace form constraint "+constraint+" not found!", pConstraintTraces.empty)
            // Each element needs to be traced to the origin PQuery
            Assert.assertTrue("This constraint cannot be traced to the original query: "+constraint, pConstraintTraces.exists[originalConstraints.contains(it)])
        ]
    }
}
