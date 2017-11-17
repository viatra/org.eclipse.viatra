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

import java.util.Collection
import java.util.Random
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.SameVariablesQuerySpecification
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import static org.junit.Assert.*

@RunWith(Parameterized)
class TestingFrameworkTest {
    extension SnapshotHelper =new SnapshotHelper
    
    @Parameters(name = "{0}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #[ "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem" ]
        )
    }
    
    @Parameter(0)
    public String modelPath
    
    // XXX This test is meaningless because derived features has been removed from the snapshot model
    @Test
    @Ignore
    def queryBasedFeatureTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        val rs = new ResourceSetImpl
        val snr = rs.getResource(modelUri, true)
        val qsn = snr.contents.findFirst[it instanceof QuerySnapshot] as QuerySnapshot
        qsn.matchSetRecords.forEach[
            it.matches.forEach[
                it.substitutions.forEach[
                    assertNotNull("Substitution is not correct", it.derivedValue)
                ]
            ]
        ]
    }
    
    @Test
    def unresolvableFileTest() {
        val randomPath = modelPath.random
        var String exMessage = null
        try {
            ViatraQueryTest.test(SameVariablesQuerySpecification.instance)
                            .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, randomPath))
                            .with(BackendType.Rete.newBackendInstance)
                            .with(BackendType.LocalSearch.newBackendInstance)
                            .assertEquals
        } catch(NullPointerException ex) {
            throw ex
        } catch(Exception ex) {
            exMessage = ex.message
        }
        assertNotNull("No exception has been thrown or message is null", exMessage)
        assertTrue('''
		The message of the caught exception does not contains the path. The message:
		«exMessage»''',
                    exMessage.contains(randomPath))
    }
    
    def String getRandom(String path) {
        return '''«path»«(new Random).nextLong»'''
    }
    
}
