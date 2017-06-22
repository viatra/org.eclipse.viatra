/*******************************************************************************
 * Copyright (c) 2010-2017, Dénes Harmath, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Dénes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage

import java.util.Set
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.EObject

/**
 * The coverage of a PSystem element is measured within a specific scope.
 * This tuple identifies a single coverage measurement.
 */
@Data
class CoverageContext<T> {
    T element
    Set<URI> scope
    
    static def <T> CoverageContext<T> create(T element, EMFScope scope) {
        new CoverageContext(element, scope.uris)
    }
    
    private static def Set<URI> getUris(EMFScope scope) {
        scope.scopeRoots.map[
            switch it {
                Resource: #{URI}
                ResourceSet: resources.map[URI]
                EObject: #{EcoreUtil.getURI(it)}
                default: #{}
            }
        ].flatten.toSet
    }
    
}