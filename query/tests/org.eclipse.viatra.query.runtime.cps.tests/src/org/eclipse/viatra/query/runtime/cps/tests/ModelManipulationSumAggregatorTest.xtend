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

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystem
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.SumPriorityQuerySpecification
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test

class ModelManipulationSumAggregatorTest extends ModelManipulationAggregatorTest {

	final val deletions = <Modification<EObject>>newArrayList(
		new Modification(CyberPhysicalSystem, [true], [ system |
			EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax1".equals(type.identifier)]))
			EcoreUtil.delete(findInstance(system, ApplicationInstance, [type|"Ax2".equals(type.identifier)]))
		], test_sum21_Priority)
	)

	@Test
	/**
	 * Test uses the same outer group. 
	 * Add two new triplets (H, AT1, v) and (H, AT2, -v) and then remove them. 
	 */
	def void testSumPriority_SameOuterGroup() {
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val A1 = createApplicationInstance(AT1, "Ax1", 7)
			AT1.instances.add(A1)
			val AT2 = findInstance(system, ApplicationType, [type|"AT2".equals(type.identifier)])
			val A2 = createApplicationInstance(AT2, "Ax2", -7)
			AT2.instances.add(A2)
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			H1.applications.add(A1)
			H1.applications.add(A2)
		], test_sum21_Priority))
		modifications.addAll(deletions)

		val test = ViatraQueryTest.test(SumPriorityQuerySpecification.instance).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(
			XmiModelUtil::resolvePlatformURI(XmiModelUtil.XmiModelUtilRunningOptionEnum.BOTH, aggregators_baseLine))
		evaluateModifications(test, modifications)
	}

	@Test
	/**
	 * Test uses the same inner group. 
	 * Add two new triplets (H1, AT, v) and (H2, AT, -v) and then remove them. 
	 */
	def void testSumPriority_SameInnerGroup() {
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val A1 = createApplicationInstance(AT1, "Ax1", 7)
			val A2 = createApplicationInstance(AT1, "Ax2", -7)
			AT1.instances.add(A1)
			AT1.instances.add(A2)
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			val H2 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			H1.applications.add(A1)
			H2.applications.add(A2)
		], test_sum21_Priority))
		modifications.addAll(deletions)

		val test = ViatraQueryTest.test(SumPriorityQuerySpecification.instance).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(
			XmiModelUtil::resolvePlatformURI(XmiModelUtil.XmiModelUtilRunningOptionEnum.BOTH, aggregators_baseLine))
		evaluateModifications(test, modifications)
	}

	@Test
	/**
	 * Test creates entirely new groups.  
	 * Add two new triplets (H1, AT1, v) and (H2, AT2, -v) and then remove them. 
	 */
	def void testSumPriority_Addition_NewGroup() {
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val HT1 = findInstance(system, HostType, [type|"HT1".equals(type.identifier)])
			val AT3 = findInstance(system, ApplicationType, [type|"AT3".equals(type.identifier)])
			val AT4 = findInstance(system, ApplicationType, [type|"AT4".equals(type.identifier)])
			val A1 = createApplicationInstance(AT3, "Ax1", 7)
			val A2 = createApplicationInstance(AT4, "Ax2", -7)
			AT3.instances.add(A1)
			AT4.instances.add(A2)
			val H7 = createHostInstance("H7")
			val H8 = createHostInstance("H8")
			HT1.instances.add(H7)
			HT1.instances.add(H8)
			H7.applications.add(A1)
			H8.applications.add(A2)
		], test_sum21_Priority))
		modifications.addAll(deletions)

		val test = ViatraQueryTest.test(SumPriorityQuerySpecification.instance).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(
			XmiModelUtil::resolvePlatformURI(XmiModelUtil.XmiModelUtilRunningOptionEnum.BOTH, aggregators_baseLine))
		evaluateModifications(test, modifications)
	}
	
	@Test
	/**
	 * Test uses an already existing outer group. 
	 * Add 2 new triplets (H, AT1, 0), (H, AT2, 0) - no effective change -  and then remove them. 
	 */
	def void testSumPriority_Neutralchange() {
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val A1 = createApplicationInstance(AT1, "Ax1", 0)
			val A2 = createApplicationInstance(AT1, "Ax2", 0)
			AT1.instances.add(A1)
			AT1.instances.add(A2)
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			H1.applications.add(A1)
			H1.applications.add(A2)
		], test_sum21_Priority))
		modifications.addAll(deletions)

		val test = ViatraQueryTest.test(SumPriorityQuerySpecification.instance).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(
			XmiModelUtil::resolvePlatformURI(XmiModelUtil.XmiModelUtilRunningOptionEnum.BOTH, aggregators_baseLine))
		evaluateModifications(test, modifications)
	}

	@Test
	/**
	 * Test uses an already existing outer group. 
	 * Add 2 new triplets (H, AT1, 0), (H, AT2, -21) - in effect the sum becomes zero, though the collection is nonempty - and then remove them. 
	 */
	def void testSumPriority_ZeroedOutchange() {
		val modifications = <Modification<EObject>>newArrayList
		modifications.add(new Modification(CyberPhysicalSystem, [true], [ system |
			val AT1 = findInstance(system, ApplicationType, [type|"AT1".equals(type.identifier)])
			val A1 = createApplicationInstance(AT1, "Ax1", 0)
			val A2 = createApplicationInstance(AT1, "Ax2", -21)
			AT1.instances.add(A1)
			AT1.instances.add(A2)
			val H1 = findInstance(system, HostInstance, [host|"H1".equals(host.identifier)])
			H1.applications.add(A1)
			H1.applications.add(A2)
		], test_sum0_Priority))
		modifications.addAll(deletions)

		val test = ViatraQueryTest.test(SumPriorityQuerySpecification.instance).with(
			BackendType.Rete.newBackendInstance).with(BackendType.LocalSearch.newBackendInstance).on(
			XmiModelUtil::resolvePlatformURI(XmiModelUtil.XmiModelUtilRunningOptionEnum.BOTH, aggregators_baseLine))
		evaluateModifications(test, modifications)
	}

}
