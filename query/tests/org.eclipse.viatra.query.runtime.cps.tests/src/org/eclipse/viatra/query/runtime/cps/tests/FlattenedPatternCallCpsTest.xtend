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
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.Callee2QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.CalleeQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.FlattenedQuerySpecification
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
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
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch

// This test is necessary because of 481265 bug
@RunWith(Parameterized)
class FlattenedPatternCallCpsTest {
    @Parameters(name = "Backend: {0}, Model: {1}")
    def static Collection<Object[]> testData() {
        newArrayList(Sets.cartesianProduct(
            newHashSet(BackendType.values),
            #{"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"},
            newHashSet(CalleeQuerySpecification.instance, Callee2QuerySpecification.instance)
        ).map[it.toArray])
    }
    
    @Parameter(0)
    public BackendType backendType
    @Parameter(1)
    public String modelPath
    @Parameter(2)
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> queryToFlatten
    ResourceSet rs
    
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }

    @Test
    def void flattenedPatternCallTest() {
        val hint = backendType.hints
        val modelProvider = new PatternBasedMatchSetModelProvider(hint)
        val notFlattenedMatchSet = modelProvider.getMatchSetRecord(rs, queryToFlatten, null)
        val flattenedMatchSet = modelProvider.getMatchSetRecord(rs, FlattenedQuerySpecification.instance, null)
        val diff = MatchSetRecordDiff.compute(notFlattenedMatchSet, flattenedMatchSet)
        if (!diff.empty) {
            throw new AssertionFailedError(diff.toString)
        }
    }
    
}
