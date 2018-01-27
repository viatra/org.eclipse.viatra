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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.launch.TransformationLaunchConfigurationDelegate;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;

import com.google.common.collect.Lists;

import static org.eclipse.viatra.transformation.debug.communication.DebuggerCommunicationConstants.*;

@SuppressWarnings("restriction")
public class TransformationRemoteDebugTab extends AbstractLaunchConfigurationTab {
    private static final String NOAGENTS = "No VIATRA Transformation Agents running on port ";
    private ComboViewer comboViewer;
    private Text transformationTypeText;
    private String projectName;
    private String typeName;
    private String selectedID;
    private Text portText;
    private String portID = "";
    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        setControl(composite);
        composite.setLayout(new GridLayout(1, false));

        Group grpPort = new Group(composite, SWT.NONE);
        grpPort.setText("Target Port");
        grpPort.setLayout(new GridLayout(2, false));
        grpPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

        portText = new Text(grpPort, SWT.BORDER);
        portText.setText(portID);
        portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        final ControlDecoration portNumberDecorator = new ControlDecoration(portText, SWT.TOP|SWT.RIGHT);
        FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry .DEC_ERROR);
        Image img = fieldDecoration.getImage();
        portNumberDecorator.setImage(img);
        portNumberDecorator.hide();
        portText.addModifyListener(e -> {
            try {
                Integer.parseInt(portText.getText());
                portID = portText.getText();
                portNumberDecorator.hide();
                
                setErrorMessage(null);
                getLaunchConfigurationDialog().updateMessage();
                getLaunchConfigurationDialog().updateButtons();
            } catch (NumberFormatException e2) {
                portNumberDecorator.setDescriptionText(portText.getText()+" is not a valid port number.");
                portNumberDecorator.show();
            
                setErrorMessage(portText.getText()+" is not a valid port number.");
                getLaunchConfigurationDialog().updateMessage();
                
                ViatraQueryLoggingUtil.getDefaultLogger().error(e2.getMessage(), e2);
            }
        });
        
        Button btnPortQuery = new Button(grpPort, SWT.NONE);
        btnPortQuery.setText("Query Port");
        Group grpTargetTransformation = new Group(composite, SWT.NONE);
        grpTargetTransformation.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        grpTargetTransformation.setText("Target VIATRA Transformation");
        grpTargetTransformation.setLayout(new GridLayout(1, false));

        comboViewer = new ComboViewer(grpTargetTransformation, SWT.READ_ONLY);
        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboViewer.setContentProvider(ArrayContentProvider.getInstance());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });
        comboViewer.addSelectionChangedListener(event -> {
            selectedID = ((String) ((StructuredSelection) comboViewer.getSelection()).getFirstElement());
            if(!selectedID.contains(CURRENTVERSION)){
                setErrorMessage(selectedID+" does not match the current version VIATRA version");
                getLaunchConfigurationDialog().updateMessage();
            }else{
                setErrorMessage(null);
                getLaunchConfigurationDialog().updateMessage();
            }
            getLaunchConfigurationDialog().updateButtons();
        });
        

        Group transformationTypeGrp = new Group(composite, SWT.NONE);
        transformationTypeGrp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        transformationTypeGrp.setLayout(new GridLayout(2, false));
        transformationTypeGrp.setText("VIATRA Transformation Class");

        transformationTypeText = new Text(transformationTypeGrp, SWT.BORDER | SWT.READ_ONLY);
        transformationTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        transformationTypeText.addModifyListener(e -> {
            typeName = transformationTypeText.getText();
            getLaunchConfigurationDialog().updateButtons();
        });

        Button btnSearchButton = new Button(transformationTypeGrp, SWT.NONE);
        btnSearchButton.addSelectionListener(new SelectionAdapter() {
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
                        typeName = type.getFullyQualifiedName();
                        projectName = type.getJavaProject().getElementName();
                        
                        getLaunchConfigurationDialog().updateButtons();
                    }
                } catch (JavaModelException e) {
                    TransformationDebugUIActivator.getDefault()
                            .logException("An error occured during the creation of the VIATRA debugger wizard tab", e);
                }

            }
        });
        btnSearchButton.setText("Search");

                
        btnPortQuery.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<String> filteredNames = Lists.newArrayList();
                
                try {
                    Integer.parseInt(portText.getText());
                    portID = portText.getText();
                    
                    
                    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+portID+"/jmxrmi");
                    try (JMXConnector jmxc = JMXConnectorFactory.connect(url, null)) {
                        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
                        List<ObjectName> queryNames = Lists.newArrayList(mbsc.queryNames(null, null));
                        for (ObjectName objectName : queryNames) {
                            if (objectName.toString().contains(MBEANNAME)) {
                                filteredNames.add(objectName.toString());
                            }
                        }
                    }
                    
                    if (!filteredNames.isEmpty()) {
                        comboViewer.setInput(filteredNames);
                        comboViewer.setSelection(new StructuredSelection(filteredNames.get(0)));
                        selectedID = filteredNames.get(0);
                        portNumberDecorator.hide();
                        
                        setErrorMessage(null);
                        getLaunchConfigurationDialog().updateMessage();
                        getLaunchConfigurationDialog().updateButtons();
                    }
                    getLaunchConfigurationDialog().updateButtons();
                } catch (NumberFormatException | IOException e2) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e2.getMessage(), e2);
                    portNumberDecorator.setDescriptionText(NOAGENTS+portText.getText());
                    filteredNames.add(NOAGENTS+portText.getText());
                    comboViewer.setInput(filteredNames);
                    comboViewer.setSelection(new StructuredSelection(filteredNames.get(0)));
                    selectedID = filteredNames.get(0);
                    
                    setErrorMessage(NOAGENTS+portText.getText());
                    getLaunchConfigurationDialog().updateMessage();
                    
                    portNumberDecorator.show();
                    
                }
            }            
        });
        
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        portID = Integer.toString(1099);
        if (portText != null) {
            portText.setText(portID);
        }
    }
    
    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            selectedID = configuration.getAttribute(TransformationLaunchConfigurationDelegate.SELECTED_TARGET, "");
            comboViewer.setInput(Lists.newArrayList(selectedID));
            comboViewer.setSelection(new StructuredSelection(selectedID));
            
            typeName = configuration.getAttribute(TransformationLaunchConfigurationDelegate.TRANSFORMATION_ATTR, "");
            projectName = configuration.getAttribute(TransformationLaunchConfigurationDelegate.PROJECT_NAME, "");
            
            transformationTypeText.setText(typeName);
            
            portID = configuration.getAttribute(TransformationLaunchConfigurationDelegate.PORT_NAME, "1099");
            portText.setText(portID+"");
        } catch (CoreException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        return !(selectedID.contains(NOAGENTS)) && selectedID.contains(CURRENTVERSION) && !projectName.isEmpty();
    }
    
    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        if (selectedID != null) {
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.SELECTED_TARGET, selectedID);
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.TRANSFORMATION_ATTR, typeName);
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.PROJECT_NAME, projectName);
            configuration.setAttribute(TransformationLaunchConfigurationDelegate.PORT_NAME, portID);
            try {
                configuration.doSave();
            } catch (CoreException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
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
