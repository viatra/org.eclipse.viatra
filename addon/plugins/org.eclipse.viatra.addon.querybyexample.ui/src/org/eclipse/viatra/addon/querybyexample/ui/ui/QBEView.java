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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.querybyexample.interfaces.ICodeGenerator;
import org.eclipse.viatra.addon.querybyexample.interfaces.IQBEService;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;
import org.eclipse.viatra.addon.querybyexample.services.QBEServiceImpl;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElement;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementAttribute;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementConstraint;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementNsUri;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPackage;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPath;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPattern;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementPatternContainer;
import org.eclipse.viatra.addon.querybyexample.ui.model.QBEViewElementVariable;

public class QBEView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.eclipse.viatra.addon.querybyexample.view.qbeview";
    private static final String GROUP_TITLE = "Exploration depth";

    private TreeViewer viewer;
    private Text depthDisplayText;
    private Scale explorationDepthScale;
    private IStructuredSelection selection;

    private QBEViewElementPackage packageNameModel;
    private QBEViewElementNsUri nsUriModel;
    private QBEViewElementPattern patternModel;

    private IQBEService service;
    private ICodeGenerator codeGenerator;

    private int defaultDepth = 1;

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.FILL);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 10;
        composite.setLayout(layout);

        Group explorationGroup = new Group(composite, SWT.NONE);
        explorationGroup.setText(GROUP_TITLE);
        explorationGroup.setLayout(layout);
        explorationGroup.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 10, 1));

        explorationDepthScale = new Scale(explorationGroup, SWT.NONE);
        explorationDepthScale.setMinimum(1);
        explorationDepthScale.setMaximum(20);
        GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gridData.horizontalSpan = 8;
        explorationDepthScale.setLayoutData(gridData);
        explorationDepthScale.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int value = explorationDepthScale.getSelection();
                depthDisplayText.setText(Integer.toString(value));
                if (service != null) {
                    service.explore(value);
                    updateViewer();
                }
            }
        });

        depthDisplayText = new Text(explorationGroup, SWT.BORDER | SWT.SINGLE);
        depthDisplayText.setEditable(false);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        depthDisplayText.setLayoutData(gridData);
        this.autoAdjust();

        viewer = new TreeViewer(composite);
        GridData viewerGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        viewerGridData.horizontalSpan = 10;
        viewer.getControl().setLayoutData(viewerGridData);
        viewer.setContentProvider(new BaseWorkbenchContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setUseHashlookup(true);
        viewer.setInput(getInitialInput());
        viewer.expandAll();
        getSite().setSelectionProvider(viewer);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void start(Collection<EObject> selection) {
        if (this.service == null)
            this.service = new QBEServiceImpl();
        service.init(selection);
        reInitialize();
    }
    public void expand(Collection<EObject> selection) {
        if (this.service == null)
            this.service = new QBEServiceImpl();
        Collection<EObject> newSelection = new HashSet<EObject>();
        newSelection.addAll(selection);
        newSelection.addAll(service.getSelection());
        service.init(newSelection);
        reInitialize();
    }

    private void reInitialize() {
        this.codeGenerator = service.getCodeGenerator();
        int coherenceDepth = this.service.determineCoherenceMinimumDepth();
        this.defaultDepth = (coherenceDepth == 0 ? 1 : coherenceDepth);
        this.autoAdjust();
    }

    public IQBEService getService() {
        return service;
    }

    public void autoAdjust() {
        explorationDepthScale.setSelection(defaultDepth);
        explorationDepthScale.notifyListeners(SWT.Selection, new Event());
    }

    private QBEViewElement getInitialInput() {
        QBEViewElement root = new QBEViewElement() {
            @Override
            public ImageDescriptor getImageDescriptor(Object object) {
                return null;
            }

            @Override
            public String getLabel(Object o) {
                return null;
            }

            @Override
            public Object getAdapter(Class adapter) {
                return null;
            }
        };
        root.setParent(root);

        packageNameModel = new QBEViewElementPackage(this);
        packageNameModel.setParent(root);
        root.getChildren().add(packageNameModel);

        nsUriModel = new QBEViewElementNsUri();
        nsUriModel.setParent(root);
        root.getChildren().add(nsUriModel);

        patternModel = new QBEViewElementPattern(this);
        patternModel.setParent(root);
        root.getChildren().add(patternModel);

        return root;
    }

    public void updateViewer() {

        String packageName = this.service.getPackageName();
        String nsUri = this.service.getNsUri();
        String patternName = this.service.getPatternName();
        this.packageNameModel.setPackageName(packageName);
        this.nsUriModel.setNsUri(nsUri);
        this.patternModel.setPatternName(patternName);

        QBEViewElementPatternContainer inputParamsContainter = this.patternModel.getInputParamsContainer();
        inputParamsContainter.getChildren().clear();
        Map<EObject, VQLVariableSetting> anchors = this.service.getAnchors();
        for (Entry<EObject, VQLVariableSetting> anchorEntry : anchors.entrySet()) {
            QBEViewElementVariable newAnchor = new QBEViewElementVariable(this, anchorEntry.getKey(),
                    anchorEntry.getValue(), true);
            inputParamsContainter.addNewElement(newAnchor);
        }

        QBEViewElementPatternContainer localVariableConstraintsContainer = this.patternModel
                .getLocalVariableConstraintsContainer();
        QBEViewElementPatternContainer constraintsContainer = this.patternModel.getConstraintsContainer();
        QBEViewElementPatternContainer pathsContainer = this.patternModel.getPathsContainer();
        QBEViewElementPatternContainer attributesContainer = this.patternModel.getAttributesContainer();
        QBEViewElementPatternContainer negativeConstraintsContainer = this.patternModel
                .getNegativeConstraintsContainer();
        localVariableConstraintsContainer.getChildren().clear();
        constraintsContainer.getChildren().clear();
        attributesContainer.getChildren().clear();
        pathsContainer.getChildren().clear();
        negativeConstraintsContainer.getChildren().clear();

        // adding local variable constraints to view
        Map<EObject, VQLVariableSetting> freeVariables = this.service.getFreeVariables();
        for (Entry<EObject, VQLVariableSetting> freeVarEntry : freeVariables.entrySet()) {
            QBEViewElementVariable newVariable = new QBEViewElementVariable(this, freeVarEntry.getKey(),
                    freeVarEntry.getValue(), false);
            if (freeVarEntry.getValue().isInputVariable()) {
                inputParamsContainter.addNewElement(newVariable);
            } else {
                localVariableConstraintsContainer.addNewElement(newVariable);
            }
        }

        // adding constraints to view
        Set<VQLConstraint> constraints = this.service.getConstraints();
        List<QBEViewElementConstraint> invisibleConstraintElements = new ArrayList<QBEViewElementConstraint>();
        for (VQLConstraint eiqc : constraints) {
            QBEViewElementConstraint newConstraint = new QBEViewElementConstraint(eiqc, codeGenerator, this);
            // first adding the visible constraints
            if (eiqc.isVisible())
                constraintsContainer.addNewElement(newConstraint);
            else
                invisibleConstraintElements.add(newConstraint);
        }
        // after that adding all the invisible constraints to the tree view
        for (QBEViewElementConstraint constraintElement : invisibleConstraintElements)
            constraintsContainer.addNewElement(constraintElement);

        // adding negative constraints to view
        Set<VQLNegConstraint> negConstraints = this.service.getNegConstraints();
        for (VQLNegConstraint eiqnc : negConstraints) {
            QBEViewElementConstraint newNegConstraint = new QBEViewElementConstraint(eiqnc, codeGenerator, this);
            negativeConstraintsContainer.addNewElement(newNegConstraint);
        }

        // expend all except paths' and attributes' subtree
        viewer.expandAll();

        // adding paths to view
        List<VQLPath> paths = this.service.getPaths();
        for (VQLPath eiqPath : paths) {
            pathsContainer.addNewElement(new QBEViewElementPath(this, eiqPath, codeGenerator));
        }

        // adding attributes to view
        List<VQLAttribute> eiqAttributes = this.service.getAttributes();
        for (VQLAttribute eiqAttr : eiqAttributes) {
            attributesContainer.addNewElement(new QBEViewElementAttribute(this, eiqAttr, codeGenerator));
        }

        viewer.refresh();
    }

    public IStructuredSelection getSelection() {
        return selection;
    }

    public void setSelection(IStructuredSelection selection) {
        this.selection = selection;
    }
}