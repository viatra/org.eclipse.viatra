/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.properties;

import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;

public class CrossReferenceElementPropertySource extends TransformationModelElementPropertySource{
    
    public CrossReferenceElementPropertySource(TransformationModelElement element) {
        super(element);
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor[0];
    }
    
    @Override
    public Object getPropertyValue(Object id) {
        return null;
    }
}
