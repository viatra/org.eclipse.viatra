/**
 * Copyright (c) 2010-2012, Denes Harmath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
