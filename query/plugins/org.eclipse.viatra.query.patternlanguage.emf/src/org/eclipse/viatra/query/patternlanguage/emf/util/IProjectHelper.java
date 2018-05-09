/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * @since 2.0
 * @noreference This interface is for internal use only, not intended to be referenced by clients.
 */
@SuppressWarnings("restriction")
@ImplementedBy(IProjectHelper.DefaultProjectHelper.class)
public interface IProjectHelper {

    /**
     * Only returns true for uris that are (a) workspace file uris, but (b) are not in source folders 
     */
    boolean isStandaloneFileURI(EObject context, URI uri);
    
    public class DefaultProjectHelper implements IProjectHelper {
        @Inject
        IProjectConfigProvider projectConfigurationProvider;
        
        @Override
        public boolean isStandaloneFileURI(EObject context, URI uri) {
            if (uri.isPlatformResource() && context.eResource() != null) {
                IProjectConfig project = projectConfigurationProvider.getProjectConfig(context.eResource().getResourceSet());
                if (project != null) {
                    return project.findSourceFolderContaining(uri) == null;
                }
            }
            return true;
        }
    }

}
