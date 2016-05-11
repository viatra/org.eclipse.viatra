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

import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.util.SingletonInstanceProvider;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Provider implementation for storing an existing query group instance.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public class SingletonQueryGroupProvider extends SingletonInstanceProvider<IQueryGroup> implements IQueryGroupProvider {

    /**
     * @param instance the instance to wrap
     */
    public SingletonQueryGroupProvider(IQueryGroup instance) {
        super(instance);
    }

    @Override
    public Set<String> getQuerySpecificationFQNs() {
        Builder<String> builder = ImmutableSet.<String>builder();
        for(IQuerySpecification<?> spec : get().getSpecifications()) {
           builder.add(spec.getFullyQualifiedName());
        }
        return builder.build();
    }

    @Override
    public Set<IQuerySpecificationProvider> getQuerySpecificationProviders() {
        Builder<IQuerySpecificationProvider> builder = ImmutableSet.<IQuerySpecificationProvider>builder();
        for(IQuerySpecification<?> spec : get().getSpecifications()) {
            builder.add(new SingletonQuerySpecificationProvider(spec));
        }
        return builder.build();
    }

}
