/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.handlers

import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry
import org.eclipse.viatra.query.tooling.ui.queryregistry.handlers.ShowPatternLocationHandler
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher

/**
 * @author Abel Hegedus
 * @since 1.4
 */
class ShowMatcherLocationHandler extends ShowPatternLocationHandler {
    
    override execute(ExecutionEvent event) throws ExecutionException {
        val selection = HandlerUtil.getCurrentSelection(event) as IStructuredSelection;
        val matcher = selection.getFirstElement() as QueryResultTreeMatcher<?>;
        if(matcher.entry !== null) {
            showPatternLocation(event, matcher.entry);
        } else {
            val fqn = matcher.matcher.specification.fullyQualifiedName
            val view = QuerySpecificationRegistry.instance.createView[IQuerySpecificationRegistryEntry entry |
                val fqnRelevant = (fqn == entry.fullyQualifiedName)
                return fqnRelevant
            ]
            view.entries.forEach[
                showPatternLocation(event, it)
            ]
        }
        return null;
    }
    
}