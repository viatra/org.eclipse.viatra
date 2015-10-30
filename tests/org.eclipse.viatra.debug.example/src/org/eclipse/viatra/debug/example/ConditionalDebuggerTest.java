package org.eclipse.viatra.debug.example;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.eclipse.viatra.debug.example.transformation.ViatraConditionalDebugTransformation;
import org.eclipse.viatra.emf.mwe2integration.initializer.MWE2IntegrationInitializer;
import org.junit.Test;


public class ConditionalDebuggerTest {
    public static final String conditionalDebugger= "src/org/eclipse/viatra/debug/example/ConditionalDebuggerTransformation.mwe2";
       
    @Test
    public void runConditionalDebuggerMWE2() {
        MWE2IntegrationInitializer initializer = new MWE2IntegrationInitializer();
        Mwe2Runner  mweRunner = initializer.initializeHeadlessEclipse(ConditionalDebuggerTest.class.getClassLoader());
        mweRunner.run(URI.createURI(conditionalDebugger), new HashMap<String,String>(), new WorkflowContextImpl());
    }
    
    @Test
    public void runConditionalDebugger() {
        ViatraConditionalDebugTransformation transformation = new ViatraConditionalDebugTransformation();
        transformation.setLocation("/org.eclipse.viatra.debug.example/testmodel/model.uml");
        transformation.setDtLocation("D://model_dt/model.dtuml");
        
        transformation.doInitialize();
    }
}
