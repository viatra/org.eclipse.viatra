/*******************************************************************************
 * Copyright (c) 2010-2016 itemis AG (http://www.itemis.eu), Peter Lunk IncQuery Labs Ltd. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 *******************************************************************************/
package org.eclipse.viatra.transformation.ui.wizards;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * A wizard that enables the user to create simple VIATRA transformation implementations. These implementations do not
 * define any rules, only contain the glue code required to create various model transformations.
 * 
 * The following transformations can be created:
 *  - Batch transformation
 *  - Batch transformation with logger support
 *  - Batch transformation with debugger support 
 *  - Event-driven transformation
 *  - Event-driven transformation with logger support
 *  - Event-driven transformation with debugger support
 * 
 * @author Peter Lunk
 *
 */
@SuppressWarnings("restriction")
public class NewTransformationWizard extends NewElementWizard {
    public static final String TITLE = "Create VIATRA transformation";
    public static final String NEWPAGENAME = "Create VIATRA transformation";
    public static final String TYPEPAGENAME = "Define VIATRA transformation details";
    private NewTransformationWizardPage page;
    private NewTransformationTypeSelectionWizardPage typePage;

    @Inject
    public NewTransformationWizard(IImageDescriptorHelper imgHelper, NewTransformationWizardPage page,
            NewTransformationTypeSelectionWizardPage typePage) {
        this.page = page;
        this.typePage = typePage;
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
        setWindowTitle(TITLE);
    }

    @Override
    public void addPages() {
        page.init(getSelection());
        addPage(page);
        addPage(typePage);
    }

    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    }

    @Override
    public IJavaElement getCreatedElement() {
        return null;
    }

    @Override
    public boolean performFinish() {
        final int size = this.page.createType(typePage.getTransformationType(), typePage.isDebugger(),
                typePage.isLogging(), typePage.getTransformationSessionName());
        final IResource resource = page.getResource();
        if (resource != null) {

            try {
                final IProject project = resource.getProject();
                ProjectGenerationHelper.checkOpenPDEProject(project);
                ProjectGenerationHelper.ensurePackageImports(project, getPackageImports());
                ProjectGenerationHelper.ensureBundleDependencies(project, getPluginDependencies());
            } catch (CoreException e1) {
                throw new RuntimeException(e1);
            }

            selectAndReveal(resource);
            final Display display = getShell().getDisplay();
            display.asyncExec(() -> {
                IEditorPart editor;
                try {
                    editor = IDE.openEditor(JavaPlugin.getActivePage(), (IFile) resource);
                    if (editor instanceof ITextEditor) {
                        final ITextEditor textEditor = (ITextEditor) editor;
                        ISelectionProvider selectionProvider = textEditor.getSelectionProvider();
                        ISelection selection = new TextSelection(size - 2, 0);
                        selectionProvider.setSelection(selection);
                    }
                } catch (PartInitException e) {
                    throw new RuntimeException(e);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private List<String> getPluginDependencies() {
        List<String> dependencies = Lists.newArrayList("org.eclipse.xtext.xbase.lib",
                "org.eclipse.viatra.query.runtime", "org.eclipse.viatra.transformation.runtime.emf",
                "org.eclipse.viatra.transformation.evm");
        if (typePage.isDebugger()) {
            dependencies.addAll(Lists.newArrayList("org.eclipse.viatra.transformation.runtime.debug"));
        }
        return dependencies;
    }

    private List<String> getPackageImports() {
        List<String> imports = Lists.newArrayList();
        if (typePage.isLogging()) {
            imports.add("org.apache.log4j");
        }
        return imports;
    }
}
