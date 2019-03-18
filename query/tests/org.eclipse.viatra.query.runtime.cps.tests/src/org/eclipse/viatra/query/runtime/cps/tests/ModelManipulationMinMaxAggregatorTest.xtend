/*******************************************************************************
 * Copyright (c) 2014-2016 Tamas Szabo, Zoltan Ujhelyi, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystem
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.MaxPriorityQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.MinPriorityQuerySpecification
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import java.util.function.Function
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher

@RunWith(Parameterized)
class ModelManipulationMinMaxAggregatorTest extends ModelManipulationAggregatorTest {

	@Parameters
	def static Collection<Object[]> testData() {
		val data = <Object[]>newArrayList

		val minDeletions = <Function<ResourceSet, Modification<EObject>>>newArrayList(
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax1".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_min1_Priority))],
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax2".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_min1_Priority))],
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"A1".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_min2_Priority))]
		)

		val maxDeletions = <Function<ResourceSet, Modification<EObject>>>newArrayList(
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax1".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_max6_Priority))],
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax2".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_max6_Priority))],
			[set | new Modification(CyberPhysicalSystem, [true], [ system |
				EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"A6".equals(type.identifier)]))
			], set.loadExpectedResultsFromUri(test_max5_Priority))]
		)

		data.add(#[minDeletions, #[0, 1, 2], MinPriorityQuerySpecification.instance, test_min0_Priority])
		data.add(#[maxDeletions, #[7, 6, 5], MaxPriorityQuerySpecification.instance, test_max7_Priority])
		data
	}

	@Parameter(0)
	public Collection<Function<ResourceSet, Modification<EObject>>> deletions

	@Parameter(1)
	public List<Integer> values

	@Parameter(2)
	public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification
	
	@Parameter(3)
	public String expectedSnapshotAfterAddition

    var ResourceSet set
    var EMFScope scope

    static extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        scope = new EMFScope(set)
    }

	@Test
	/**
	 * Test uses the same outer group. Current minimum is m. 
	 * Add three new triplets (H, AT1, m - 1), (H, AT2, m), and (H, AT3, m + 1) and then remove them. 
	 */
	def void testMinMaxPriority_SameOuterGroup() {
	    set.loadAdditionalResourceFromUri(aggregators_baseLine)
        val snapshot = set.loadExpectedResultsFromUri(expectedSnapshotAfterAddition)
	    
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val AT2 = findInstance(system, ApplicationType, [type|"AT2".equals(type.identifier)])
			val AT3 = findInstance(system, ApplicationType, [type|"AT3".equals(type.identifier)])
			val Ax1 = createApplicationInstance(AT1, "Ax1", values.get(0))
			val Ax2 = createApplicationInstance(AT2, "Ax2", values.get(1))
			val Ax3 = createApplicationInstance(AT3, "Ax3", values.get(2))
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			H1.applications.add(Ax1)
			H1.applications.add(Ax2)
			H1.applications.add(Ax3)
		], snapshot))
		modifications.addAll(deletions.map[apply(set)])

		val test = ViatraQueryTest.test(specification)
		.with(BackendType.Rete.newBackendInstance)
		.with(BackendType.LocalSearch.newBackendInstance)
		.on(scope)
		evaluateModifications(test, modifications)
	}

	@Test
	/**
	 * Test uses the same inner group. Current minimum is m. 
	 * Add three new triplets (H1, AT, m - 1), (H2, AT, m), and (H3, AT, m + 1) and then remove them. 
	 */
	def void testMinMaxPriority_SameInnerGroup() {
	    set.loadAdditionalResourceFromUri(aggregators_baseLine)
        val snapshot = set.loadExpectedResultsFromUri(expectedSnapshotAfterAddition)
	    
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val Ax1 = createApplicationInstance(AT1, "Ax1", values.get(0))
			val Ax2 = createApplicationInstance(AT1, "Ax2", values.get(1))
			val Ax3 = createApplicationInstance(AT1, "Ax3", values.get(2))
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			val H2 = findInstance(system, HostInstance, [host|"H2".equals(host.identifier)])
			val H3 = findInstance(system, HostInstance, [host|"H3".equals(host.identifier)])
			H1.applications.add(Ax1)
			H2.applications.add(Ax2)
			H3.applications.add(Ax3)
		], snapshot))
		modifications.addAll(deletions.map[apply(set)])

		val test = ViatraQueryTest.test(specification).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(scope)
		evaluateModifications(test, modifications)
	}

	@Test
	/**
	 * Test uses new groups. Current minimum is m. 
	 * Add three new triplets (H1, AT1, m - 1), (H2, AT2, m), and (H3, AT3, m + 1) and then remove them. 
	 */
	def void testMinMaxPriority_NewGroup() {
	    set.loadAdditionalResourceFromUri(aggregators_baseLine)
        val snapshot = set.loadExpectedResultsFromUri(expectedSnapshotAfterAddition)
	    
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val HT1 = findInstance(system, HostType, [type|"HT1".equals(type.identifier)])
			val AT3 = findInstance(system, ApplicationType, [type|"AT3".equals(type.identifier)])
			val AT4 = findInstance(system, ApplicationType, [type|"AT4".equals(type.identifier)])
			val AT5 = findInstance(system, ApplicationType, [type|"AT5".equals(type.identifier)])
			val A3 = createApplicationInstance(AT3, "Ax1", values.get(0))
			val A4 = createApplicationInstance(AT4, "Ax2", values.get(1))
			val A5 = createApplicationInstance(AT5, "Ax3", values.get(2))
			AT3.instances.add(A3);
			AT4.instances.add(A4)
			AT5.instances.add(A5)
			val H7 = createHostInstance("H7")
			val H8 = createHostInstance("H8")
			val H9 = createHostInstance("H9")
			HT1.instances.add(H7)
			HT1.instances.add(H8)
			HT1.instances.add(H9)
			H7.applications.add(A3)
			H8.applications.add(A4)
			H9.applications.add(A5)
		], snapshot))
		modifications.addAll(deletions.map[apply(set)])

		val test = ViatraQueryTest.test(specification).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(scope)
		evaluateModifications(test, modifications)
	}

}
