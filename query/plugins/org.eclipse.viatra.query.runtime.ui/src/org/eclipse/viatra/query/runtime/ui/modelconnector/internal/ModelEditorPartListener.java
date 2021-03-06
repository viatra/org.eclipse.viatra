/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.ui.modelconnector.internal;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector;

/**
 * The PartListener is used to observe {@link IEditorPart} close actions.
 */
public class ModelEditorPartListener implements IPartListener {

    private final IModelConnector modelConnector;
    
    public ModelEditorPartListener(IModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        // Only unload if the closed editor belongs to our model connector
        if (part instanceof IEditorPart && part.equals(this.modelConnector.getOwner())) {
            modelConnector.unloadModel();
        }
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        // empty method
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        // empty method
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
        // empty method
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        // empty method
    }

}
