/*******************************************************************************
 * Copyright (c) 2010-2016, Balazs Grill and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests.api;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.RetePatternMatcher;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

/**
 * Test that the {@link ViatraQueryEngine} takes {@link QueryEvaluationHint} into account when creating matchers
 * 
 * @author Balazs Grill
 *
 */
@RunWith(XtextRunner.class)
@InjectWith(CustomizedEMFPatternLanguageInjectorProvider.class)
public class QueryEvaluationsHintTest {

    @Inject
    ParseHelper<PatternModel> parseHelper;
    
    private AdvancedViatraQueryEngine engine;
    private IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification;
    
    @Before
    public void setUp() throws Exception{
        ResourceSetImpl rs = new ResourceSetImpl();
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs));
        String patternCode = "package org.eclipse.viatra.query.patternlanguage.emf.tests\n"
                            + "import \"http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage\"\n"
                            + "pattern p(p : Pattern) = {\n"
                            + " Pattern(p);\n"
                            + "}";
        PatternModel model = parseHelper.parse(patternCode);
        specification = new SpecificationBuilder().getOrCreateSpecification(model.getPatterns().get(0));
    }
    
    @After
    public void tearDown() {
        engine.wipe();
    }
    
    /**
     * Test that an empty engine returns a local search matcher when requested
     */
    @Test
    public void testLocalSearch() throws Exception {
        ViatraQueryMatcher<?> matcher = engine.getMatcher(specification, LocalSearchHints.getDefault().build());
        IQueryResultProvider resultProvider = engine.getResultProviderOfMatcher(matcher);
        Assert.assertTrue(resultProvider + "is not local search", resultProvider instanceof LocalSearchResultProvider);
    }
    
    /**
     * Test that an empty engine returns a local search matcher when requested
     */
    @Test
    public void testEngineWideLocalSearch() throws Exception {
        QueryEvaluationHint localSearchHint = LocalSearchHints.getDefault().build();
        ViatraQueryEngineOptions options = ViatraQueryEngineOptions.defineOptions()
                .withDefaultHint(localSearchHint).build();
        ResourceSetImpl rs = new ResourceSetImpl();
        AdvancedViatraQueryEngine lsengine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs), options);
        ViatraQueryMatcher<?> matcher = lsengine.getMatcher(specification);
        IQueryResultProvider resultProvider = lsengine.getResultProviderOfMatcher(matcher);
        Assert.assertTrue(resultProvider + "is not local search", resultProvider instanceof LocalSearchResultProvider);
        lsengine.dispose();
    }
    
    /**
     * Test that an empty engine returns a rete matcher when requested
     */
    @Test
    public void testRete() throws Exception {
        ViatraQueryMatcher<?> matcher = engine.getMatcher(specification, new QueryEvaluationHint(null, ReteBackendFactory.INSTANCE));
        IQueryResultProvider resultProvider = engine.getResultProviderOfMatcher(matcher);
        Assert.assertTrue(resultProvider + "is not rete", resultProvider instanceof RetePatternMatcher);
    }
    
    /**
     * Test that no local search matcher is created when a Rete is already initialized
     */
    @Test
    public void testReteOverridesLS() throws Exception {
        // First, initialize a Rete matcher
        engine.getMatcher(specification, new QueryEvaluationHint(null, ReteBackendFactory.INSTANCE));
        // Then try to request a local search matcher
        ViatraQueryMatcher<?> matcher = engine.getMatcher(specification, LocalSearchHints.getDefault().build());
        IQueryResultProvider resultProvider = engine.getResultProviderOfMatcher(matcher);
        Assert.assertTrue(resultProvider + "is not rete", resultProvider instanceof RetePatternMatcher);
    }
}
