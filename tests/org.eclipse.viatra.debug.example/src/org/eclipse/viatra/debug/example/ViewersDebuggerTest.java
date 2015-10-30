package org.eclipse.viatra.debug.example;

import org.eclipse.viatra.debug.example.transformation.ViatraViewersDebugTransformation;
import org.junit.Test;


public class ViewersDebuggerTest {
      
    @Test
    public void runViewersDebugger() {
        ViatraViewersDebugTransformation transformation = new ViatraViewersDebugTransformation();
        transformation.setLocation("/org.eclipse.viatra.debug.example/testmodel/model.uml");
        transformation.setDtLocation("D://model_dt/model.dtuml");
        
        transformation.doInitialize();
    }
}
