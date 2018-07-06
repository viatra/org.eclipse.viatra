/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.handlers

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView
import org.eclipse.viatra.query.tooling.ui.queryresult.util.QueryResultViewUtil
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.swt.widgets.Shell

/**
 * @author Abel Hegedus
 */
class LoadQueriesHandler extends AbstractHandler {

    public static final String COMMAND_ID = "org.eclipse.viatra.query.tooling.ui.result.loadqueries";

    override Object execute(ExecutionEvent event) throws ExecutionException {
        val selection = HandlerUtil.getCurrentSelection(event)
        val resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            val active = resultView.hasActiveEngine
            
            if (active && selection instanceof IStructuredSelection) {
                val selectedQueries = QueryResultViewUtil.getRegistryEntriesFromSelection(selection as IStructuredSelection)
                resultView.loadQueriesIntoActiveEngineInBackground(selectedQueries)
            } else {
                resultView.site.shell.queryLoadingFailed
            }
        }
        return null
    }
    
    def static queryLoadingFailed(Shell parent) {
        MessageDialog.openError(parent, "Query loading failed",
            "Please load a model into the Query Results view before loading queries!"
        )
    }
}
