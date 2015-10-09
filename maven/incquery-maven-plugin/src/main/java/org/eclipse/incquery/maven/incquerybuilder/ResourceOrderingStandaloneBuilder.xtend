/*******************************************************************************
 * Copyright (c) 2010-2012, Denes Harmath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.maven.incquerybuilder

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.builder.standalone.StandaloneBuilder

/**
 * {@link StandaloneBuilder} implementation customized to sort resources by filename.
 */
class ResourceOrderingStandaloneBuilder extends StandaloneBuilder {

    override protected collectResources(Iterable<String> roots, ResourceSet resourceSet) {
        super.collectResources(roots, resourceSet).sortBy[lastSegment]
    }

}
