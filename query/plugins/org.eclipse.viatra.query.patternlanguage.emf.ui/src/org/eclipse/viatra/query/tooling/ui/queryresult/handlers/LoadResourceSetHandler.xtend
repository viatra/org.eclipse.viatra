/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.tooling.ui.queryresult.handlers

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector
import org.eclipse.viatra.query.runtime.ui.modelconnector.AdapterUtil
import org.eclipse.viatra.query.runtime.ui.modelconnector.EMFModelConnector
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
