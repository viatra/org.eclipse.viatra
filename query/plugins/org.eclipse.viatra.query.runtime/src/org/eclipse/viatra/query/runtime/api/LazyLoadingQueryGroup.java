/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.api;

import java.util.Set;

import org.eclipse.viatra.query.runtime.api.impl.BaseQueryGroup;
import org.eclipse.viatra.query.runtime.matchers.util.IProvider;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Initializes a query group from a set of query providers. The query providers are not executed until the queries
 * themselves are asked in the {@link #getSpecifications()} method.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class LazyLoadingQueryGroup extends BaseQueryGroup {

    private final Set<? extends IProvider<IQuerySpecification<?>>> providers;
    private Set<IQuerySpecification<?>> specifications = null;

    /**
     * @param providers a non-null set to initialize the group
     */
    public LazyLoadingQueryGroup(Set<? extends IProvider<IQuerySpecification<?>>> providers) {
        Preconditions.checkArgument(providers != null, "The set of providers must not be null");
        this.providers = providers;
    }

    /**
     * @param providers a non-null set to initialize the group
     */
    public static IQueryGroup of(Set<? extends IProvider<IQuerySpecification<?>>> querySpecifications) {
        return new LazyLoadingQueryGroup(querySpecifications);
    }

    @Override
    public Set<IQuerySpecification<?>> getSpecifications() {
        if (specifications == null) {
            specifications = Sets.newHashSet(Iterables.transform(providers,
                    new Function<IProvider<IQuerySpecification<?>>, IQuerySpecification<?>>() {

                        @Override
                        public IQuerySpecification<?> apply(IProvider<IQuerySpecification<?>> input) {
                            if (input == null) {
                                return null;
                            }
                            return input.get();
                        }
                    }));
        }
        return specifications;
    }

}
