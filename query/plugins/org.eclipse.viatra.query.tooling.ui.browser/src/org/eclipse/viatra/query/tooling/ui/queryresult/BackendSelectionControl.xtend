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
package org.eclipse.viatra.query.tooling.ui.queryresult

import org.eclipse.viatra.query.tooling.ui.queryexplorer.AbstractBackendSelectionControl
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint

/**
 * @author Abel Hegedus
 *
 */
class BackendSelectionControl extends AbstractBackendSelectionControl {
    
    override protected getHints() {
        val resultView = workbenchWindow.activePage.findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            val queryResultView = (resultView as QueryResultView)
            return queryResultView.hint
        }
        return null
    }
    
    override protected setHints(QueryEvaluationHint newHint) {
        val resultView = workbenchWindow.activePage.findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            val queryResultView = (resultView as QueryResultView)
            queryResultView.hint = newHint
        }
    }
    
}