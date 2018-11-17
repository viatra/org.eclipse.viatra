/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
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
import java.util.Collections;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.dialect.command.CreateRepresentationCommand;
import org.eclipse.sirius.business.api.modelingproject.ModelingProject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.command.semantic.AddSemanticResourceCommand;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelectionCallback;
import org.eclipse.sirius.ui.business.internal.commands.ChangeViewpointSelectionCommand;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternPackage;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.VgqlFactory;

/**
 * A wizard implementation used to create new eiq files.
 * 
 */
public class NewVgqlFileWizard extends Wizard implements INewWizard {

    private static final String NEW_EMF_INC_QUERY_QUERY_DEFINITION_FILE = "Create a new VIATRA Query Definition file.";
    private NewVgqlFileConfigurationPage page1;
    private ISelection selection;
    private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    public NewVgqlFileWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        page1 = new NewVgqlFileConfigurationPage();
        page1.init((IStructuredSelection) selection);
        page1.setDescription(NEW_EMF_INC_QUERY_QUERY_DEFINITION_FILE);
        addPage(page1);
        setForcePreviousAndNextButtons(false);
    }

    @Override
    public boolean performFinish() {
        final String containerName = page1.getContainerName();
        final String fileName = page1.getFileName();

        // replace dots with slash in the path
        final String packageName = page1.getPackageText().replaceAll("\\.", "/");

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Creating " + fileName, 1);
                    createVgqlFile(containerName, fileName, packageName, monitor);
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
            Thread.currentThread().interrupt();
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            EMFPatternLanguageUIPlugin.getInstance().logException(
                    "Cannot create Query Definition file: " + realException.getMessage(), realException);
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }

    private IFile createVgqlFile(String containerName, String fileName, String packageName, IProgressMonitor monitor) throws CoreException {
        final SubMonitor subMonitor = SubMonitor.convert(monitor, 7);
        IResource containerResource = root.findMember(new Path(containerName));

        IPath filePath = containerResource.getFullPath().append(packageName).append(fileName);
        IFile file = root.getFile(filePath);
        String fullPath = filePath.toString();

        URI fileURI = URI.createPlatformResourceURI(fullPath, false);
        
        final URI representationURI = ModelingProject.asModelingProject(containerResource.getProject()).get().getMainRepresentationsFileURI(subMonitor.split(1)).get();
        Session session = SessionManager.INSTANCE.getSession(representationURI, subMonitor.split(1));
        
        final TransactionalEditingDomain ted = session.getTransactionalEditingDomain();
        Resource resource = ted.createResource(fileURI.toString());

        PatternPackage pkg = createPackage(packageName, ted, resource);

        final AddSemanticResourceCommand addResourceCommand = new AddSemanticResourceCommand(session, fileURI, subMonitor.split(1));
        ted.getCommandStack().execute(addResourceCommand);
        
        containerResource.refreshLocal(0, subMonitor.split(1));
        ViewpointRegistry.getInstance().getViewpoints()
            .stream()
            .filter(vp -> Objects.equals(vp.getName(), "vp_vql_editor"))
            .findFirst()
            .ifPresent(vp -> ted.getCommandStack().execute(new RecordingCommand(ted) {
            
            @Override
            protected void doExecute() {
                session.createView(vp, Collections.singletonList(pkg), subMonitor.split(1));
                new ChangeViewpointSelectionCommand(session, new ViewpointSelectionCallback(), Collections.singleton(vp), Collections.emptySet(), subMonitor.split(1)).execute();
                
                vp.getOwnedRepresentations()
                    .stream()
                    .filter(rep -> Objects.equals("dd_vql_model_diagram", rep.getName()))
                    .findFirst()
                    .ifPresent(repDescriptor -> {
                        new CreateRepresentationCommand(session, repDescriptor, pkg, fileName, subMonitor.split(1)).execute();
                        final DRepresentation representation = DialectManager.INSTANCE.getRepresentations(repDescriptor, session).iterator().next();
                        
                        DialectUIManager.INSTANCE.openEditor(session, representation, subMonitor.split(1));
                        SessionManager.INSTANCE.notifyRepresentationCreated(session);
                        
                    });
            }
         }));
        session.save(subMonitor.split(1));
        return file;
    }

    private PatternPackage createPackage(String packageName, final TransactionalEditingDomain ted, Resource resource) {
        PatternPackage pkg = VgqlFactory.eINSTANCE.createPatternPackage();

        // Setting package name
        if (packageName != null && !packageName.isEmpty()) {
            pkg.setPackageName(packageName.replace("/", "."));
        }

        ted.getCommandStack().execute(new RecordingCommand(ted) {
            
            @Override
            protected void doExecute() {
                resource.getContents().add(pkg);
            }
        });
        return pkg;
    }
    
    
}