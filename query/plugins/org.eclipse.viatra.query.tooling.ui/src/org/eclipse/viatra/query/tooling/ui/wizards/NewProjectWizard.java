/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.operations.CreateProjectOperation;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * A wizard class for initializing a VIATRA Query project.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class NewProjectWizard extends Wizard implements INewWizard {

    private WizardNewProjectCreationPage projectCreationPage;
    private IProject project;
    private IWorkbench workbench;
    private IWorkspace workspace;

    @Inject
    private Logger logger;

    @Override
    public void addPages() {
        projectCreationPage = new WizardNewProjectCreationPage("NewViatraQueryProject");
        projectCreationPage.setTitle("New VIATRA Query Project");
        projectCreationPage.setDescription("Create a new VIATRA Query project.");
        addPage(projectCreationPage);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        workspace = ResourcesPlugin.getWorkspace();
    }

    @Override
    public boolean performFinish() {
        if (project != null) {
            return true;
        }
        final IProject projectHandle = projectCreationPage.getProjectHandle();
        if (projectHandle.exists()) {
            return false;
        }
        URI projectURI = (!projectCreationPage.useDefaults()) ? projectCreationPage.getLocationURI() : null;
        final IProjectDescription description = workspace.newProjectDescription(projectHandle.getName());
        description.setLocationURI(projectURI);

        WorkspaceModifyOperation op = new CreateProjectOperation(projectHandle, description, ImmutableList.<String> of());

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            // Removing project if it is partially created
            if (projectHandle.exists()) {
                try {
                    projectHandle.delete(true, new NullProgressMonitor());
                } catch (CoreException e1) {
                    logger.error("Cannot remove partially created VIATRA Query project.", e1);
                }
            }
            Throwable realException = e.getTargetException();
            logger.error("Cannot create VIATRA Query project: " + realException.getMessage(), realException);
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        project = projectHandle;

        BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

        return true;

    }

}
