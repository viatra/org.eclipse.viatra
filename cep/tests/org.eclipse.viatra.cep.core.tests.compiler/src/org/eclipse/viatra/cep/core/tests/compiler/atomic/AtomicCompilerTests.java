/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.core.tests.compiler.atomic;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventModel;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.Trace;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AtomicCompilerTests {

    private ResourceSet resourceSet;
    private EventModel eventModel;
    private InternalModel internalModel;
    private TraceModel traceModel;

    @Before
    public void setUp() throws Exception {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("cep", new XMIResourceFactoryImpl());

        resourceSet = new ResourceSetImpl();

        internalModel = AutomatonFactory.eINSTANCE.createInternalModel();
        Resource internalModelResource = resourceSet.createResource(TransformationBasedCompiler.AUTOMATON_MODEL_URI);
        internalModelResource.getContents().add(internalModel);

        eventModel = EventsFactory.eINSTANCE.createEventModel();
        Resource eventModelResource = resourceSet.createResource(TransformationBasedCompiler.EVENT_MODEL_URI);
        eventModelResource.getContents().add(eventModel);

        traceModel = TraceFactory.eINSTANCE.createTraceModel();
        Resource traceModelResource = resourceSet.createResource(TransformationBasedCompiler.TRACE_MODEL_URI);
        traceModelResource.getContents().add(traceModel);
    }

    @After
    public void tearDown() throws Exception {
        resourceSet = null;

        eventModel = null;
        internalModel = null;
        traceModel = null;
    }

    @Test
    public void atomicTest() {
        AtomicEventPattern atomicEventPattern = EventsFactory.eINSTANCE.createAtomicEventPattern();
        atomicEventPattern.setId("testAtomicPattern");
        atomicEventPattern.setType("testAtomicPattern");
        eventModel.getEventPatterns().add(atomicEventPattern);

        assertEquals(1, eventModel.getEventPatterns().size());
        assertEquals(0, internalModel.getAutomata().size());
        assertEquals(0, traceModel.getTraces().size());

        new TransformationBasedCompiler().compile(resourceSet);

        assertEquals(1, eventModel.getEventPatterns().size());
        assertEquals(1, internalModel.getAutomata().size());
        assertEquals(1, traceModel.getTraces().size());

        Automaton automaton = internalModel.getAutomata().get(0);
        Trace trace = traceModel.getTraces().get(0);

        assertEquals(eventModel.getEventPatterns().get(0), trace.getEventPattern());
        assertEquals(automaton, trace.getAutomaton());

        assertEquals(3, automaton.getStates().size());
        assertEquals(1, automaton.getInitialState().getOutTransitions().size());
        assertEquals(1, automaton.getFinalStates().size());
        assertEquals(1, automaton.getFinalStates().get(0).getInTransitions().size());
        assertEquals(automaton.getInitialState().getOutTransitions().get(0), automaton.getFinalStates().get(0)
                .getInTransitions().get(0));
    }
}
