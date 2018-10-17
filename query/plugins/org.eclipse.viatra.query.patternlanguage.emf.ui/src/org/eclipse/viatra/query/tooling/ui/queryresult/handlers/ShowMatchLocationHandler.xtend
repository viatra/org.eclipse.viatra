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
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView

/**
 * @author Abel Hegedus
 * @since 1.4
 */
class ShowMatchLocationHandler extends AbstractHandler {
    
    override execute(ExecutionEvent event) throws ExecutionException {
        val selection = HandlerUtil.getCurrentSelection(event)
        val resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            val active = resultView.hasActiveEngine
            
            if (active && selection instanceof IStructuredSelection) {
                val connector = resultView.modelConnector
                if(connector !== null) {
                    val eObjectsInSelection = newHashSet()
                    (selection as IStructuredSelection).iterator.forEach[
                        switch it {
                            IPatternMatch : {
                                eObjectsInSelection.addAll(it.toArray.filter(EObject))
                            }
                            EObject : {
                                eObjectsInSelection.add(it)
                            }
                            default : {
                                // do nothing
                            }
                        }
                    ]
                    connector.showLocation(eObjectsInSelection)
                }
            } 
        }
        return null
    }
    
}