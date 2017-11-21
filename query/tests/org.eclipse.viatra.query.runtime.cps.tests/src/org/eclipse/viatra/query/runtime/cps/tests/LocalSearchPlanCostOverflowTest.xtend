/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackendFactory
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.StatisticsBasedConstraintCostFunction
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.EMFOperationCompiler
import org.eclipse.viatra.query.runtime.localsearch.plan.SimplePlanProvider

class LocalSearchPlanCostOverflowTest {
 
    val snapshot = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test.snapshot"
    
    ResourceSet rs
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snapshot)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }
    
    @Test
    def void costOverFlowTest(){
        // Test to reproduce problem in https://bugs.eclipse.org/bugs/show_bug.cgi?id=468118
        // A simple pattern is executed (ApplicationType.instances(AT, AI)) with empty adornment
        // This pattern is trivially solvable with only one iteration, but the cost overflow
        // causes the second iteration to be negative cost, resulting a plan which involves iteration
        // of both ends of the reference
        val pattern = ApplicationInstancesOfApplicationTypeQuerySpecification.instance
        val statistics = #{ 
            new EClassTransitiveInstancesKey(CyberPhysicalSystemPackage.eINSTANCE.applicationInstance) -> 2,
            new EClassTransitiveInstancesKey(CyberPhysicalSystemPackage.eINSTANCE.applicationType) -> Integer.MAX_VALUE
        }
        val hints = LocalSearchHints.defaultNoBase.setCostFunction(new StatisticsBasedConstraintCostFunction(){
        
            override countTuples(IConstraintEvaluationContext input, IInputKey supplierKey) {
                if (statistics.containsKey(supplierKey)) statistics.get(supplierKey) else 0
            }
           
       });
       
       val scope = new EMFScope(rs);
       val engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope)
       
       val backend = engine.getQueryBackend(LocalSearchBackendFactory.INSTANCE) as LocalSearchBackend
       
       val planner = new SimplePlanProvider(null);
       val adornment = #{}
       val compiler = new EMFOperationCompiler(backend.getRuntimeContext(), hints.isUseBase());
       val plan = planner.getPlan(backend.backendContext, compiler, hints, new MatcherReference(pattern.internalQueryRepresentation, adornment))
       
       Assert.assertEquals(1,plan.iteratedKeys.size)
       engine.dispose
    }
    
}
