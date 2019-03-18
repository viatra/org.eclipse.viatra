/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;

public class ShowLocationHandler extends ShowPatternLocationHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        QueryRegistryTreeEntry entry = (QueryRegistryTreeEntry) selection.getFirstElement();
        IQuerySpecificationRegistryEntry registryEntry = entry.getEntry();
        showPatternLocation(event, registryEntry);   
        return null;
    }

}
