/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.api;

import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;

/**
 * Implementations of this interface may provide analysis information about query executions
 *
 * @since 1.6
 */
public interface IPatternExecutionAnalyzer {

    /**
     * Configure this analyzer to instrument the given hints in order to be able to analyze pattern executions.
     * If modifying the used {@link QueryEvaluationHint} is required, this method shall return an instance with
     * overridden values, otherwise it shall return the hints unchanged.
     */
    public QueryEvaluationHint configure(QueryEvaluationHint hints);
    
    /**
     * This method is called after a {@link ViatraQueryMatcher} is created, allowing this analyzer to extract 
     * analysis information.
     * 
     * @throws ViatraQueryRuntimeException
     */
    public void processMatcher(ViatraQueryMatcher<?> matcher);
    
}
