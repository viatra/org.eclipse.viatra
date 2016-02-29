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

public class QBEViewElementNsUri extends QBEViewElement {

    private String nsUri;

    private static final String INIT_NSURI_VALUE = "#NSURI#";
    private static final String NSURI_LABEL_TEMPLATE = "import \"%s\"";

    private static final ImageDescriptor uriImg = ImageDescriptor.createFromFile(QBEViewElementNsUri.class,
            "/icons/qbe_nsurl.gif");

    public QBEViewElementNsUri() {
        this.nsUri = INIT_NSURI_VALUE;
    }

    public String getNsUri() {
        return nsUri;
    }

    public void setNsUri(String nsUri) {
        this.nsUri = nsUri;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return uriImg;
    }

    @Override
    public String getLabel(Object o) {
        return String.format(NSURI_LABEL_TEMPLATE, nsUri);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class)
            return this;
        return null;
    }
}
