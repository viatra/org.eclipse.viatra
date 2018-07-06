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
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import java.util.function.Predicate
import java.util.function.Consumer
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot

abstract class ModelManipulationAggregatorTest {

	// INITIAL MODELS
	protected static val aggregators_baseLine = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/aggregators.cyberphysicalsystem"

	// SNAPSHOTS
	protected static val test_min0_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_min0_Priority.snapshot"
	protected static val test_min1_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_min1_Priority.snapshot"
	protected static val test_min2_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_min2_Priority.snapshot"
	protected static val test_max5_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_max5_Priority.snapshot"
	protected static val test_max6_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_max6_Priority.snapshot"
	protected static val test_max7_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_max7_Priority.snapshot"
	protected static val test_sum21_Priority = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_sum21_Priority.snapshot"
	protected static val test_sum0_Priority  = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_sum0_Priority.snapshot"

	protected def void evaluateModifications(ViatraQueryTest test, Collection<Modification<EObject>> modifications) {
		modifications.fold(test, [acc, modification |
			test.modify(modification.clazz, modification.condition, modification.operation).with(modification.expected).
				assertEqualsThen
		])
	}

	protected static class Modification<T> {
		val Class<T> clazz
		val Predicate<T> condition
		val Consumer<T> operation
		val QuerySnapshot expected

		new(Class<T> clazz, Predicate<T> condition, Consumer<T> operation, QuerySnapshot expected) {
			this.clazz = clazz
			this.condition = condition
			this.operation = operation
			this.expected = expected
		}
	}

	protected def HostInstance createHostInstance(String name) {
		val instance = CyberPhysicalSystemFactory::eINSTANCE.createHostInstance => [
			it.identifier = name
		]
		instance
	}

	protected def ApplicationInstance createApplicationInstance(ApplicationType appType, String name, int priority) {
		val instance = CyberPhysicalSystemFactory::eINSTANCE.createApplicationInstance => [
			it.identifier = name
			it.type = appType
			it.priority = priority
		]
		instance
	}

	protected static def <T extends EObject> T findInstance(EObject root, Class<T> clazz, (T)=>boolean condition) {
		val iterator = root.eAllContents
		while (iterator.hasNext) {
			val element = iterator.next
			if (clazz.isInstance(element)) {
				val cast = clazz.cast(element)
				if (condition.apply(cast)) {
					return clazz.cast(element)
				}
			}
		}
		null
	}

}
