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

package org.eclipse.viatra.query.patternlanguage.emf.sirius.wizard;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ui.tools.api.project.ModelingProjectManager;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.builder.GraphicalQueryProjectBuilder;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;

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
    
    @Inject
    private EclipseOutputConfigurationProvider outputConfigurationProvider;

    @Override
    public void addPages() {
        projectCreationPage = new WizardNewProjectCreationPage("NewGraphicalViatraQueryProject");
        projectCreationPage.setTitle("New Graphical VIATRA Query Project");
        projectCreationPage.setDescription("Create a new Graphical VIATRA Query project.");
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
        IProjectDescription description = workspace.newProjectDescription(projectHandle.getName());
        description.setLocationURI((!projectCreationPage.useDefaults()) ? projectCreationPage.getLocationURI() : null);
        
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            private void addBuilder(IProject project, IProgressMonitor monitor) throws CoreException {
                IProjectDescription description = project.getDescription();
                final ICommand[] buildCommands = description.getBuildSpec();
                final ICommand[] newCommands = new ICommand[buildCommands.length + 1];
                newCommands[0] = description.newCommand();
                newCommands[0].setBuilderName(GraphicalQueryProjectBuilder.BUILDER_ID);
                System.arraycopy(buildCommands, 0, newCommands, 1, buildCommands.length);
                description.setBuildSpec(newCommands);
                project.setDescription(description, monitor);
            }
            
            @Override
            protected void execute(IProgressMonitor monitor)
                    throws CoreException, InvocationTargetException, InterruptedException {
                ProjectGenerationHelper.createProject(description, projectHandle, ImmutableList.of(), monitor);
                ProjectGenerationHelper.ensureSourceFolder(projectHandle, outputConfigurationProvider.getOutputConfigurations(projectHandle), monitor);
                projectHandle.getFolder("vgql-gen");
                ProjectGenerationHelper.ensureSourceFolder(projectHandle, "vgql-gen", monitor);
                
                addBuilder(projectHandle, monitor);
                
                // Initialize Sirius Session
                ModelingProjectManager.INSTANCE.convertToModelingProject(projectHandle, monitor);
                final Session session = ModelingProjectManager.INSTANCE.createLocalRepresentationsFile(projectHandle, monitor);
                session.save(monitor);
            }
            
        };

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
