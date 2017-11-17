/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystem
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.State
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithHostedInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithoutHostedInstanceQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypesIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.CommunicateWithQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.FinalPatternQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasTheMostHostedApplicationInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasTheMostHostedApplicationsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithAtLeastAsMuchTotalRamAsTotalHddQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstancesWithZeroTotalRamQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostedApplicationsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.InTheCommunicationChainsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.InstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.TransitionsOfApplicationTypeIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.TransitionsOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test

class ModelManipulationCpsTest {
    public static val SNAPSHOT_PATH = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test.snapshot"

    @Test
    def void test_newAppInstance() {
        ViatraQueryTest.test(ApplicationInstancesQuerySpecification.instance)
                        .and(ApplicationInstancesOfApplicationTypeQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationType,
                            [true],
                            [appType |
                                // MODEL MODIFICATION HERE
                                // add a new Application Instance to all Application Types in the model
                                CyberPhysicalSystemFactory::eINSTANCE.createApplicationInstance => [
                                    it.identifier = appType.identifier+".instNew"
                                    it.type = appType
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newAppInstance.snapshot")
                        .assertEquals
    }

    @Test
    def void test_changeAppInstanceIdentifier() {
        ViatraQueryTest.test(ApplicationInstancesIdentifiersQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationInstance,
                            [it.identifier == "simple.cps.app.FirstAppClass0.inst1"],
                            [appInst |
                                // MODEL MODIFICATION HERE
                                // change the Application Instance "simple.cps.app.FirstAppClass0.inst1" 
                                // identifier to "simple.cps.app.FirstAppClass0.instModified"
                                appInst.identifier = "simple.cps.app.FirstAppClass0.instModified"
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_changeAppInstanceIdentifier.snapshot")
                        .assertEquals
    }

    @Test
    def void test_changeAppTypeIdentifier() {
        ViatraQueryTest.test(ApplicationTypesIdentifiersQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationType,
                            [it.identifier == "simple.cps.app.FirstAppClass0"],
                            [appType |
                                // MODEL MODIFICATION HERE
                                // change the Application Type "simple.cps.app.FirstAppClass0"
                                // identifier to "simple.cps.app.FirstAppClassModified"
                                appType.identifier = "simple.cps.app.FirstAppClassModified"
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_changeAppTypeIdentifier.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_deleteAppInstance() {
        ViatraQueryTest.test(ApplicationInstancesQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationInstance,
                            [it.identifier == "simple.cps.app.FirstAppClass0.inst0"],
                            [appInst |
                                // MODEL MODIFICATION HERE
                                // delete the Application Instance "simple.cps.app.FirstAppClass0.inst1"
                                EcoreUtil.delete(appInst)
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteAppInstance.snapshot")
                        .assertEquals
    }

    @Test
    def void test_deleteAppType() {
        ViatraQueryTest.test(ApplicationTypesIdentifiersQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationType,
                            [it.identifier == "simple.cps.app.FirstAppClass0"],
                            [appType |
                                // MODEL MODIFICATION HERE
                                // delete the Application Type "simple.cps.app.FirstAppClass0"
                                EcoreUtil.delete(appType)
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteAppType.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_newHostInstance() {
        ViatraQueryTest.test(CommunicateWithQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostInstance,
                            [it.identifier == "simple.cps.host.FirstHostClass0.inst0"],
                            [hostInst |
                                // MODEL MODIFICATION HERE
                                // add a new Host Instance as communication partner to
                                // Host Instance "simple.cps.host.FirstHostClass0.inst0"
                                CyberPhysicalSystemFactory::eINSTANCE.createHostInstance => [
                                    it.identifier = "simple.cps.host.FirstHostClass0.instNew"
                                    it.nodeIp = "simple.cps.host.FirstHostClass0.instNew"
                                    (hostInst.eContainer as HostType).instances += it
                                    it.communicateWith += hostInst
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newHostInstance.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_changeAppInstanceAllocationLocation() {
        ViatraQueryTest.test(HostedApplicationsQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationInstance,
                            [it.identifier == "simple.cps.app.FirstAppClass0.inst0"],
                            [appInst |
                                // MODEL MODIFICATION HERE
                                val hostInst = appInst.eResource
                                                        .allContents
                                                        .filter(HostInstance)
                                                        .findFirst[it.identifier == "simple.cps.host.SecondHostClass0.inst0"]
                                appInst.allocatedTo = hostInst
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_changeAppInstanceAllocationLocation.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_changeAppInstanceType() {
        ViatraQueryTest.test(ApplicationTypeWithHostedInstancesQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(ApplicationInstance,
                            [it.identifier == "simple.cps.app.FirstAppClass0.inst1"],
                            [appInst |
                                // MODEL MODIFICATION HERE
                                val appType = appInst.eResource
                                                        .allContents
                                                        .filter(ApplicationType)
                                                        .findFirst[it.identifier == "simple.cps.app.SecondAppClass0"]
                                appInst.type = appType
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_changeAppInstanceType.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_newTransition() {
        ViatraQueryTest.test(TransitionsOfApplicationTypeIdentifiersQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(State,
                            [it.identifier == "simple.cps.app.FirstAppClass0.sm0.s1"],
                            [state |
                                // MODEL MODIFICATION HERE
                                CyberPhysicalSystemFactory::eINSTANCE.createTransition => [
                                    it.identifier = "simple.cps.app.FirstAppClass0.sm0.s1.tNew"
                                    it.action = "Dummy Action"
                                    state.outgoingTransitions += it
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newTransition.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_deleteHostType() {
        ViatraQueryTest.test(InTheCommunicationChainsQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostType,
                            [it.identifier == "simple.cps.host.FirstHostClass0"],
                            [ EcoreUtil.delete(it, true) ] )
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteHostType.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_newHostInstanceWithMoreRamThanHdd() {
        ViatraQueryTest.test(HostInstanceWithAtLeastAsMuchTotalRamAsTotalHddQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostType,
                            [it.identifier == "simple.cps.host.FirstHostClass0"],
                            [hostType |
                                // MODEL MODIFICATION HERE
                                CyberPhysicalSystemFactory::eINSTANCE.createHostInstance => [
                                    it.identifier = "simple.cps.host.FirstHostClass0.instNew"
                                    it.totalRam = 2
                                    it.totalHdd = 1
                                    hostType.instances += it
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newHostInstanceWithMoreRamThanHdd.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_newHostInstanceWithMoreHddThanRam() {
        ViatraQueryTest.test(HostInstanceWithAtLeastAsMuchTotalRamAsTotalHddQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostType,
                            [it.identifier == "simple.cps.host.FirstHostClass0"],
                            [hostType |
                                // MODEL MODIFICATION HERE
                                CyberPhysicalSystemFactory::eINSTANCE.createHostInstance => [
                                    it.identifier = "simple.cps.host.FirstHostClass0.instNew"
                                    it.nodeIp = "simple.cps.host.FirstHostClass0.instNew"
                                    it.totalRam = 1
                                    it.totalHdd = 2
                                    hostType.instances += it
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newHostInstanceWithMoreHddThanRam.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_deleteHostInstanceWithTheMostHostedApplication() {
        ViatraQueryTest.test(FinalPatternQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostInstance,
                            [it.identifier == "simple.cps.host.SecondHostClass0.inst1"],
                            [ EcoreUtil.delete(it) ] )
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteHostInstanceWithTheMostHostedApplication.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_deleteAllHostType() {
        ViatraQueryTest.test(ApplicationTypeWithHostedInstancesQuerySpecification.instance)
        .and(HostInstancesWithZeroTotalRamQuerySpecification.instance)
        .and(InTheCommunicationChainsQuerySpecification.instance)
        .and(HasTheMostHostedApplicationsQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostType,
                            [true],
                            [ EcoreUtil.delete(it) ] )
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteAllHostType.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_deleteAllHostInstance() {
        ViatraQueryTest.test(ApplicationTypeWithoutHostedInstanceQuerySpecification.instance)
        .and(HasTheMostHostedApplicationsQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(HostInstance,
                            [true],
                            [ EcoreUtil.delete(it) ] )
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_deleteAllHostInstance.snapshot")
                        .assertEquals
    }
    
    @Test
    def void test_newComplexStructure() {
        ViatraQueryTest.test(HasTheMostHostedApplicationInstancesQuerySpecification.instance)
        .and(TransitionsOfApplicationTypeQuerySpecification.instance)
        .and(InstancesQuerySpecification.instance)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .with(SNAPSHOT_PATH)
                        .assumeInputs
                        .assertEqualsThen
                        .modify(CyberPhysicalSystem,
                            [true],
                            [cps |
                                // MODEL MODIFICATION HERE
                                extension val factory =  CyberPhysicalSystemFactory::eINSTANCE
                                createApplicationType => [ appType |
                                    appType.identifier = "simple.cps.app.NewAppClass0"
                                    cps.appTypes += appType
                                    createStateMachine => [ sm |
                                        sm.identifier = '''«appType.identifier».sm0'''
                                        appType.behavior = sm
                                        val s0 = createState => [ state |
                                            state.identifier = '''«sm.identifier».s0'''
                                            sm.states += state
                                            sm.initial = state
                                        ]
                                        val s1 = createState => [ state |
                                            state.identifier = '''«sm.identifier».s1'''
                                            sm.states += state
                                        ]
                                        createTransition => [ t |
                                            t.identifier = '''«s0.identifier».t0'''
                                            s0.outgoingTransitions += t
                                            t.targetState = s1
                                        ]
                                    ]
                                    val hostInstances = cps.hostTypes
                                                           .findFirst[it.identifier=="simple.cps.host.FirstHostClass0"]
                                                           .instances
                                    createApplicationInstance => [ appInst |
                                        appInst.identifier = '''«appType.identifier».inst0'''
                                        appInst.type = appType
                                        appInst.allocatedTo = hostInstances.findFirst[it.identifier.contains("inst0")]
                                    ]
                                    createApplicationInstance => [ appInst |
                                        appInst.identifier = '''«appType.identifier».inst1'''
                                        appInst.type = appType
                                        appInst.allocatedTo = hostInstances.findFirst[it.identifier.contains("inst1")]
                                    ]
                                    createApplicationInstance => [ appInst |
                                        appInst.identifier = '''«appType.identifier».inst2'''
                                        appInst.type = appType
                                        appInst.allocatedTo = hostInstances.findFirst[it.identifier.contains("inst2")]
                                    ]
                                ]
                            ])
                        .with("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_newComplexStructure.snapshot")
                        .assertEquals
    }
}
