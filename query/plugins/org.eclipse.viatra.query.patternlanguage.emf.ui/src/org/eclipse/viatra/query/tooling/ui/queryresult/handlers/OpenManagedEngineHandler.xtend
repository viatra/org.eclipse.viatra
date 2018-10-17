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
import org.eclipse.swt.widgets.Shell
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView
import org.eclipse.viatra.query.tooling.ui.queryresult.util.ViatraQueryEngineContentProvider
import org.eclipse.viatra.query.tooling.ui.queryresult.util.ViatraQueryEngineLabelProvider
import org.eclipse.ui.dialogs.ISelectionStatusValidator
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin

/** 
 * @author Abel Hegedus
 */
class OpenManagedEngineHandler extends AbstractHandler {
    
    override Object execute(ExecutionEvent event) throws ExecutionException {
        val resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            var queryResultView = (resultView)
            // open dialog with list of managed engines
            val engine = openDialog(queryResultView.site.shell)
            if(engine !== null){
                queryResultView.loadExistingEngine(engine)
            }
        }
        return null
    }
    
    private def openDialog(Shell shell) {
        val contentProvider = new ViatraQueryEngineContentProvider => [
            traverseResources = true
            traverseEObjects = true
        ]
        val engineSelectionDialog = new ElementTreeSelectionDialog(shell, new ViatraQueryEngineLabelProvider, contentProvider)
        engineSelectionDialog.setTitle("Managed engine selection")
        engineSelectionDialog.setMessage("Select a managed engine (root elements of the tree)!")
        engineSelectionDialog.input = ViatraQueryEngineManager.instance
        engineSelectionDialog.helpAvailable = false
        engineSelectionDialog.validator = new ISelectionStatusValidator() {
            override validate(Object[] selection) {
                if (selection.size == 1 && selection.filter(AdvancedViatraQueryEngine).size == 1){
                    return new Status(IStatus.OK, EMFPatternLanguageUIPlugin.ID, 0, "", null)
                } else {
                    return new Status(IStatus.ERROR, EMFPatternLanguageUIPlugin.ID, 0, "", null)
                }
            }
        }
        engineSelectionDialog.open()
        val result = engineSelectionDialog.getResult()
        return result?.filter(AdvancedViatraQueryEngine)?.head
    }
}
