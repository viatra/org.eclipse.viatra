/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.migrator.metadata;

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
import org.eclipse.viatra.migrator.MigratorConstants;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ProjectNatureUpdater extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

        if (currentSelection instanceof IStructuredSelection) {
            for (Object element : ((IStructuredSelection) currentSelection).toList()) {
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
                }
                if (project != null) {
                    final NatureUpdaterJob job = new NatureUpdaterJob(project);
                    job.schedule();
                    try {
                    	ICommandService service = ((ICommandService)HandlerUtil.getActiveSite(event).getService(ICommandService.class));
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
