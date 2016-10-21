/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.ui.handlers;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class RefreshCodeToLinkedFileHandler extends AbstractHandler {

    private QBEView qbeView;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        if (this.qbeView == null)
            this.qbeView = QBEViewUtils.getQBEView(event);

        if (QBEViewUtils.getLinkedFile() != null) {
            try {
                String code = ((this.qbeView == null || this.qbeView.getService() == null) ? null
                        : this.qbeView.getService().getPatternCode());
                QBEViewUtils.getLinkedFile().setContents(new ByteArrayInputStream(code.getBytes()), true, false, new NullProgressMonitor());
            } catch (CoreException ex) {
                StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID,
                        IStatus.ERROR, ex.getMessage(), ex));
            }
        } else {
            StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID,
                    IStatus.ERROR, "Linked file should not be null at this point", new IllegalStateException()));
        }

        return null;
    }

}
