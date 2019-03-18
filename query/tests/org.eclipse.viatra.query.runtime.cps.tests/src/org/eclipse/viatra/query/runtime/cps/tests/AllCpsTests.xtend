/*******************************************************************************
 * Copyright (c) 2014-2016 Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite)
@SuiteClasses(#[
    AnonymousVariablesCpsTest,
    APICpsTest,
    BasicCpsTest,
    FlattenedPatternCallCpsTest,
    FunctionalDependencyAnalysisTest,
    ModelManipulationCpsTest,
    RecursionCpsTest,
    TestingFrameworkTest,
    VariableEqualityCpsTest,
    BaseIndexerTest,
    BaseIndexerUnsetTest,
    AggregatorTest,
    AggregatorComparisonTest,
    ModelManipulationSumAggregatorTest,
    ModelManipulationMinMaxAggregatorTest,
    ModelManipulationAvgAggregatorTest,
    ModelManipulationTrickyJoinTest,
    LocalSearchPlanCostOverflowTest,
    LiteralValuesTest,
    DanglingTest,
    NonRecursiveReachabilityTest,
    RecursiveReachabilityTest,
    TransitiveClosureTest,
    CrossResourceContainmentTest,
    CrossResourceReferenceTest,
    LongLiteralTest
])
class AllCpsTests {}
