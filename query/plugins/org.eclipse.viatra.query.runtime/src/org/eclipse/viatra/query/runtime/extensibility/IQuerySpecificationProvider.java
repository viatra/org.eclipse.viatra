/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
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
