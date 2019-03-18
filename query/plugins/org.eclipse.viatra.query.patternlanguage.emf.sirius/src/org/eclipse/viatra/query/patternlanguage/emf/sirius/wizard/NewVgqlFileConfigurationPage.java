/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.sirius.wizard;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.SiriusVQLGraphicalEditorPlugin;

/**
 * First page of the {@link NewVgqlFileWizard} which allows to specify the details of the container for a pattern.
 * 
 */
@SuppressWarnings("restriction")
public class NewVgqlFileConfigurationPage extends NewTypeWizardPage {

    private Text fileText;
    private static final String TITLE = "VIATRA Graphical Query Definition Wizard";
    private static final String THE_GIVEN_FILE_ALREADY_EXISTS = "The given file already exists!";
    private static final String DEFAULT_FILENAME = "Queries";
    private static final String SOURCE_FOLDER_ERROR = "A valid source folder must be specified!";
    private static final String FILE_NAME_ERROR = "File name must be specified!";
    private static final String FILE_NAME_NOT_VALID = "File name must be valid!";
    private static final String DEFAULT_PACKAGE_ERROR = "A non-empty Java package must be specified!";
    private static final String PACKAGE_NAME_WARNING = "Only lower case Java package names supported.";

    public NewVgqlFileConfigurationPage() {
        super(false, "vgql");
        setTitle(TITLE);
    }

    /**
     * Initialization based on the current selection.
     * 
     * @param selection
     *            the current selection in the workspace
     */
    public void init(IStructuredSelection selection) {
        IJavaElement jElement = getInitialJavaElement(selection);
        initContainerPage(jElement);

        if (jElement != null) {
            IPackageFragment pack = (IPackageFragment) jElement.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
            setPackageFragment(pack, true);
        }

        packageChanged();
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        int nColumns = 4;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);

        Label label = new Label(composite, SWT.NULL);
        label.setText("&File name:");
        fileText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        fileText.setText(DEFAULT_FILENAME);
        GridData gd_1 = new GridData(GridData.FILL_HORIZONTAL);
        gd_1.horizontalSpan = 3;
        fileText.setLayoutData(gd_1);
        fileText.addModifyListener(e -> validatePage());

        setControl(composite);

        validatePage();
    }

    @Override
    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        validatePage();
    }

    /**
     * Used to validate the page.
     * 
     * Note that because of policy restrictions, a wizard must not come up with an error.
     * 
     */
    private void validatePage() {
        IStatus packageStatus = validatePackageName(getPackageText());
        StatusInfo si = new StatusInfo(packageStatus.getSeverity(), packageStatus.getMessage());
        String containerPath = getPackageFragmentRootText();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource containerResource = root.findMember(new Path(containerPath));

        if (containerPath.matches("") || containerResource == null) {
            si.setError(SOURCE_FOLDER_ERROR);
        }

        if (fileText != null) {

            String fileName = fileText.getText();
            String packageName = getPackageText().replaceAll("\\.", "/");

            if (root.findMember(new Path(containerPath + "/" + packageName + "/" + fileText.getText())) != null) {
                si.setError(THE_GIVEN_FILE_ALREADY_EXISTS);
            }

            if (fileName.length() == 0) {
                si.setError(FILE_NAME_ERROR);
            }

            if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
                si.setError(FILE_NAME_NOT_VALID);
            }

            IStatus nameValidatorStatus = JavaConventions.validateTypeVariableName(fileName, JavaCore.VERSION_1_8,
                    JavaCore.VERSION_1_8);
            if (nameValidatorStatus.getSeverity() == IStatus.ERROR) {
                si.setError(String.format("Filename %s is not a valid Java type name.", fileName));
            }

        }

        if (si.getSeverity() == IStatus.OK) {
            si.setInfo("");
        }

        if (si.isError()) {
            setErrorMessage(si.getMessage());
        }

        updateStatus(si);
    }

    private IStatus validatePackageName(String text) {
        if (text == null || text.isEmpty()) {
            return new Status(IStatus.ERROR, SiriusVQLGraphicalEditorPlugin.PLUGIN_ID, DEFAULT_PACKAGE_ERROR);
        }
        IJavaProject project = getJavaProject();
        if (project == null || !project.exists()) {
            return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_8, JavaCore.VERSION_1_8);
        }
        IStatus status = JavaConventionsUtil.validatePackageName(text, project);
        if (!Objects.equals(text, text.toLowerCase())) {
            return new Status(IStatus.ERROR, SiriusVQLGraphicalEditorPlugin.PLUGIN_ID, PACKAGE_NAME_WARNING);
        }
        return status;
    }

    /**
     * Returns the name of the new eiq file set in the wizard.
     * 
     * @return the name of the file
     */
    public String getFileName() {
        return fileText.getText() + ".vgql";
    }

    /**
     * Returns the name of the container set in the wizard.
     * 
     * @return the name of the container (folder)
     */
    public String getContainerName() {
        return getPackageFragmentRootText();
    }

    public IProject getProject() {
        return this.getPackageFragmentRoot().getJavaProject().getProject();
    }
}
