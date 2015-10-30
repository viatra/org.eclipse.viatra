package org.eclipse.viatra.debug.example;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.eclipse.viatra.debug.example.transformation.ViatraTraceExecutorTransformation;
import org.eclipse.viatra.emf.mwe2integration.initializer.MWE2IntegrationInitializer;
import org.junit.Test;

public class TraceExecutorTest {
    public static final String executor= "src/org/eclipse/viatra/debug/example/TraceExecutorTransformation.mwe2";
    
    @Test
    public void runExecutorMWE2() {
        MWE2IntegrationInitializer initializer = new MWE2IntegrationInitializer();
        Mwe2Runner  mweRunner = initializer.initializeHeadlessEclipse(TraceExecutorTest.class.getClassLoader());
        mweRunner.run(URI.createURI(executor), new HashMap<String,String>(), new WorkflowContextImpl());
    }    
    
    @Test
    public void runExecutor() {
        ViatraTraceExecutorTransformation transformation = new ViatraTraceExecutorTransformation();
        transformation.setLocation("/org.eclipse.viatra.debug.example/testmodel/model.uml");
        transformation.setDtLocation("D://model_dt/model.dtuml");
        
        transformation.doInitialize();
    }
}
