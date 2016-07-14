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
package org.eclipse.viatra.query.tooling.ui.util;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * A generic double-click listener that invokes a command with the given identifier
 * and logs any exceptions with the given message.
 * 
 * @author Abel Hegedus
 * @since 1.4
 *
 */
public class CommandInvokingDoubleClickListener implements IDoubleClickListener {

    protected String commandId;
    protected String exceptionMessage;
    
    @Inject Logger logger;
    
    public CommandInvokingDoubleClickListener(String commandId, String exceptionMessage) {
        super();
        Preconditions.checkArgument(commandId != null, "CommandId cannot be null");
        Preconditions.checkArgument(exceptionMessage != null, "Exception message cannot be null");
        this.commandId = commandId;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public void doubleClick(DoubleClickEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof TreeSelection) {
            IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getService(IHandlerService.class);
            try {
                handlerService.executeCommand(commandId, null);
            } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
                logger.error(exceptionMessage, e);
            }
        }
    }

}