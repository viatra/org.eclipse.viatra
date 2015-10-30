package org.eclipse.viatra.debug.example;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.eclipse.viatra.debug.example.transformation.ViatraDebugTransformation;
import org.eclipse.viatra.emf.mwe2integration.initializer.MWE2IntegrationInitializer;
import org.junit.Test;

public class DebuggerTest {
    public static final String debugger= "src/org/eclipse/viatra/debug/example/DebuggerTransformation.mwe2";
    
    @Test
    public void runDebuggerMWE2() {
        MWE2IntegrationInitializer initializer = new MWE2IntegrationInitializer();
        Mwe2Runner  mweRunner = initializer.initializeHeadlessEclipse(DebuggerTest.class.getClassLoader());
        mweRunner.run(URI.createURI(debugger), new HashMap<String,String>(), new WorkflowContextImpl());
    }
    
    @Test
    public void runDebugger() {
        ViatraDebugTransformation transformation = new ViatraDebugTransformation();
        transformation.setLocation("/org.eclipse.viatra.debug.example/testmodel/model.uml");
        transformation.setDtLocation("D://model_dt/model.dtuml");
        
        transformation.doInitialize();
    }
}
