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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.handlers.QBEViewMainSourceProvider;

public class RefreshCodeDialog extends Dialog {

    private static final String REFRESH_CODE_DIALOG_TITLE = "Save generated pattern to...";
    private static final String RADIO_LABEL_TWO = "New .vql file inside an existing VIATRA project";
    private static final String RADIO_LABEL_THREE = "Existing .vql file (will be overwritten!)";
    private static final String RADIO_LABEL_FOUR = "Clipboard";
    private static final String OPEN_RESOURCE_DIALOG_TITLE = "Choose existing .vql file to be overwritten";
    private static final String VQL_FILE_EXTENSION = "vql";

    private Button radioButtonOne;
    private Button radioButtonTwo;
    private Button radioButtonThree;

    private QBEView qbeView;
    private QBEViewMainSourceProvider provider;

    private static class EIQFilesViewerFilter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof IFile && !VQL_FILE_EXTENSION.equals(((IFile) element).getFileExtension()))
                return false;
            return true;
        }
    }

    public RefreshCodeDialog(Shell parentShell, QBEView view, QBEViewMainSourceProvider provider) {
        super(parentShell);
        this.qbeView = view;
        this.provider = provider;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 15;
        layout.marginWidth = 10;
        container.setLayout(layout);

        radioButtonOne = new Button(container, SWT.RADIO);
        radioButtonOne.setText(RADIO_LABEL_TWO);
        radioButtonOne.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        radioButtonTwo = new Button(container, SWT.RADIO);
        radioButtonTwo.setText(RADIO_LABEL_THREE);
        radioButtonTwo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        radioButtonThree = new Button(container, SWT.RADIO);
        radioButtonThree.setText(RADIO_LABEL_FOUR);

        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(375, 175);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(REFRESH_CODE_DIALOG_TITLE);
    }

    @Override
    protected void okPressed() {

        String code = ((this.qbeView == null || this.qbeView.getService() == null) ? null
                : this.qbeView.getService().getPatternCode());

        if (code != null) {
            if (radioButtonOne.getSelection())
                this.performNewFileExistingProjectAction(code);
            else if (radioButtonTwo.getSelection())
                this.performExistingFileAction(code);
            else if (radioButtonThree.getSelection())
                this.performClipboardAction(code);
        }

        super.okPressed();
    }

    private void performNewFileExistingProjectAction(String code) {
        if (qbeView.getSelection() != null) {
            WizardNewEIQFileCreation newFileWizard = new WizardNewEIQFileCreation(code, this.provider);
            newFileWizard.init(PlatformUI.getWorkbench(), qbeView.getSelection());
            WizardDialog wizardDialog = new WizardDialog(this.getShell(), newFileWizard);
            wizardDialog.open();
        } else {
            StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID,
                    IStatus.ERROR, "Active selection not found.", new IllegalStateException()));
        }
    }

    private void performExistingFileAction(String code) {
        ViewerFilter eiqFilesFilter = new EIQFilesViewerFilter();
        List<ViewerFilter> eiqFilesFilterList = new ArrayList<ViewerFilter>();
        eiqFilesFilterList.add(eiqFilesFilter);
        IFile[] files = WorkspaceResourceDialog.openFileSelection(this.getShell(), OPEN_RESOURCE_DIALOG_TITLE, "",
                false, null, eiqFilesFilterList);
        for (IFile f : files) {
            try {
                f.setContents(new ByteArrayInputStream(code.getBytes()), true, false, null);
                QBEViewUtils.setLinkedFile(f);
                this.provider.setLinkedFileExistingState();
            } catch (CoreException ex) {
                StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID,
                        IStatus.ERROR, ex.getMessage(), ex));
            }
            break;
        }
    }

    private void performClipboardAction(String code) {
        new Clipboard(null).setContents(new Object[] { code }, new Transfer[] { TextTransfer.getInstance() });
    }
}
