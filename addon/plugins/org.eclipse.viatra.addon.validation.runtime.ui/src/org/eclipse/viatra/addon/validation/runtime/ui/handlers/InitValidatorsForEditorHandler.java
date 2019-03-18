/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.runtime.ui.handlers;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.addon.validation.runtime.ui.ValidationInitUtil;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.ui.modelconnector.AdapterUtil;
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector;

public class InitValidatorsForEditorHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        
        final Optional<IModelConnector> optionalConnector = AdapterUtil.getModelConnectorFromIEditorPartChecked(editorPart);
        if (optionalConnector.isPresent()) {
            ResourceSet resourceSet = (ResourceSet) optionalConnector.get().getNotifier(IModelConnectorTypeEnum.RESOURCESET);
            if (resourceSet != null) {
                ValidationInitUtil.initializeAdapters(editorPart, resourceSet);
            }
        } else {
            
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), "No Model Connector",
                    String.format("No model connector registered for editor %s", editorPart.getTitle()));
        }
        return null;
    }

}