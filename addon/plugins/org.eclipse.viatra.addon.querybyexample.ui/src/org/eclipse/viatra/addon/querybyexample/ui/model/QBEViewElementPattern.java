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
package org.eclipse.viatra.addon.querybyexample.ui.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementPatternProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementPattern extends QBEViewElement {

    private String patternName;
    private QBEView plugin;

    private static final String INIT_PATTERN_NAME = "#PATTERNNAME#";
    private static final String PATTERN_LABEL_TEMPLATE = "pattern %s";
    private static final String INPUT_PARAMETERS_LABEL = "Input Parameters";
    private static final String PATTERN_TRUNK_LOCAL_VAR_CONSTRAINTS_LABEL = "Local Variable Type Constraints";
    private static final String PATTERN_TRUNK_CONSTRAINTS_LABEL = "Edge Constraints";
    private static final String PATTERN_TRUNK_NEGATIVE_CONSTRAINTS_LABEL = "Negative Edge Constraints";
    private static final String PATTERN_TRUNK_PATHS_LABEL = "All Paths";
    private static final String PATTERN_TRUNK_ATTRS_LABEL = "Attributes";

    private static final ImageDescriptor patternImg = ImageDescriptor.createFromFile(QBEViewElementPattern.class,
            "/icons/qbe_pattern.gif");

    private QBEViewElementPatternContainer inputParamsContainer;
    private QBEViewElementPatternContainer localVariableConstraintsContainer;
    private QBEViewElementPatternContainer constraintsContainer;
    private QBEViewElementPatternContainer attributesContainer;
    private QBEViewElementPatternContainer pathsContainer;
    private QBEViewElementPatternContainer negativeConstraintsContainer;

    public QBEViewElementPattern(QBEView qbeView) {
        this.patternName = INIT_PATTERN_NAME;
        this.plugin = qbeView;

        this.inputParamsContainer = new QBEViewElementPatternContainer(QBEViewElementPattern.INPUT_PARAMETERS_LABEL);
        this.inputParamsContainer.setParent(this);
        this.localVariableConstraintsContainer = new QBEViewElementPatternContainer(
                QBEViewElementPattern.PATTERN_TRUNK_LOCAL_VAR_CONSTRAINTS_LABEL);
        this.localVariableConstraintsContainer.setParent(this);
        this.constraintsContainer = new QBEViewElementPatternContainer(
                QBEViewElementPattern.PATTERN_TRUNK_CONSTRAINTS_LABEL);
        this.constraintsContainer.setParent(this);
        this.negativeConstraintsContainer = new QBEViewElementPatternContainer(
                QBEViewElementPattern.PATTERN_TRUNK_NEGATIVE_CONSTRAINTS_LABEL);
        this.negativeConstraintsContainer.setParent(this);
        this.attributesContainer = new QBEViewElementPatternContainer(QBEViewElementPattern.PATTERN_TRUNK_ATTRS_LABEL);
        this.attributesContainer.setParent(this);
        this.pathsContainer = new QBEViewElementPatternContainer(QBEViewElementPattern.PATTERN_TRUNK_PATHS_LABEL);
        this.pathsContainer.setParent(this);

        this.children.add(this.inputParamsContainer);
        this.children.add(this.localVariableConstraintsContainer);
        this.children.add(this.constraintsContainer);
        this.children.add(this.attributesContainer);
        this.children.add(this.negativeConstraintsContainer);
        this.children.add(this.pathsContainer);
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public QBEView getPlugin() {
        return plugin;
    }

    public QBEViewElementPatternContainer getInputParamsContainer() {
        return inputParamsContainer;
    }

    public QBEViewElementPatternContainer getLocalVariableConstraintsContainer() {
        return localVariableConstraintsContainer;
    }

    public QBEViewElementPatternContainer getConstraintsContainer() {
        return constraintsContainer;
    }

    public QBEViewElementPatternContainer getPathsContainer() {
        return pathsContainer;
    }

    public QBEViewElementPatternContainer getNegativeConstraintsContainer() {
        return negativeConstraintsContainer;
    }

    public QBEViewElementPatternContainer getAttributesContainer() {
        return attributesContainer;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return patternImg;
    }

    @Override
    public String getLabel(Object o) {
        return String.format(PATTERN_LABEL_TEMPLATE, patternName);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class)
            return this;
        if (adapter == IPropertySource.class)
            return new QBEViewElementPatternProperties(this);
        return null;
    }
}
