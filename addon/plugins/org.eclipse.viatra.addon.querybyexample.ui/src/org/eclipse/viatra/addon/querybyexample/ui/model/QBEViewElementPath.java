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
import org.eclipse.viatra.addon.querybyexample.interfaces.ICodeGenerator;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementPathProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementPath extends QBEViewElement {

    private VQLPath path;

    private ICodeGenerator codeGenerator;

    private QBEView container;

    private static final ImageDescriptor pathImg = ImageDescriptor.createFromFile(QBEViewElementConstraint.class,
            "/icons/qbe_path.gif");
    private static final ImageDescriptor pathImgNeg = ImageDescriptor.createFromFile(QBEViewElementConstraint.class,
            "/icons/qbe_path_disabled.gif");

    private static final String VIEWELEMENT_PATH_LABEL_PREFIX = "Path ";

    public QBEViewElementPath(QBEView container, VQLPath path, ICodeGenerator codeGenerator) {
        this.container = container;
        this.path = path;
        this.codeGenerator = codeGenerator;

        for (VQLConstraint constraint : path.getConstraints()) {
            QBEViewElementConstraint newElement = new QBEViewElementConstraint(constraint, codeGenerator, container);
            newElement.setParent(this);
            this.children.add(newElement);
        }
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return this.path.isVisible() ? pathImg : pathImgNeg;
    }

    @Override
    public String getLabel(Object o) {
        return VIEWELEMENT_PATH_LABEL_PREFIX + codeGenerator.generatePathLabel(this.path);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class)
            return new QBEViewElementPathProperties(this);
        if (adapter == IWorkbenchAdapter.class)
            return this;
        return null;
    }

    public VQLPath getPath() {
        return path;
    }

    public QBEView getContainer() {
        return container;
    }
}
