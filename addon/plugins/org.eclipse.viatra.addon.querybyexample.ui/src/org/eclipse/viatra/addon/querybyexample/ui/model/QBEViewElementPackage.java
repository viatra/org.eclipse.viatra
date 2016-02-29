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
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementPackageProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementPackage extends QBEViewElement {

    private String packageName;
    private QBEView container;

    private static final String INIT_PCKG_NAME = "#PACKAGENAME#";
    private static final String PCKG_LABEL_TEMPLATE = "package %s";

    private static final ImageDescriptor packageImg = ImageDescriptor.createFromFile(QBEViewElementPackage.class,
            "/icons/qbe_package.gif");

    public QBEViewElementPackage(QBEView container) {
        this.packageName = INIT_PCKG_NAME;
        this.container = container;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public QBEView getContainer() {
        return container;
    }

    public void setContainer(QBEView container) {
        this.container = container;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return packageImg;
    }

    @Override
    public String getLabel(Object o) {
        return String.format(PCKG_LABEL_TEMPLATE, packageName);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class)
            return this;
        if (adapter == IPropertySource.class)
            return new QBEViewElementPackageProperties(this);
        return null;
    }
}
