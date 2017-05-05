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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.viatra.query.tooling.core.targetplatform.ITargetPlatformMetamodelLoader;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.ImportListAdapter;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.ImportListLabelProvider;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.ObjectListAdapter;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.ObjectListLabelProvider;
import org.eclipse.viatra.query.tooling.ui.wizards.internal.ObjectParameter;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Second page of the {@link NewVqlFileWizard} which allows to specify pattern parameters and imported {@link EPackage}
 * s.
 * 
 * @author Tamas Szabo
 * 
 */
@SuppressWarnings("restriction")
public class NewVqlFileWizardPatternConfigurationPage extends WizardPage {

    private static final String TITLE = "VIATRA Query Definition Wizard";
    private static final String PATTERN_NAME_SHOULD_BE_SPECIFIED = "Pattern name should be specified!";
    private static final String PATTERN_NAME_MUST_BE_SPECIFIED = "Pattern name must be specified, if at least one parameter is set!";
    private Text patternText;
    private ListDialogField<String> importList;
    private ListDialogField<ObjectParameter> objectList;
    private ImportListLabelProvider importListLabelProvider;
    private ObjectListLabelProvider objectListLabelProvider;
    private ImportListAdapter importListAdapter;
    private ObjectListAdapter objectListAdapter;
    public boolean parameterSet;

    @Inject
    private ITargetPlatformMetamodelLoader metamodelLoader;
    @Inject
    private IResourceSetProvider resourceSetProvider;
    
    private ResourceSet resourceSet;

    public NewVqlFileWizardPatternConfigurationPage() {
        super(TITLE);
        setTitle(TITLE);
        parameterSet = false;
    }

    private void createImportsControl(Composite parent, int nColumns) {
        String[] buttonLiterals = new String[] { "Add", "Remove" };

        NewVqlFileWizardContainerConfigurationPage firstPage = (NewVqlFileWizardContainerConfigurationPage) this
                .getPreviousPage();
        resourceSet = resourceSetProvider.get(firstPage.getProject());

        importListAdapter = new ImportListAdapter(metamodelLoader);
        importListLabelProvider = new ImportListLabelProvider();

        importList = new ListDialogField<String>(importListAdapter, buttonLiterals, importListLabelProvider);
        importList.setLabelText("&Imported packages:");
        importList.setTableColumns(new ListDialogField.ColumnsDescription(new String[] { "EPackage" }, true));
        importList.setRemoveButtonIndex(1);
        importList.doFillIntoGrid(parent, nColumns);
    }

    private void createObjectSelectionControl(Composite parent, int nColumns) {
        String[] buttonLiterals = new String[] { "Add", "Modify", "Remove" };
        objectListAdapter = new ObjectListAdapter(this, importList, metamodelLoader);
        objectListLabelProvider = new ObjectListLabelProvider();

        objectList = new ListDialogField<ObjectParameter>(objectListAdapter, buttonLiterals, objectListLabelProvider);
        objectList.setLabelText("&Pattern parameters:");
        objectList.setTableColumns(new ListDialogField.ColumnsDescription(new String[] { "Name", "Type" }, true));
        // disable modify button for an empty list
        objectList.enableButton(1, false);
        objectList.setRemoveButtonIndex(2);
        objectList.doFillIntoGrid(parent, nColumns);
    }

    @Override
    public void createControl(Composite parent) {
        int nColumns = 5;

        initializeDialogUnits(parent);
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NULL);
        label.setText("&Pattern name:");
        patternText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        patternText.setText("");
        GridData gd_1 = new GridData(GridData.FILL_HORIZONTAL);
        gd_1.horizontalSpan = 3;
        patternText.setLayoutData(gd_1);
        patternText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validatePage();
            }
        });

        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd_2 = new GridData(GridData.FILL_HORIZONTAL);
        gd_2.horizontalSpan = nColumns;
        label.setLayoutData(gd_2);

        createImportsControl(composite, nColumns);
        createObjectSelectionControl(composite, nColumns);

        setControl(composite);
        validatePage();
    }

    public void validatePage() {

        StatusInfo si = new StatusInfo(StatusInfo.OK, "");

        if (patternText != null) {
            String patternName = patternText.getText();
            if (patternName == null || patternName.length() == 0) {
                if (parameterSet) {
                    si.setWarning(PATTERN_NAME_MUST_BE_SPECIFIED);
                } else {
                    si.setWarning(PATTERN_NAME_SHOULD_BE_SPECIFIED);
                }
            }
        }

        if (si.getSeverity() == IStatus.OK) {
            si.setInfo("");
        }

        updateStatus(si);

        if (si.isError()) {
            setErrorMessage(si.getMessage());
        }
    }

    protected void updateStatus(IStatus status) {
        setPageComplete(!status.matches(IStatus.ERROR));
        StatusUtil.applyToStatusLine(this, status);
    }

    /**
     * Returns the name of the pattern specified in the wizard.
     * 
     * @return the name of the pattern
     */
    public String getPatternName() {
        return patternText.getText();
    }

    /**
     * Returns the import list of {@link EPackage}s specified in the wizard.
     * 
     * @return the list of imports
     */
    public List<EPackage> getImports() {
        return Lists.transform(importList.getElements(), new Function<String, EPackage>() {

            @Override
            public EPackage apply(String input) {
                return metamodelLoader.loadPackage(resourceSet, input);
            }});
    }

    /**
     * Returns the list of pattern parameters specified in the wizard.
     * 
     * @return the list of pattern parameters
     */
    public List<ObjectParameter> getParameters() {
        return objectList.getElements();
    }

    public ResourceSet getResourceSet() {
        return resourceSet;
    }
    
    
}
