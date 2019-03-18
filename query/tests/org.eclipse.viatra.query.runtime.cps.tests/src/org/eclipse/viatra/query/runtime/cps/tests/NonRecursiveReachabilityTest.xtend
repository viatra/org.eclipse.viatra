/*******************************************************************************
 * Copyright (c) 2010-2017, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
class NonRecursiveReachabilityTest {

	val initial = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/nonRecursive.cyberphysicalsystem"
	val snapshot_nonRecursive_deps_init = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_nonRecursive_deps_init.snapshot"
	val test_nonRecursive_deps_a2a1_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_nonRecursive_deps_a2a1_del.snapshot"
	val test_nonRecursive_deps_a3a1_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_nonRecursive_deps_a3a1_del.snapshot"
	val test_nonRecursive_deps_a4a1_del = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_nonRecursive_deps_a4a1_del.snapshot"

	var ResourceSet set
	var EMFScope scope
	var QueryEvaluationHint hints

	extension ModelLoadHelper = new ModelLoadHelper

	@Parameters(name="BackendFactory: {0}")
	def static Collection<IQueryBackendFactory> testData() {
		newArrayList(
			BackendType.Rete_DRed.newBackendInstance,
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
		val snapshot = set.loadExpectedResultsFromUri(snapshot_nonRecursive_deps_init)

		ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(
			BackendType.Rete.newBackendInstance).with(snapshot).assertEquals
	}

	@Test
	def void testInvalidate1_6() {
		set.loadAdditionalResourceFromUri(initial)
		val snapshot1 = set.loadExpectedResultsFromUri(test_nonRecursive_deps_a2a1_del)
		val snapshot2 = set.loadExpectedResultsFromUri(test_nonRecursive_deps_a3a1_del)
		val snapshot3 = set.loadExpectedResultsFromUri(test_nonRecursive_deps_a4a1_del)

		val first = ViatraQueryTest.test(AllDependenciesQuerySpecification.instance).on(scope).with(hints).assumeInputs.
			modify(
				ApplicationInstance,
				[it.identifier.equals("A2")],
				[ app |
					val toRemove = app.dependOn.findFirst[it.identifier.equals("A1")]
					app.dependOn.remove(toRemove)
				]
			)

		first.with(snapshot1).assertEquals

		val second = first.modify(ApplicationInstance, [it.identifier.equals("A3")], [ app |
			val toRemove = app.dependOn.findFirst[it.identifier.equals("A1")]
			app.dependOn.remove(toRemove)
		])

		second.with(snapshot2).assertEquals

		val third = second.modify(ApplicationInstance, [it.identifier.equals("A4")], [ app |
			val toRemove = app.dependOn.findFirst[it.identifier.equals("A1")]
			app.dependOn.remove(toRemove)
		])

		third.with(snapshot3).assertEquals
	}

}
