package org.eclipse.viatra.transformation.debug.util;

import java.util.List;

import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public class ViatraDebuggerUtil {
    public static TransformationThread getThread(TransformationState state){
        List<TransformationThread> transformationThreads = TransformationThreadFactory.getInstance().getTransformationThreads();
        for (TransformationThread transformationThread : transformationThreads) {
            if(transformationThread.getTransformationState().equals(state)){
                return transformationThread;
            }
        }
        return null;
    }
    
        
}
