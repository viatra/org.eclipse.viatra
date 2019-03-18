/*******************************************************************************
 * Copyright (c) 2017-2017 Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.eclipse.viatra.query.runtime.cps.tests.queries.TrickyJoin
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.junit.runners.Parameterized.Parameter
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.emf.EMFScope

@RunWith(Parameterized)
class ModelManipulationTrickyJoinTest {
    public static val SNAPSHOT_PATH = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_trickyJoin.snapshot"
    
    @Parameters(name = "{index}: {1}")
    static def data() {
        return TrickyJoin::instance.specifications.toList.sortBy[fullyQualifiedName].map[
        	val Object[] params = #[it,fullyQualifiedName]
        	params
        ];
    }    
    
    @Parameter(0) 
    public var IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> query;
    @Parameter(1) 
    public var String fqn;

    var ResourceSet set
    var EMFScope scope

    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        scope = new EMFScope(set)
    }
    
    @Test
    def void test_trickyJoin() {
        ViatraQueryTest.test(query)
                        .on(scope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(set.loadExpectedResultsFromUri(SNAPSHOT_PATH))
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostInstance,
                            [true],
                            [
                            	val lastCharIndex = identifier.length-1
                            	val lastChar = identifier.charAt(lastCharIndex)
                            	val oldNumber = lastChar - '0'
                            	val newNumber = (oldNumber + 1) % 5
                            	val char replacement = Character.forDigit(newNumber, 10) 
                            	val newIdentifier = identifier.substring(0, lastCharIndex) + replacement
                            	identifier = newIdentifier
							])
                        .with(set.loadExpectedResultsFromUri("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_trickyJoin.snapshot"))
                        .assertEquals
    }
    
}
