/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.cps.tests.queries.TransitionsOfApplicationTypeMatcher
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.TransitionsOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackendFactory
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

class APICpsTest {
    extension ModelLoadHelper = new ModelLoadHelper
    extension SnapshotHelper snHelper = new SnapshotHelper

    public static val TESTED_PATTERN = "org.eclipse.viatra.query.runtime.cps.tests.queries.transitionsOfApplicationType"
    public static val TESTED_QUERY_FILE = "org.eclipse.viatra.query.runtime.cps.tests/org/eclipse/viatra/query/runtime/cps/tests/queries/simpleCpsQueries.vql"
    public static val TESTED_SNAPSHOT = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test.snapshot"

    @BeforeClass
    static def initParser() {
        EMFPatternLanguageStandaloneSetup.doSetup
    }

    def snapshot() {
        val snp = TESTED_SNAPSHOT
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snp)
        val rs = new ResourceSetImpl
        val snr = rs.getResource(modelUri, true)
        return snr.allContents.filter(QuerySnapshot).head
    }

    def queryInput() {
        val qp = TESTED_QUERY_FILE
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, qp)
        val rs = new ResourceSetImpl
        val qpr = rs.getResource(modelUri, true)
        return qpr.allContents.filter(PatternModel).head
    }

    @Test()
    def resultMatchImmutableGeneric() {
        val sns = snapshot
        val pm = queryInput

        val matcher = pm.initializeMatcherFromModel(sns.EMFRootForSnapshot, TESTED_PATTERN)
        val match = matcher.oneArbitraryMatch
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1)
            assertNotNull(match.get(i))
        assertFalse(match.mutable)
    }

    @Test()
    def matchToArrayGeneric() {
        val sns = snapshot
        val pm = queryInput

        val matcher = pm.initializeMatcherFromModel(sns.EMFRootForSnapshot, TESTED_PATTERN)
        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)

        val sampleMatchAsArray = sampleMatch.toArray
        assertNotNull(sampleMatchAsArray)
        assertEquals(sampleMatchAsArray.size, sampleMatch.parameterNames.size)
        for (int i : 0 .. sampleMatchAsArray.size - 1) {
            assertEquals(sampleMatchAsArray.get(i), sampleMatch.get(i))
        }
    }

    @Test()
    def newMatchImmutableGeneric() {
        val sns = snapshot
        val pm = queryInput

        val matcher = pm.initializeMatcherFromModel(sns.EMFRootForSnapshot, TESTED_PATTERN)
        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)

        val sampleMatchAsArray = sampleMatch.toArray
        assertNotNull(sampleMatchAsArray)

        val match = matcher.newMatch(sampleMatchAsArray)
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1) {
            assertEquals(match.get(i), sampleMatch.get(i))
        }
        assertFalse(match.mutable)
    }

    @Test()
    def newMatchMutableGeneric() {
        val sns = snapshot
        val pm = queryInput

        val matcher = pm.initializeMatcherFromModel(sns.EMFRootForSnapshot, TESTED_PATTERN)

        val match = matcher.newEmptyMatch()
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1) {
            assertNull(match.get(i))
        }
        assertTrue(match.mutable)

        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)
        for (int i : 0 .. match.parameterNames.size - 1) {
            match.set(i, sampleMatch.get(i));
            assertEquals(match.get(i), sampleMatch.get(i))
        }
    }

    @Test(expected=typeof(UnsupportedOperationException))
    def void immutableModificationExpectedExceptionGeneric() {
        val sns = snapshot
        val pm = queryInput

        val matcher = pm.initializeMatcherFromModel(sns.EMFRootForSnapshot, TESTED_PATTERN)
        val match = matcher.newMatch(null, null)
        assertNotNull(match)
        match.set(0, null);
    }

    @Test()
    def resultMatchImmutableGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))
        val match = matcher.oneArbitraryMatch
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1) {
            assertNotNull(match.get(i))
        }
        assertNotNull(match.AT)
        assertNotNull(match.t)
        assertFalse(match.mutable)
    }

    @Test()
    def matchToArrayGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))
        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)

        val sampleMatchAsArray = sampleMatch.toArray
        assertNotNull(sampleMatchAsArray)
        assertEquals(sampleMatchAsArray.size, sampleMatch.parameterNames.size)
        for (int i : 0 .. sampleMatchAsArray.size - 1) {
            assertEquals(sampleMatchAsArray.get(i), sampleMatch.get(i))
        }
    }

    @Test()
    def newMatchImmutableGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))
        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)

        val match = matcher.newMatch(sampleMatch.t, sampleMatch.AT)
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1) {
            assertEquals(match.get(i), sampleMatch.get(i))
        }
        assertEquals(match.t, sampleMatch.get(0))
        assertEquals(match.AT, sampleMatch.get(1))
        assertFalse(match.mutable)
    }

    @Test()
    def newMatchMutableGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))

        val match = matcher.newEmptyMatch()
        assertNotNull(match)
        assertEquals(match.specification, matcher.specification)
        assertArrayEquals(match.parameterNames, matcher.parameterNames)
        for (int i : 0 .. match.parameterNames.size - 1) {
            assertNull(match.get(i))
        }
        assertNull(match.t)
        assertNull(match.AT)
        assertTrue(match.mutable)

        val sampleMatch = matcher.oneArbitraryMatch
        assertNotNull(sampleMatch)
        for (int i : 0 .. match.parameterNames.size - 1) {
            match.set(i, sampleMatch.get(i));
            assertEquals(match.get(i), sampleMatch.get(i))
        }
        assertEquals(match.t, sampleMatch.get(0))
        assertEquals(match.AT, sampleMatch.get(1))

        match.t = null
        assertNull(match.get(0))
        assertNull(match.t)
        match.AT = null
        assertNull(match.get(1))
        assertNull(match.AT)
    }

    @Test(expected=typeof(UnsupportedOperationException))
    def void immutableModification1ExpectedExceptionGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))
        val match = matcher.newMatch(null, null)
        assertNotNull(match)
        match.set(0, null)
    }

    @Test(expected=typeof(UnsupportedOperationException))
    def void immutableModification2ExpectedExceptionGenerated() {
        val sns = snapshot

        val matcher = TransitionsOfApplicationTypeMatcher.on(ViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot)))
        val match = matcher.newMatch(null, null)
        assertNotNull(match)
        match.AT = null;
    }

    @Test
    def void immutablePBodyUsage() {
        val instance = TransitionsOfApplicationTypeQuerySpecification.instance
        instance.internalQueryRepresentation.disjunctBodies.bodies.forEach[assertFalse(it.mutable)]
    }
    
    
    @Test
    def void engineOptionsSupported() {
        val sns = snapshot

        val engineOptions = ViatraQueryEngineOptions.defineOptions
            .withDefaultBackend(LocalSearchBackendFactory.INSTANCE)
            .withDefaultHint(LocalSearchHints.defaultNoBase.build)
            .build
        val engine = AdvancedViatraQueryEngine.on(new EMFScope(sns.EMFRootForSnapshot), engineOptions) as AdvancedViatraQueryEngine
        val matcher = TransitionsOfApplicationTypeMatcher.on(engine)
        assertTrue(matcher.capabilities instanceof LocalSearchHints)
    }
}
