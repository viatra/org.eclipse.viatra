/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public class ModelInstanceViewer extends ViewPart implements IDebuggerHostAgentListener {
    //TODO Adaptation of this feature is non trivial may require serious changes in the Model Instance Viewer: Bug 493206
    @Override
    public void transformationStateChanged(TransformationState state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void terminated(IDebuggerHostAgent agent) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }
}
