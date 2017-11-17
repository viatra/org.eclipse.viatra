/*******************************************************************************
 * Copyright (c) 2010-2017, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.HashMap
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AllDependenciesQuerySpecification
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption
import org.eclipse.viatra.query.runtime.rete.util.ReteHintOptions
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test

class DRedTest {

	val initial = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/dred.cyberphysicalsystem"
	val snapshot_dred_deps_init = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_init.snapshot"
	val snapshot_dred_deps_a3a2_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_a3a2_del.snapshot"
	val snapshot_dred_deps_a3a5_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_a3a5_del.snapshot"
	val snapshot_dred_deps_all = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_all.snapshot"
	val snapshot_dred_deps_components = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_components.snapshot"

	@Test
	def void testInitialDependencies() {
		val initial_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, initial)
		val snapshot_dred_deps_init_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH,
			snapshot_dred_deps_init)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(initial_URI).with(
			BackendType.Rete.newBackendInstance).with(snapshot_dred_deps_init_URI).assertEquals
	}

	@Test
	def void testOutsideOfCycleDelete() {
		val initial_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, initial)
		val snapshot_dred_deps_a3a2_del_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH,
			snapshot_dred_deps_a3a2_del)

		val mappings = new HashMap<QueryHintOption, Object>()
		mappings.put(ReteHintOptions.deleteRederiveEvaluation, true)
		val factory = BackendType.Rete.newBackendInstance
		val engineHints = new QueryEvaluationHint(mappings, factory)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(initial_URI).with(engineHints).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A2")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot_dred_deps_a3a2_del_URI).assertEquals
	}

	@Test
	def void testInsideCycleDelete() {
		val initial_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, initial)
		val snapshot_dred_deps_a3a5_del_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH,
			snapshot_dred_deps_a3a5_del)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(initial_URI).with(
			BackendType.Rete.newBackendInstance).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A5")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot_dred_deps_a3a5_del_URI).assertEquals
	}

	@Test
	def void testComponentsBuildUpBreakDown() {
		val initial_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, initial)
		val snapshot_dred_deps_components_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH,
			snapshot_dred_deps_components)
		val snapshot_dred_deps_all_URI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH,
			snapshot_dred_deps_all)

		val mappings = new HashMap<QueryHintOption, Object>()
		mappings.put(ReteHintOptions.deleteRederiveEvaluation, true)
		val factory = BackendType.Rete.newBackendInstance
		val engineHints = new QueryEvaluationHint(mappings, factory)

		val buildUp = ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(initial_URI).with(engineHints).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ A3 |
				val A2 = A3.dependOn.findFirst[it.identifier.equals("A2")]
				A2.dependOn.add(A3)
			]
		).modify(
			ApplicationInstance,
			[it.identifier.equals("A2")],
			[ A2 |
				val A1 = A2.dependOn.findFirst[it.identifier.equals("A1")]
				A1.dependOn.add(A2)
			]
		)

		buildUp.with(snapshot_dred_deps_all_URI).assertEquals

		val breakDown = buildUp.modify(
			ApplicationInstance,
			[it.identifier.equals("A2")],
			[ A2 |
				val A3 = A2.dependOn.findFirst[it.identifier.equals("A3")]
				A2.dependOn.remove(A3)
			]
		).modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ A3 |
				val A2 = A3.dependOn.findFirst[it.identifier.equals("A2")]
				A3.dependOn.remove(A2)
			]
		)

		breakDown.with(snapshot_dred_deps_components_URI).assertEquals
	}

}
