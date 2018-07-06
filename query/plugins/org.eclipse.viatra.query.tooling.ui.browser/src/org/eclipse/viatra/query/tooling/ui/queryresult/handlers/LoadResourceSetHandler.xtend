/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryresult.handlers

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.AdapterUtil
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.EMFModelConnector
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException

/** 
 * @author Abel Hegedus
 */
class LoadResourceSetHandler extends AbstractHandler {
    
    override Object execute(ExecutionEvent event) throws ExecutionException {
        var IEditorPart editorPart = HandlerUtil.getActiveEditorChecked(event)
        val resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            try {
                var IModelConnector modelConnector = AdapterUtil.getModelConnectorFromIEditorPart(editorPart)
                if (modelConnector instanceof EMFModelConnector) {
                    modelConnector.loadModel(IModelConnectorTypeEnum.RESOURCESET)
                    resultView.loadModel(modelConnector, IModelConnectorTypeEnum.RESOURCESET)
                }
            } catch (ViatraQueryException ex) {
                throw new ExecutionException("Error while initializing Query Engine: " + ex.message, ex)
            }
        }
        return null
    }
}
