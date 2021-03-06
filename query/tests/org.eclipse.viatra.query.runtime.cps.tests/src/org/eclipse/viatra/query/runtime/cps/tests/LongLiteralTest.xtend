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
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueConstantQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueConstantWithCheckQuerySpecification
import org.eclipse.viatra.query.patternlanguage.emf.specification.GenericQuerySpecification
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
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
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider
import org.junit.Rule
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.XtextInfrastructureRule

/**
 * This test reproduces bug 520878. As the bug only affects the generated code, this test executes the same patterns with {@link GenericQuerySpecification}
 * parsed in-place to provide regression detection toward parsed patterns. Because of this, it executed EMFPatternLanguageStandaloneSetup#doSetup
 */ 
@RunWith(Parameterized)
class LongLiteralTest {
    
    @Parameters(name = "Backend: {0}, Model: {1}")
    def static Collection<Object[]> testData() {
        newArrayList(Sets.cartesianProduct(
            newHashSet(BackendType.values),
            #{"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"}
        ).map[it.toArray])
    }
    
    @Parameter(0)
    public BackendType backendType
    @Parameter(1)
    public String modelPath

    ResourceSet rs
    Injector injector
    
    @Rule
    public val rule = new XtextInfrastructureRule(this, CustomizedEMFPatternLanguageInjectorProvider)
    
    @Before
    def void prepareTest() {
        injector = rule.injector
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }

    @Test
    def void longLiteralTest() {
        val hint = backendType.hints
        val modelProvider = new PatternBasedMatchSetModelProvider(hint)
        val equalityMatchSet = modelProvider.getMatchSetRecord(rs, LongValueConstantQuerySpecification.instance, null)
        val withCheckMatchSet = modelProvider.getMatchSetRecord(rs, LongValueConstantWithCheckQuerySpecification.instance, null)
        val diff = MatchSetRecordDiff.compute(equalityMatchSet, withCheckMatchSet)
        if (!diff.empty) {
            throw new AssertionFailedError(diff.toString)
        }
    }

    @Test
    def void longLiteralTestWithGeneric() {
        val hint = backendType.hints
        val modelProvider = new PatternBasedMatchSetModelProvider(hint)
        
        val patterns = PatternParserBuilder.instance().withInjector(injector).parse('''
			package test
			import "http://org.eclipse.viatra/model/cps"        
			
			pattern longValueConstant(appT : ApplicationType){
			    ApplicationType.exeFileSize(appT, 0l);
			}
			
			pattern longValueConstantWithCheck(appT : ApplicationType){
			    ApplicationType.exeFileSize(appT, value);
			    check(value == 0l);
			}        
		''').querySpecifications
        
        val equalityMatchSet = modelProvider.getMatchSetRecord(rs, patterns.findFirst[it.fullyQualifiedName == "test.longValueConstant"] as IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, null)
        val withCheckMatchSet = modelProvider.getMatchSetRecord(rs, patterns.findFirst[it.fullyQualifiedName == "test.longValueConstantWithCheck"] as IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, null)
        val diff = MatchSetRecordDiff.compute(equalityMatchSet, withCheckMatchSet)
        if (!diff.empty) {
            throw new AssertionFailedError(diff.toString)
        }
    }
    
}
