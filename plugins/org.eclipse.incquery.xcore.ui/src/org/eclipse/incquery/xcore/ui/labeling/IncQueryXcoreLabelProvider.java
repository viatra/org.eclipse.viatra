/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.ui.labeling;

import java.net.URL;

import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.incquery.xcore.XIncQueryDerivedFeature;
import org.eclipse.incquery.xcore.ui.IncQueryXcoreActivator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.xbase.ui.labeling.XbaseLabelProvider;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class IncQueryXcoreLabelProvider extends XbaseLabelProvider {

    private ImageDescriptor attributeImageDescriptor;
    private ImageDescriptor referenceImageDescriptor;

    @Inject
    public IncQueryXcoreLabelProvider(AdapterFactoryLabelProvider delegate) {
        super(delegate);
        this.attributeImageDescriptor = new OverlayImageDescriptor(IncQueryXcoreActivator.getInstance()
                .getImageRegistry().getDescriptor("underlay").createImage(),
                ImageDescriptor.createFromURL((URL) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EAttribute")));
        this.referenceImageDescriptor = new OverlayImageDescriptor(IncQueryXcoreActivator.getInstance()
                .getImageRegistry().getDescriptor("underlay").createImage(),
                ImageDescriptor.createFromURL((URL) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EReference")));
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof XIncQueryDerivedFeature) {
            if (((XIncQueryDerivedFeature) element).isReference()) {
                return referenceImageDescriptor.createImage();
            }
            else {
                return attributeImageDescriptor.createImage();
            }
        }
        return super.getImage(element);
    }

}
