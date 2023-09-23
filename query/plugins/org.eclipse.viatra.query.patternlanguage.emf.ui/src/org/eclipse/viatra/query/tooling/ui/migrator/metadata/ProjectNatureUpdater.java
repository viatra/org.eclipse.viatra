/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.migrator.metadata;

import com.google.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.tooling.ui.migrator.MigratorConstants;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ProjectNatureUpdater extends AbstractHandler {

    @Inject
    private EclipseOutputConfigurationProvider outputConfigurationProvider;
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

        if (currentSelection instanceof IStructuredSelection) {
            for (Object element : ((IStructuredSelection) currentSelection).toList()) {
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = ((IAdaptable) element).getAdapter(IProject.class);
                }
                if (project != null) {
                    final NatureUpdaterJob job = new NatureUpdaterJob(project, outputConfigurationProvider);
                    job.schedule();
                    try {
                        ICommandService service = HandlerUtil.getActiveSite(event).getService(ICommandService.class);
                        service.getCommand(MigratorConstants.API_MIGRATOR_COMMAND_ID).executeWithChecks(event);
                    } catch (NotDefinedException | NotEnabledException | NotHandledException e) {
                        throw new ExecutionException("Error migrating project", e);
                    }
                }
            }
        }
        return null;
    }

}
