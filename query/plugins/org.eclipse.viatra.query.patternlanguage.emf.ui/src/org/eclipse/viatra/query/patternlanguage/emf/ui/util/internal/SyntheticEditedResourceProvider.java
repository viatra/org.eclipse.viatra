/*******************************************************************************
 * Copyright (c) 2010-2016, IncQuery Labs Ltd., Peter Lunk
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

/**
 * @author Peter Lunk
 *
 */
package org.eclipse.viatra.query.patternlanguage.emf.ui.util.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

/**
 * 
 * Resource provider that creates an empty Xtext resource with a synthetic URI. Used by the 
 * VIATRA Transformation debugger.
 * 
 * @author Peter Lunk
 * @since 1.3
 */
@SuppressWarnings("restriction")
public class SyntheticEditedResourceProvider implements IEditedResourceProvider {
    @Inject
    private IResourceSetProvider resourceSetProvider;
    @Inject
    private FileExtensionProvider ext;

    @Override
    public XtextResource createResource() {
        ResourceSet resourceSet = resourceSetProvider.get(null);
        URI uri = URI.createURI("synthetic:/pattern." + ext.getPrimaryFileExtension());
        XtextResource result = (XtextResource) resourceSet.createResource(uri);
        resourceSet.getResources().add(result);
        return result;
    }
}
