/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tamas Szabo, Zoltan Ujhelyi, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AvgCPUQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.apache.log4j.Level
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl

@RunWith(Parameterized)
class ModelManipulationAvgAggregatorTest {

	@Parameters(name="Model: {0}")
	def static Collection<Object[]> testData() {
		newArrayList(
			#[
				"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"
			]
		)
	}

	@Parameter(0)
	public String modelPath
	
    var ResourceSet set
    var EMFScope scope

    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        scope = new EMFScope(set)
    }
	
	@Test
	def void test_avgCPU() {
	    set.loadAdditionalResourceFromUri(modelPath)
        val snapshot = set.loadExpectedResultsFromUri("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_avgCPU.snapshot")
		ViatraQueryTest.test(AvgCPUQuerySpecification.instance).with(BackendType.Rete.newBackendInstance)
			.on(scope).
			modify(HostInstance, [true], [host|host.availableCpu = 10]).with(snapshot)
				//Divison by zero happens in an eval
				.assertEquals(Level::WARN)
	}

}
