/*******************************************************************************
 * Copyright (c) 2010-2016, Peter Lunk and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;

public class PropertySourceAdapterFactory implements IAdapterFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (TransformationModelElement.class.isInstance(adaptableObject)) {
            return new TransformationModelElementPropertySource((TransformationModelElement) adaptableObject);
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class<?>[] { IPropertySource.class };
    }
    
    
}
