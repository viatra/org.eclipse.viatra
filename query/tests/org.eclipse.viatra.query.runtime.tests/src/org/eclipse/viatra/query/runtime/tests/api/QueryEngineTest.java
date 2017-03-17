/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests.api;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.testing.core.injector.EMFPatternLanguageInjectorProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class QueryEngineTest {

    @Inject
    ParseHelper<PatternModel> parseHelper;
    
    /**
     * Test that duplicate FQNs are allowed by the engine.
     * This test ensures that we detect any changes in this semantic.
     * See bug 496638
     */
    @Test
    public void duplicateFQNTest() throws Exception {
        ResourceSetImpl rs = new ResourceSetImpl();
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs));
        String patternCode = "package org.eclipse.viatra.query.patternlanguage.emf.tests\n"
                            + "import \"http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage\"\n"
                            + "pattern p(p : Pattern) = {\n"
                            + " Pattern(p);\n"
                            + "}";
        PatternModel model = parseHelper.parse(patternCode);
        PatternModel model2 = parseHelper.parse(patternCode);
        
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification = new SpecificationBuilder().getOrCreateSpecification(model.getPatterns().get(0));
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification2 = new SpecificationBuilder().getOrCreateSpecification(model2.getPatterns().get(0));
        
        ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification);
        ViatraQueryMatcher<? extends IPatternMatch> matcher2 = engine.getMatcher(specification2);
        assertTrue(matcher != matcher2);
    }
    
}
