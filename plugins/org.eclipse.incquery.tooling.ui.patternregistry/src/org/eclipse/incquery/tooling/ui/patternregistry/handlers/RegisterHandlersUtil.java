/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

public class RegisterHandlersUtil {

    public static void registerSingleFile(IFile file, IResourceSetProvider resourceSetProvider) {
        if (file != null && resourceSetProvider != null && file.getName().endsWith(".eiq")) {
            ResourceSet resourceSet = resourceSetProvider.get(file.getProject());
            Resource resource = resourceSet.getResource(
                    URI.createPlatformPluginURI(file.getFullPath().toOSString(), false), true);
            PatternRegistry.INSTANCE.addPatternsToRegistry(resource, file);
        }
    }

}
