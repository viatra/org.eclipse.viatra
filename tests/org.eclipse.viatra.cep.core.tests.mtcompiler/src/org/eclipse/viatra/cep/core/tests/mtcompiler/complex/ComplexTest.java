package org.eclipse.viatra.cep.core.tests.mtcompiler.complex;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory;
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.automaton.Parameter;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;
import org.eclipse.viatra.cep.core.metamodels.events.EventModel;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceFactory;
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class ComplexTest {
    protected ResourceSet resourceSet;
    protected EventModel eventModel;
    protected InternalModel internalModel;
    protected TraceModel traceModel;
    protected TransformationBasedCompiler compiler;

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
        compiler = null;
        resourceSet = null;

        eventModel = null;
        internalModel = null;
        traceModel = null;
    }

    protected void assertTransitionTypedWith(Transition transition, List<? extends Class<? extends EventPattern>> clazzes) {
        for (Class<? extends EventPattern> clazz : clazzes) {
            Assert.assertTrue(transitionTypedWith(transition, clazz));
        }
    }

    protected void assertTransitionTypedWith(Transition transition, Class<? extends EventPattern> clazz) {
        Assert.assertTrue(transitionTypedWith(transition, clazz));
    }

    protected boolean transitionTypedWith(Transition transition, Class<? extends EventPattern> clazz) {
        if (!(transition instanceof TypedTransition)) {
            return false;
        }

        for (Guard guard : ((TypedTransition) transition).getGuards()) {
            if (guard.getEventType().getClass().equals(clazz)) {
                return true;
            }
        }

        return false;
    }

    protected void assertParameterizedTransition(Transition transition, String parameterSymbolicname) {
        Assert.assertTrue(transition instanceof TypedTransition);
        Assert.assertFalse(((TypedTransition) transition).getParameters().isEmpty());
        List<Parameter> parameters = ((TypedTransition) transition).getParameters();

        boolean paramNameContainedInParamList = false;
        for (Parameter parameter : parameters) {
            if (parameter.getSymbolicName().equalsIgnoreCase(parameterSymbolicname)) {
                paramNameContainedInParamList = true;
            }
        }

        Assert.assertTrue(paramNameContainedInParamList);
    }
}
