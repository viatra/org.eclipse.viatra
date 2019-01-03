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

import java.util.Collection
import java.util.HashMap
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AllDependenciesQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class RecursiveReachabilityTest {

	val initial = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/recursive.cyberphysicalsystem"
	val snapshot_recursive_deps_init = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_init.snapshot"
	val snapshot_recursive_deps_a2a1_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_a2a1_del.snapshot"
	val snapshot_recursive_deps_a3a2_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_a3a2_del.snapshot"
	val snapshot_recursive_deps_a3a5_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_a3a5_del.snapshot"
	val snapshot_recursive_deps_all = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_all.snapshot"
	val snapshot_recursive_deps_components = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursive_deps_components.snapshot"

	var ResourceSet set
	var EMFScope scope
	var QueryEvaluationHint hints

	extension ModelLoadHelper = new ModelLoadHelper

	@Parameters(name="BackendFactory: {0}")
	def static Collection<IQueryBackendFactory> testData() {
		newArrayList(
			//BackendType.Rete_DRed.newBackendInstance,
			BackendType.Rete_Differential.newBackendInstance
		)
	}

	new(IQueryBackendFactory backendFactory) {
		this.set = new ResourceSetImpl
		this.scope = new EMFScope(set)
		this.hints = new QueryEvaluationHint(new HashMap<QueryHintOption<?>, Object>(), backendFactory)
	}

	@Test
	def void testInitialDependencies() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot = set.loadExpectedResultsFromUri(snapshot_recursive_deps_init)
		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).with(snapshot).
			assertEquals
	}

	@Test
	def void testOutsideOfCycleDelete1() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot = set.loadExpectedResultsFromUri(snapshot_recursive_deps_a3a2_del)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A3")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A2")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot).assertEquals
	}
	
	@Test
	def void testOutsideOfCycleDelete2() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot = set.loadExpectedResultsFromUri(snapshot_recursive_deps_a2a1_del)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).assumeInputs.modify(
			ApplicationInstance,
			[it.identifier.equals("A2")],
			[ app |
				val toRemove = app.dependOn.findFirst[it.identifier.equals("A1")]
				app.dependOn.remove(toRemove)
			]
		).with(snapshot).assertEquals
	}

	@Test
	def void testInsideCycleDelete() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot = set.loadExpectedResultsFromUri(snapshot_recursive_deps_a3a5_del)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).assumeInputs.modify(
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
		val snapshot_components = set.loadExpectedResultsFromUri(snapshot_recursive_deps_components)
		val snapshot_all = set.loadExpectedResultsFromUri(snapshot_recursive_deps_all)

		val buildUp = ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).
			assumeInputs.modify(
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
