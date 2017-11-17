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
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ArgumentsUnifiedByCallerQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.EnumNotEqualQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.MultipleEvalsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NotApplicationInstanceIdentifiableQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NotOneInstanceQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.PartiallyUnboundUnifiedExportedParametersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.SameVariablesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.UnifiedParametersWithDifferentValues2QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.UnifiedParametersWithDifferentValuesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.UseVarInEvalQuerySpecification
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NotApplicationInstanceIdentifiable3QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.NotApplicationInstanceIdentifiable2QuerySpecification

// This test is necessary because of 481263 and 491248 bugs
@RunWith(Parameterized)
class VariableEqualityCpsTest {
    extension org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes = new org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes
    
    @Parameters(name = "Model: {0}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #[ 
                "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"
            ]
        )
    }
    
    @Parameter(0)
    public String modelPath
    
    
    @Test
    def void variableEqualityTest() {
        ViatraQueryTest.test(SameVariablesQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void enumNotEqualsTest() {
        ViatraQueryTest.test(EnumNotEqualQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void partiallyUnboundUnifiedExportedParametersTest(){
        ViatraQueryTest.test(PartiallyUnboundUnifiedExportedParametersQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void unifiedParametersWithDifferentValues2Test(){
        ViatraQueryTest.test(UnifiedParametersWithDifferentValues2QuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void unifiedParametersWithDifferentValuesTest(){
        ViatraQueryTest.test(UnifiedParametersWithDifferentValuesQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void multipleEvals(){
        ViatraQueryTest.test(MultipleEvalsQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void useVarInEval(){
        ViatraQueryTest.test(UseVarInEvalQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void notOneInstance(){
        ViatraQueryTest.test(NotOneInstanceQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void argumentsUnifiedByCaller(){
        ViatraQueryTest.test(ArgumentsUnifiedByCallerQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
    @Test
    def void invalidInferredParameterType(){
        ViatraQueryTest.test(NotApplicationInstanceIdentifiableQuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    @Test
    def void invalidInferredParameterType2(){
        ViatraQueryTest.test(NotApplicationInstanceIdentifiable2QuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    @Test
    def void invalidInferredParameterType3(){
        ViatraQueryTest.test(NotApplicationInstanceIdentifiable3QuerySpecification.instance)
                        .on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath))
                        .withAll
                        .assertEquals
    }
    
}
