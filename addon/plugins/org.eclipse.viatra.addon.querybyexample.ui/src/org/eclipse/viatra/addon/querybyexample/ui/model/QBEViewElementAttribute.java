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
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementAttributeProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementAttribute extends QBEViewElement {

    private VQLAttribute eiqAttribute;
    private ICodeGenerator codeGenerator;

    private QBEView container;

    private static final ImageDescriptor attrImg = ImageDescriptor.createFromFile(QBEViewElementConstraint.class,
            "/icons/qbe_attr.gif");
    private static final ImageDescriptor attrImgNeg = ImageDescriptor.createFromFile(QBEViewElementConstraint.class,
            "/icons/qbe_attr_disabled.gif");

    public QBEViewElementAttribute(QBEView container, VQLAttribute attr, ICodeGenerator codeGenerator) {
        this.container = container;
        this.eiqAttribute = attr;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return this.eiqAttribute.isVisible() ? attrImg : attrImgNeg;
    }

    @Override
    public String getLabel(Object o) {
        return this.codeGenerator.generateAttribute(this.eiqAttribute);
    }

    public VQLAttribute getEiqAttribute() {
        return eiqAttribute;
    }

    public QBEView getContainer() {
        return container;
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class)
            return new QBEViewElementAttributeProperties(this);
        if (adapter == IWorkbenchAdapter.class)
            return this;
        return null;
    }
}
