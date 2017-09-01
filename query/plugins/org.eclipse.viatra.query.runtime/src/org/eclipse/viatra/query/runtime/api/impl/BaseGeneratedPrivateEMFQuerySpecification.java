/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.api.impl;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * 
 * Provides common functionality of pattern-specific generated query specifications for private patterns over the EMF
 * scope.
 * 
 * @since 1.6
 * @deprecated Replaced with the more generic {@link BaseGeneratedEMFQuerySpecificationWithGenericMatcher}
 */
@Deprecated
public abstract class BaseGeneratedPrivateEMFQuerySpecification extends BaseGeneratedEMFQuerySpecificationWithGenericMatcher {

    public BaseGeneratedPrivateEMFQuerySpecification(PQuery wrappedPQuery) {
        super(wrappedPQuery);
    }
}
