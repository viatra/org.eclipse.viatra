/**
 * Copyright (c) 2010-2017, Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi, Peter Lunk, Istvan Rath, Daniel Varro, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *   Peter Lunk - SerializedJavaObjectSubstitution
 */
package org.eclipse.viatra.query.testing.ui.wizards;


import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.viatra.query.testing.snapshot.InputSpecification;
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot;
import org.eclipse.viatra.query.testing.snapshot.SnapshotFactory;
import org.eclipse.viatra.query.testing.ui.ViatraQueryTestingUIPlugin;


/**
 * This is a simple wizard for creating a new snapshot model
 */
public class SnapshotModelWizard extends Wizard implements INewWizard {
    
    public static final String FILE_EXTENSION = "snapshot";

    /**
     * This is the file creation page.
     */
    protected SnapshotModelWizardNewFileCreationPage newFileCreationPage;

    /**
     * Remember the selection during initialization for populating the default container.
     */
    protected IStructuredSelection selection;

    /**
     * Remember the workbench during initialization.
     */
    protected IWorkbench workbench;

    /**
     * Caches the names of the types that can be created as the root object.
     */
    protected List<String> initialObjectNames;

    /**
     * This just records the information.
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
        setWindowTitle("Create new Query Snapshot");
    }

    /**
     * Do the work after everything is specified.
     */
    @Override
    public boolean performFinish() {
        try {
            final IFile modelFile = newFileCreationPage.getModelFile();

            WorkspaceModifyOperation operation =
                new WorkspaceModifyOperation() {
                    @Override
                    protected void execute(IProgressMonitor progressMonitor) {
                        try {
                            ResourceSet resourceSet = new ResourceSetImpl();
                            URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);
                            Resource resource = resourceSet.createResource(fileURI);

                            // Add the initial model object to the contents.
                            QuerySnapshot rootObject = SnapshotFactory.eINSTANCE.createQuerySnapshot();
                            rootObject.setInputSpecification(InputSpecification.RESOURCE_SET);
                            resource.getContents().add(rootObject);

                            resource.save(new HashMap<>());
                        }
                        catch (Exception exception) {
                            ViatraQueryTestingUIPlugin.getDefault().log(exception);
                        }
                        finally {
                            progressMonitor.done();
                        }
                    }
                };

            getContainer().run(false, false, operation);

            // Select the new file resource in the current view.
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = workbenchWindow.getActivePage();
            final IWorkbenchPart activePart = page.getActivePart();
            if (activePart instanceof ISetSelectionTarget) {
                final ISelection targetSelection = new StructuredSelection(modelFile);
                getShell().getDisplay().asyncExec
                    (new Runnable() {
                         public void run() {
                             ((ISetSelectionTarget)activePart).selectReveal(targetSelection);
                         }
                     });
            }

            // Open an editor on the new file.
            try {
                page.openEditor
                    (new FileEditorInput(modelFile),
                     workbench.getEditorRegistry().getDefaultEditor(modelFile.getFullPath().toString()).getId());					 	 
            }
            catch (PartInitException exception) {
                MessageDialog.openError(workbenchWindow.getShell(), "Error opening Snapshot Editor", exception.getMessage());
                return false;
            }

            return true;
        }
        catch (Exception exception) {
            ViatraQueryTestingUIPlugin.getDefault().log(exception);
            return false;
        }
    }

    public class SnapshotModelWizardNewFileCreationPage extends WizardNewFileCreationPage {

        public SnapshotModelWizardNewFileCreationPage(IStructuredSelection selection) {
            super(SnapshotModelWizardNewFileCreationPage.class.getName(), selection);
        }

        @Override
        protected boolean validatePage() {
            if (super.validatePage()) {
                String extension = new Path(getFileName()).getFileExtension();
                if (!Objects.equals(extension, FILE_EXTENSION)) {
                    setErrorMessage("Snapshot models have to have a file extension of 'snapshot'");
                    return false;
                }
                return true;
            }
            return false;
        }

        public IFile getModelFile() {
            return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
        }
    }


    @Override
    public void addPages() {
        final String defaultModelBaseFilename = "QuerySnapshot";
            
        newFileCreationPage = new SnapshotModelWizardNewFileCreationPage(selection);
        newFileCreationPage.setTitle("New Query Snapshot");
        newFileCreationPage.setDescription("Initializes an empty query snapshot file");
        newFileCreationPage.setFileName(defaultModelBaseFilename + "." + FILE_EXTENSION);
        addPage(newFileCreationPage);

        // Try and get the resource selection to determine a current directory for the file dialog.
        if (selection != null && !selection.isEmpty()) {
            Object selectedElement = selection.iterator().next();
            if (selectedElement instanceof IResource) {
                IResource selectedResource = (IResource)selectedElement;
                if (selectedResource.getType() == IResource.FILE) {
                    selectedResource = selectedResource.getParent();
                }

                if (selectedResource instanceof IFolder || selectedResource instanceof IProject) {
                    newFileCreationPage.setContainerFullPath(selectedResource.getFullPath());

                    String modelFilename = defaultModelBaseFilename + "." + FILE_EXTENSION;
                    for (int i = 1; ((IContainer)selectedResource).findMember(modelFilename) != null; ++i) {
                        modelFilename = defaultModelBaseFilename + i + "." + FILE_EXTENSION;
                    }
                    newFileCreationPage.setFileName(modelFilename);
                }
            }
        }
    }

}
