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
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.apache.log4j.Level

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
	
	@Test
	def void test_avgCPU() {
		ViatraQueryTest.test(AvgCPUQuerySpecification.instance).with(BackendType.Rete.newBackendInstance)
			.on(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)).
			modify(HostInstance, [true], [host|host.availableCpu = 10]).with(
				"org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_avgCPU.snapshot")
				//Divison by zero happens in an eval
				.assertEquals(Level::WARN)
	}

}
