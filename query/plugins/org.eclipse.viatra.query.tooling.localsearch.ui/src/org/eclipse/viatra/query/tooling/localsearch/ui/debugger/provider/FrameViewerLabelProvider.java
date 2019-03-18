/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor.Registry;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * An initial implementation for the label provider to show the selected matching frame in a Zest viewer
 * 
 * @author Marton Bur
 *
 */
public class FrameViewerLabelProvider extends LabelProvider {

    private AdapterFactoryLabelProvider delegate;

    public FrameViewerLabelProvider() {
        final Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
        final ComposedAdapterFactory factory = new ComposedAdapterFactory(registry);
        delegate = new AdapterFactoryLabelProvider(factory);
        
    }
    
    @Override
    public String getText(Object element) {
        if (element instanceof EObject) {
            return delegate.getText(element);
        } else if (element != null) {
            return element.getClass().toString();
        } else {
            return null;
        }
    }
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof EObject) {
            return delegate.getImage(element);
        } else {
            return super.getImage(element);
        }
    }

    @Override
    public void dispose() {
        if (delegate != null) {
            delegate.dispose();
        }
        super.dispose();
    }
    
}
