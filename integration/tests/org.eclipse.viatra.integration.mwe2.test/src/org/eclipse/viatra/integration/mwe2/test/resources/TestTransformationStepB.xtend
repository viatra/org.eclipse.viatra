/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.mwe2.test.resources

import java.util.concurrent.BlockingQueue
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.viatra.integration.mwe2.mwe2impl.TransformationStep

class TestTransformationStepB extends TransformationStep {
    override void dispose() {}

    override void doExecute() {
        // The transformation is executed 
        val list = context.get("TestOutput")as BlockingQueue<String>
        if(list!=null){
            list.put("exec_B")
        }
    }

    override void publishMessages() {
        publishings.forEach[ p |
            p.publishMessage("message_B"+p.topicName)
        ]
    }
    
    override doInitialize(IWorkflowContext ctx) {
        //do nothing
    }

}
