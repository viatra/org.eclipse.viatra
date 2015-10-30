package org.eclipse.viatra.debug.example;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.eclipse.viatra.debug.example.transformation.ViatraTraceCodeTransformation;
import org.eclipse.viatra.emf.mwe2integration.initializer.MWE2IntegrationInitializer;
import org.junit.Test;

public class TraceCoderTest {
    public static final String coder= "src/org/eclipse/viatra/debug/example/ActivationCoderTransformation.mwe2";
       
    @Test
    public void runCoderMWE2() {
        MWE2IntegrationInitializer initializer = new MWE2IntegrationInitializer();
        Mwe2Runner  mweRunner = initializer.initializeHeadlessEclipse(TraceCoderTest.class.getClassLoader());
        mweRunner.run(URI.createURI(coder), new HashMap<String,String>(), new WorkflowContextImpl());
    }
    
    @Test
    public void runCoder() {
        ViatraTraceCodeTransformation transformation = new ViatraTraceCodeTransformation();
        transformation.setLocation("/org.eclipse.viatra.debug.example/testmodel/model.uml");
        transformation.setDtLocation("D://model_dt/model.dtuml");
        
        transformation.doInitialize();
    }
}
