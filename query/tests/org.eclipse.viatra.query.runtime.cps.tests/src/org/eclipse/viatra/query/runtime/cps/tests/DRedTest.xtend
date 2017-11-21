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
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl

class DRedTest {

	val initial = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/dred.cyberphysicalsystem"
	val snapshot_dred_deps_init = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_init.snapshot"
	val snapshot_dred_deps_a3a2_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_a3a2_del.snapshot"
	val snapshot_dred_deps_a3a5_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_a3a5_del.snapshot"
	val snapshot_dred_deps_all = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_all.snapshot"
	val snapshot_dred_deps_components = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_dred_deps_components.snapshot"

    var ResourceSet set
    var EMFScope scope

    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        scope = new EMFScope(set)
    }

	@Test
	def void testInitialDependencies() {
        set.loadAdditionalResourceFromUri(initial)
        val snapshot = set.loadExpectedResultsFromUri(snapshot_dred_deps_init)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(
			BackendType.Rete.newBackendInstance).with(snapshot).assertEquals
	}

	@Test
	def void testOutsideOfCycleDelete() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot = set.loadExpectedResultsFromUri(snapshot_dred_deps_a3a2_del)

		val mappings = new HashMap<QueryHintOption<?>, Object>()
		mappings.put(ReteHintOptions.deleteRederiveEvaluation, true)
		val factory = BackendType.Rete.newBackendInstance
		val engineHints = new QueryEvaluationHint(mappings, factory)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(engineHints).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A2")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot).assertEquals
	}

	@Test
	def void testInsideCycleDelete() {
	    set.loadAdditionalResourceFromUri(initial)
        val snapshot = set.loadExpectedResultsFromUri(snapshot_dred_deps_a3a5_del)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(
			BackendType.Rete.newBackendInstance).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A5")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot).assertEquals
	}

	@Test
	def void testComponentsBuildUpBreakDown() {
	    set.loadAdditionalResourceFromUri(initial)
        val snapshot_components = set.loadExpectedResultsFromUri(snapshot_dred_deps_components)
        val snapshot_all = set.loadExpectedResultsFromUri(snapshot_dred_deps_all)

		val mappings = new HashMap<QueryHintOption<?>, Object>()
		mappings.put(ReteHintOptions.deleteRederiveEvaluation, true)
		val factory = BackendType.Rete.newBackendInstance
		val engineHints = new QueryEvaluationHint(mappings, factory)

		val buildUp = ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(engineHints).assumeInputs.modify(
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

		buildUp.with(snapshot_all).assertEquals

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

		breakDown.with(snapshot_components).assertEquals
	}

}
