/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.views.internal;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers.CreateBreakPointHandler;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;

import com.google.common.collect.Maps;

/**
 * 
 * @author Marton Bur
 *
 */
public class BreakPointListener implements IDoubleClickListener {

    
    private LocalSearchDebugger localSearchDebugger;

    public BreakPointListener(LocalSearchDebugger localSearchDebugger){
        this.localSearchDebugger = localSearchDebugger;}
    
    @Override
	public void doubleClick(DoubleClickEvent event) {
		// TreeViewer viewer = (TreeViewer) event.getViewer();
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();

		final LocalSearchDebugView debuggerView = localSearchDebugger.getLocalSearchDebugView();
        final IWorkbenchPartSite site = debuggerView.getSite();
		Map<String, Object> eventContextParameters = Maps.newHashMap();
		eventContextParameters.put(ISources.ACTIVE_WORKBENCH_WINDOW_NAME, site.getWorkbenchWindow());
		eventContextParameters.put(ISources.ACTIVE_PART_NAME, debuggerView);
		eventContextParameters.put(ISources.ACTIVE_PART_ID_NAME, LocalSearchDebugView.ID);
		eventContextParameters.put(ISources.ACTIVE_CURRENT_SELECTION_NAME, thisSelection);
		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		try {
		    commandService.getCommand("org.eclipse.incquery.tooling.localsearch.ui.debugger.localsearch.placebreakpoint").executeWithChecks(
		            new ExecutionEvent(null, eventContextParameters, null, null));
		}
		catch (Exception e) {
		    IncQueryLoggingUtil.getLogger(getClass()).error("Error setting up breakpoint", e);
		}
		
        
    }


}
