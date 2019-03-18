/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.runtime;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The PartListener is used to observe EditorPart close actions.
 * 
 * @author Tamas Szabo
 * 
 */
public class ValidationPartListener implements IPartListener {

    private static ValidationPartListener instance;

    public static ValidationPartListener getInstance() {
        if (instance == null) {
            instance = new ValidationPartListener();
        }
        return instance;
    }

    protected ValidationPartListener() {
        // Hiding public constructor
    }

    @Override
    public void partActivated(IWorkbenchPart part) {

    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {

    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            IEditorPart closedEditor = (IEditorPart) part;
            ConstraintAdapter adapter = ValidationUtil.getAdapterMap().remove(part);
            if (adapter != null) {
                adapter.dispose();
            }
            ValidationUtil.unregisterEditorPart(closedEditor);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {

    }

    @Override
    public void partOpened(IWorkbenchPart part) {

    }
}
