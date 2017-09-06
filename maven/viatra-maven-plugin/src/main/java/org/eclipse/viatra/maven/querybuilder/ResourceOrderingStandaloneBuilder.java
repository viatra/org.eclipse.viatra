/**
 * Copyright (c) 2010-2012, Denes Harmath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Denes Harmath - initial API and implementation
 */
package org.eclipse.viatra.maven.querybuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.builder.standalone.StandaloneBuilder;

/**
 * {@link StandaloneBuilder} implementation customized to sort resources by filename.
 */
public class ResourceOrderingStandaloneBuilder extends StandaloneBuilder {
  @Override
  protected List<URI> collectResources(final Iterable<String> roots, final ResourceSet resourceSet) {
      List<URI> resources = super.collectResources(roots, resourceSet);
      Collections.sort(resources, new Comparator<URI>() {

        @Override
        public int compare(URI o1, URI o2) {
            return o1.lastSegment().compareTo(o2.lastSegment());
        }
    });
    return resources;
  }
}
