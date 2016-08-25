/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.launch;

import java.io.IOException;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.viatra.transformation.debug.communication.DebuggerTargetEndpoint;
import org.eclipse.viatra.transformation.debug.launch.TransformationLaunchConfigurationDelegate;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;

import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class TransformationRemoteDebugTab extends AbstractLaunchConfigurationTab {
    private ComboViewer comboViewer;
    private Text transformationTypeText;
    private String projectName;
    private String selectedID;

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        setControl(composite);
        composite.setLayout(new FormLayout());
        Group grpTargetEvm = new Group(composite, SWT.NONE);
        grpTargetEvm.setLayout(new FormLayout());
        FormData fd_grpTargetEvm = new FormData();
        fd_grpTargetEvm.left = new FormAttachment(0);
        fd_grpTargetEvm.right = new FormAttachment(100);
        fd_grpTargetEvm.bottom = new FormAttachment(0, 59);
        fd_grpTargetEvm.top = new FormAttachment(0);
        grpTargetEvm.setLayoutData(fd_grpTargetEvm);
        grpTargetEvm.setText("Target EVM");

        comboViewer = new ComboViewer(grpTargetEvm, SWT.NONE);
        Combo combo = comboViewer.getCombo();
        FormData fd_combo = new FormData();
        fd_combo.bottom = new FormAttachment(100, 3);
        fd_combo.right = new FormAttachment(0, 531);
        fd_combo.top = new FormAttachment(0, 5);
        fd_combo.left = new FormAttachment(0, 7);
        combo.setLayoutData(fd_combo);

        Group transformationTypeGrp = new Group(composite, SWT.NONE);
        FormData fd_transformationTypeGrp = new FormData();
        fd_transformationTypeGrp.bottom = new FormAttachment(100, -170);
        fd_transformationTypeGrp.top = new FormAttachment(grpTargetEvm, 1);
        fd_transformationTypeGrp.left = new FormAttachment(0);
        fd_transformationTypeGrp.right = new FormAttachment(100);
        transformationTypeGrp.setLayoutData(fd_transformationTypeGrp);
        transformationTypeGrp.setText("Transformation Class");

        transformationTypeText = new Text(transformationTypeGrp, SWT.BORDER);
        transformationTypeText.setBounds(10, 32, 443, 23);

        Button btnSearchButton = new Button(transformationTypeGrp, SWT.NONE);
        btnSearchButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSearchButtonSelected();
            }

            private void handleSearchButtonSelected() {
                try {
                    SelectionDialog dialog = JavaUI.createTypeDialog(JDIDebugUIPlugin.getActiveWorkbenchShell(),
                            JDIDebugUIPlugin.getActiveWorkbenchWindow(), SearchEngine.createWorkspaceScope(),
                            IJavaElementSearchConstants.CONSIDER_CLASSES, false);
                    dialog.setTitle("Select Transformation Class");
                    dialog.create();
                    if (dialog.open() == Window.CANCEL) {
                        return;
                    }
                    Object[] results = dialog.getResult();
                    IType type = (IType) results[0];
                    if (type != null) {
                        transformationTypeText.setText(type.getFullyQualifiedName());
                        projectName = type.getJavaProject().getElementName();
                        getLaunchConfigurationDialog().updateButtons();
                    }
                } catch (JavaModelException e) {
                    TransformationDebugUIActivator.getDefault()
                            .logException("An error occured during the creation of the VIATRA debugger wizard tab", e);
                }

            }
        });
        btnSearchButton.setBounds(459, 30, 75, 25);
        btnSearchButton.setText("Search");
        comboViewer.setContentProvider(ArrayContentProvider.getInstance());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });

        
        
        JMXServiceURL url;
        try {
            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            List<ObjectName> queryNames = Lists.newArrayList(mbsc.queryNames(null, null));
            List<ObjectName> filteredNames = Lists.newArrayList();
            for (ObjectName objectName : queryNames) {
                if(objectName.toString().contains(DebuggerTargetEndpoint.MBEANNAME)){
                    filteredNames.add(objectName);
                }
            }
            
            comboViewer.setInput(filteredNames);
            
            if (!queryNames.isEmpty()) {
                comboViewer.setSelection(new StructuredSelection(queryNames.get(0)));
                selectedID = queryNames.get(0).toString();
            }
            
            comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    selectedID = ((ObjectName) ((StructuredSelection) comboViewer.getSelection()).getFirstElement()).toString();
                    getLaunchConfigurationDialog().updateButtons();
                }
            });

            
            jmxc.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }

    public String getTargetID() {
        return selectedID;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {

    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        if (getTargetID() != null) {
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.SELECTED_TARGET,
                    getTargetID());
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.TRANSFORMATION_ATTR,
                    transformationTypeText.getText());
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.PROJECT_NAME, projectName);
        }
    }

    @Override
    public String getName() {
        return "VIATRA Remote Debug Parameters";
    }

    @Override
    public Image getImage() {
        return TransformationDebugUIActivator.getDefault().getImageRegistry()
                .get(TransformationDebugUIActivator.ICON_VIATRA_LOGO);
    }
}
