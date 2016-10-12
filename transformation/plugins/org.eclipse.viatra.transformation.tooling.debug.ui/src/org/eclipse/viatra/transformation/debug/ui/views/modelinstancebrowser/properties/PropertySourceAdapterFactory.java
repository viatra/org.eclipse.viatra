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
