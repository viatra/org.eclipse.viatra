/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius.util;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sirius.ui.editor.SessionEditor;
import org.eclipse.sirius.ui.editor.SessionEditorPlugin;
import org.eclipse.sirius.ui.editor.api.pages.AbstractSessionEditorPage;
import org.eclipse.sirius.ui.editor.api.pages.PageProviderRegistry.PositioningKind;
import org.eclipse.sirius.ui.editor.api.pages.PageUpdateCommandBuilder.PageUpdateCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.wizard.NewVgqlFileWizard;

public class VGQLSessionEditorPage extends AbstractSessionEditorPage {

    public static final String PAGE_ID = "org.eclipse.viatra.query.patternlanguage.emf.sirius.vgql";
    private static final String PAGE_TITLE = "VIATRA Query";

    private SessionEditor editor;

    public VGQLSessionEditorPage(SessionEditor editor) {
        super(editor, PAGE_ID, PAGE_TITLE);
        this.editor = editor;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm scrolledForm = managedForm.getForm();

        FormToolkit toolkit = managedForm.getToolkit();

        scrolledForm.setText(PAGE_TITLE);
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = managedForm.getForm().getBody();
        body.setLayout(GridLayoutFactory.swtDefaults().create());

        Composite subBody = toolkit.createComposite(body);
        subBody.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        subBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Button btn = toolkit.createButton(subBody, "Create Query Definition", SWT.PUSH);
        btn.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                final NewVgqlFileWizard wizard = new NewVgqlFileWizard(editor.getSession());
                final IFile currentFile = ResourceUtil.getFile(editor.getEditorInput());
                wizard.init(editor.getSite().getWorkbenchWindow().getWorkbench(),
                        currentFile == null ? new StructuredSelection()
                                : new StructuredSelection(currentFile.getProject()));
                
                final WizardDialog wizardDialog = new WizardDialog(managedForm.getForm().getShell(), wizard);
                wizardDialog.create();
                wizardDialog.open();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    @Override
    public Optional<String> getLocationId() {
        return Optional.of(SessionEditorPlugin.DEFAULT_PAGE_ID);
    }

    @Override
    public Optional<PositioningKind> getPositioning() {
        return Optional.of(PositioningKind.AFTER);
    }

    @Override
    public Optional<PageUpdateCommand> resourceSetChanged(ResourceSetChangeEvent resourceSetChangeEvent) {
        return Optional.empty();
    }

    @Override
    public Optional<PageUpdateCommand> pageChanged(boolean isVisible) {
        return Optional.empty();
    }

}
