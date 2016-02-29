/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.ui.ui;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.handlers.QBEViewMainSourceProvider;

public class WizardNewEIQFileCreation extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private String code;
    private WizardNewFileCreationPage newFileWizardPage;
    private QBEViewMainSourceProvider qbeViewMainSourceProvider;

    private static final String WIZARD_WINDOW_TITLE = "New VIATRA Query Definition";
    private static final String WIZARD_PAGE_TITLE = "VIATRA QBE Query Definition Wizard";
    private static final String WIZARD_PAGE_DESCRIPTION = "Create a new VIATRA Query Definition file";
    private static final String WIZARD_PAGE_NAME = "org.eclipse.viatra.addon.querybyexample.view.ui.WizardNewEIQFileCreation:newFileWizardPage";
    private static final String WIZARD_PAGE_EIQ_FILE_EXTENSION = "vql";

    public WizardNewEIQFileCreation(String code, QBEViewMainSourceProvider provider) {
        this.setWindowTitle(WIZARD_WINDOW_TITLE);
        this.code = code;
        this.setHelpAvailable(false);
        this.qbeViewMainSourceProvider = provider;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.newFileWizardPage = new WizardNewFileCreationPage(WIZARD_PAGE_NAME, this.selection);
        this.newFileWizardPage.setTitle(WIZARD_PAGE_TITLE);
        this.newFileWizardPage.setDescription(WIZARD_PAGE_DESCRIPTION);
        this.newFileWizardPage.setFileExtension(WIZARD_PAGE_EIQ_FILE_EXTENSION);
    }

    @Override
    public void addPages() {
        this.addPage(this.newFileWizardPage);
    }

    @Override
    public boolean performFinish() {
        IFile file = newFileWizardPage.createNewFile();
        if (file != null) {
            try {
                file.setContents(new ByteArrayInputStream(this.code.getBytes()), true, false, null);
                QBEViewUtils.setLinkedFile(file);
                this.qbeViewMainSourceProvider.setLinkedFileExistingState();
                return true;
            } catch (CoreException e) {
                StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID,
                        IStatus.ERROR, e.getMessage(), e));
            }
        }
        return false;
    }
}
