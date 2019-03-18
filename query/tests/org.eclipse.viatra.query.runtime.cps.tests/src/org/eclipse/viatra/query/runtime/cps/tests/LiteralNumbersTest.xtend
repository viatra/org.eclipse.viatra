/*******************************************************************************
 * Copyright (c) 2014-2018 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IntValueNegativeConstantQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IntValueNegativeConstantWithCheckQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueConstantQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueConstantWithCheckQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueNegativeConstantQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.LongValueNegativeConstantWithCheckQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class LiteralNumbersTest extends AbstractQueryComparisonTest {
    
    override getSnapshotUri() {
        "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_literalNumbers.snapshot"
    }
    
    @Test
    def void negativeIntegerTest() {
        ViatraQueryTest.test(IntValueNegativeConstantQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void negativeIntegerWithCheckTest() {
        ViatraQueryTest.test(IntValueNegativeConstantWithCheckQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void zeroLongTest() {
        ViatraQueryTest.test(LongValueConstantQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void zeroLongWithCheckTest() {
        ViatraQueryTest.test(LongValueConstantWithCheckQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void negativeLongTest() {
        ViatraQueryTest.test(LongValueNegativeConstantQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void negativeLongWithCheckTest() {
        ViatraQueryTest.test(LongValueNegativeConstantWithCheckQuerySpecification.instance)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }

}
