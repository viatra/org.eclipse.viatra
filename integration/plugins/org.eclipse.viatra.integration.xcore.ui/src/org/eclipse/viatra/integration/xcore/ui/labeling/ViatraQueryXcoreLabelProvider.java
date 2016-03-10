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
package org.eclipse.viatra.integration.xcore.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.integration.xcore.model.XViatraQueryDerivedFeature;
import org.eclipse.viatra.integration.xcore.ui.ViatraQueryXcoreActivator;
import org.eclipse.xtext.xbase.ui.labeling.XbaseLabelProvider;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class ViatraQueryXcoreLabelProvider extends XbaseLabelProvider {

    private ImageDescriptor attributeImageDescriptor;
    private ImageDescriptor referenceImageDescriptor;

    @Inject
    public ViatraQueryXcoreLabelProvider(AdapterFactoryLabelProvider delegate) {
        super(delegate);
        this.attributeImageDescriptor = new OverlayImageDescriptor(ViatraQueryXcoreActivator.getInstance()
                .getImageRegistry().getDescriptor("Underlay").createImage(),
                ViatraQueryXcoreActivator.getInstance().getImageRegistry().getDescriptor("EAttribute"));
        this.referenceImageDescriptor = new OverlayImageDescriptor(ViatraQueryXcoreActivator.getInstance()
                .getImageRegistry().getDescriptor("Underlay").createImage(),
                ViatraQueryXcoreActivator.getInstance()
                .getImageRegistry().getDescriptor("EReference"));
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof XViatraQueryDerivedFeature) {
            if (((XViatraQueryDerivedFeature) element).isReference()) {
                return referenceImageDescriptor.createImage();
            }
            else {
                return attributeImageDescriptor.createImage();
            }
        }
        return super.getImage(element);
    }

}
