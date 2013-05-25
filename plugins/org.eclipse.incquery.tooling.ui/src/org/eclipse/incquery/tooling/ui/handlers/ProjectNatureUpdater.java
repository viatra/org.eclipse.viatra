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
package org.eclipse.incquery.tooling.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.ImmutableList;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ProjectNatureUpdater extends AbstractHandler {

    private class NatureUpdaterJob extends Job {

        private IProject project;
        private static final String incorrectBuilderID = "org.eclipse.incquery.tooling.ui.projectbuilder"; //$NON-NLS-1
        private static final String oldNatureID = "org.eclipse.viatra2.emf.incquery.projectnature"; //$NON-NLS-1

        public NatureUpdaterJob(IProject project) {
            super(String.format("Updating project %s", project.getName()));
            this.project = project;
        }

        /**
         * This method checks for an earlier IncQuery builder entry, and updates it to the current version. See bug
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=404952 for details.
         */
        private void repairErroneousBuilderEntry(IProject project) throws CoreException {
            IProjectDescription desc = project.getDescription();
            ICommand[] commands = desc.getBuildSpec();
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].getBuilderName().equals(incorrectBuilderID)) {
                    commands[i].setBuilderName(IncQueryNature.BUILDER_ID);
                }
            }
            desc.setBuildSpec(commands);
            project.setDescription(desc, null);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                repairErroneousBuilderEntry(project);

                final ImmutableList<String> newIDs = project.hasNature(IncQueryNature.NATURE_ID) ? ImmutableList
                        .<String> of() : ImmutableList.of(IncQueryNature.NATURE_ID);
                final ImmutableList<String> oldIDs = project.hasNature(oldNatureID) ? ImmutableList.of(oldNatureID)
                        : ImmutableList.<String> of();
                if (newIDs.size() + oldIDs.size() > 0) {
                    ProjectGenerationHelper.updateNatures(project, newIDs, oldIDs, monitor);
                }
            } catch (CoreException e) {
                return new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Error updating project natures", e);
            }
            return Status.OK_STATUS;
        }

    }

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
                    new NatureUpdaterJob(project).schedule();
                }
            }
        }
        return null;
    }

}
