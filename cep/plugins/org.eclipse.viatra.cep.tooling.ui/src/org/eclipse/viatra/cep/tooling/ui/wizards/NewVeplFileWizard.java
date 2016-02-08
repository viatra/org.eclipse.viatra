/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *   Istvan David - updated for VIATRA-CEP
 *******************************************************************************/

package org.eclipse.viatra.cep.tooling.ui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.viatra.cep.tooling.ui.internal.Activator;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewVeplFileWizard extends Wizard implements INewWizard {

    private static final String NEW_VEPL_FILE = "Create a new VEPL file.";
    private NewVeplFileWizardContainerConfigurationPage page1;
    private ISelection selection;
    private IWorkbench workbench;
    private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    private IPath filePath;

    @Inject
    private IResourceSetProvider resourceSetProvider;

    public NewVeplFileWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        page1 = new NewVeplFileWizardContainerConfigurationPage();
        page1.init((IStructuredSelection) selection);
        page1.setDescription(NEW_VEPL_FILE);
        addPage(page1);
        setForcePreviousAndNextButtons(false);
    }

    private String getFileNameWithExtension(String originalFileName) {
        if (originalFileName.endsWith(".vepl")) {
            return originalFileName;
        } else {
            return originalFileName.concat(".vepl");
        }
    }

    @Override
    public boolean performFinish() {
        final String containerName = page1.getContainerName();
        final String fileName = getFileNameWithExtension(page1.getFileName());

        // replace dots with slash in the path
        final String packageName = page1.getPackageName().replaceAll("\\.", "/");

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Creating " + fileName, 1);
                    IFile file = createVeplFile(containerName, fileName, packageName);
                    BasicNewResourceWizard.selectAndReveal(file, workbench.getActiveWorkbenchWindow());
                    IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), file, true);
                    monitor.worked(1);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(false, false, op);
        } catch (InterruptedException e) {
            // This is never thrown as of false cancelable parameter of getContainer().run
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            Activator.getDefault()
                    .logException("Cannot create VEPL file: " + realException.getMessage(), realException);
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    private IFile createVeplFile(String containerName, String fileName, String packageName) throws IOException,
            CoreException {
        IResource containerResource = root.findMember(new Path(containerName));
        ResourceSet resourceSet = resourceSetProvider.get(containerResource.getProject());

        filePath = containerResource.getFullPath().append(packageName + "/" + fileName);
        IFile file = root.getFile(filePath);
        String fullPath = filePath.toString();

        URI fileURI = URI.createPlatformResourceURI(fullPath, false);
        Resource resource = resourceSet.createResource(fileURI);

        resource.getContents().add(getDefaultEventModel(packageName));

        resource.save(Collections.EMPTY_MAP);
        containerResource.refreshLocal(0, new NullProgressMonitor());
        return file;
    }

    private EventModel getDefaultEventModel(String packageName) {
        EventModel eventModel = VeplFactory.eINSTANCE.createEventModel();
        eventModel.setName(packageName.replaceAll("/", "\\."));

        return eventModel;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
}