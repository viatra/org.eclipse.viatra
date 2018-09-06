/*******************************************************************************
 * Copyright (c) 2014-2018 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Called_dQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Called_iQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Called_sQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Callee_dQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Callee_iQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.flattenerpredicate.util.Callee_sQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class FlattenerPredicateTest {
    static val queryBackends = #[ReteBackendFactory.INSTANCE, LocalSearchEMFBackendFactory.INSTANCE, LocalSearchGenericBackendFactory.INSTANCE]
    static val DEFAULT_OPTION = ViatraQueryEngineOptions.^default
    static val SEARCH_OPTION = ViatraQueryEngineOptions.defineOptions.withDefaultBackend(LocalSearchEMFBackendFactory.INSTANCE).build
    static val INCREMENTAL_OPTION = ViatraQueryEngineOptions.defineOptions.withDefaultBackend(ReteBackendFactory.INSTANCE).build
    @Parameters()
    def static Collection<Object[]> testData() {
        #[
            // Default engine options, default modifiers, should be flattened
            #[DEFAULT_OPTION, Callee_dQuerySpecification.instance, Called_dQuerySpecification.instance, true],
            // Default engine options, incremental modifiers, should not be flattened
            #[DEFAULT_OPTION, Callee_iQuerySpecification.instance, Called_iQuerySpecification.instance, false],
            // Default engine options, search modifiers, should be flattened
            #[DEFAULT_OPTION, Callee_sQuerySpecification.instance, Called_sQuerySpecification.instance, true],
            // Search engine, default modifiers, should be flattened
            #[SEARCH_OPTION, Callee_dQuerySpecification.instance, Called_dQuerySpecification.instance, true],
            // Search engine, incremental modifiers, should not be flattened
            #[SEARCH_OPTION, Callee_iQuerySpecification.instance, Called_iQuerySpecification.instance, false],
            // Search engine, search modifiers, should be flattened
            #[SEARCH_OPTION, Callee_sQuerySpecification.instance, Called_sQuerySpecification.instance, true],
            // Incremental engine, default modifiers, should be flattened
            #[INCREMENTAL_OPTION, Callee_dQuerySpecification.instance, Called_dQuerySpecification.instance, true],
            // Incremental engine, incremental modifiers, should not be flattened
            #[INCREMENTAL_OPTION, Callee_iQuerySpecification.instance, Called_iQuerySpecification.instance, false],
            // Incremental engine, search modifiers, should be flattened
            #[INCREMENTAL_OPTION, Callee_sQuerySpecification.instance, Called_sQuerySpecification.instance, true]
        ]
    }
    
    @Parameter(0)
    public ViatraQueryEngineOptions engineOptions
    @Parameter(1)
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> queryToFlatten 
    @Parameter(2)
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> queryFlattened 
    @Parameter(3)
    public boolean shouldCallBeFlattened
    
    
    public String modelPath = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"
    ResourceSet rs
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }

    @Test
    def void flattenedPatternCallTest() {
        val engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs), engineOptions)
        try {
            engine.getMatcher(queryToFlatten, LocalSearchHints.defaultGenericHybrid.build)
            Assert.assertTrue(shouldCallBeFlattened.xor(engine.hasResultProviderForQuery(queryFlattened)))
        } finally {
            engine.dispose
        }
    }
    
    def boolean hasResultProviderForQuery(AdvancedViatraQueryEngine engine, IQuerySpecification<?> query) {
        queryBackends.exists[backend | 
            engine.getQueryBackend(backend).peekExistingResultProvider(query.internalQueryRepresentation) !== null
        ]
    }
    
    
}
