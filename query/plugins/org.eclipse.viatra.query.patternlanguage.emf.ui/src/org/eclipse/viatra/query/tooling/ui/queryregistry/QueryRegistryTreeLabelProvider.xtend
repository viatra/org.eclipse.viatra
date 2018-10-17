/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryregistry

import org.eclipse.jface.viewers.ColumnLabelProvider
import org.eclipse.swt.graphics.Image
import org.eclipse.viatra.query.runtime.registry.ExtensionBasedQuerySpecificationLoader
import org.eclipse.viatra.query.runtime.ui.ViatraQueryRuntimeUIPlugin
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdater
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.resources.IProject
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin

class QueryRegistryTreeLabelProvider extends ColumnLabelProvider {
    
    val imageRegistry = ViatraQueryRuntimeUIPlugin.getDefault().getImageRegistry();
    
    override Image getImage(Object element) {
        return element.imageInternal;
    }

    dispatch def Image getImageInternal(QueryRegistryTreeEntry element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_VQL)
    }
    
    dispatch def Image getImageInternal(QueryRegistryTreeSource element) {
        val sourceId = element.sourceIdentifier        
        if (sourceId.startsWith(XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX)) {
            val member = ResourcesPlugin.workspace.root.findMember(sourceId.replace(XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX, ""))
            if (member instanceof IProject) {                
                return imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_PROJECT)
            }
        } 
        return imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_ROOT)
    }
    
    dispatch def Image getImageInternal(QueryRegistryTreePackage element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_EPACKAGE)
    }
    
    dispatch def Image getImageInternal(Object element) {
        return super.getImage(element)
    }
    
    override String getText(Object element) {
        return element.textInternal
    }
    
    dispatch def String getTextInternal(QueryRegistryTreeEntry element) {
        element.simpleName
    }
    
    dispatch def String getTextInternal(QueryRegistryTreeSource element) {
        val sourceId = element.sourceIdentifier
        if(sourceId == ExtensionBasedQuerySpecificationLoader.CONNECTOR_ID){
            return "Registered queries"
        } else if(sourceId.startsWith(XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX)){
            val label = sourceId.replace(XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX,"")
            return label
        }
        return sourceId
    }
    
    dispatch def String getTextInternal(QueryRegistryTreePackage element) {
        element.packageName
    }
    
    dispatch def String getTextInternal(Object element) {
        super.getText(element)
    }
    
}
