/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.extensibility;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.util.IProvider;

/**
 * Provider interface for {@link IQuerySpecification} instances with added method for
 * requesting the FQN for the query specification.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IQuerySpecificationProvider extends IProvider<IQuerySpecification<?>> {

    /**
     * Note that the provider will usually not load the query specification class to return the FQN.
     * 
     * @return the fully qualified name of the provided query specification
     */
    String getFullyQualifiedName();

    /**
     * Returns the name of project providing the specification (or null if not calculable)
     */
    String getSourceProjectName();
    
}
