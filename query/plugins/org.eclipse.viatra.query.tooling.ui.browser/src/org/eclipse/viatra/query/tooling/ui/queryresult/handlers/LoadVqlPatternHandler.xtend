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

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdater
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import java.util.Set

/**
 * @author Abel Hegedus
 */
class LoadVqlPatternHandler extends AbstractHandler {
    @Inject package Injector injector

    override Object execute(ExecutionEvent event) throws ExecutionException {
        val IEditorPart editorPart = HandlerUtil.getActiveEditorChecked(event)
        val resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID)
        if (resultView instanceof QueryResultView) {
            val queryResultView = (resultView as QueryResultView)
            val active = queryResultView.hasActiveEngine
            
            if (active && editorPart instanceof XtextEditor) {
                val XtextEditor xtextEditor = (editorPart as XtextEditor)
                val resource = xtextEditor.resource
                val sourceId = XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX + resource.project.name
                val Set<String> patternFQNs = xtextEditor.getDocument().readOnly[ XtextResource state |
                    val fqns = newHashSet
                    val iterator = state.allContents
                    iterator.forEach[pattern | 
                        if (pattern instanceof Pattern) {
                            fqns.add(CorePatternLanguageHelper.getFullyQualifiedName(pattern))
                            iterator.prune
                        }
                    ]
                    return fqns
                ]
                
                queryResultView.loadQueriesIntoActiveEngineInBackground(patternFQNs, sourceId)
                    
            } else {
                MessageDialog.openError(queryResultView.site.shell, "Query loading failed",
                    "Please load a model into the Query Results view before loading queries!"
                )
            }
        }
        return null
    }
}
