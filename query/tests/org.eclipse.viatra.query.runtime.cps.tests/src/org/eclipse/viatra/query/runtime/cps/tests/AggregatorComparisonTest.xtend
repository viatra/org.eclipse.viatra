/*******************************************************************************
 * Copyright (c) 2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Grill, Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import com.google.common.collect.Sets
import java.util.Collection
import java.util.List
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithMinCPU1QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithMinCPU2QuerySpecification
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.testing.core.MatchSetRecordDiff
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch

@RunWith(Parameterized)
class AggregatorComparisonTest {
        
    @Parameters(name = "Backend: {0}, Model: {1}")
    def static Collection<Object[]> testData() {
        newArrayList(Sets.cartesianProduct(
            newHashSet(BackendType.values),
            #{"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"},
            <List<IQuerySpecification<?>>>newHashSet(
                #[HostInstanceWithMinCPU1QuerySpecification.instance, HostInstanceWithMinCPU2QuerySpecification.instance]
            )
        ).map[it.toArray])
    }
    
    @Parameter(0)
    public BackendType backendType
    @Parameter(1)
    public String modelPath
    @Parameter(2)
    public List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> queries
    ResourceSet rs
    
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }

    @Test
    def void compareResultsTest() {
        Assume.assumeFalse(queries.empty);
        val hint = backendType.hints
        val modelProvider = new PatternBasedMatchSetModelProvider(hint)
        val reference = modelProvider.getMatchSetRecord(rs, queries.get(0), null)
        for(var i=1;i<queries.length;i++){
            val actual = modelProvider.getMatchSetRecord(rs, queries.get(i), null)
            val diff = MatchSetRecordDiff.compute(reference, actual)
            Assert.assertTrue('''Additions: «diff.additions», Removals: «diff.removals»''', diff.empty);
        }
    }

}
