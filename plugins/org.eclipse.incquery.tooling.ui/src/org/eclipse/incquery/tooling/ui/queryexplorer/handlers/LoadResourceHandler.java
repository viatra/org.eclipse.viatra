/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *   Andras Okros - second version implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.AdapterUtil;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.EMFModelConnector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Default Resource loader.
 */
public class LoadResourceHandler extends LoadResourceSetHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        IModelConnector modelConnector = AdapterUtil.getModelConnectorFromIEditorPart(editorPart);
        if (modelConnector instanceof EMFModelConnector) {
            modelConnector.loadModel(IModelConnectorTypeEnum.RESOURCE);
            QueryExplorer.getInstance().load(((EMFModelConnector) modelConnector).getKey(), modelConnector);
        }
        return null;
    }

}
