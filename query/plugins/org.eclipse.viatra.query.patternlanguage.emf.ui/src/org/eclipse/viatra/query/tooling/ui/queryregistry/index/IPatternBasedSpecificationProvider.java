/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.index;

import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.emf.common.util.URI;


/**
 * @author Abel Hegedus
 *
 */
public interface IPatternBasedSpecificationProvider extends IQuerySpecificationProvider {

    IQuerySpecification<?> getSpecification(SpecificationBuilder builder);
    
    /**
     * Returns the EMF URI the specification is initialized from.
     * 
     * @return the URI of the pattern the specification is built from, or null if not applicable
     */
    URI getSpecificationSourceURI();
}
