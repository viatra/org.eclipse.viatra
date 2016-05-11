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
import org.eclipse.viatra.query.runtime.matchers.util.SingletonInstanceProvider;

/**
 * Provider implementation for storing an existing query specification instance.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public class SingletonQuerySpecificationProvider extends SingletonInstanceProvider<IQuerySpecification<?>>
        implements IQuerySpecificationProvider {

    /**
     * 
     * @param instance the instance to wrap
     */
    public SingletonQuerySpecificationProvider(IQuerySpecification<?> instance) {
        super(instance);
    }

    @Override
    public String getFullyQualifiedName() {
        return get().getFullyQualifiedName();
    }

}
