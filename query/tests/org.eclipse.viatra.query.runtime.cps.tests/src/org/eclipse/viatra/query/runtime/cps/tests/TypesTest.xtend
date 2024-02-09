/*******************************************************************************
 * Copyright (c) 2016-2024 Gabor Bergmann, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.junit.Test
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IdentifierAnyQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IdentifierEStringQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IdentifierJStringQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NonceJStringQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NonceEStringQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NonceAnyQuerySpecification
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.junit.Ignore

class TypesTest {
    
    extension AllBackendTypes = new AllBackendTypes
    
    val String modelPath = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"
    val String snapshot = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_types.snapshot"
    
    var ResourceSet set
    var EMFScope scope

    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        set.loadAdditionalResourceFromUri(modelPath)
        scope = new EMFScope(set)
    }

    @Test
    def void testIdentifierAny() {
        ViatraQueryTest.test(IdentifierAnyQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testIdentifierEString() {
        ViatraQueryTest.test(IdentifierEStringQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testIdentifierJString() {
        ViatraQueryTest.test(IdentifierJStringQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
    
    @Test
    def void testNonceAny() {
        ViatraQueryTest.test(NonceAnyQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
    @Test
    @Ignore("This test case does not behave consistently between LS and Rete networks, " +
            "we should clarify the expected behavior before fixing either side. Note that "+
            "correctly checking an object being the instance of an EDataType might be prohibitively expensive.")
    def void testNonceEStringRete() {
        ViatraQueryTest.test(NonceEStringQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testNonceJString() {
        ViatraQueryTest.test(NonceJStringQuerySpecification::instance)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri(snapshot))
                        .withAll
                        .assertEquals 
    }
    
}